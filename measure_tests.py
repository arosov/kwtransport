import subprocess
import time
import sys

tests = [
    "io.github.arosov.kwtransport.CancellationTest",
    "io.github.arosov.kwtransport.ConnectionTest",
    "io.github.arosov.kwtransport.EndpointTest",
    "io.github.arosov.kwtransport.ErrorPropagationTest",
    "io.github.arosov.kwtransport.FfiStressTest",
    "io.github.arosov.kwtransport.IntegrationTest",
    "io.github.arosov.kwtransport.KwTransportJniTest",
    "io.github.arosov.kwtransport.LargeTransferTest",
    "io.github.arosov.kwtransport.LeakTest",
    "io.github.arosov.kwtransport.WTransportReproductionTest"
]

slow_tests = []

print("Starting test measurement...", flush=True)

for test in tests:
    print(f"Running {test}...", flush=True)
    start_time = time.time()
    
    cmd = [
        "./gradlew", 
        ":kwtransport:cleanJvmTest", 
        ":kwtransport:jvmTest", 
        "--tests", test
    ]
    
    process = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    
    # Wait for completion while printing a dot every 10 seconds to keep the tool alive
    while process.poll() is None:
        time.sleep(10)
        print(".", end="", flush=True)
    
    stdout, stderr = process.communicate()
    duration = time.time() - start_time
    print(f"\nFinished {test} in {duration:.2f}s", flush=True)
    
    if process.returncode != 0:
        print(f"FAIL: {test} failed to run.", flush=True)
    else:
        print(f"PASS: {test}", flush=True)
        if duration > 60:
            slow_tests.append((test, duration))

print("\n--- Slow Tests (> 60s) ---", flush=True)
if slow_tests:
    for test, duration in slow_tests:
        print(f"{test}: {duration:.2f}s", flush=True)
else:
    print("No tests took longer than 60 seconds.", flush=True)