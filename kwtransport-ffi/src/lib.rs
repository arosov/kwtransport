use robusta_jni::bridge;

#[bridge]
mod jni {
    use robusta_jni::jni::errors::Result as JniResult;
    use robusta_jni::jni::JNIEnv;
    use robusta_jni::jni::objects::{JObject, JValue};
    use std::net::SocketAddr;
    use std::time::Duration;
    use crate::{
        NativeEndpoint, NativeConnection, NativeSendStream, NativeRecvStream, NativeIdentity, 
        NativeStreamPair, NativeDatagram,
        PtrSend, PtrSendConnection, PtrSendSendStream, PtrSendRecvStream,
        RUNTIME, JAVA_VM, EndpointInner,
        map_conn_err, map_stream_err, map_send_datagram_err
    };
    use wtransport::{ClientConfig, ServerConfig, Identity};
    use wtransport::error::{ConnectingError, ConnectionError, StreamOpeningError, SendDatagramError};

    #[package(ovh.devcraft.kwtransport)]
    pub struct KwTransport;

    impl KwTransport {
        pub extern "jni" fn hello() -> String {
            "Hello from Rust!".to_string()
        }
    }

    #[package(ovh.devcraft.kwtransport)]
    pub struct JniHelper;

    impl JniHelper {
        pub extern "java" fn throwConnectingException(env: &JNIEnv, message: String, type_name: String) -> JniResult<()> {}
        pub extern "java" fn throwConnectionException(env: &JNIEnv, message: String, type_name: String) -> JniResult<()> {}
        pub extern "java" fn throwStreamOpeningException(env: &JNIEnv, message: String, type_name: String) -> JniResult<()> {}
        pub extern "java" fn throwSendDatagramException(env: &JNIEnv, message: String, type_name: String) -> JniResult<()> {}
        pub extern "java" fn onNotify(env: &JNIEnv, id: i64, result: i64, error_type: String, error_message: String) -> JniResult<()> {}
    }

    #[package(ovh.devcraft.kwtransport)]
    pub struct Endpoint;

    impl Endpoint {
        #[call_type(unchecked)]
        pub extern "jni" fn createClient(env: &JNIEnv, bind_addr: String, accept_all_certs: bool, max_idle_timeout_millis: i64) -> JniResult<i64> {
            let _ = JAVA_VM.set(env.get_java_vm().unwrap());
            let _guard = RUNTIME.enter();
            
            let addr: SocketAddr = match bind_addr.parse() {
                Ok(a) => a,
                Err(e) => {
                    let _ = env.throw_new("java/lang/IllegalArgumentException", e.to_string());
                    return Ok(0);
                }
            };
            
            let builder = ClientConfig::builder()
                .with_bind_address(addr);
            
            let builder = if accept_all_certs {
                builder.with_no_cert_validation()
            } else {
                builder.with_native_certs()
            };
            
            let mut builder = builder;
            if max_idle_timeout_millis > 0 {
                builder = builder.max_idle_timeout(Some(Duration::from_millis(max_idle_timeout_millis as u64))).unwrap();
            }
            
            let client_config = builder.build();

            let endpoint = match wtransport::Endpoint::client(client_config) {
                Ok(e) => e,
                Err(e) => {
                    let _ = env.throw_new("java/lang/RuntimeException", e.to_string());
                    return Ok(0);
                }
            };
            
            Ok(Box::into_raw(Box::new(NativeEndpoint::new_client(endpoint))) as i64)
        }

        #[call_type(unchecked)]
        pub extern "jni" fn createServer(env: &JNIEnv, bind_addr: String, cert_handle: i64) -> JniResult<i64> {
            let _ = JAVA_VM.set(env.get_java_vm().unwrap());
            let _guard = RUNTIME.enter();
            
            let addr: SocketAddr = match bind_addr.parse() {
                Ok(a) => a,
                Err(e) => {
                    let _ = env.throw_new("java/lang/IllegalArgumentException", e.to_string());
                    return Ok(0);
                }
            };

            let identity = unsafe { Box::from_raw(cert_handle as *mut NativeIdentity) }.0;

            let server_config = ServerConfig::builder()
                .with_bind_address(addr)
                .with_identity(identity)
                .build();

            let endpoint = match wtransport::Endpoint::server(server_config) {
                Ok(e) => e,
                Err(e) => {
                    let _ = env.throw_new("java/lang/RuntimeException", e.to_string());
                    return Ok(0);
                }
            };
            
            Ok(Box::into_raw(Box::new(NativeEndpoint::new_server(endpoint))) as i64)
        }

