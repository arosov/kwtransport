#!/bin/bash
set -e

echo "Starting Test Echo Server in background..."
./gradlew :server:runTestServer > test_server.log 2>&1 &
SERVER_PID=$!

# Function to kill the server on exit
cleanup() {
    echo "Stopping Test Echo Server (PID: $SERVER_PID)..."
    kill $SERVER_PID || true
}
trap cleanup EXIT

echo "Waiting for server to be ready..."
until grep -q "SERVER_READY" test_server.log; do
    if ! kill -0 $SERVER_PID 2>/dev/null; then
        echo "Server failed to start. See test_server.log"
        exit 1
    fi
    sleep 1
done
echo "Server is ready."

# Check if Chrome is available
if ! command -v google-chrome >/dev/null 2>&1 && ! command -v chromium >/dev/null 2>&1 && [ -z "$CHROME_BIN" ]; then
    echo "WARNING: No Chrome or Chromium binary found. Skipping browser integration tests."
    exit 0
fi

echo "Running JS and WASM integration tests..."
./gradlew :kwtransport:cleanJsTest :kwtransport:jsTest :kwtransport:cleanWasmJsTest :kwtransport:wasmJsTest --no-configuration-cache

echo "Tests completed successfully!"
