use robusta_jni::bridge;

#[bridge]
mod jni {
    #[package(ovh.devcraft.kwtransport)]
    pub struct KwTransport;

    impl KwTransport {
        pub extern "jni" fn hello() -> String {
            "Hello from Rust!".to_string()
        }
    }
}
