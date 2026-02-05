use std::sync::atomic::AtomicI64;
use once_cell::sync::OnceCell;

pub static ACTIVE_OBJECT_COUNT: AtomicI64 = AtomicI64::new(0);

lazy_static::lazy_static! {
    pub static ref RUNTIME: tokio::runtime::Runtime = tokio::runtime::Runtime::new().unwrap();
}

pub static JAVA_VM: OnceCell<robusta_jni::jni::JavaVM> = OnceCell::new();
