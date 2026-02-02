# Quick Start Guide for IndiChess Microservices

## 🚀 5-Minute Quick Start

### Prerequisites
- Docker installed (`docker --version`)
- Docker Compose installed (`docker-compose --version`)
- 8GB RAM available

### Step 1: Start Services Locally

```bash
cd ~/projects/MicroServices

# Build and start all services
docker-compose -f docker-compose-microservices.yml up -d

# Wait 30 seconds for services to initialize
sleep 30

# Check all services are running
docker-compose -f docker-compose-microservices.yml ps
```

### Step 2: Test the API

```bash
# Sign up
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "player1",
    "email": "player1@example.com",
    "password": "password123",
    "confirmPassword": "password123"
  }'

# Expected response: JWT tokens and user info

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "player1",
    "password": "password123"
  }'

# Save the token from response as:
# TOKEN="eyJhbGciOiJIUzUxMiJ9..."
```

### Step 3: Access Frontend

Open browser: `http://localhost:3000`

### Stop Services

```bash
docker-compose -f docker-compose-microservices.yml down
```

---

## 📦 Kubernetes Deployment (Local with Minikube)

### Prerequisites
- minikube installed
- kubectl installed
- 4GB RAM allocated to minikube

### Step 1: Start Minikube

```bash
minikube start --cpus=4 --memory=8192
minikube status
```

### Step 2: Build Images in Minikube

```bash
# Use minikube's Docker environment
eval $(minikube docker-env)

# Build all images
cd api-gateway && docker build -t indichess/api-gateway:latest . && cd ..
cd auth-service && docker build -t indichess/auth-service:latest . && cd ..
cd user-service && docker build -t indichess/user-service:latest . && cd ..
cd game-service && docker build -t indichess/game-service:latest . && cd ..
```

### Step 3: Deploy to Kubernetes

```bash
# Make deploy script executable
chmod +x k8s/deploy.sh

# Run deployment
./k8s/deploy.sh

# Monitor deployment
kubectl get pods -n indichess -w

# Wait for all pods to be ready (5-10 minutes)
```

### Step 4: Access Services

```bash
# Get Minikube IP
MINIKUBE_IP=$(minikube ip)
echo $MINIKUBE_IP

# Port forward API Gateway
kubectl port-forward svc/api-gateway -n indichess 8080:8080 &

# Access via: http://localhost:8080
# or: http://$MINIKUBE_IP/api/*
```

### Step 5: View Logs

```bash
# Real-time logs
kubectl logs -f deployment/api-gateway -n indichess

# Logs from all services
kubectl logs -f -n indichess -l app=auth-service
kubectl logs -f -n indichess -l app=game-service

# Dashboard
minikube dashboard
```

### Step 6: Cleanup

```bash
# Delete all resources
./k8s/cleanup.sh

# Stop minikube
minikube stop
```

---

## ☁️ Deploy to Cloud (AWS/GCP/Azure)

### AWS EKS (Recommended for Production)

```bash
# 1. Create cluster (takes ~15 minutes)
eksctl create cluster \
  --name indichess \
  --region us-east-1 \
  --nodegroup-name standard \
  --node-type t3.medium \
  --nodes 3

# 2. Verify cluster
kubectl cluster-info
kubectl get nodes

# 3. Create ECR repositories
for service in api-gateway auth-service user-service game-service; do
  aws ecr create-repository --repository-name indichess/$service --region us-east-1
done

# 4. Login to ECR
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin \
  123456789.dkr.ecr.us-east-1.amazonaws.com

# 5. Build and push images
# Update image URLs in k8s/ files first!
./scripts/build-and-push.sh

# 6. Deploy to EKS
./k8s/deploy.sh

# 7. Get LoadBalancer URL
kubectl get ingress -n indichess
# Or for LoadBalancer service:
kubectl get svc api-gateway -n indichess

# 8. Setup domain (Route 53)
# Point your domain to the LoadBalancer URL
```

### Google Cloud GKE

```bash
# 1. Create cluster
gcloud container clusters create indichess \
  --zone us-central1-a \
  --num-nodes 3 \
  --machine-type n1-standard-2

# 2. Get credentials
gcloud container clusters get-credentials indichess --zone us-central1-a

# 3. Create Container Registry
gcloud container images list

# 4. Push images
docker tag api-gateway:latest gcr.io/PROJECT_ID/indichess/api-gateway:latest
docker push gcr.io/PROJECT_ID/indichess/api-gateway:latest

# 5. Deploy
./k8s/deploy.sh

# 6. Get IP
kubectl get svc api-gateway -n indichess
```

