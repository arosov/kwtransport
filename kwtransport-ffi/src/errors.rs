use wtransport::error::{ConnectionError, StreamOpeningError, SendDatagramError};

pub fn map_conn_err(error: ConnectionError) -> (String, String) {
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

pub fn map_stream_err(error: StreamOpeningError) -> (String, String) {
    let (ex_type, msg) = match error {
        StreamOpeningError::NotConnected => ("NOT_CONNECTED", "Not connected"),
        StreamOpeningError::Refused => ("REFUSED", "Opening stream refused"),
    };
    (ex_type.to_string(), msg.to_string())
}

pub fn map_send_datagram_err(error: SendDatagramError) -> (String, String) {
    let (ex_type, msg) = match error {
        SendDatagramError::NotConnected => ("NOT_CONNECTED", "Not connected"),
        SendDatagramError::UnsupportedByPeer => ("UNSUPPORTED_BY_PEER", "Unsupported by peer"),
        SendDatagramError::TooLarge => ("TOO_LARGE", "Too large"),
    };
    (ex_type.to_string(), msg.to_string())
}
