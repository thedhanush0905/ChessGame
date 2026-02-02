# IndiChess Microservices + Kubernetes Deployment Guide

## 📋 Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [Prerequisites](#prerequisites)
3. [Local Development Setup](#local-development-setup)
4. [Building Docker Images](#building-docker-images)
5. [Deploying to Kubernetes](#deploying-to-kubernetes)
6. [Cloud Deployment (AWS/GCP/Azure)](#cloud-deployment)
7. [Monitoring & Logging](#monitoring--logging)
8. [Troubleshooting](#troubleshooting)

## Architecture Overview

### Microservices Components
```
┌─────────────────────────────────────────────────────────────┐
│                    Frontend (React)                         │
│               Kubernetes Ingress / LoadBalancer              │
└──────────────────────────┬──────────────────────────────────┘
                           │
                    API Gateway (8080)
                   Spring Cloud Gateway
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
   Auth Service      User Service       Game Service
   (JWT, Login)     (Profiles, ELO)   (Game Logic)
        │                  │                  │
        └──────────────────┴──────────────────┘
                           │
              ┌────────────┴────────────┐
              │                         │
          PostgreSQL              Redis Cache
         (Main Database)        (Session Store)
```

### Service Responsibilities

| Service | Port | Purpose | Replicas |
|---------|------|---------|----------|
| API Gateway | 8080 | Routes requests, JWT auth | 2-5 |
| Auth Service | 8081 | Login, signup, JWT tokens | 2-5 |
| User Service | 8082 | Profiles, statistics, leaderboard | 2-5 |
| Game Service | 8083 | Game creation, state management | 3-8 |
| PostgreSQL | 5432 | Persistent data storage | 1 |
| Redis | 6379 | Caching, sessions | 1 |

## Prerequisites

### Required Tools
- **Docker & Docker Compose**: 20.10+
- **kubectl**: 1.27+
- **Helm** (optional): 3.12+
- **Git**: For version control

### Kubernetes Cluster Options

#### Option 1: Local Development
```bash
# Minikube (Recommended for local)
minikube start --cpus=4 --memory=8192
kubectl get nodes

# Docker Desktop (Alternative)
# Enable Kubernetes in Docker Desktop settings
kubectl get nodes

# Kind (Kubernetes in Docker)
kind create cluster --name indichess
kubectl cluster-info --context kind-indichess
```

#### Option 2: Cloud Providers

**AWS EKS**
```bash
# Install AWS CLI
aws configure

# Create cluster
eksctl create cluster --name indichess --region us-east-1 --nodegroup-name standard-nodes --node-type t3.medium --nodes 3

# Update kubeconfig
aws eks update-kubeconfig --name indichess --region us-east-1
```

**Google Cloud GKE**
```bash
# Initialize gcloud
gcloud init
gcloud config set project YOUR_PROJECT_ID

# Create cluster
gcloud container clusters create indichess \
  --zone us-central1-a \
  --num-nodes 3 \
  --machine-type n1-standard-2
```

**Azure AKS**
```bash
# Create resource group
az group create --name indichess --location eastus

# Create cluster
az aks create \
  --resource-group indichess \
  --name indichess-aks \
  --node-count 3 \
  --enable-managed-identity

# Get credentials
az aks get-credentials --resource-group indichess --name indichess-aks
```

## Local Development Setup

### 1. Clone and Build Locally

```bash
cd ~/projects/MicroServices

# Install Maven (if not already installed)
# Then build each service
cd api-gateway && mvn clean install && cd ..
cd auth-service && mvn clean install && cd ..
cd user-service && mvn clean install && cd ..
cd game-service && mvn clean install && cd ..
```

### 2. Run with Docker Compose

```bash
# Build all services
docker-compose -f docker-compose-microservices.yml build

# Start all services
docker-compose -f docker-compose-microservices.yml up -d

# Verify services are running
docker-compose -f docker-compose-microservices.yml ps

# View logs
docker-compose -f docker-compose-microservices.yml logs -f api-gateway
```

### 3. Test Local Setup

```bash
# Sign up
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "confirmPassword": "password123"
  }'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# Save token and test authenticated endpoint
TOKEN="your_token_here"
curl -X GET http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer $TOKEN"
```

## Building Docker Images

### Prerequisites
```bash
# Login to Docker Hub
docker login

# Create repository (if using Docker Hub)
# indichess/api-gateway
# indichess/auth-service
# indichess/user-service
# indichess/game-service
```

### Build and Push

```bash
# Using the provided script
chmod +x scripts/build-and-push.sh
./scripts/build-and-push.sh latest

# Or build manually
cd api-gateway
mvn clean package -DskipTests
docker build -t yourusername/api-gateway:latest .
docker push yourusername/api-gateway:latest
```

## Deploying to Kubernetes

### Step 1: Verify Cluster Connection

```bash
kubectl cluster-info
kubectl get nodes
kubectl get all
```

### Step 2: Update Image References

Edit `k8s/06-api-gateway.yaml`, `k8s/07-auth-service.yaml`, etc.:

```yaml
# Change FROM:
image: indichess/api-gateway:latest

# TO:
image: yourusername/api-gateway:latest
```

### Step 3: Configure Secrets

```bash
# Update database credentials in k8s/02-secrets.yaml
kubectl apply -f k8s/02-secrets.yaml
```

### Step 4: Deploy Components

**Option A: Using Script (Recommended)**
```bash
chmod +x k8s/deploy.sh
./k8s/deploy.sh
```

**Option B: Manual Deployment**
```bash
# Create namespace
kubectl apply -f k8s/00-namespaces.yaml

# Create config and secrets
kubectl apply -f k8s/01-configmaps.yaml
kubectl apply -f k8s/02-secrets.yaml

# Create storage
kubectl apply -f k8s/03-storage.yaml

# Deploy database and cache
kubectl apply -f k8s/04-postgres.yaml
kubectl apply -f k8s/05-redis.yaml

# Wait for ready
kubectl wait --for=condition=ready pod -l app=postgres -n indichess --timeout=300s

# Deploy services
kubectl apply -f k8s/06-api-gateway.yaml
kubectl apply -f k8s/07-auth-service.yaml
kubectl apply -f k8s/08-user-service.yaml
kubectl apply -f k8s/09-game-service.yaml

# Setup networking
kubectl apply -f k8s/10-rbac.yaml
kubectl apply -f k8s/11-ingress.yaml
```

### Step 5: Verify Deployment

```bash
# Check pods
kubectl get pods -n indichess
kubectl get pods -n indichess -o wide

# Check services
kubectl get svc -n indichess

# Check HPA status
kubectl get hpa -n indichess

# View logs
kubectl logs -f -n indichess -l app=api-gateway
kubectl logs -f -n indichess deployment/auth-service
```

## Cloud Deployment

### AWS EKS Deployment

```bash
# 1. Setup ECR (Elastic Container Registry)
aws ecr create-repository --repository-name indichess/api-gateway

# 2. Push images to ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 123456789.dkr.ecr.us-east-1.amazonaws.com

docker tag api-gateway:latest 123456789.dkr.ecr.us-east-1.amazonaws.com/indichess/api-gateway:latest
docker push 123456789.dkr.ecr.us-east-1.amazonaws.com/indichess/api-gateway:latest

# 3. Deploy to EKS
kubectl apply -f k8s/00-namespaces.yaml
# ... continue with other manifests

# 4. Setup Load Balancer
# AWS will automatically create a load balancer for the Ingress
kubectl get ingress -n indichess -w

# 5. Get LoadBalancer URL
aws elb describe-load-balancers --query 'LoadBalancerDescriptions[0].DNSName'
```

### GCP GKE Deployment

```bash
# 1. Setup Container Registry
gcloud auth configure-docker

# 2. Push images
docker tag api-gateway:latest gcr.io/YOUR_PROJECT/indichess/api-gateway:latest
docker push gcr.io/YOUR_PROJECT/indichess/api-gateway:latest

# 3. Deploy
kubectl apply -f k8s/

# 4. Setup ingress
gcloud compute addresses create indichess-ip --global
kubectl patch ingress indichess-ingress -n indichess -p '{"spec":{"rules":[{"host":"indichess.example.com"}]}}'
```

### Azure AKS Deployment

```bash
# 1. Setup Azure Container Registry
az acr create --resource-group indichess --name indichessregistry --sku Basic

# 2. Push images
az acr build --registry indichessregistry --image indichess/api-gateway:latest ./api-gateway/

# 3. Deploy to AKS
kubectl apply -f k8s/

# 4. Get public IP
kubectl get service api-gateway -n indichess
```

## Monitoring & Logging

### Install Prometheus & Grafana

```bash
# Add Prometheus Helm chart
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update

# Install Prometheus
helm install prometheus prometheus-community/prometheus \
  -n indichess-monitoring \
  --create-namespace

# Install Grafana
helm install grafana grafana/grafana \
  -n indichess-monitoring \
  --create-namespace \
  --set adminPassword=admin

# Access Grafana
kubectl port-forward svc/grafana 3000:80 -n indichess-monitoring
# Visit http://localhost:3000
```

### Enable Pod Metrics

```bash
# Deploy Metrics Server
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml

# Verify HPA works
kubectl get hpa -n indichess
```

### View Logs

```bash
# Real-time logs
kubectl logs -f deployment/api-gateway -n indichess

# Logs from all pods
kubectl logs -f -l app=api-gateway -n indichess --all-containers=true

# Export logs
kubectl logs deployment/api-gateway -n indichess > api-gateway.log
```

## Troubleshooting

### Common Issues

#### 1. Pods not starting

```bash
# Check pod status
kubectl describe pod <pod-name> -n indichess

# Check events
kubectl get events -n indichess

# View pod logs
kubectl logs <pod-name> -n indichess
```

#### 2. Database connection issues

```bash
# Check PostgreSQL
kubectl exec -it postgres-0 -n indichess -- psql -U indichess_user

# Create databases
CREATE DATABASE indichess_auth;
CREATE DATABASE indichess_user;
CREATE DATABASE indichess_game;
```

#### 3. Service not accessible

```bash
# Check service endpoints
kubectl get endpoints -n indichess

# Test connectivity
kubectl run -it --rm debug --image=busybox --restart=Never -n indichess -- sh
# Inside container:
wget -O- http://api-gateway:8080/health
```

#### 4. Image pull errors

```bash
# Check image pull secrets
kubectl get secrets -n indichess

# Verify registry access
docker pull your-registry/image:tag

# Update imagePullSecrets in deployments
```

### Useful Commands

```bash
# Restart deployment
kubectl rollout restart deployment/api-gateway -n indichess

# Scale deployment
kubectl scale deployment api-gateway --replicas=5 -n indichess

# Port forward for testing
kubectl port-forward svc/api-gateway 8080:8080 -n indichess

# Execute commands in pod
kubectl exec -it <pod-name> -n indichess -- bash

# Delete and recreate
kubectl delete deployment api-gateway -n indichess
kubectl apply -f k8s/06-api-gateway.yaml
```

## Additional Resources

- [Spring Boot Kubernetes Guide](https://spring.io/guides/gs/spring-boot-kubernetes/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [12 Factor App](https://12factor.net/)

## Security Checklist

- [ ] Change default database passwords
- [ ] Update JWT secret key
- [ ] Configure TLS/SSL certificates
- [ ] Setup network policies
- [ ] Enable pod security policies
- [ ] Configure RBAC properly
- [ ] Use private container registry
- [ ] Implement rate limiting
- [ ] Setup monitoring and alerting
- [ ] Implement CI/CD pipeline

## Support

For issues or questions:
1. Check logs: `kubectl logs -f`
2. Check events: `kubectl get events`
3. Use `kubectl describe` for detailed information
4. Consult the troubleshooting section above
