# IndiChess Complete Microservices - Quick Start Guide

## 🚀 30-Second Start

### Using Docker Compose (Recommended for Testing)

```bash
cd C:\Users\Admin\Downloads\MicroServices
docker-compose -f docker-compose-microservices.yml up -d
```

✅ **All 7 services + databases running in 2 minutes!**

### Access Services

| Service | URL | Port |
|---------|-----|------|
| **Frontend** | http://localhost:3000 | 3000 |
| **API Gateway** | http://localhost:8080 | 8080 |
| **Eureka Dashboard** | http://localhost:8761 | 8761 |
| **Auth Service** | http://localhost:8081 | 8081 |
| **User Service** | http://localhost:8082 | 8082 |
| **Game Service** | http://localhost:8083 | 8083 |
| **Matchmaking Service** | http://localhost:8084 | 8084 |
| **Notification Service** | http://localhost:8085 | 8085 |

---

## 📋 Architecture Overview

```
                    Frontend (React)
                        ↓
                  API Gateway (8080)
                   ↙ ↓ ↙ ↓ ↙ ↓
    ┌─────────────────────────────────────┐
    │  Service Registry (Eureka 8761)     │
    │                                     │
    │  - Auth Service (8081)              │
    │  - User Service (8082)              │
    │  - Game Service (8083)              │
    │  - Matchmaking Service (8084)       │
    │  - Notification Service (8085)      │
    └─────────────────────────────────────┘
         ↓              ↓
    PostgreSQL      Redis Cache
     (5432)         (6379)
```

---

## 🔧 What's New (vs Initial Setup)

### ✨ Added Services

1. **Eureka Server** - Service Registry & Discovery
   - Automatic service registration
   - Dynamic routing without hardcoded URLs
   - Health monitoring

2. **Matchmaking Service** - Game Queue & Pairing
   - Player skill-based matching
   - Time control selection
   - Queue management
   - Automatic matching every 5 seconds

3. **Notification Service** - Real-time Communication
   - WebSocket support (STOMP)
   - Game invites & match notifications
   - In-game chat notifications
   - Real-time updates

### 🔄 Updated Services

All existing services now:
- Register with Eureka on startup
- Use service discovery instead of hardcoded URLs
- Support auto-scaling in Kubernetes
- Include health/readiness endpoints

---

## 📊 Service Details

### **Eureka Server (Port 8761)**
```
Dashboard: http://localhost:8761/
- View all registered services
- Check service status
- Monitor heartbeats
```

### **API Gateway (Port 8080)**
Centralized routing with JWT authentication:
- `/api/auth/**` → Auth Service
- `/api/users/**` → User Service  
- `/api/games/**` → Game Service
- `/api/matchmaking/**` → Matchmaking Service
- `/ws-notifications` → Notification Service

### **Auth Service (Port 8081)**
```bash
POST   /api/auth/register          # Create account
POST   /api/auth/login             # Get JWT token
POST   /api/auth/refresh           # Refresh token
GET    /api/auth/validate          # Verify token
```

### **User Service (Port 8082)**
```bash
GET    /api/users/{id}             # Get profile
PUT    /api/users/{id}             # Update profile
GET    /api/users/leaderboard      # Top 100 players
GET    /api/users/stats/{id}       # Player statistics
```

### **Game Service (Port 8083)**
```bash
POST   /api/games                  # Create game
GET    /api/games/{id}             # Get game state
POST   /api/games/{id}/move        # Make move
GET    /api/games/{id}/history     # Move history
```

### **Matchmaking Service (Port 8084)**
```bash
POST   /api/matchmaking/join       # Join queue
POST   /api/matchmaking/leave      # Leave queue
GET    /api/matchmaking/status/{userId}  # Check status
```

### **Notification Service (Port 8085)**
```
WebSocket: ws://localhost:8085/ws-notifications
Subscribe to: /topic/notifications/{userId}

Message Types:
- GAME_INVITE
- MATCH_FOUND
- GAME_START
- GAME_END
- CHAT_MESSAGE
```

---

## ✅ Testing the System

### 1. Check All Services Running

```bash
# Windows PowerShell
Invoke-WebRequest http://localhost:8761  # Eureka
Invoke-WebRequest http://localhost:8080/actuator/health  # Gateway
Invoke-WebRequest http://localhost:8081/actuator/health  # Auth
Invoke-WebRequest http://localhost:8082/actuator/health  # User
Invoke-WebRequest http://localhost:8083/actuator/health  # Game
Invoke-WebRequest http://localhost:8084/actuator/health  # Matchmaking
Invoke-WebRequest http://localhost:8085/actuator/health  # Notification
```

### 2. Register User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'
```

### 3. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# Save the returned token for next requests
```

### 4. Join Matchmaking

```bash
TOKEN="your_token_here"
curl -X POST http://localhost:8080/api/matchmaking/join \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "timeControl": "rapid",
    "skillLevel": 1200
  }'
```

### 5. Check Eureka Dashboard

Visit: **http://localhost:8761/**

