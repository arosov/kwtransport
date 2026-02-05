use wtransport::endpoint::endpoint_side::{Client, Server};
use tokio_util::sync::CancellationToken;

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
    pub send: Option<NativeSendStream>,
    pub recv: Option<NativeRecvStream>,
}

pub struct NativeDatagram(pub Box<[u8]>);

#[derive(Copy, Clone)]
pub struct PtrSend(pub *const NativeEndpoint);
unsafe impl Send for PtrSend {}
unsafe impl Sync for PtrSend {}

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
    pub unsafe fn as_ref(&self) -> &NativeEndpoint { &*self.0 }
}
impl PtrSendConnection {
    pub unsafe fn as_ref(&self) -> &NativeConnection { &*self.0 }
}
impl PtrSendSendStream {
    pub unsafe fn as_mut(self) -> &'static mut NativeSendStream { &mut *self.0 }
}
impl PtrSendRecvStream {
    pub unsafe fn as_mut(self) -> &'static mut NativeRecvStream { &mut *self.0 }
}
