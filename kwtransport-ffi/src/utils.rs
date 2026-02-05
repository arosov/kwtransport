pub fn apply_transport_config(config: &mut wtransport::config::QuicTransportConfig, settings: &[i64]) {
    use wtransport::quinn::VarInt;
    
    if let Some(&val) = settings.get(0) {
        if val >= 0 { let _ = config.max_concurrent_bidi_streams(VarInt::try_from(val as u64).unwrap_or(VarInt::MAX)); }
    }
    if let Some(&val) = settings.get(1) {
        if val >= 0 { let _ = config.max_concurrent_uni_streams(VarInt::try_from(val as u64).unwrap_or(VarInt::MAX)); }
    }
    if let Some(&val) = settings.get(2) {
        // initialMaxData -> receive_window
        if val >= 0 { let _ = config.receive_window(VarInt::try_from(val as u64).unwrap_or(VarInt::MAX)); }
    }
    if let Some(&val) = settings.get(3) {
        // initialMaxStreamDataBidiLocal -> stream_receive_window
        if val >= 0 { let _ = config.stream_receive_window(VarInt::try_from(val as u64).unwrap_or(VarInt::MAX)); }
    }
    
    // index 4: initialMaxStreamDataBidiRemote -> Not directly in wtransport QuicTransportConfig (quinn alias)
    // Actually quinn has max_stream_receive_window but it's for ALL streams.
    // wtransport/quinn TransportConfig has:
    // receive_window (Max Data)
    // stream_receive_window (Max Stream Data)
    
    if let Some(&val) = settings.get(6) {
        if val >= 0 { let _ = config.datagram_receive_buffer_size(Some(val as usize)); }
    }
    if let Some(&val) = settings.get(7) {
        if val >= 0 { let _ = config.datagram_send_buffer_size(val as usize); }
    }
    if let Some(&val) = settings.get(8) {
        if val >= 0 {
            let factory: Option<std::sync::Arc<dyn wtransport::quinn::congestion::ControllerFactory + Send + Sync>> = match val {
                1 => Some(std::sync::Arc::new(wtransport::quinn::congestion::NewRenoConfig::default())),
                2 => Some(std::sync::Arc::new(wtransport::quinn::congestion::CubicConfig::default())),
                3 => {
                    Some(std::sync::Arc::new(wtransport::quinn::congestion::BbrConfig::default()))
                }
                _ => None,
            };
            if let Some(f) = factory {
                config.congestion_controller_factory(f);
            }
        }
    }
}