        pub extern "jni" fn connect(_env: &JNIEnv, handle: i64, id: i64, url: String) {
            let ptr = PtrSend(handle as *const NativeEndpoint);
            
            RUNTIME.spawn(async move {
                let endpoint_ref = unsafe { ptr.as_ref() };
                let client = match &endpoint_ref.inner {
                    EndpointInner::Client(c) => c,
                    _ => return,
                };

                let result = client.connect(&url).await;
                
                let vm = JAVA_VM.get().expect("JavaVM not initialized");
                let env = vm.attach_current_thread().expect("Failed to attach thread");
                
                match result {
                    Ok(connection) => {
                        let conn_handle = Box::into_raw(Box::new(NativeConnection(connection))) as i64;
                        let _ = JniHelper::onNotify(&env, id, conn_handle, "".to_string(), "".to_string());
                    }
                    Err(e) => {
                        let (ex_type, msg) = match e {
                            ConnectingError::InvalidUrl(s) => ("INVALID_URL", s),
                            ConnectingError::DnsLookup(e) => ("DNS_LOOKUP", e.to_string()),
                            ConnectingError::DnsNotFound => ("DNS_NOT_FOUND", "DNS not found".to_string()),
                            ConnectingError::ConnectionError(_) => ("CONNECTION", e.to_string()),
                            ConnectingError::SessionRejected => ("SESSION_REJECTED", "Session rejected".to_string()),
                            ConnectingError::ReservedHeader(s) => ("RESERVED_HEADER", s),
                            ConnectingError::EndpointStopping => ("ENDPOINT_STOPPING", "Endpoint stopping".to_string()),
                            ConnectingError::CidsExhausted => ("CIDS_EHAUSTED", "CIDs exhausted".to_string()),
                            ConnectingError::InvalidServerName(s) => ("INVALID_SERVER_NAME", s),
                            ConnectingError::InvalidRemoteAddress(a) => ("INVALID_REMOTE_ADDRESS", a.to_string()),
                        };
                        let _ = JniHelper::onNotify(&env, id, 0, ex_type.to_string(), msg);
                    }
                }
            });
        }

        pub extern "jni" fn listenSessions(_env: &JNIEnv, handle: i64, id: i64) {
            let ptr = PtrSend(handle as *const NativeEndpoint);
            let endpoint_ref = unsafe { &*ptr.0 };
            let cancel_token = endpoint_ref.cancel_token.clone();
            
            RUNTIME.spawn(async move {
                let endpoint_ref = unsafe { ptr.as_ref() };
                let server = match &endpoint_ref.inner {
                    EndpointInner::Server(s) => s,
                    _ => return,
                };

                let vm = JAVA_VM.get().expect("JavaVM not initialized");
                
                loop {
                    tokio::select! {
                        _ = cancel_token.cancelled() => break,
                        incoming_session = server.accept() => {
                             let session_request = match incoming_session.await {
                                 Ok(r) => r,
                                 Err(_) => continue,
                             };
                             
                             let result = session_request.accept().await;
                             
                             let env = vm.attach_current_thread().expect("Failed to attach thread");
                             match result {
                                 Ok(connection) => {
                                     let conn_handle = Box::into_raw(Box::new(NativeConnection(connection))) as i64;
                                     let _ = JniHelper::onNotify(&env, id, conn_handle, "".to_string(), "".to_string());
                                 }
                                 Err(e) => {
                                     // For multi-shot, we might not want to close the flow on a single connection error
                                     // But let's log it or notify for now.
                                     let _ = JniHelper::onNotify(&env, id, 0, "CONNECTION".to_string(), e.to_string());
                                 }
                             }
                        }
                    }
                }
            });
        }

        pub extern "jni" fn stopListenSessions(_env: &JNIEnv, handle: i64) {
             let endpoint_ref = unsafe { &*(handle as *const NativeEndpoint) };
             endpoint_ref.cancel_token.cancel();
        }

        pub extern "jni" fn destroy(handle: i64) {
            if handle != 0 {
                unsafe {
                    let _ = Box::from_raw(handle as *mut NativeEndpoint);
                }
            }
        }
    }
    
    #[package(ovh.devcraft.kwtransport)]
    pub struct Connection;