You'll see all services registered:
- eureka-server
- api-gateway
- auth-service
- user-service
- game-service
- matchmaking-service
- notification-service

---

## 🐳 Docker Compose Commands

```bash
# Start all services
docker-compose -f docker-compose-microservices.yml up -d

# View logs
docker-compose -f docker-compose-microservices.yml logs -f

# View specific service logs
docker-compose -f docker-compose-microservices.yml logs -f auth-service

# Stop all services
docker-compose -f docker-compose-microservices.yml down

# Stop and remove volumes
docker-compose -f docker-compose-microservices.yml down -v

# Restart a service
docker-compose -f docker-compose-microservices.yml restart auth-service

# Scale a service
docker-compose -f docker-compose-microservices.yml up -d --scale game-service=3
```

---

## ☸️ Kubernetes Deployment

### Prerequisites
- kubectl configured
- Kubernetes 1.27+

### Deploy All Services

```bash
# Create namespace
kubectl create namespace indichess

# Create secrets
kubectl create secret generic postgres-credentials \
  --from-literal=username=indichess_user \
  --from-literal=password=secure_password_change_me \
  -n indichess

# Deploy infrastructure
kubectl apply -f 00-namespaces.yaml
kubectl apply -f 01-rbac.yaml
kubectl apply -f 02-postgres-pvc.yaml
kubectl apply -f 03-postgres.yaml
kubectl apply -f 04-redis-pvc.yaml
kubectl apply -f 05-redis.yaml

# Wait 60 seconds for databases
sleep 60

# Deploy Eureka first
kubectl apply -f 13-eureka-server.yaml
sleep 30

# Deploy all services
kubectl apply -f 06-api-gateway.yaml
kubectl apply -f 07-auth-service.yaml
kubectl apply -f 08-user-service.yaml
kubectl apply -f 09-game-service.yaml
kubectl apply -f 14-matchmaking-service.yaml
kubectl apply -f 15-notification-service.yaml
kubectl apply -f 10-ingress.yaml
kubectl apply -f 12-frontend.yaml

# Check status
kubectl get pods -n indichess
kubectl get svc -n indichess

# View dashboard
kubectl get svc -n indichess  # Get LoadBalancer IP
```

---

## 📁 Project Structure

```
MicroServices/
├── eureka-server/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── api-gateway/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── auth-service/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── user-service/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── game-service/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── matchmaking-service/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── notification-service/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── indichessfrontend/
│   ├── src/
│   ├── package.json
│   └── Dockerfile
├── docker-compose-microservices.yml
├── init.sql
├── *.yaml  (Kubernetes manifests)
└── COMPLETE_DEPLOYMENT_GUIDE.md
```

---

## 🆘 Troubleshooting

### Services not registering with Eureka?
```bash
# Check Eureka is running
curl http://localhost:8761/actuator/health

# Check service logs
docker-compose -f docker-compose-microservices.yml logs auth-service | grep -i eureka
```

### Database connection errors?
```bash
# Verify PostgreSQL is running
docker-compose -f docker-compose-microservices.yml logs postgres

# Check credentials
docker-compose -f docker-compose-microservices.yml exec postgres \
  psql -U indichess_user -c "\l"
```

### Port already in use?
```bash
# Windows: Find and kill process
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac: Find and kill process
lsof -i :8080
kill -9 <PID>
```

### WebSocket not connecting?
```bash
# Check notification service is running
curl http://localhost:8085/actuator/health

# Verify WebSocket endpoint
wscat -c ws://localhost:8085/ws-notifications
```

---

## 📚 Key Features

✅ **Service Discovery** - Eureka registry  
✅ **Real-time Updates** - WebSocket notifications  
✅ **Auto-scaling** - Kubernetes HPA  
✅ **JWT Security** - Token-based auth  
✅ **Database per Service** - Data isolation  
✅ **Caching Layer** - Redis cache  
✅ **Load Balancing** - API Gateway  
✅ **Health Checks** - Liveness & readiness probes  
✅ **Monitoring** - Actuator endpoints & Prometheus  
✅ **Containerization** - Docker & Docker Compose  
✅ **Orchestration** - Kubernetes manifests  

---

## 🎯 What's Next?

1. **Add Message Broker** - Kafka/RabbitMQ for async events
2. **Distributed Tracing** - Jaeger for request tracing
3. **Monitoring Stack** - Prometheus + Grafana
4. **Service Mesh** - Istio for advanced traffic management
5. **API Documentation** - Swagger/OpenAPI
6. **CI/CD Pipeline** - GitHub Actions / Jenkins
7. **Load Testing** - JMeter / k6 for performance testing

---

## 📞 Support

All services run on Kubernetes-friendly architecture with:
- Health endpoints: `/actuator/health`
- Metrics endpoints: `/actuator/prometheus`
- Ready probes for graceful startup
- Liveness probes for crash recovery

---

**Your complete microservices ecosystem is ready! 🚀**

For detailed documentation, see: **COMPLETE_DEPLOYMENT_GUIDE.md**
