#!/bin/bash

# IndiChess Kubernetes Cleanup Script
# This script removes all IndiChess components from the cluster

set -e

NAMESPACE="indichess"

echo "🗑️ Starting cleanup of IndiChess resources..."
echo "⚠️  This will delete all resources in namespace: $NAMESPACE"
read -p "Are you sure you want to continue? (yes/no): " confirm

if [ "$confirm" != "yes" ]; then
    echo "❌ Cleanup cancelled."
    exit 0
fi

# Delete Deployments
echo "Deleting Deployments..."
kubectl delete deployment -n $NAMESPACE --all

# Delete StatefulSets
echo "Deleting StatefulSets..."
kubectl delete statefulset -n $NAMESPACE --all

# Delete Services
echo "Deleting Services..."
kubectl delete svc -n $NAMESPACE --all

# Delete PVCs
echo "Deleting PersistentVolumeClaims..."
kubectl delete pvc -n $NAMESPACE --all

# Delete ConfigMaps
echo "Deleting ConfigMaps..."
kubectl delete configmap -n $NAMESPACE --all

# Delete Secrets
echo "Deleting Secrets..."
kubectl delete secret -n $NAMESPACE --all

# Delete Ingress
echo "Deleting Ingress..."
kubectl delete ingress -n $NAMESPACE --all

# Delete HPA
echo "Deleting HorizontalPodAutoscalers..."
kubectl delete hpa -n $NAMESPACE --all

# Delete RBAC
echo "Deleting RBAC resources..."
kubectl delete role,rolebinding -n $NAMESPACE --all

# Delete Namespace
echo "Deleting Namespace..."
kubectl delete namespace $NAMESPACE

echo "✅ Cleanup completed!"
