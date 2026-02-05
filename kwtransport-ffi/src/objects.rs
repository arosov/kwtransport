use wtransport::endpoint::endpoint_side::{Client, Server};
use tokio_util::sync::CancellationToken;
use std::sync::Arc;
use tokio::sync::Mutex;

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

pub struct NativeStreamPair {
    pub send: Option<Arc<Mutex<NativeSendStream>>>,
    pub recv: Option<Arc<Mutex<NativeRecvStream>>>,
}

pub struct NativeDatagram(pub Box<[u8]>);

unsafe impl Send for NativeEndpoint {}
unsafe impl Sync for NativeEndpoint {}

unsafe impl Send for NativeConnection {}
unsafe impl Sync for NativeConnection {}

unsafe impl Send for NativeSendStream {}
unsafe impl Sync for NativeSendStream {}

unsafe impl Send for NativeRecvStream {}
unsafe impl Sync for NativeRecvStream {}