    impl Connection {
        pub extern "jni" fn openUni(_env: &JNIEnv, handle: i64, id: i64) {
             let ptr = PtrSendConnection(handle as *const NativeConnection);
             RUNTIME.spawn(async move {
                 let conn = unsafe { ptr.as_ref() };
                 
                 match conn.0.open_uni().await {
                    Ok(opening) => {
                        match opening.await {
                            Ok(stream) => {
                                let vm = JAVA_VM.get().expect("JavaVM not initialized");
                                let env = vm.attach_current_thread().expect("Failed to attach thread");
                                let h = Box::into_raw(Box::new(NativeSendStream(stream))) as i64;
                                let _ = JniHelper::onNotify(&env, id, h, "".to_string(), "".to_string());
                            }
                            Err(e) => {
                                let vm = JAVA_VM.get().expect("JavaVM not initialized");
                                let env = vm.attach_current_thread().expect("Failed to attach thread");
                                let (t, m) = map_stream_err(e);
                                let _ = JniHelper::onNotify(&env, id, 0, t, m);
                            }
                        }
                    }
                    Err(e) => {
                        let vm = JAVA_VM.get().expect("JavaVM not initialized");
                        let env = vm.attach_current_thread().expect("Failed to attach thread");
                        let (t, m) = map_conn_err(e);
                        let _ = JniHelper::onNotify(&env, id, 0, t, m);
                    }
                 }
             });
        }

        pub extern "jni" fn openBi(_env: &JNIEnv, handle: i64, id: i64) {
             let ptr = PtrSendConnection(handle as *const NativeConnection);
             RUNTIME.spawn(async move {
                 let conn = unsafe { ptr.as_ref() };
                 
                 match conn.0.open_bi().await {
                    Ok(opening) => {
                        match opening.await {
                            Ok((send, recv)) => {
                                let vm = JAVA_VM.get().expect("JavaVM not initialized");
                                let env = vm.attach_current_thread().expect("Failed to attach thread");
                                let pair = NativeStreamPair {
                                    send: Some(NativeSendStream(send)),
                                    recv: Some(NativeRecvStream(recv)),
                                };
                                let h = Box::into_raw(Box::new(pair)) as i64;
                                let _ = JniHelper::onNotify(&env, id, h, "".to_string(), "".to_string());
                            }
                            Err(e) => {
                                let vm = JAVA_VM.get().expect("JavaVM not initialized");
                                let env = vm.attach_current_thread().expect("Failed to attach thread");
                                let (t, m) = map_stream_err(e);
                                let _ = JniHelper::onNotify(&env, id, 0, t, m);
                            }
                        }
                    }
                    Err(e) => {
                        let vm = JAVA_VM.get().expect("JavaVM not initialized");
                        let env = vm.attach_current_thread().expect("Failed to attach thread");
                        let (t, m) = map_conn_err(e);
                        let _ = JniHelper::onNotify(&env, id, 0, t, m);
                    }
                 }
             });
        }

        pub extern "jni" fn acceptUni(_env: &JNIEnv, handle: i64, id: i64) {
             let ptr = PtrSendConnection(handle as *const NativeConnection);
             RUNTIME.spawn(async move {
                 let conn = unsafe { ptr.as_ref() };
                 
                 match conn.0.accept_uni().await {
                    Ok(stream) => {
                        let vm = JAVA_VM.get().expect("JavaVM not initialized");
                        let env = vm.attach_current_thread().expect("Failed to attach thread");
                        let h = Box::into_raw(Box::new(NativeRecvStream(stream))) as i64;
                        let _ = JniHelper::onNotify(&env, id, h, "".to_string(), "".to_string());
                    }
                    Err(e) => {
                        let vm = JAVA_VM.get().expect("JavaVM not initialized");
                        let env = vm.attach_current_thread().expect("Failed to attach thread");
                        let (t, m) = map_conn_err(e);
                        let _ = JniHelper::onNotify(&env, id, 0, t, m);
                    }
                 }
             });
        }

        pub extern "jni" fn acceptBi(_env: &JNIEnv, handle: i64, id: i64) {
             let ptr = PtrSendConnection(handle as *const NativeConnection);
             RUNTIME.spawn(async move {
                 let conn = unsafe { ptr.as_ref() };
                 
                 match conn.0.accept_bi().await {
                    Ok((send, recv)) => {
                        let vm = JAVA_VM.get().expect("JavaVM not initialized");
                        let env = vm.attach_current_thread().expect("Failed to attach thread");
                        let pair = NativeStreamPair {
                            send: Some(NativeSendStream(send)),
                            recv: Some(NativeRecvStream(recv)),
                        };
                        let h = Box::into_raw(Box::new(pair)) as i64;
                        let _ = JniHelper::onNotify(&env, id, h, "".to_string(), "".to_string());
                    }
                    Err(e) => {
                        let vm = JAVA_VM.get().expect("JavaVM not initialized");
                        let env = vm.attach_current_thread().expect("Failed to attach thread");
                        let (t, m) = map_conn_err(e);
                        let _ = JniHelper::onNotify(&env, id, 0, t, m);
                    }
                 }
             });
        }

