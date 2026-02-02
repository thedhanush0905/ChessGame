#!/bin/bash

# Health check script for all services

NAMESPACE="indichess"
MAX_WAIT=300
INTERVAL=5

echo "🏥 Checking health of all services..."
echo "⏱️  Max wait time: ${MAX_WAIT}s"
echo ""

services=(
    "postgres:5432"
    "redis:6379"
    "api-gateway:8080"
    "auth-service:8081"
    "user-service:8082"
    "game-service:8083"
)

check_service() {
    local service=$1
    local host="${service%:*}"
    local port="${service##*:}"
    local elapsed=0
    
    echo -n "Checking $service..."
    
    while [ $elapsed -lt $MAX_WAIT ]; do
        if timeout 2 bash -c "echo >/dev/tcp/localhost/$port" 2>/dev/null; then
            echo " ✅"
            return 0
        fi
        elapsed=$((elapsed + INTERVAL))
        echo -n "."
        sleep $INTERVAL
    done
    
    echo " ❌ (Timeout after ${MAX_WAIT}s)"
    return 1
}

failed=0
for service in "${services[@]}"; do
    if ! check_service "$service"; then
        failed=$((failed + 1))
    fi
done

echo ""
if [ $failed -eq 0 ]; then
    echo "✅ All services are healthy!"
    exit 0
else
    echo "❌ $failed service(s) failed health check"
    exit 1
fi
