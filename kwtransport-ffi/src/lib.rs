use std::sync::Arc;
use tokio::sync::Mutex;
use uniffi;

uniffi::include_scaffolding!("api");

pub struct Endpoint {
    inner: wtransport::Endpoint<wtransport::endpoint::endpoint_side::Client>,
}

pub struct Connection {
    inner: wtransport::Connection,
}

pub struct SendStream {
    inner: Mutex<wtransport::SendStream>,
}

pub struct RecvStream {
    inner: Mutex<wtransport::RecvStream>,
}

pub struct BiStream {
    pub send: Arc<SendStream>,
    pub recv: Arc<RecvStream>,
}

impl Endpoint {
    pub fn new() -> Self {
        let config = wtransport::ClientConfig::builder()
            .with_bind_default()
            .with_no_cert_validation()
            .build();
        
        let inner = wtransport::Endpoint::client(config).expect("Failed to create endpoint");
        Self { inner }
    }

    pub async fn connect(&self, url: String) -> Arc<Connection> {
        let connection = self.inner.connect(url).await.expect("Failed to connect");
        Arc::new(Connection { inner: connection })
    }
}

impl Connection {
    pub async fn open_uni(&self) -> Arc<SendStream> {
        let stream = self.inner.open_uni().await.expect("Failed to open uni stream").await.expect("Stream open failed");
        Arc::new(SendStream { inner: Mutex::new(stream) })
    }

    pub async fn accept_uni(&self) -> Arc<RecvStream> {
        let stream = self.inner.accept_uni().await.expect("Failed to accept uni stream");
        Arc::new(RecvStream { inner: Mutex::new(stream) })
    }

    pub async fn open_bi(&self) -> BiStream {
        let (send, recv) = self.inner.open_bi().await.expect("Failed to open bi stream").await.expect("Stream open failed");
        BiStream {
            send: Arc::new(SendStream { inner: Mutex::new(send) }),
            recv: Arc::new(RecvStream { inner: Mutex::new(recv) }),
        }
    }

    pub async fn accept_bi(&self) -> BiStream {
        let (send, recv) = self.inner.accept_bi().await.expect("Failed to accept bi stream");
        BiStream {
            send: Arc::new(SendStream { inner: Mutex::new(send) }),
            recv: Arc::new(RecvStream { inner: Mutex::new(recv) }),
        }
    }
}

impl SendStream {
    pub async fn write(&self, data: Vec<u8>) {
        let mut stream = self.inner.lock().await;
        stream.write_all(&data).await.expect("Failed to write to stream");
    }

    pub async fn close(&self) {
        let mut stream = self.inner.lock().await;
        stream.finish().await.expect("Failed to finish stream");
    }
}

impl RecvStream {
    pub async fn read(&self, max_bytes: u64) -> Option<Vec<u8>> {
        let mut stream = self.inner.lock().await;
        let mut buf = vec![0u8; max_bytes as usize];
        match stream.read(&mut buf).await.expect("Failed to read from stream") {
            Some(n) => {
                buf.truncate(n);
                Some(buf)
            }
            None => None,
        }
    }
}