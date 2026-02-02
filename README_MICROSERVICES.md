# 🎯 IndiChess: Microservices Architecture + Kubernetes Deployment

Transform your monolithic IndiChess application into a scalable microservices architecture with Kubernetes cloud deployment.

## 📋 What's Included

### ✅ Completed Tasks

1. **Microservices Architecture Design** - Complete blueprint for service separation
2. **5 Microservices** - Fully functional Spring Boot microservices:
   - 🌐 **API Gateway** - Request routing, authentication, load balancing
   - 🔐 **Auth Service** - User registration, login, JWT tokens
   - 👤 **User Service** - Profiles, statistics, leaderboard
   - ♟️ **Game Service** - Game management, state handling
   - 🗄️ **Infrastructure** - PostgreSQL, Redis, Nginx

3. **Kubernetes Ready** - Complete K8s manifests for:
   - Deployments with auto-scaling (HPA)
   - Services for internal and external communication
   - ConfigMaps and Secrets for configuration
   - Persistent storage for databases
   - RBAC for security
   - Ingress for external access
   - Namespaces for organization

4. **Local Development** - Docker Compose setup for testing before cloud deployment

5. **Cloud Deployment** - Ready-to-use scripts and guides for:
   - AWS EKS (Elastic Kubernetes Service)
   - Google Cloud GKE
   - Azure AKS

6. **Helper Scripts**:
   - `deploy.sh` - Automated Kubernetes deployment
   - `cleanup.sh` - Clean up resources
   - `build-and-push.sh` - Build and push Docker images
   - `test-api.sh` - Test API endpoints
   - `health-check.sh` - Monitor service health

7. **Documentation**:
   - `MICROSERVICES_ARCHITECTURE.md` - Detailed architecture
   - `DEPLOYMENT_GUIDE.md` - Step-by-step deployment
   - `QUICKSTART.md` - Quick reference guide

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                   Internet / Users                          │
└──────────────────────────┬──────────────────────────────────┘
                           │
                    LoadBalancer / Ingress
                           │
        ┌──────────────────┴──────────────────┐
        │                                     │
   ┌────▼──────────┐            ┌────────────▼──────┐
   │  Frontend     │            │   API Gateway     │
   │  (React)      │            │  (Spring Cloud)   │
   │  Port: 80/443 │            │   Port: 8080      │
   └────────────────┘            └────────┬──────────┘
                                          │
        ┌─────────────────┬───────────────┼────────────┐
        │                 │               │            │
   ┌────▼──────┐   ┌──────▼────┐   ┌─────▼───┐  ┌────▼───┐
   │ Auth Svc  │   │ User Svc  │   │Game Svc │  │Move Svc│
   │ :8081     │   │  :8082    │   │  :8083  │  │ :8084  │
   │ x2 pods   │   │ x2 pods   │   │ x3 pods │  │ x2 pods│
   └───────────┘   └───────────┘   └─────────┘  └────────┘
        │                 │               │            │
        └─────────────────┴───────────────┴────────────┘
                      │
        ┌─────────────┴─────────────┐
        │                           │
   ┌────▼──────────┐         ┌─────▼──────┐
   │  PostgreSQL   │         │   Redis    │
   │  Database     │         │   Cache    │
   │  Port: 5432   │         │ Port: 6379 │
   │  StatefulSet  │         │ Deployment │
   └───────────────┘         └────────────┘
```

## 📦 Project Structure

```
MicroServices/
├── api-gateway/                    # Spring Cloud Gateway
│   ├── src/main/java/...
│   ├── pom.xml
│   └── Dockerfile
├── auth-service/                   # Authentication Service
│   ├── src/main/java/...
│   ├── pom.xml
│   └── Dockerfile
├── user-service/                   # User Management Service
│   ├── src/main/java/...
│   ├── pom.xml
│   └── Dockerfile
├── game-service/                   # Game Logic Service
│   ├── src/main/java/...
│   ├── pom.xml
│   └── Dockerfile
├── indichessfrontend/              # React Frontend
│   ├── src/
│   ├── public/
│   └── package.json
├── k8s/                            # Kubernetes Manifests
│   ├── 00-namespaces.yaml
│   ├── 01-configmaps.yaml
│   ├── 02-secrets.yaml
│   ├── 03-storage.yaml
│   ├── 04-postgres.yaml
│   ├── 05-redis.yaml
│   ├── 06-api-gateway.yaml
│   ├── 07-auth-service.yaml
│   ├── 08-user-service.yaml
│   ├── 09-game-service.yaml
│   ├── 10-rbac.yaml
│   ├── 11-ingress.yaml
│   ├── 12-frontend.yaml
│   ├── deploy.sh
│   └── cleanup.sh
├── scripts/                        # Helper Scripts
│   ├── build-and-push.sh
│   ├── init-db.sh
│   ├── health-check.sh
│   ├── test-api.sh
│   └── generate-jwt.sh
├── docker-compose-microservices.yml  # Local testing
├── MICROSERVICES_ARCHITECTURE.md   # Architecture docs
├── DEPLOYMENT_GUIDE.md             # Detailed deployment guide
├── QUICKSTART.md                   # Quick reference
└── README.md                       # This file
```

## 🚀 Quick Start

### Option 1: Local Development with Docker Compose

```bash
# Start all services
docker-compose -f docker-compose-microservices.yml up -d

