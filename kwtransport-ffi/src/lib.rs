use std::sync::Arc;
use uniffi;

uniffi::include_scaffolding!("api");

pub struct Endpoint {
    inner: wtransport::Endpoint<wtransport::endpoint::endpoint_side::Client>,
}

pub struct Connection {
    inner: wtransport::Connection,
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
