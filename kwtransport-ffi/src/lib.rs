use robusta_jni::bridge;

#[bridge]
mod jni {
    use robusta_jni::jni::errors::Result as JniResult;
    use robusta_jni::jni::JNIEnv;
    use robusta_jni::jni::objects::{JObject, JValue};
    use std::net::SocketAddr;
    use crate::{NativeEndpoint, NativeConnection, NativeSendStream, NativeRecvStream, NativeIdentity, RUNTIME};
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
    }

    #[package(ovh.devcraft.kwtransport)]
    pub struct Endpoint;

    impl Endpoint {
        pub extern "jni" fn createClient(env: &JNIEnv, bind_addr: String, accept_all_certs: bool) -> JniResult<i64> {
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
            
            let client_config = if accept_all_certs {
                builder.with_no_cert_validation().build()
            } else {
                builder.with_native_certs().build()
            };

            let endpoint = match wtransport::Endpoint::client(client_config) {
                Ok(e) => e,
                Err(e) => {
                    let _ = env.throw_new("java/lang/RuntimeException", e.to_string());
                    return Ok(0);
                }
            };
            
            Ok(Box::into_raw(Box::new(NativeEndpoint::Client(endpoint))) as i64)
        }

        pub extern "jni" fn createServer(env: &JNIEnv, bind_addr: String, cert_handle: i64) -> JniResult<i64> {
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
            
            Ok(Box::into_raw(Box::new(NativeEndpoint::Server(endpoint))) as i64)
        }

        pub extern "jni" fn connect(env: &JNIEnv, handle: i64, url: String) -> JniResult<i64> {
            let _guard = RUNTIME.enter();
            let native_endpoint = unsafe { &*(handle as *const NativeEndpoint) };
            
            let client = match native_endpoint {
                NativeEndpoint::Client(c) => c,
                _ => {
                    let _ = env.throw_new("java/lang/IllegalStateException", "Not a client endpoint");
                    return Ok(0);
                }
            };
            
            match RUNTIME.block_on(async { client.connect(&url).await }) {
                Ok(connection) => {
                    Ok(Box::into_raw(Box::new(NativeConnection(connection))) as i64)
                }
                Err(e) => {
                    let (ex_type, msg) = match e {
                        ConnectingError::InvalidUrl(s) => ("INVALID_URL", s),
                        ConnectingError::DnsLookup(e) => ("DNS_LOOKUP", e.to_string()),
                        ConnectingError::DnsNotFound => ("DNS_NOT_FOUND", "DNS not found".to_string()),
                        ConnectingError::ConnectionError(e) => {
                            throw_conn_ex(env, e);
                            return Ok(0);
                        },
                        ConnectingError::SessionRejected => ("SESSION_REJECTED", "Session rejected".to_string()),
                        ConnectingError::ReservedHeader(s) => ("RESERVED_HEADER", s),
                        ConnectingError::EndpointStopping => ("ENDPOINT_STOPPING", "Endpoint stopping".to_string()),
                        ConnectingError::CidsExhausted => ("CIDS_EHAUSTED", "CIDs exhausted".to_string()),
                        ConnectingError::InvalidServerName(s) => ("INVALID_SERVER_NAME", s),
                        ConnectingError::InvalidRemoteAddress(a) => ("INVALID_REMOTE_ADDRESS", a.to_string()),
                    };
                    let _ = JniHelper::throwConnectingException(env, msg, ex_type.to_string());
                    Ok(0)
                }
            }
        }

        pub extern "jni" fn acceptSession(env: &JNIEnv, handle: i64) -> JniResult<i64> {
             let _guard = RUNTIME.enter();
             let native_endpoint = unsafe { &*(handle as *const NativeEndpoint) };
             
             let server = match native_endpoint {
                 NativeEndpoint::Server(s) => s,
                 _ => {
                     let _ = env.throw_new("java/lang/IllegalStateException", "Not a server endpoint");
                     return Ok(0);
                 }
             };

             let incoming_session = RUNTIME.block_on(async { server.accept().await });
             
             match RUNTIME.block_on(async { incoming_session.await }) {
                 Ok(session_request) => {
                     match RUNTIME.block_on(async { session_request.accept().await }) {
                         Ok(connection) => {
                             Ok(Box::into_raw(Box::new(NativeConnection(connection))) as i64)
                         }
                         Err(e) => {
                             throw_conn_ex(env, e);
                             Ok(0)
                         }
                     }
                 }
                 Err(e) => {
                     throw_conn_ex(env, e);
                     Ok(0)
                 }
             }
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
        pub extern "jni" fn openUni(env: &JNIEnv, handle: i64) -> JniResult<i64> {
            let _guard = RUNTIME.enter();
            let native_connection = unsafe { &*(handle as *const NativeConnection) };
            
            let opening = match RUNTIME.block_on(async { native_connection.0.open_uni().await }) {
                Ok(o) => o,
                Err(e) => {
                    throw_conn_ex(env, e);
                    return Ok(0);
                }
            };

            let stream = match RUNTIME.block_on(async { opening.await }) {
                Ok(s) => s,
                Err(e) => {
                    throw_stream_opening_ex(env, e);
                    return Ok(0);
                }
            };
            
            Ok(Box::into_raw(Box::new(NativeSendStream(stream))) as i64)
        }

        pub extern "jni" fn openBi<'env>(env: &JNIEnv<'env>, handle: i64) -> JniResult<JObject<'env>> {
            let _guard = RUNTIME.enter();
            let native_connection = unsafe { &*(handle as *const NativeConnection) };
            
            let opening = match RUNTIME.block_on(async { native_connection.0.open_bi().await }) {
                Ok(o) => o,
                Err(e) => {
                    throw_conn_ex(env, e);
                    return Ok(JObject::null());
                }
            };

            let (send, recv) = match RUNTIME.block_on(async { opening.await }) {
                Ok(pair) => pair,
                Err(e) => {
                    throw_stream_opening_ex(env, e);
                    return Ok(JObject::null());
                }
            };
            
            let send_handle = Box::into_raw(Box::new(NativeSendStream(send))) as i64;
            let recv_handle = Box::into_raw(Box::new(NativeRecvStream(recv))) as i64;
            
            let pair_class = env.find_class("ovh/devcraft/kwtransport/StreamPair")?;
            let send_class = env.find_class("ovh/devcraft/kwtransport/SendStream")?;
            let recv_class = env.find_class("ovh/devcraft/kwtransport/RecvStream")?;
            
            let send_obj = env.new_object(send_class, "(J)V", &[JValue::Long(send_handle)])?;
            let recv_obj = env.new_object(recv_class, "(J)V", &[JValue::Long(recv_handle)])?;
            
            let pair_obj = env.new_object(pair_class, "(Lovh/devcraft/kwtransport/SendStream;Lovh/devcraft/kwtransport/RecvStream;)V", &[
                JValue::Object(send_obj),
                JValue::Object(recv_obj),
            ])?;
            
            Ok(pair_obj)
        }

        pub extern "jni" fn acceptUni(env: &JNIEnv, handle: i64) -> JniResult<i64> {
            let _guard = RUNTIME.enter();
            let native_connection = unsafe { &*(handle as *const NativeConnection) };
            
            let stream = match RUNTIME.block_on(async { native_connection.0.accept_uni().await }) {
                Ok(s) => s,
                Err(e) => {
                    throw_conn_ex(env, e);
                    return Ok(0);
                }
            };
            
            Ok(Box::into_raw(Box::new(NativeRecvStream(stream))) as i64)
        }

        pub extern "jni" fn acceptBi<'env>(env: &JNIEnv<'env>, handle: i64) -> JniResult<JObject<'env>> {
            let _guard = RUNTIME.enter();
            let native_connection = unsafe { &*(handle as *const NativeConnection) };
            
            let (send, recv) = match RUNTIME.block_on(async { native_connection.0.accept_bi().await }) {
                Ok(pair) => pair,
                Err(e) => {
                    throw_conn_ex(env, e);
                    return Ok(JObject::null());
                }
            };
            
            let send_handle = Box::into_raw(Box::new(NativeSendStream(send))) as i64;
            let recv_handle = Box::into_raw(Box::new(NativeRecvStream(recv))) as i64;
            
            let pair_class = env.find_class("ovh/devcraft/kwtransport/StreamPair")?;
            let send_class = env.find_class("ovh/devcraft/kwtransport/SendStream")?;
            let recv_class = env.find_class("ovh/devcraft/kwtransport/RecvStream")?;
            
            let send_obj = env.new_object(send_class, "(J)V", &[JValue::Long(send_handle)])?;
            let recv_obj = env.new_object(recv_class, "(J)V", &[JValue::Long(recv_handle)])?;
            
            let pair_obj = env.new_object(pair_class, "(Lovh/devcraft/kwtransport/SendStream;Lovh/devcraft/kwtransport/RecvStream;)V", &[
                JValue::Object(send_obj),
                JValue::Object(recv_obj),
            ])?;
            
            Ok(pair_obj)
        }

        pub extern "jni" fn sendDatagram(env: &JNIEnv, handle: i64, data: Box<[u8]>) -> JniResult<()> {
            let _guard = RUNTIME.enter();
            let native_connection = unsafe { &*(handle as *const NativeConnection) };
            
            if let Err(e) = native_connection.0.send_datagram(data) {
                throw_send_datagram_ex(env, e);
            }
            Ok(())
        }

        pub extern "jni" fn receiveDatagram(env: &JNIEnv, handle: i64) -> JniResult<Box<[u8]>> {
            let _guard = RUNTIME.enter();
            let native_connection = unsafe { &*(handle as *const NativeConnection) };
            
            match RUNTIME.block_on(async { native_connection.0.receive_datagram().await }) {
                Ok(datagram) => Ok(datagram.to_vec().into_boxed_slice()),
                Err(e) => {
                    throw_conn_ex(env, e);
                    Ok(Box::new([]))
                }
            }
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
    pub struct SendStream;

    impl SendStream {
        pub extern "jni" fn write(env: &JNIEnv, handle: i64, data: Box<[u8]>) -> JniResult<()> {
            let _guard = RUNTIME.enter();
            let native_stream = unsafe { &mut *(handle as *mut NativeSendStream) };
            
            if let Err(e) = RUNTIME.block_on(async { native_stream.0.write_all(&data).await }) {
                let _ = env.throw_new("java/io/IOException", e.to_string());
            }
            Ok(())
        }

        pub extern "jni" fn destroy(handle: i64) {
            if handle != 0 {
                unsafe {
                    let _ = Box::from_raw(handle as *mut NativeSendStream);
                }
            }
        }
    }

    #[package(ovh.devcraft.kwtransport)]
    pub struct RecvStream;

    impl RecvStream {
        pub extern "jni" fn read<'env>(env: &JNIEnv<'env>, handle: i64, jbuffer: JObject<'env>) -> JniResult<i32> {
            let _guard = RUNTIME.enter();
            let native_stream = unsafe { &mut *(handle as *mut NativeRecvStream) };
            
            let jbuffer_raw = jbuffer.into_inner();
            let buffer_len = env.get_array_length(jbuffer_raw as robusta_jni::jni::sys::jbyteArray)? as usize;
            let mut temp_buffer = vec![0u8; buffer_len];
            
            let bytes_read = match RUNTIME.block_on(async { native_stream.0.read(&mut temp_buffer).await }) {
                Ok(b) => b,
                Err(e) => {
                    let _ = env.throw_new("java/io/IOException", e.to_string());
                    return Ok(0);
                }
            };
            
            match bytes_read {
                Some(n) => {
                    env.set_byte_array_region(jbuffer_raw as robusta_jni::jni::sys::jbyteArray, 0, bytemuck::cast_slice(&temp_buffer[..n]))?;
                    Ok(n as i32)
                }
                None => Ok(-1), // EOF
            }
        }

        pub extern "jni" fn destroy(handle: i64) {
            if handle != 0 {
                unsafe {
                    let _ = Box::from_raw(handle as *mut NativeRecvStream);
                }
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

    fn throw_conn_ex(env: &JNIEnv, error: ConnectionError) {
        let (ex_type, msg) = match error {
            ConnectionError::ConnectionClosed(_) => ("CONNECTION_CLOSED", "Connection closed"),
            ConnectionError::ApplicationClosed(_) => ("APPLICATION_CLOSED", "Application closed"),
            ConnectionError::LocallyClosed => ("LOCALLY_CLOSED", "Locally closed"),
            ConnectionError::LocalH3Error(_) => ("LOCAL_H3_ERROR", "Local H3 error"),
            ConnectionError::TimedOut => ("TIMED_OUT", "Timed out"),
            ConnectionError::QuicProto(_) => ("QUIC_PROTO", "QUIC protocol error"),
            ConnectionError::CidsExhausted => ("CIDS_EHAUSTED", "CIDs exhausted"),
        };
        let _ = JniHelper::throwConnectionException(env, msg.to_string(), ex_type.to_string());
    }

    fn throw_stream_opening_ex(env: &JNIEnv, error: StreamOpeningError) {
        let (ex_type, msg) = match error {
            StreamOpeningError::NotConnected => ("NOT_CONNECTED", "Not connected"),
            StreamOpeningError::Refused => ("REFUSED", "Opening stream refused"),
        };
        let _ = JniHelper::throwStreamOpeningException(env, msg.to_string(), ex_type.to_string());
    }

    fn throw_send_datagram_ex(env: &JNIEnv, error: SendDatagramError) {
        let (ex_type, msg) = match error {
            SendDatagramError::NotConnected => ("NOT_CONNECTED", "Not connected"),
            SendDatagramError::UnsupportedByPeer => ("UNSUPPORTED_BY_PEER", "Unsupported by peer"),
            SendDatagramError::TooLarge => ("TOO_LARGE", "Too large"),
        };
        let _ = JniHelper::throwSendDatagramException(env, msg.to_string(), ex_type.to_string());
    }
}

use wtransport::endpoint::endpoint_side::{Client, Server};
pub enum NativeEndpoint {
    Client(wtransport::Endpoint<Client>),
    Server(wtransport::Endpoint<Server>),
}

pub struct NativeConnection(pub wtransport::Connection);
pub struct NativeSendStream(pub wtransport::SendStream);
pub struct NativeRecvStream(pub wtransport::RecvStream);
pub struct NativeIdentity(pub wtransport::Identity);

lazy_static::lazy_static! {
    pub static ref RUNTIME: tokio::runtime::Runtime = tokio::runtime::Runtime::new().unwrap();
}