use robusta_jni::bridge;

pub mod errors;
pub mod objects;
pub mod runtime;
pub mod utils;

#[bridge]
mod jni {
    use robusta_jni::jni::errors::Result as JniResult;
    use robusta_jni::jni::JNIEnv;
    use robusta_jni::jni::objects::{JObject, JValue};
    use std::net::SocketAddr;
    use std::time::Duration;
    use std::sync::atomic::Ordering;
    use std::sync::Arc;
    use tokio::sync::Mutex;
    
    use crate::objects::{
        NativeEndpoint, NativeConnection, NativeSendStream, NativeRecvStream, NativeIdentity, 
        NativeStreamPair, NativeDatagram,
        EndpointInner
    };
    use crate::runtime::{RUNTIME, JAVA_VM, ACTIVE_OBJECT_COUNT};
    use crate::errors::{map_conn_err, map_stream_err, map_send_datagram_err, map_connecting_err};
    use crate::utils::apply_transport_config;
    
    use wtransport::{ClientConfig, ServerConfig, Identity};
    use wtransport::config::Ipv6DualStackConfig;

    #[package(ovh.devcraft.kwtransport)]
    pub struct KwTransport;

    impl KwTransport {
        pub extern "jni" fn hello() -> String {
            "Hello from Rust!".to_string()
        }
        pub extern "jni" fn getDiagnosticCount() -> i64 {
            ACTIVE_OBJECT_COUNT.load(Ordering::Relaxed)
        }
    }

    #[package(ovh.devcraft.kwtransport)]
    pub struct JniHelper;

    impl JniHelper {
        pub extern "java" fn throwConnectingException(env: &JNIEnv, message: String, type_name: String) -> JniResult<()> {}
        pub extern "java" fn throwConnectionException(env: &JNIEnv, message: String, type_name: String) -> JniResult<()> {}
        pub extern "java" fn throwStreamOpeningException(env: &JNIEnv, message: String, type_name: String) -> JniResult<()> {}
        pub extern "java" fn throwSendDatagramException(env: &JNIEnv, message: String, type_name: String) -> JniResult<()> {}
        pub extern "java" fn onNotify(env: &JNIEnv, id: i64, result: i64, error_type: String, error_message: String, error_code: i64, error_context: String) -> JniResult<()> {}
    }

    #[package(ovh.devcraft.kwtransport)]
    pub struct Endpoint;

    impl Endpoint {
        #[call_type(unchecked)]
        pub extern "jni" fn createClient(
            env: &JNIEnv, 
            bind_addr: String, 
            accept_all_certs: bool, 
            max_idle_timeout_millis: i64,
            certificate_hashes: Vec<String>,
            keep_alive_interval_millis: i64,
            ipv6_dual_stack_config: i32,
            quic_config: Vec<i64>
        ) -> JniResult<i64> {
            let _ = JAVA_VM.set(env.get_java_vm().unwrap());
            let _guard = RUNTIME.enter();
            
            let addr: SocketAddr = match bind_addr.parse() {
                Ok(a) => a,
                Err(e) => {
                    let _ = env.throw_new("java/lang/IllegalArgumentException", e.to_string());
                    return Ok(0);
                }
            };

            let dual_stack_config = match ipv6_dual_stack_config {
                1 => Ipv6DualStackConfig::Allow,
                2 => Ipv6DualStackConfig::Deny,
                _ => Ipv6DualStackConfig::OsDefault,
            };
            
            let builder = match addr {
                SocketAddr::V4(_) => ClientConfig::builder().with_bind_address(addr),
                SocketAddr::V6(addr_v6) => ClientConfig::builder().with_bind_address_v6(addr_v6, dual_stack_config),
            };
            
            let builder = if !certificate_hashes.is_empty() {
                let mut hashes = Vec::new();
                for h_str in certificate_hashes {
                    match h_str.parse::<wtransport::tls::Sha256Digest>() {
                        Ok(h) => hashes.push(h),
                        Err(_) => {
                            let _ = env.throw_new("java/lang/IllegalArgumentException", format!("Invalid certificate hash: {}", h_str));
                            return Ok(0);
                        }
                    }
                }
                builder.with_server_certificate_hashes(hashes)
            } else if accept_all_certs {
                builder.with_no_cert_validation()
            } else {
                builder.with_native_certs()
            };
            
            let mut builder = builder;
            if max_idle_timeout_millis > 0 {
                builder = builder.max_idle_timeout(Some(Duration::from_millis(max_idle_timeout_millis as u64))).unwrap();
            }

            if keep_alive_interval_millis > 0 {
                builder = builder.keep_alive_interval(Some(Duration::from_millis(keep_alive_interval_millis as u64)));
            }
            
            let mut client_config = builder.build();
            
            if !quic_config.is_empty() {
                let mut transport_config = wtransport::config::QuicTransportConfig::default();
                apply_transport_config(&mut transport_config, &quic_config);
                client_config.quic_config_mut().transport_config(std::sync::Arc::new(transport_config));
            }

            let endpoint = match wtransport::Endpoint::client(client_config) {
                Ok(e) => e,
                Err(e) => {
                    let _ = env.throw_new("java/lang/RuntimeException", e.to_string());
                    return Ok(0);
                }
            };
            
            ACTIVE_OBJECT_COUNT.fetch_add(1, Ordering::Relaxed);
            Ok(Arc::into_raw(Arc::new(NativeEndpoint::new_client(endpoint))) as i64)
        }

