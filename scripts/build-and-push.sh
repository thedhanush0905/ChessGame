#!/bin/bash

# Docker Build and Push Script
# This script builds Docker images for all microservices and pushes them to registry

set -e

REGISTRY="docker.io"
IMAGE_PREFIX="indichess"
VERSION="${1:-latest}"

echo "🐳 Building Docker Images for IndiChess"
echo "📦 Version: $VERSION"
echo "🏗️ Registry: $REGISTRY"

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# Function to build and push image
build_image() {
    local service=$1
    local dockerfile=$2
    
    echo "---"
    echo "Building: $IMAGE_PREFIX/$service:$VERSION"
    
    if [ ! -d "$PROJECT_ROOT/$service" ]; then
        echo "❌ Directory not found: $PROJECT_ROOT/$service"
        return 1
    fi
    
    # Build with Maven
    echo "📦 Building Maven project..."
    cd "$PROJECT_ROOT/$service"
    mvn clean package -DskipTests
    
    # Build Docker image
    echo "🐳 Building Docker image..."
    docker build -t $REGISTRY/$IMAGE_PREFIX/$service:$VERSION \
                 -t $REGISTRY/$IMAGE_PREFIX/$service:latest \
                 -f Dockerfile .
    
    echo "✅ Built: $IMAGE_PREFIX/$service:$VERSION"
}

# Function to push image
push_image() {
    local service=$1
    
    echo "---"
    echo "Pushing: $IMAGE_PREFIX/$service:$VERSION"
    
    docker push $REGISTRY/$IMAGE_PREFIX/$service:$VERSION
    docker push $REGISTRY/$IMAGE_PREFIX/$service:latest
    
    echo "✅ Pushed: $IMAGE_PREFIX/$service:$VERSION"
}

# Build all services
echo "🔨 Building services..."
build_image "api-gateway" "api-gateway/Dockerfile"
build_image "auth-service" "auth-service/Dockerfile"
build_image "user-service" "user-service/Dockerfile"
build_image "game-service" "game-service/Dockerfile"

# Push all images
echo ""
echo "📤 Pushing images to registry..."
read -p "Push images to $REGISTRY? (yes/no): " confirm

if [ "$confirm" = "yes" ]; then
    push_image "api-gateway"
    push_image "auth-service"
    push_image "user-service"
    push_image "game-service"
    echo ""
    echo "✅ All images pushed successfully!"
else
    echo "⏭️ Skipped pushing images"
fi

echo ""
echo "📝 Summary:"
echo "  API Gateway:    $REGISTRY/$IMAGE_PREFIX/api-gateway:$VERSION"
echo "  Auth Service:   $REGISTRY/$IMAGE_PREFIX/auth-service:$VERSION"
echo "  User Service:   $REGISTRY/$IMAGE_PREFIX/user-service:$VERSION"
echo "  Game Service:   $REGISTRY/$IMAGE_PREFIX/game-service:$VERSION"