        pub extern "jni" fn sendDatagram(env: &JNIEnv, handle: i64, data: Box<[u8]>) -> JniResult<()> {
            let _guard = RUNTIME.enter();
            let native_connection = unsafe { &*(handle as *const NativeConnection) };
            
            if let Err(e) = native_connection.0.send_datagram(data) {
                let (t, m) = map_send_datagram_err(e);
                let _ = JniHelper::throwSendDatagramException(env, m, t);
            }
            Ok(())
        }

        pub extern "jni" fn receiveDatagram(_env: &JNIEnv, handle: i64, id: i64) {
             let ptr = PtrSendConnection(handle as *const NativeConnection);
             RUNTIME.spawn(async move {
                 let conn = unsafe { ptr.as_ref() };
                 
                 match conn.0.receive_datagram().await {
                    Ok(datagram) => {
                        let vm = JAVA_VM.get().expect("JavaVM not initialized");
                        let env = vm.attach_current_thread().expect("Failed to attach thread");
                        let d = NativeDatagram(datagram.to_vec().into_boxed_slice());
                        let h = Box::into_raw(Box::new(d)) as i64;
                        let _ = JniHelper::onNotify(&env, id, h, "".to_string(), "".to_string());
                    }
                    Err(e) => {
                        let vm = JAVA_VM.get().expect("JavaVM not initialized");
                        let env = vm.attach_current_thread().expect("Failed to attach thread");
                        let (t, m) = map_conn_err(e);
                        let _ = JniHelper::onNotify(&env, id, 0, t, m);
                    }
                 }
             });
        }

        pub extern "jni" fn destroy(handle: i64) {
            if handle != 0 {
                unsafe {
                    let _ = Box::from_raw(handle as *mut NativeConnection);
                }
            }
        }
    }

    #[package(ovh.devcraft.kwtransport)]
    pub struct StreamPairHelper;

    impl StreamPairHelper {
        pub extern "jni" fn getSend(handle: i64) -> i64 {
            let pair = unsafe { &mut *(handle as *mut NativeStreamPair) };
            match pair.send.take() {
                Some(s) => Box::into_raw(Box::new(s)) as i64,
                None => 0,
            }
        }
        pub extern "jni" fn getRecv(handle: i64) -> i64 {
            let pair = unsafe { &mut *(handle as *mut NativeStreamPair) };
            match pair.recv.take() {
                Some(s) => Box::into_raw(Box::new(s)) as i64,
                None => 0,
            }
        }
        pub extern "jni" fn destroy(handle: i64) {
            if handle != 0 {
                unsafe { let _ = Box::from_raw(handle as *mut NativeStreamPair); }
            }
        }
    }

    #[package(ovh.devcraft.kwtransport)]
    pub struct DatagramHelper;

    impl DatagramHelper {
        pub extern "jni" fn getData(_env: &JNIEnv, handle: i64) -> JniResult<Box<[u8]>> {
             let d = unsafe { &*(handle as *const NativeDatagram) };
             Ok(d.0.clone())
        }
        pub extern "jni" fn destroy(handle: i64) {
            if handle != 0 {
                unsafe { let _ = Box::from_raw(handle as *mut NativeDatagram); }
            }
        }
    }

            #[package(ovh.devcraft.kwtransport)]

            pub struct SendStream;

        

            impl SendStream {

                pub extern "jni" fn write(_env: &JNIEnv, handle: i64, data: Box<[u8]>, id: i64) {

                     let ptr = PtrSendSendStream(handle as *mut NativeSendStream);

                     RUNTIME.spawn(async move {

                         let stream = unsafe { ptr.as_mut() };

                         

                         match stream.0.write_all(&data).await {

                             Ok(_) => {

                                 let vm = JAVA_VM.get().expect("JavaVM not initialized");

                                 let env = vm.attach_current_thread().expect("Failed to attach thread");

                                 let _ = JniHelper::onNotify(&env, id, 1, "".to_string(), "".to_string());

                             },

                             Err(e) => {

                                 let vm = JAVA_VM.get().expect("JavaVM not initialized");

                                 let env = vm.attach_current_thread().expect("Failed to attach thread");

                                 let _ = JniHelper::onNotify(&env, id, 0, "IO_EXCEPTION".to_string(), e.to_string());

                             }

                         }

                     });

                }