# Check services
docker-compose -f docker-compose-microservices.yml ps

# Test API
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "player1",
    "email": "player1@example.com",
    "password": "pass123",
    "confirmPassword": "pass123"
  }'

# Access frontend
open http://localhost:3000
```

### Option 2: Kubernetes with Minikube

```bash
# Start minikube
minikube start --cpus=4 --memory=8192

# Deploy
./k8s/deploy.sh

# Monitor
kubectl get pods -n indichess -w

# Access
kubectl port-forward svc/api-gateway -n indichess 8080:8080
```

### Option 3: Cloud Deployment (AWS EKS)

```bash
# Create cluster
eksctl create cluster --name indichess --region us-east-1 --nodes 3

# Update kubeconfig
aws eks update-kubeconfig --name indichess --region us-east-1

# Deploy
./k8s/deploy.sh

# Get access
kubectl get ingress -n indichess
```

## 🔐 Key Features

### Security
- ✅ JWT Token-based authentication
- ✅ API Gateway authentication filter
- ✅ Kubernetes RBAC
- ✅ Secrets management for credentials
- ✅ TLS/SSL support via Ingress
- ✅ Network policies ready

### Scalability
- ✅ Horizontal Pod Autoscaling (HPA)
- ✅ Load balancing across replicas
- ✅ Database connection pooling
- ✅ Redis caching layer
- ✅ Multi-zone deployment ready

### Reliability
- ✅ Health checks (liveness & readiness probes)
- ✅ Rolling updates with zero downtime
- ✅ Resource limits and requests
- ✅ Pod anti-affinity rules
- ✅ Persistent storage for state

### Operations
- ✅ Logging with structured logs
- ✅ Metrics exposure via Prometheus
- ✅ Service discovery via Kubernetes DNS
- ✅ Config management via ConfigMaps
- ✅ Secret management via Kubernetes Secrets

## 📊 Service Details

### API Gateway (Spring Cloud Gateway)
- **Purpose**: Request routing, authentication, rate limiting
- **Features**: JWT validation, CORS handling, load balancing
- **Replicas**: 2-5 (auto-scaling)
- **Port**: 8080

### Auth Service
- **Purpose**: User authentication and JWT token management
- **Endpoints**:
  - `POST /api/auth/signup` - User registration
  - `POST /api/auth/login` - User login
  - `POST /api/auth/refresh` - Refresh token
  - `GET /api/auth/validate` - Validate token
- **Replicas**: 2-5 (auto-scaling)
- **Port**: 8081

### User Service
- **Purpose**: User profiles, statistics, and leaderboard
- **Endpoints**:
  - `GET /api/users/{userId}` - Get user profile
  - `PUT /api/users/{userId}` - Update profile
  - `GET /api/users/leaderboard` - Get leaderboard
  - `POST /api/users/{userId}/stats` - Update stats
- **Replicas**: 2-5 (auto-scaling)
- **Port**: 8082

### Game Service
- **Purpose**: Game management and state handling
- **Endpoints**:
  - `POST /api/games` - Create game
  - `GET /api/games/{gameId}` - Get game
  - `PUT /api/games/{gameId}/state` - Update state
  - `POST /api/games/{gameId}/end` - End game
  - `GET /api/games/user/{userId}` - User games
- **Replicas**: 3-8 (auto-scaling)
- **Port**: 8083

## 🛠️ Technology Stack

### Backend
- **Framework**: Spring Boot 4.0.1
- **API Gateway**: Spring Cloud Gateway
- **Security**: Spring Security + JWT (JJWT)
- **Database**: PostgreSQL 16
- **Cache**: Redis 7
- **ORM**: Spring Data JPA
- **Java**: OpenJDK 21

### Frontend
- **Library**: React 19
- **HTTP Client**: Axios
- **WebSocket**: SockJS/STOMP
- **Build**: Node.js 20

### Infrastructure
- **Orchestration**: Kubernetes 1.27+
- **Container**: Docker 20.10+
- **Networking**: Nginx Ingress Controller
- **Monitoring**: Prometheus ready, Grafana compatible

## 📚 Documentation

| Document | Purpose |
|----------|---------|
| [MICROSERVICES_ARCHITECTURE.md](./MICROSERVICES_ARCHITECTURE.md) | Detailed architecture overview |
| [DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md) | Step-by-step deployment instructions |
| [QUICKSTART.md](./QUICKSTART.md) | Quick reference for common tasks |

## 🔄 Development Workflow

### 1. Local Development
```bash
# Clone and setup
git clone <repo>
cd MicroServices

