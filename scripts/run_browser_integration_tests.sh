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

echo "Running JS and WASM integration tests..."
./gradlew :shared:cleanJsTest :shared:jsTest :shared:cleanWasmJsTest :shared:wasmJsTest --no-configuration-cache

echo "Tests completed successfully!"
