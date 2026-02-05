use wtransport::error::{ConnectionError, StreamOpeningError, SendDatagramError, ConnectingError};

pub fn map_conn_err(error: ConnectionError) -> (String, String, i64, String) {
    let (ex_type, msg, code, context) = match &error {
        ConnectionError::ConnectionClosed(_) => (
            "CONNECTION_CLOSED", 
            "Connection closed", 
            0, 
            "".to_string()
        ),
        ConnectionError::ApplicationClosed(e) => (
            "APPLICATION_CLOSED", 
            "Application closed", 
            e.code().into_inner() as i64, 
            String::from_utf8_lossy(e.reason()).to_string()
        ),
        ConnectionError::LocallyClosed => ("LOCALLY_CLOSED", "Locally closed", 0, "".to_string()),
        ConnectionError::LocalH3Error(_) => ("LOCAL_H3_ERROR", "Local H3 error", 0, "".to_string()),
        ConnectionError::TimedOut => ("TIMED_OUT", "Timed out", 0, "".to_string()),
        ConnectionError::QuicProto(_) => ("QUIC_PROTO", "QUIC protocol error", 0, "".to_string()),
        ConnectionError::CidsExhausted => ("CIDS_EHAUSTED", "CIDs exhausted", 0, "".to_string()),
    };
    (ex_type.to_string(), msg.to_string(), code, context)
}

pub fn map_connecting_err(error: ConnectingError) -> (String, String, i64, String) {
    match error {
        ConnectingError::InvalidUrl(s) => ("INVALID_URL".to_string(), s, 0, "".to_string()),
        ConnectingError::DnsLookup(e) => ("DNS_LOOKUP".to_string(), e.to_string(), 0, "".to_string()),
        ConnectingError::DnsNotFound => ("DNS_NOT_FOUND".to_string(), "DNS not found".to_string(), 0, "".to_string()),
        ConnectingError::ConnectionError(e) => map_conn_err(e),
        ConnectingError::SessionRejected => ("SESSION_REJECTED".to_string(), "Session rejected".to_string(), 0, "".to_string()),
        ConnectingError::ReservedHeader(s) => ("RESERVED_HEADER".to_string(), s, 0, "".to_string()),
        ConnectingError::EndpointStopping => ("ENDPOINT_STOPPING".to_string(), "Endpoint stopping".to_string(), 0, "".to_string()),
        ConnectingError::CidsExhausted => ("CIDS_EHAUSTED".to_string(), "CIDs exhausted".to_string(), 0, "".to_string()),
        ConnectingError::InvalidServerName(s) => ("INVALID_SERVER_NAME".to_string(), s, 0, "".to_string()),
        ConnectingError::InvalidRemoteAddress(a) => ("INVALID_REMOTE_ADDRESS".to_string(), a.to_string(), 0, "".to_string()),
    }
}

pub fn map_stream_err(error: StreamOpeningError) -> (String, String, i64, String) {
    let (ex_type, msg) = match error {
        StreamOpeningError::NotConnected => ("NOT_CONNECTED", "Not connected"),
        StreamOpeningError::Refused => ("REFUSED", "Opening stream refused"),
    };
    (ex_type.to_string(), msg.to_string(), 0, "".to_string())
}

pub fn map_send_datagram_err(error: SendDatagramError) -> (String, String, i64, String) {
    let (ex_type, msg) = match error {
        SendDatagramError::NotConnected => ("NOT_CONNECTED", "Not connected"),
        SendDatagramError::UnsupportedByPeer => ("UNSUPPORTED_BY_PEER", "Unsupported by peer"),
        SendDatagramError::TooLarge => ("TOO_LARGE", "Too large"),
    };
    (ex_type.to_string(), msg.to_string(), 0, "".to_string())
}