# Build services
for service in api-gateway auth-service user-service game-service; do
  cd $service && mvn clean install && cd ..
done

# Run locally
docker-compose -f docker-compose-microservices.yml up -d

# Test
./scripts/test-api.sh
```

### 2. Make Changes
```bash
# Edit service code
vi api-gateway/src/main/java/...

# Rebuild service
cd api-gateway && mvn clean package -DskipTests && cd ..

# Restart service
docker-compose -f docker-compose-microservices.yml restart api-gateway
```

### 3. Deploy to Kubernetes
```bash
# Build and push images
./scripts/build-and-push.sh v1.0

# Update manifests with new version
sed -i 's/:latest/:v1.0/g' k8s/*.yaml

# Deploy
./k8s/deploy.sh

# Monitor rollout
kubectl rollout status deployment/api-gateway -n indichess
```

## 🐛 Troubleshooting

### Service won't start
```bash
# Check logs
kubectl logs deployment/api-gateway -n indichess

# Describe pod for events
kubectl describe pod <pod-name> -n indichess

# Check resource limits
kubectl top pods -n indichess
```

### Database connection failed
```bash
# Verify PostgreSQL is running
kubectl get pod postgres-0 -n indichess

# Check database exists
kubectl exec -it postgres-0 -n indichess -- \
  psql -U indichess_user -l
```

### High latency/performance issues
```bash
# Check HPA status
kubectl get hpa -n indichess

# Scale manually if needed
kubectl scale deployment api-gateway --replicas=5 -n indichess

# Check resource usage
kubectl top nodes
kubectl top pods -n indichess
```

## 🔒 Security Checklist

Before production deployment:
- [ ] Change database passwords in `k8s/02-secrets.yaml`
- [ ] Update JWT secret key
- [ ] Configure TLS/SSL certificates
- [ ] Setup network policies
- [ ] Enable pod security policies
- [ ] Configure RBAC properly
- [ ] Use private container registry
- [ ] Implement rate limiting
- [ ] Setup monitoring and alerting
- [ ] Implement CI/CD pipeline
- [ ] Setup backup strategy
- [ ] Document runbooks

## 📈 Performance Optimization

### Horizontal Scaling
- API Gateway: 2-5 replicas (70% CPU threshold)
- Auth Service: 2-5 replicas (75% CPU threshold)
- User Service: 2-5 replicas (75% CPU threshold)
- Game Service: 3-8 replicas (70% CPU threshold)

### Caching
- Redis for session storage
- Database query caching
- CDN for frontend assets

### Database
- Connection pooling
- Query optimization
- Index optimization
- Regular backups

## 📞 Support & Contributing

### Getting Help
1. Check [QUICKSTART.md](./QUICKSTART.md) for common tasks
2. Review [DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md) for detailed instructions
3. Check Kubernetes logs: `kubectl logs -f`
4. Use `kubectl describe` for detailed pod information

### Resources
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Cloud Gateway](https://cloud.spring.io/spring-cloud-gateway/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Docker Documentation](https://docs.docker.com/)

## 🎉 What's Next?

1. **Test Locally**: Start with Docker Compose setup
2. **Setup Kubernetes Cluster**: Use Minikube or cloud provider
3. **Deploy Services**: Use `./k8s/deploy.sh`
4. **Monitor**: Setup Prometheus & Grafana
5. **CI/CD**: Implement GitHub Actions or Jenkins
6. **Scale**: Configure auto-scaling policies
7. **Backup**: Setup automated backups
8. **Documentation**: Create runbooks for your team

## 📄 License

This microservices architecture is part of the IndiChess project.

---

**Built with ❤️ for scalable chess gaming**

Last Updated: February 2026