                pub extern "jni" fn destroy(handle: i64) {

                    if handle != 0 {

                        unsafe { let _ = Box::from_raw(handle as *mut NativeSendStream); }

                    }

                }

            }

        

            #[package(ovh.devcraft.kwtransport)]

            pub struct RecvStream;

        

            impl RecvStream {

                pub extern "jni" fn read<'env>(env: &JNIEnv<'env>, handle: i64, jbuffer: JObject<'env>, id: i64) -> JniResult<()> {

                     let ptr = PtrSendRecvStream(handle as *mut NativeRecvStream);

                     let jbuffer_ref = env.new_global_ref(jbuffer)?;

                     

                     RUNTIME.spawn(async move {

                         let stream = unsafe { ptr.as_mut() };

                         let vm = JAVA_VM.get().expect("JavaVM not initialized");

                         

                         let len = {

                             let env = vm.attach_current_thread().expect("Failed to attach thread");

                             let jbuff = jbuffer_ref.as_obj();

                             let raw_jbuff = jbuff.into_inner();

                             match env.get_array_length(raw_jbuff as robusta_jni::jni::sys::jbyteArray) {

                                 Ok(l) => l as usize,

                                 Err(e) => {

                                     let _ = JniHelper::onNotify(&env, id, 0, "IO_EXCEPTION".to_string(), e.to_string());

                                     return;

                                 }

                             }

                         };

                         

                         let mut buf = vec![0u8; len];

                         match stream.0.read(&mut buf).await {

                             Ok(bytes_read) => {

                                 let env = vm.attach_current_thread().expect("Failed to attach thread");

                                 match bytes_read {

                                     Some(n) => {

                                         let jbuff = jbuffer_ref.as_obj();

                                         let raw_jbuff = jbuff.into_inner();

                                         if let Err(e) = env.set_byte_array_region(raw_jbuff as robusta_jni::jni::sys::jbyteArray, 0, bytemuck::cast_slice(&buf[..n])) {

                                              let _ = JniHelper::onNotify(&env, id, 0, "IO_EXCEPTION".to_string(), e.to_string());

                                              return;

                                         }

                                         let _ = JniHelper::onNotify(&env, id, n as i64, "".to_string(), "".to_string());

                                     },

                                     None => {

                                         let _ = JniHelper::onNotify(&env, id, -1, "".to_string(), "".to_string());

                                     }

                                 }

                             },

                             Err(e) => {

                                 let env = vm.attach_current_thread().expect("Failed to attach thread");

                                 let _ = JniHelper::onNotify(&env, id, 0, "IO_EXCEPTION".to_string(), e.to_string());

                             }

                         }

                     });

                     Ok(())

                }

                pub extern "jni" fn destroy(handle: i64) {

                    if handle != 0 {

                        unsafe { let _ = Box::from_raw(handle as *mut NativeRecvStream); }

                    }

                }

            }

    #[package(ovh.devcraft.kwtransport)]
    pub struct Certificate;

    impl Certificate {
        pub extern "jni" fn selfSigned(env: &JNIEnv, sans: Vec<String>) -> JniResult<i64> {
             let _guard = RUNTIME.enter();
             match Identity::self_signed(sans) {
                 Ok(identity) => Ok(Box::into_raw(Box::new(NativeIdentity(identity))) as i64),
                 Err(e) => {
                     let _ = env.throw_new("java/lang/IllegalArgumentException", e.to_string());
                     Ok(0)
                 }
             }
        }

        pub extern "jni" fn destroy(handle: i64) {
            if handle != 0 {
                unsafe {
                    let _ = Box::from_raw(handle as *mut NativeIdentity);
                }
            }
        }
    }
}

use wtransport::endpoint::endpoint_side::{Client, Server};
use once_cell::sync::OnceCell;
use tokio_util::sync::CancellationToken;
use wtransport::error::{ConnectingError, ConnectionError, StreamOpeningError, SendDatagramError};

pub struct NativeEndpoint {
    pub inner: EndpointInner,
    pub cancel_token: CancellationToken,
}

pub enum EndpointInner {
    Client(wtransport::Endpoint<Client>),
    Server(wtransport::Endpoint<Server>),
}