        #[call_type(unchecked)]
        pub extern "jni" fn createServer(
            env: &JNIEnv, 
            bind_addr: String, 
            cert_handle: i64,
            max_idle_timeout_millis: i64,
            keep_alive_interval_millis: i64,
            allow_migration: bool,
            ipv6_dual_stack_config: i32,
            quic_config: Vec<i64>
        ) -> JniResult<i64> {
            let _ = JAVA_VM.set(env.get_java_vm().unwrap());
            let _guard = RUNTIME.enter();
            
            let addr: SocketAddr = match bind_addr.parse() {
                Ok(a) => a,
                Err(e) => {
                    let _ = env.throw_new("java/lang/IllegalArgumentException", e.to_string());
                    return Ok(0);
                }
            };

            let identity = unsafe { Arc::from_raw(cert_handle as *const NativeIdentity) };
            let identity_cloned = identity.0.clone_identity();
            
            ACTIVE_OBJECT_COUNT.fetch_sub(1, Ordering::Relaxed);

            let dual_stack_config = match ipv6_dual_stack_config {
                1 => Ipv6DualStackConfig::Allow,
                2 => Ipv6DualStackConfig::Deny,
                _ => Ipv6DualStackConfig::OsDefault,
            };

            let mut builder = match addr {
                SocketAddr::V4(_) => ServerConfig::builder().with_bind_address(addr),
                SocketAddr::V6(addr_v6) => ServerConfig::builder().with_bind_address_v6(addr_v6, dual_stack_config),
            }.with_identity(identity_cloned);

            if max_idle_timeout_millis > 0 {
                builder = builder.max_idle_timeout(Some(Duration::from_millis(max_idle_timeout_millis as u64))).unwrap();
            }

            if keep_alive_interval_millis > 0 {
                builder = builder.keep_alive_interval(Some(Duration::from_millis(keep_alive_interval_millis as u64)));
            }

            builder = builder.allow_migration(allow_migration);

            let mut server_config = builder.build();

             if !quic_config.is_empty() {
                let mut transport_config = wtransport::config::QuicTransportConfig::default();
                apply_transport_config(&mut transport_config, &quic_config);
                server_config.quic_config_mut().transport_config(std::sync::Arc::new(transport_config));
            }

            let endpoint = match wtransport::Endpoint::server(server_config) {
                Ok(e) => e,
                Err(e) => {
                    let _ = env.throw_new("java/lang/RuntimeException", e.to_string());
                    return Ok(0);
                }
            };
            
            ACTIVE_OBJECT_COUNT.fetch_add(1, Ordering::Relaxed);
            Ok(Arc::into_raw(Arc::new(NativeEndpoint::new_server(endpoint))) as i64)
        }

