#!/bin/bash

# IndiChess Kubernetes Deployment Script
# This script deploys all components to a Kubernetes cluster

set -e

NAMESPACE="indichess"
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo "🚀 Starting IndiChess Kubernetes Deployment..."
echo "📦 Namespace: $NAMESPACE"

# Check if kubectl is installed
if ! command -v kubectl &> /dev/null; then
    echo "❌ kubectl is not installed. Please install kubectl first."
    exit 1
fi

# Check cluster connection
if ! kubectl cluster-info &> /dev/null; then
    echo "❌ Cannot connect to Kubernetes cluster. Please configure kubeconfig."
    exit 1
fi

echo "✅ kubectl is installed and cluster is accessible"

# Step 1: Create Namespaces
echo "📋 Step 1: Creating namespaces..."
kubectl apply -f "$SCRIPT_DIR/00-namespaces.yaml"
sleep 5

# Step 2: Create ConfigMaps and Secrets
echo "🔐 Step 2: Creating ConfigMaps and Secrets..."
kubectl apply -f "$SCRIPT_DIR/01-configmaps.yaml"
kubectl apply -f "$SCRIPT_DIR/02-secrets.yaml"
sleep 5

# Step 3: Create Storage
echo "💾 Step 3: Creating persistent volumes and claims..."
kubectl apply -f "$SCRIPT_DIR/03-storage.yaml"
sleep 5

# Step 4: Deploy PostgreSQL
echo "🐘 Step 4: Deploying PostgreSQL..."
kubectl apply -f "$SCRIPT_DIR/04-postgres.yaml"
echo "⏳ Waiting for PostgreSQL to be ready..."
kubectl wait --for=condition=ready pod -l app=postgres -n $NAMESPACE --timeout=300s
sleep 10

# Step 5: Deploy Redis
echo "🔴 Step 5: Deploying Redis..."
kubectl apply -f "$SCRIPT_DIR/05-redis.yaml"
echo "⏳ Waiting for Redis to be ready..."
kubectl wait --for=condition=ready pod -l app=redis -n $NAMESPACE --timeout=300s
sleep 5

# Step 6: Create RBAC
echo "🔐 Step 6: Setting up RBAC..."
kubectl apply -f "$SCRIPT_DIR/10-rbac.yaml"
sleep 5

# Step 7: Deploy API Gateway
echo "🌐 Step 7: Deploying API Gateway..."
kubectl apply -f "$SCRIPT_DIR/06-api-gateway.yaml"
echo "⏳ Waiting for API Gateway to be ready..."
kubectl wait --for=condition=ready pod -l app=api-gateway -n $NAMESPACE --timeout=300s
sleep 5

# Step 8: Deploy Auth Service
echo "🔐 Step 8: Deploying Auth Service..."
kubectl apply -f "$SCRIPT_DIR/07-auth-service.yaml"
echo "⏳ Waiting for Auth Service to be ready..."
kubectl wait --for=condition=ready pod -l app=auth-service -n $NAMESPACE --timeout=300s
sleep 5

# Step 9: Deploy User Service
echo "👤 Step 9: Deploying User Service..."
kubectl apply -f "$SCRIPT_DIR/08-user-service.yaml"
echo "⏳ Waiting for User Service to be ready..."
kubectl wait --for=condition=ready pod -l app=user-service -n $NAMESPACE --timeout=300s
sleep 5

# Step 10: Deploy Game Service
echo "♟️ Step 10: Deploying Game Service..."
kubectl apply -f "$SCRIPT_DIR/09-game-service.yaml"
echo "⏳ Waiting for Game Service to be ready..."
kubectl wait --for=condition=ready pod -l app=game-service -n $NAMESPACE --timeout=300s
sleep 5

# Step 11: Deploy Ingress
echo "🔗 Step 11: Setting up Ingress..."
kubectl apply -f "$SCRIPT_DIR/11-ingress.yaml"
sleep 5

# Print deployment info
echo ""
echo "✅ Deployment Complete!"
echo ""
echo "📊 Deployment Status:"
kubectl get all -n $NAMESPACE
echo ""
echo "🔗 Service Endpoints:"
kubectl get svc -n $NAMESPACE
echo ""
echo "📈 Pods Status:"
kubectl get pods -n $NAMESPACE -o wide
echo ""
echo "💡 Next Steps:"
echo "1. Configure your DNS provider to point to the LoadBalancer IP"
echo "2. Monitor logs: kubectl logs -f -l app=api-gateway -n $NAMESPACE"
echo "3. Access dashboard: kubectl proxy"