impl NativeEndpoint {
    pub fn new_client(c: wtransport::Endpoint<Client>) -> Self {
        Self {
            inner: EndpointInner::Client(c),
            cancel_token: CancellationToken::new(),
        }
    }
    pub fn new_server(s: wtransport::Endpoint<Server>) -> Self {
        Self {
            inner: EndpointInner::Server(s),
            cancel_token: CancellationToken::new(),
        }
    }
}

pub struct NativeConnection(pub wtransport::Connection);
pub struct NativeSendStream(pub wtransport::SendStream);
pub struct NativeRecvStream(pub wtransport::RecvStream);
pub struct NativeIdentity(pub wtransport::Identity);

lazy_static::lazy_static! {
    pub static ref RUNTIME: tokio::runtime::Runtime = tokio::runtime::Runtime::new().unwrap();
}

pub static JAVA_VM: OnceCell<robusta_jni::jni::JavaVM> = OnceCell::new();

#[derive(Copy, Clone)]
pub struct PtrSend(pub *const NativeEndpoint);
unsafe impl Send for PtrSend {}
unsafe impl Sync for PtrSend {}

pub struct NativeStreamPair {
    pub send: Option<NativeSendStream>,
    pub recv: Option<NativeRecvStream>,
}

pub struct NativeDatagram(pub Box<[u8]>);

#[derive(Copy, Clone)]
pub struct PtrSendConnection(pub *const NativeConnection);
unsafe impl Send for PtrSendConnection {}
unsafe impl Sync for PtrSendConnection {}

#[derive(Copy, Clone)]
pub struct PtrSendSendStream(pub *mut NativeSendStream);
unsafe impl Send for PtrSendSendStream {}
unsafe impl Sync for PtrSendSendStream {}

#[derive(Copy, Clone)]
pub struct PtrSendRecvStream(pub *mut NativeRecvStream);
unsafe impl Send for PtrSendRecvStream {}
unsafe impl Sync for PtrSendRecvStream {}

unsafe impl Send for NativeEndpoint {}
unsafe impl Sync for NativeEndpoint {}

unsafe impl Send for NativeConnection {}
unsafe impl Sync for NativeConnection {}

unsafe impl Send for NativeSendStream {}
unsafe impl Sync for NativeSendStream {}

unsafe impl Send for NativeRecvStream {}
unsafe impl Sync for NativeRecvStream {}

impl PtrSend {
    unsafe fn as_ref(&self) -> &NativeEndpoint { &*self.0 }
}
impl PtrSendConnection {
    unsafe fn as_ref(&self) -> &NativeConnection { &*self.0 }
}
impl PtrSendSendStream {
    unsafe fn as_mut(self) -> &'static mut NativeSendStream { &mut *self.0 }
}
impl PtrSendRecvStream {
    unsafe fn as_mut(self) -> &'static mut NativeRecvStream { &mut *self.0 }
}

fn map_conn_err(error: ConnectionError) -> (String, String) {
    let (ex_type, msg) = match error {
        ConnectionError::ConnectionClosed(_) => ("CONNECTION_CLOSED", "Connection closed"),
        ConnectionError::ApplicationClosed(_) => ("APPLICATION_CLOSED", "Application closed"),
        ConnectionError::LocallyClosed => ("LOCALLY_CLOSED", "Locally closed"),
        ConnectionError::LocalH3Error(_) => ("LOCAL_H3_ERROR", "Local H3 error"),
        ConnectionError::TimedOut => ("TIMED_OUT", "Timed out"),
        ConnectionError::QuicProto(_) => ("QUIC_PROTO", "QUIC protocol error"),
        ConnectionError::CidsExhausted => ("CIDS_EHAUSTED", "CIDs exhausted"),
    };
    (ex_type.to_string(), msg.to_string())
}

fn map_stream_err(error: StreamOpeningError) -> (String, String) {
    let (ex_type, msg) = match error {
        StreamOpeningError::NotConnected => ("NOT_CONNECTED", "Not connected"),
        StreamOpeningError::Refused => ("REFUSED", "Opening stream refused"),
    };
    (ex_type.to_string(), msg.to_string())
}

fn map_send_datagram_err(error: SendDatagramError) -> (String, String) {
    let (ex_type, msg) = match error {
        SendDatagramError::NotConnected => ("NOT_CONNECTED", "Not connected"),
        SendDatagramError::UnsupportedByPeer => ("UNSUPPORTED_BY_PEER", "Unsupported by peer"),
        SendDatagramError::TooLarge => ("TOO_LARGE", "Too large"),
    };
    (ex_type.to_string(), msg.to_string())
}