        pub extern "jni" fn connect(_env: &JNIEnv, handle: i64, id: i64, url: String) {
            let endpoint = unsafe { Arc::from_raw(handle as *const NativeEndpoint) };
            let endpoint_clone = Arc::clone(&endpoint);
            std::mem::forget(endpoint);
            
            RUNTIME.spawn(async move {
                let client = match &endpoint_clone.inner {
                    EndpointInner::Client(c) => c,
                    _ => return,
                };

                let result = client.connect(&url).await;
                
                let vm = JAVA_VM.get().expect("JavaVM not initialized");
                let env = vm.attach_current_thread().expect("Failed to attach thread");
                
                match result {
                    Ok(connection) => {
                        let conn_handle = Arc::into_raw(Arc::new(NativeConnection(connection))) as i64;
                        ACTIVE_OBJECT_COUNT.fetch_add(1, Ordering::Relaxed);
                        let _ = JniHelper::onNotify(&env, id, conn_handle, "".to_string(), "".to_string(), 0, "".to_string());
                    }
                    Err(e) => {
                        let (ex_type, msg, code, ctx) = map_connecting_err(e);
                        let _ = JniHelper::onNotify(&env, id, 0, ex_type, msg, code, ctx);
                    }
                }
            });
        }

        pub extern "jni" fn listenSessions(_env: &JNIEnv, handle: i64, id: i64) {
            let endpoint = unsafe { Arc::from_raw(handle as *const NativeEndpoint) };
            let endpoint_clone = Arc::clone(&endpoint);
            let cancel_token = endpoint_clone.cancel_token.clone();
            std::mem::forget(endpoint);
            
            RUNTIME.spawn(async move {
                let server = match &endpoint_clone.inner {
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
                                     let conn_handle = Arc::into_raw(Arc::new(NativeConnection(connection))) as i64;
                                     ACTIVE_OBJECT_COUNT.fetch_add(1, Ordering::Relaxed);
                                     let _ = JniHelper::onNotify(&env, id, conn_handle, "".to_string(), "".to_string(), 0, "".to_string());
                                 }
                                 Err(e) => {
                                     let _ = JniHelper::onNotify(&env, id, 0, "CONNECTION".to_string(), e.to_string(), 0, "".to_string());
                                 }
                             }
                        }
                    }
                }
            });
        }

        pub extern "jni" fn stopListenSessions(_env: &JNIEnv, handle: i64) {
             let endpoint = unsafe { Arc::from_raw(handle as *const NativeEndpoint) };
             endpoint.cancel_token.cancel();
             std::mem::forget(endpoint);
        }

        pub extern "jni" fn getLocalAddr(env: &JNIEnv, handle: i64) -> JniResult<String> {
            let endpoint = unsafe { Arc::from_raw(handle as *const NativeEndpoint) };
            let addr = match &endpoint.inner {
                EndpointInner::Client(c) => c.local_addr(),
                EndpointInner::Server(s) => s.local_addr(),
            };
            std::mem::forget(endpoint);
            
            match addr {
                Ok(a) => Ok(a.to_string()),
                Err(e) => {
                    let _ = env.throw_new("java/lang/RuntimeException", e.to_string());
                    Ok("".to_string())
                }
            }
        }

        pub extern "jni" fn destroy(handle: i64) {
            if handle != 0 {
                unsafe {
                    let _ = Arc::from_raw(handle as *const NativeEndpoint);
                    ACTIVE_OBJECT_COUNT.fetch_sub(1, Ordering::Relaxed);
                }
            }
        }
    }
    
    #[package(ovh.devcraft.kwtransport)]
    pub struct Connection;

    impl Connection {
        pub extern "jni" fn openUni(_env: &JNIEnv, handle: i64, id: i64) {
             let conn = unsafe { Arc::from_raw(handle as *const NativeConnection) };
             let conn_clone = Arc::clone(&conn);
             std::mem::forget(conn);

             RUNTIME.spawn(async move {
                 match conn_clone.0.open_uni().await {
                    Ok(opening) => {
                        match opening.await {
                            Ok(stream) => {
                                let vm = JAVA_VM.get().expect("JavaVM not initialized");
                                let env = vm.attach_current_thread().expect("Failed to attach thread");
                                let h = Arc::into_raw(Arc::new(Mutex::new(NativeSendStream(stream)))) as i64;
                                ACTIVE_OBJECT_COUNT.fetch_add(1, Ordering::Relaxed);
                                let _ = JniHelper::onNotify(&env, id, h, "".to_string(), "".to_string(), 0, "".to_string());
                            }
                            Err(e) => {
                                let vm = JAVA_VM.get().expect("JavaVM not initialized");
                                let env = vm.attach_current_thread().expect("Failed to attach thread");
                                let (t, m, c, ctx) = map_stream_err(e);
                                let _ = JniHelper::onNotify(&env, id, 0, t, m, c, ctx);
                            }
                        }
                    }
                    Err(e) => {
                        let vm = JAVA_VM.get().expect("JavaVM not initialized");
                        let env = vm.attach_current_thread().expect("Failed to attach thread");
                        let (t, m, c, ctx) = map_conn_err(e);
                        let _ = JniHelper::onNotify(&env, id, 0, t, m, c, ctx);
                    }
                 }
             });
        }

        pub extern "jni" fn openBi(_env: &JNIEnv, handle: i64, id: i64) {
             let conn = unsafe { Arc::from_raw(handle as *const NativeConnection) };
             let conn_clone = Arc::clone(&conn);
             std::mem::forget(conn);

             RUNTIME.spawn(async move {
                 match conn_clone.0.open_bi().await {
                    Ok(opening) => {
                        match opening.await {
                            Ok((send, recv)) => {
                                let vm = JAVA_VM.get().expect("JavaVM not initialized");
                                let env = vm.attach_current_thread().expect("Failed to attach thread");
                                let pair = NativeStreamPair {
                                    send: Some(Arc::new(Mutex::new(NativeSendStream(send)))),
                                    recv: Some(Arc::new(Mutex::new(NativeRecvStream(recv)))),
                                };
                                let h = Box::into_raw(Box::new(pair)) as i64;
                                ACTIVE_OBJECT_COUNT.fetch_add(1, Ordering::Relaxed);
                                let _ = JniHelper::onNotify(&env, id, h, "".to_string(), "".to_string(), 0, "".to_string());
                            }
                            Err(e) => {
                                let vm = JAVA_VM.get().expect("JavaVM not initialized");
                                let env = vm.attach_current_thread().expect("Failed to attach thread");
                                let (t, m, c, ctx) = map_stream_err(e);
                                let _ = JniHelper::onNotify(&env, id, 0, t, m, c, ctx);
                            }
                        }
                    }
                    Err(e) => {
                        let vm = JAVA_VM.get().expect("JavaVM not initialized");
                        let env = vm.attach_current_thread().expect("Failed to attach thread");
                        let (t, m, c, ctx) = map_conn_err(e);
                        let _ = JniHelper::onNotify(&env, id, 0, t, m, c, ctx);
                    }
                 }
             });
        }

        pub extern "jni" fn acceptUni(_env: &JNIEnv, handle: i64, id: i64) {
             let conn = unsafe { Arc::from_raw(handle as *const NativeConnection) };
             let conn_clone = Arc::clone(&conn);
             std::mem::forget(conn);

             RUNTIME.spawn(async move {
                 match conn_clone.0.accept_uni().await {
                    Ok(stream) => {
                        let vm = JAVA_VM.get().expect("JavaVM not initialized");
                        let env = vm.attach_current_thread().expect("Failed to attach thread");
                        let h = Arc::into_raw(Arc::new(Mutex::new(NativeRecvStream(stream)))) as i64;
                        ACTIVE_OBJECT_COUNT.fetch_add(1, Ordering::Relaxed);
                        let _ = JniHelper::onNotify(&env, id, h, "".to_string(), "".to_string(), 0, "".to_string());
                    }
                    Err(e) => {
                        let vm = JAVA_VM.get().expect("JavaVM not initialized");
                        let env = vm.attach_current_thread().expect("Failed to attach thread");
                        let (t, m, c, ctx) = map_conn_err(e);
                        let _ = JniHelper::onNotify(&env, id, 0, t, m, c, ctx);
                    }
                 }
             });
        }

        pub extern "jni" fn acceptBi(_env: &JNIEnv, handle: i64, id: i64) {
             let conn = unsafe { Arc::from_raw(handle as *const NativeConnection) };
             let conn_clone = Arc::clone(&conn);
             std::mem::forget(conn);

             RUNTIME.spawn(async move {
                 match conn_clone.0.accept_bi().await {
                    Ok((send, recv)) => {
                        let vm = JAVA_VM.get().expect("JavaVM not initialized");
                        let env = vm.attach_current_thread().expect("Failed to attach thread");
                        let pair = NativeStreamPair {
                            send: Some(Arc::new(Mutex::new(NativeSendStream(send)))),
                            recv: Some(Arc::new(Mutex::new(NativeRecvStream(recv)))),
                        };
                        let h = Box::into_raw(Box::new(pair)) as i64;
                        ACTIVE_OBJECT_COUNT.fetch_add(1, Ordering::Relaxed);
                        let _ = JniHelper::onNotify(&env, id, h, "".to_string(), "".to_string(), 0, "".to_string());
                    }
                    Err(e) => {
                        let vm = JAVA_VM.get().expect("JavaVM not initialized");
                        let env = vm.attach_current_thread().expect("Failed to attach thread");
                        let (t, m, c, ctx) = map_conn_err(e);
                        let _ = JniHelper::onNotify(&env, id, 0, t, m, c, ctx);
                    }
                 }
             });
        }

        pub extern "jni" fn sendDatagram(env: &JNIEnv, handle: i64, data: Box<[u8]>) -> JniResult<()> {
            let _guard = RUNTIME.enter();
            let conn = unsafe { Arc::from_raw(handle as *const NativeConnection) };
            
            let res = if let Err(e) = conn.0.send_datagram(data) {
                let (t, m, _c, _ctx) = map_send_datagram_err(e);
                JniHelper::throwSendDatagramException(env, m, t)
            } else {
                Ok(())
            };
            std::mem::forget(conn);
            res
        }

        pub extern "jni" fn receiveDatagram(_env: &JNIEnv, handle: i64, id: i64) {
             let conn = unsafe { Arc::from_raw(handle as *const NativeConnection) };
             let conn_clone = Arc::clone(&conn);
             std::mem::forget(conn);

             RUNTIME.spawn(async move {
                 match conn_clone.0.receive_datagram().await {
                    Ok(datagram) => {
                        let vm = JAVA_VM.get().expect("JavaVM not initialized");
                        let env = vm.attach_current_thread().expect("Failed to attach thread");
                        let d = NativeDatagram(datagram.to_vec().into_boxed_slice());
                        let h = Box::into_raw(Box::new(d)) as i64;
                        ACTIVE_OBJECT_COUNT.fetch_add(1, Ordering::Relaxed);
                        let _ = JniHelper::onNotify(&env, id, h, "".to_string(), "".to_string(), 0, "".to_string());
                    }
                    Err(e) => {
                        let vm = JAVA_VM.get().expect("JavaVM not initialized");
                        let env = vm.attach_current_thread().expect("Failed to attach thread");
                        let (t, m, c, ctx) = map_conn_err(e);
                        let _ = JniHelper::onNotify(&env, id, 0, t, m, c, ctx);
                    }
                 }
             });
        }

        pub extern "jni" fn getStats<'env>(env: &JNIEnv<'env>, handle: i64) -> JniResult<JObject<'env>> {
            let conn = unsafe { Arc::from_raw(handle as *const NativeConnection) };
            
            let stats = conn.0.quic_connection().stats();
            let path_stats = stats.path;

            let cls = env.find_class("ovh/devcraft/kwtransport/ConnectionStats")?;
            let constructor = env.get_method_id(cls, "<init>", "(JJJJ)V")?;
            
            let obj = env.new_object_unchecked(cls, constructor, &[
                JValue::Long(path_stats.rtt.as_millis() as i64),
                JValue::Long(path_stats.lost_packets as i64),
                JValue::Long(path_stats.sent_packets as i64),
                JValue::Long(path_stats.congestion_events as i64)
            ])?;

            std::mem::forget(conn);
            Ok(obj)
        }

        pub extern "jni" fn close(_env: &JNIEnv, handle: i64, code: i64, reason: String) {
            let conn = unsafe { Arc::from_raw(handle as *const NativeConnection) };
            let var_int_code = wtransport::VarInt::try_from(code as u64).unwrap_or(wtransport::VarInt::from_u32(0));
            conn.0.close(var_int_code, reason.as_bytes());
            std::mem::forget(conn);
        }

        pub extern "jni" fn destroy(handle: i64) {
            if handle != 0 {
                unsafe {
                    let _ = Arc::from_raw(handle as *const NativeConnection);
                    ACTIVE_OBJECT_COUNT.fetch_sub(1, Ordering::Relaxed);
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
                Some(s) => {
                    ACTIVE_OBJECT_COUNT.fetch_add(1, Ordering::Relaxed);
                    Arc::into_raw(s) as i64
                },
                None => 0,
            }
        }
        pub extern "jni" fn getRecv(handle: i64) -> i64 {
            let pair = unsafe { &mut *(handle as *mut NativeStreamPair) };
            match pair.recv.take() {
                Some(s) => {
                    ACTIVE_OBJECT_COUNT.fetch_add(1, Ordering::Relaxed);
                    Arc::into_raw(s) as i64
                },
                None => 0,
            }
        }
        pub extern "jni" fn destroy(handle: i64) {
            if handle != 0 {
                unsafe { 
                    let _ = Box::from_raw(handle as *mut NativeStreamPair); 
                    ACTIVE_OBJECT_COUNT.fetch_sub(1, Ordering::Relaxed);
                }
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
                unsafe { 
                    let _ = Box::from_raw(handle as *mut NativeDatagram); 
                    ACTIVE_OBJECT_COUNT.fetch_sub(1, Ordering::Relaxed);
                }
            }
        }
    }

            #[package(ovh.devcraft.kwtransport)]
            pub struct SendStream;

            impl SendStream {
                pub extern "jni" fn write(_env: &JNIEnv, handle: i64, data: Box<[u8]>, id: i64) {
                     let stream = unsafe { Arc::from_raw(handle as *const Mutex<NativeSendStream>) };
                     let stream_clone = Arc::clone(&stream);
                     std::mem::forget(stream);

                     RUNTIME.spawn(async move {
                         let mut guard = stream_clone.lock().await;
                         match guard.0.write_all(&data).await {
                             Ok(_) => {
                                 let vm = JAVA_VM.get().expect("JavaVM not initialized");
                                 let env = vm.attach_current_thread().expect("Failed to attach thread");
                                 let _ = JniHelper::onNotify(&env, id, 1, "".to_string(), "".to_string(), 0, "".to_string());
                             },
                             Err(e) => {
                                 let vm = JAVA_VM.get().expect("JavaVM not initialized");
                                 let env = vm.attach_current_thread().expect("Failed to attach thread");
                                 let _ = JniHelper::onNotify(&env, id, 0, "IO_EXCEPTION".to_string(), e.to_string(), 0, "".to_string());
                             }
                         }
                     });
                }
                pub extern "jni" fn destroy(handle: i64) {
                    if handle != 0 {
                        unsafe { 
                            let _ = Arc::from_raw(handle as *const Mutex<NativeSendStream>); 
                            ACTIVE_OBJECT_COUNT.fetch_sub(1, Ordering::Relaxed);
                        }
                    }
                }
            }

            #[package(ovh.devcraft.kwtransport)]
            pub struct RecvStream;

            impl RecvStream {
                pub extern "jni" fn read<'env>(env: &JNIEnv<'env>, handle: i64, jbuffer: JObject<'env>, id: i64) -> JniResult<()> {
                     let stream = unsafe { Arc::from_raw(handle as *const Mutex<NativeRecvStream>) };
                     let stream_clone = Arc::clone(&stream);
                     std::mem::forget(stream);
                     
                     let jbuffer_ref = env.new_global_ref(jbuffer)?;
                     
                     RUNTIME.spawn(async move {
                         let vm = JAVA_VM.get().expect("JavaVM not initialized");
                         
                         let len = {
                             let env = vm.attach_current_thread().expect("Failed to attach thread");
                             let jbuff = jbuffer_ref.as_obj();
                             let raw_jbuff = jbuff.into_inner();
                             match env.get_array_length(raw_jbuff as robusta_jni::jni::sys::jbyteArray) {
                                 Ok(l) => l as usize,
                                 Err(e) => {
                                     let _ = JniHelper::onNotify(&env, id, 0, "IO_EXCEPTION".to_string(), e.to_string(), 0, "".to_string());
                                     return;
                                 }
                             }
                         };
                         
                         let mut buf = vec![0u8; len];
                         let mut guard = stream_clone.lock().await;
                         match guard.0.read(&mut buf).await {
                             Ok(bytes_read) => {
                                 let env = vm.attach_current_thread().expect("Failed to attach thread");
                                 match bytes_read {
                                     Some(n) => {
                                         let jbuff = jbuffer_ref.as_obj();
                                         let raw_jbuff = jbuff.into_inner();
                                         if let Err(e) = env.set_byte_array_region(raw_jbuff as robusta_jni::jni::sys::jbyteArray, 0, bytemuck::cast_slice(&buf[..n])) {
                                              let _ = JniHelper::onNotify(&env, id, 0, "IO_EXCEPTION".to_string(), e.to_string(), 0, "".to_string());
                                              return;
                                         }
                                         let _ = JniHelper::onNotify(&env, id, n as i64, "".to_string(), "".to_string(), 0, "".to_string());
                                     },
                                     None => {
                                         let _ = JniHelper::onNotify(&env, id, -1, "".to_string(), "".to_string(), 0, "".to_string());
                                     }
                                 }
                             },
                             Err(e) => {
                                 let env = vm.attach_current_thread().expect("Failed to attach thread");
                                 let _ = JniHelper::onNotify(&env, id, 0, "IO_EXCEPTION".to_string(), e.to_string(), 0, "".to_string());
                             }
                         }
                     });
                     Ok(())
                }
                pub extern "jni" fn destroy(handle: i64) {
                    if handle != 0 {
                        unsafe { 
                            let _ = Arc::from_raw(handle as *const Mutex<NativeRecvStream>); 
                            ACTIVE_OBJECT_COUNT.fetch_sub(1, Ordering::Relaxed);
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
                 Ok(identity) => {
                     ACTIVE_OBJECT_COUNT.fetch_add(1, Ordering::Relaxed);
                     Ok(Arc::into_raw(Arc::new(NativeIdentity(identity))) as i64)
                 },
                 Err(e) => {
                     let _ = env.throw_new("java/lang/IllegalArgumentException", e.to_string());
                     Ok(0)
                 }
             }
        }

        pub extern "jni" fn getHash(env: &JNIEnv, handle: i64) -> JniResult<String> {
             let identity = unsafe { Arc::from_raw(handle as *const NativeIdentity) };
             let res = match identity.0.certificate_chain().as_slice().first() {
                 Some(cert) => Ok(cert.hash().to_string()),
                 None => {
                     let _ = env.throw_new("java/lang/IllegalStateException", "Identity has no certificates");
                     Ok("".to_string())
                 }
             };
             std::mem::forget(identity);
             res
        }

        pub extern "jni" fn destroy(handle: i64) {
            if handle != 0 {
                unsafe {
                    let _ = Arc::from_raw(handle as *const NativeIdentity);
                    ACTIVE_OBJECT_COUNT.fetch_sub(1, Ordering::Relaxed);
                }
            }
        }
    }
}