### Azure AKS

```bash
# 1. Create resource group
az group create --name indichess --location eastus

# 2. Create cluster
az aks create \
  --resource-group indichess \
  --name indichess-aks \
  --node-count 3

# 3. Get credentials
az aks get-credentials --resource-group indichess --name indichess-aks

# 4. Create Container Registry
az acr create --resource-group indichess --name indichessacr --sku Basic

# 5. Push images
az acr build --registry indichessacr --image indichess/api-gateway:latest ./api-gateway

# 6. Deploy
./k8s/deploy.sh

# 7. Get IP
kubectl get svc api-gateway -n indichess
```

---

## 🔧 Troubleshooting

### Services won't start

```bash
# Check logs
docker-compose -f docker-compose-microservices.yml logs api-gateway

# Or in Kubernetes
kubectl logs deployment/api-gateway -n indichess
kubectl describe pod <pod-name> -n indichess
```

### Database connection failed

```bash
# Check database is running
docker-compose -f docker-compose-microservices.yml ps postgres

# Connect to database
docker-compose -f docker-compose-microservices.yml exec postgres \
  psql -U indichess_user -d indichess_auth
```

### Port already in use

```bash
# Find process using port
lsof -i :8080
# Or on Windows:
netstat -ano | findstr :8080

# Kill process or use different port
```

### Kubernetes pod stuck in pending

```bash
# Check node resources
kubectl describe nodes

# Check pod events
kubectl describe pod <pod-name> -n indichess

# Scale down other services or add more nodes
```

---

## 📊 Monitoring

### Local (Docker Compose)

```bash
# Services are available at:
# - API Gateway: http://localhost:8080
# - Auth Service: http://localhost:8081/actuator/health
# - User Service: http://localhost:8082/actuator/health
# - Game Service: http://localhost:8083/actuator/health
# - Frontend: http://localhost:3000
```

### Kubernetes

```bash
# View resource usage
kubectl top nodes
kubectl top pods -n indichess

# Get deployment status
kubectl rollout status deployment/api-gateway -n indichess

# Watch HPA
kubectl get hpa -n indichess -w
```

---

## 💡 Common Commands

```bash
# Docker Compose
docker-compose -f docker-compose-microservices.yml build
docker-compose -f docker-compose-microservices.yml up -d
docker-compose -f docker-compose-microservices.yml down
docker-compose -f docker-compose-microservices.yml logs -f

# Kubernetes - Deployment
kubectl apply -f k8s/
kubectl delete -f k8s/
kubectl get all -n indichess
kubectl describe deployment api-gateway -n indichess

# Kubernetes - Pods
kubectl get pods -n indichess -o wide
kubectl logs deployment/api-gateway -n indichess
kubectl exec -it <pod-name> -n indichess -- bash

# Kubernetes - Scaling
kubectl scale deployment api-gateway --replicas=5 -n indichess
kubectl autoscale deployment api-gateway --min=2 --max=5 -n indichess
```

---

## 🔐 Environment Variables

Update credentials in:
- `docker-compose-microservices.yml` for local
- `k8s/02-secrets.yaml` for Kubernetes

**IMPORTANT: Change default passwords before production deployment!**

Default credentials (CHANGE THESE):
```
DB_USER: indichess_user
DB_PASSWORD: changeme123!secure
JWT_SECRET: mySecretKeyForJWTTokenGenerationAndValidationPurposeThatIsLongEnough
```

---

## 📚 Next Steps

1. [ ] Test local setup with Docker Compose
2. [ ] Familiarize with Kubernetes manifests
3. [ ] Setup cloud account (AWS/GCP/Azure)
4. [ ] Configure DNS for your domain
5. [ ] Setup CI/CD pipeline (GitHub Actions)
6. [ ] Configure monitoring (Prometheus/Grafana)
7. [ ] Setup logging (ELK Stack or cloud provider)
8. [ ] Configure backup strategy
9. [ ] Setup load testing
10. [ ] Document runbooks for your team

---

## 📞 Support Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Docker Documentation](https://docs.docker.com/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [AWS EKS](https://docs.aws.amazon.com/eks/)
- [Google Cloud GKE](https://cloud.google.com/kubernetes-engine/docs)
- [Azure AKS](https://docs.microsoft.com/en-us/azure/aks/)
