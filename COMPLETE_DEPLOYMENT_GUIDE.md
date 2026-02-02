# Complete Microservices Architecture & Deployment Guide

## Overview

This complete IndiChess microservices ecosystem includes:
- **7 Services**: API Gateway, Auth, User, Game, Matchmaking, Notification, and Eureka Server
- **Service Registry**: Eureka for dynamic service discovery
- **Real-time Communication**: WebSocket support for notifications
- **Auto-scaling**: Kubernetes HPA for elastic scaling
- **Security**: JWT-based authentication and RBAC

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                     Frontend (React)                             │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                    ┌──────────┴──────────┐
                    │                     │
              HTTP  │              WebSocket
                    │                     │
        ┌───────────▼──────────┐  ┌──────▼──────────────┐
        │   API Gateway        │  │ Notification Service│
        │   (8080)             │  │ (8085)              │
        │   - JWT Auth Filter  │  │ - WebSocket Handler │
        │   - Route to Services│  │ - Real-time Updates │
        └───────────┬──────────┘  └────────────────────┘
                    │
        ┌───────────┴──────────┬──────────┬──────────┐
        │                      │          │          │
    ┌───▼───┐  ┌───────┐  ┌───▼───┐  ┌──▼────┐ ┌──▼───────┐
    │ Auth  │  │ User  │  │ Game  │  │Match- │ │ Eureka   │
    │Service│  │Service│  │Service│  │making │ │ Server   │
    │(8081) │  │(8082) │  │(8083) │  │(8084) │ │ (8761)   │
    └───┬───┘  └───┬───┘  └───┬───┘  └──┬────┘ └──┬───────┘
        │          │          │         │        │
        └──────────┴──────────┴─────────┴────────┘
                   │
        ┌──────────┴──────────┐
        │                     │
    ┌───▼────┐         ┌──────▼──┐
    │PostgreSQL         │  Redis   │
    │(5432)            │  (6379)   │
    └────────┘         └──────────┘
```

## Services Overview

### 1. **Eureka Server (Port 8761)**
Service registry and discovery server.
- **Location**: `eureka-server/`
- **Key Endpoint**: `http://localhost:8761/` (Dashboard)
- **Replicas**: 1
- **Database**: None (in-memory registry)

### 2. **API Gateway (Port 8080)**
Central entry point for all client requests.
- **Location**: `api-gateway/`
- **Routes**:
  - `/api/auth/**` → Auth Service (8081)
  - `/api/users/**` → User Service (8082)
  - `/api/games/**` → Game Service (8083)
  - `/api/matchmaking/**` → Matchmaking Service (8084)
  - `/ws-notifications/**` → Notification Service (8085)
- **Features**: JWT validation, request routing
- **Replicas**: 1

### 3. **Auth Service (Port 8081)**
User authentication and JWT token management.
- **Location**: `auth-service/`
- **Endpoints**:
  - `POST /api/auth/register` - User registration
  - `POST /api/auth/login` - User login
  - `POST /api/auth/refresh` - Token refresh
  - `GET /api/auth/validate` - Token validation
- **Database**: `indichess_auth` (PostgreSQL)
- **Cache**: Redis (JWT token storage)
- **Replicas**: 2

### 4. **User Service (Port 8082)**
User profile and statistics management.
- **Location**: `user-service/`
- **Endpoints**:
  - `GET /api/users/{id}` - Get user profile
  - `PUT /api/users/{id}` - Update profile
  - `GET /api/users/leaderboard` - Get top players
  - `GET /api/users/stats` - Get user statistics
- **Database**: `indichess_user` (PostgreSQL)
- **Replicas**: 2

### 5. **Game Service (Port 8083)**
Game creation, state management, and move validation.
- **Location**: `game-service/`
- **Endpoints**:
  - `POST /api/games` - Create new game
  - `GET /api/games/{id}` - Get game details
  - `POST /api/games/{id}/move` - Make a move
  - `GET /api/games/{id}/history` - Get move history
- **Database**: `indichess_game` (PostgreSQL)
- **Replicas**: 2

### 6. **Matchmaking Service (Port 8084)**
Player matching and queue management.
- **Location**: `matchmaking-service/`
- **Endpoints**:
  - `POST /api/matchmaking/join` - Join queue
  - `POST /api/matchmaking/leave` - Leave queue
  - `GET /api/matchmaking/status/{userId}` - Check queue status
- **Database**: `indichess_matchmaking` (PostgreSQL)
- **Features**: Skill-based matching, time control selection
- **Auto-matching**: Runs every 5 seconds
- **Replicas**: 2

### 7. **Notification Service (Port 8085)**
Real-time notifications via WebSocket.
- **Location**: `notification-service/`
- **Endpoints**:
  - `WS /ws-notifications` - WebSocket connection
  - `POST /api/notifications/send` - Send notification
- **Topics**:
  - `/topic/notifications/{userId}` - Per-user notifications
- **Message Types**:
  - `GAME_INVITE` - Player invitation
  - `MATCH_FOUND` - Matchmaking found a match
  - `GAME_START` - Game has started
  - `GAME_END` - Game ended
  - `CHAT_MESSAGE` - In-game chat
- **Replicas**: 2

## Local Development Setup

### Prerequisites
- Java 21
- Docker & Docker Compose
- PostgreSQL 16 (for Docker)
- Maven 3.9+
- Node.js 18+ (for frontend)

### 1. Build All Services

```bash
cd eureka-server
mvn clean package
cd ../api-gateway
mvn clean package
cd ../auth-service
mvn clean package
cd ../user-service
mvn clean package
cd ../game-service
mvn clean package
cd ../matchmaking-service
mvn clean package
cd ../notification-service
mvn clean package
cd ../indichessfrontend
npm install && npm run build
```

### 2. Run with Docker Compose

```bash
# Navigate to workspace root
cd /c/Users/Admin/Downloads/MicroServices

# Start all services
docker-compose -f docker-compose-microservices.yml up -d

# Check logs
docker-compose -f docker-compose-microservices.yml logs -f

# Stop services
docker-compose -f docker-compose-microservices.yml down
```

### 3. Verify Services

```bash
# Eureka Dashboard
curl http://localhost:8761

# API Gateway health
curl http://localhost:8080/actuator/health

# Auth Service health
curl http://localhost:8081/actuator/health

# User Service health
curl http://localhost:8082/actuator/health

# Game Service health
curl http://localhost:8083/actuator/health

# Matchmaking Service health
curl http://localhost:8084/actuator/health

# Notification Service health
curl http://localhost:8085/actuator/health
```

## Kubernetes Deployment

### Prerequisites
- Kubernetes 1.27+
- kubectl configured
- Docker registry access (for image push)

### 1. Build and Push Docker Images

```bash
# Build images
for service in eureka-server api-gateway auth-service user-service game-service matchmaking-service notification-service; do
  cd $service
  docker build -t indichess/$service:latest .
  docker push indichess/$service:latest
  cd ..
done

# Build frontend
cd indichessfrontend
docker build -t indichess/frontend:latest .
docker push indichess/frontend:latest
cd ..
```

### 2. Deploy to Kubernetes

```bash
# Create namespace
kubectl create namespace indichess

# Create secrets
kubectl create secret generic postgres-credentials \
  --from-literal=username=indichess_user \
  --from-literal=password=secure_password_change_me \
  -n indichess

# Deploy services (in order)
kubectl apply -f 00-namespaces.yaml
kubectl apply -f 01-rbac.yaml
kubectl apply -f 02-postgres-pvc.yaml
kubectl apply -f 03-postgres.yaml
kubectl apply -f 04-redis-pvc.yaml
kubectl apply -f 05-redis.yaml
kubectl apply -f 13-eureka-server.yaml

# Wait for Eureka to be ready (60 seconds)
sleep 60

kubectl apply -f 06-api-gateway.yaml
kubectl apply -f 07-auth-service.yaml
kubectl apply -f 08-user-service.yaml
kubectl apply -f 09-game-service.yaml
kubectl apply -f 14-matchmaking-service.yaml
kubectl apply -f 15-notification-service.yaml
kubectl apply -f 10-ingress.yaml
kubectl apply -f 12-frontend.yaml

# Check deployment status
kubectl get pods -n indichess
kubectl get svc -n indichess

# Scale services as needed
kubectl scale deployment/auth-service --replicas=3 -n indichess
kubectl scale deployment/user-service --replicas=3 -n indichess
kubectl scale deployment/game-service --replicas=3 -n indichess
```

## API Usage Examples

### 1. Register User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "chessplayer",
    "email": "player@example.com",
    "password": "password123"
  }'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "chessplayer",
    "password": "password123"
  }'
# Response: { "token": "eyJhbGc..." }
```

### 3. Get User Profile
```bash
TOKEN="your_jwt_token_here"
curl -X GET http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer $TOKEN"
```

### 4. Join Matchmaking Queue
```bash
TOKEN="your_jwt_token_here"
curl -X POST http://localhost:8080/api/matchmaking/join \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "timeControl": "rapid",
    "skillLevel": 1200
  }'
```

### 5. Create New Game
```bash
TOKEN="your_jwt_token_here"
curl -X POST http://localhost:8080/api/games \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "whitePlayerId": 1,
    "blackPlayerId": 2
  }'
```

### 6. WebSocket Notifications
```bash
# Using wscat or similar WebSocket client
wscat -c "ws://localhost:8085/ws-notifications"

# Subscribe to user notifications
# Message format: {"type": "GAME_INVITE", "message": "...", ...}
```

## Monitoring & Logging

### Health Checks
All services expose health endpoints:
```bash
curl http://localhost:8080/actuator/health  # API Gateway
curl http://localhost:8081/actuator/health  # Auth Service
curl http://localhost:8082/actuator/health  # User Service
curl http://localhost:8083/actuator/health  # Game Service
curl http://localhost:8084/actuator/health  # Matchmaking Service
curl http://localhost:8085/actuator/health  # Notification Service
```

### Metrics
Access Prometheus metrics at:
- `http://localhost:8080/actuator/prometheus`
- `http://localhost:8081/actuator/prometheus`
- etc.

### Logs
```bash
# Docker Compose
docker-compose -f docker-compose-microservices.yml logs -f [service-name]

# Kubernetes
kubectl logs -f deployment/api-gateway -n indichess
kubectl logs -f deployment/auth-service -n indichess
# etc.
```

## Database Schema

### Auth Service Database (`indichess_auth`)
```sql
Table: users
- id (BIGINT, PRIMARY KEY)
- username (VARCHAR(50), UNIQUE)
- email (VARCHAR(100), UNIQUE)
- password (VARCHAR(255))
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)
```

### User Service Database (`indichess_user`)
```sql
Table: user_profiles
- id (BIGINT, PRIMARY KEY)
- user_id (BIGINT, UNIQUE)
- display_name (VARCHAR(100))
- bio (TEXT)
- avatar_url (VARCHAR(500))
- elo_rating (INTEGER)
- wins, losses, draws, total_games (INTEGER)
- created_at, updated_at (TIMESTAMP)
```

### Game Service Database (`indichess_game`)
```sql
Table: games
- id (BIGINT, PRIMARY KEY)
- white_player_id, black_player_id (BIGINT)
- status (VARCHAR(20))
- result (VARCHAR(20))
- created_at, updated_at (TIMESTAMP)

Table: moves
- id (BIGINT, PRIMARY KEY)
- game_id (BIGINT)
- from_square, to_square (VARCHAR(2))
- piece_type (VARCHAR(10))
- created_at (TIMESTAMP)

Table: game_pgn
- id (BIGINT, PRIMARY KEY)
- game_id (BIGINT)
- pgn_text, fen_position (TEXT/VARCHAR)
```

### Matchmaking Service Database (`indichess_matchmaking`)
```sql
Table: match_queue
- id (BIGINT, PRIMARY KEY)
- user_id (BIGINT)
- status (VARCHAR(20)) - WAITING, MATCHED, CANCELLED, COMPLETED
- skill_level (INTEGER)
- time_control (VARCHAR(20))
- joined_at, matched_at (TIMESTAMP)
- matched_opponent_id, game_id (BIGINT)
```

## Configuration & Environment Variables

### Application Configuration
Each service reads from `application.properties`:

**Common Properties:**
```properties
spring.application.name=service-name
server.port=PORT
eureka.client.serviceUrl.defaultZone=http://eureka-server:8761/eureka/
eureka.instance.preferIpAddress=true
```

**Database Configuration:**
```properties
spring.datasource.url=jdbc:postgresql://postgres:5432/database_name
spring.datasource.username=indichess_user
spring.datasource.password=secure_password_change_me
spring.jpa.hibernate.ddl-auto=update
```

**Redis Configuration:**
```properties
spring.redis.host=redis
spring.redis.port=6379
```

## Troubleshooting

### Service Won't Register with Eureka
1. Check Eureka is running: `curl http://localhost:8761`
2. Verify service URL in `application.properties`
3. Check logs for connection errors
4. Ensure firewall allows port 8761

### Database Connection Issues
1. Verify PostgreSQL is running
2. Check connection string in `application.properties`
3. Verify credentials match Docker Compose environment
4. Check database exists: `psql -U indichess_user -l`

### Port Already in Use
```bash
# Windows
netstat -ano | findstr :PORT
taskkill /PID PID_NUMBER /F

# Linux/Mac
lsof -i :PORT
kill -9 PID
```

### WebSocket Connection Issues
1. Ensure notification service is running on port 8085
2. Check frontend WebSocket URL: `ws://localhost:8085/ws-notifications`
3. Verify CORS is enabled in notification service

## Performance Tuning

### Auto-Scaling Configuration
Kubernetes HPA scales based on:
- CPU: 70% average utilization
- Memory: 80% average utilization
- Min replicas: 2
- Max replicas: 5

### Database Optimization
```sql
-- Add indexes for frequently queried columns
CREATE INDEX idx_user_elo ON indichess_user.user_profiles(elo_rating);
CREATE INDEX idx_game_status ON indichess_game.games(status);
CREATE INDEX idx_match_queue_user ON indichess_matchmaking.match_queue(user_id);
```

### Redis Configuration
- Session timeout: 24 hours
- Token cache TTL: Matches JWT expiration
- Memory limit: 512MB

## Security Considerations

1. **JWT Secret**: Change `JWT_SECRET` in production
2. **Database Password**: Change `POSTGRES_PASSWORD` in Docker Compose
3. **HTTPS**: Use ingress with TLS certificates in production
4. **RBAC**: Services run under `indichess-service-account`
5. **Network Policies**: Restrict inter-service communication if needed

## Deployment Checklist

- [ ] All services built and tested locally
- [ ] Docker images built and pushed to registry
- [ ] Kubernetes cluster available
- [ ] Namespace created
- [ ] Secrets configured (Postgres credentials)
- [ ] PVs and PVCs created for databases
- [ ] Eureka Server deployed first
- [ ] Dependent services deployed
- [ ] Ingress configured with TLS
- [ ] Health checks passing
- [ ] Monitoring and logging configured
- [ ] Auto-scaling policies applied

## Next Steps

1. **Add Message Broker**: Integrate Kafka/RabbitMQ for async communication
2. **Implement Event Sourcing**: Track all game events
3. **Add Caching Layer**: Redis cache for frequently accessed data
4. **API Documentation**: Generate Swagger/OpenAPI docs
5. **CI/CD Pipeline**: GitHub Actions or Jenkins integration
6. **Distributed Tracing**: Add Jaeger for request tracing
7. **Service Mesh**: Implement Istio for advanced traffic management

## Support & Documentation

- **Spring Boot**: https://spring.io/projects/spring-boot
- **Spring Cloud**: https://spring.io/projects/spring-cloud
- **Kubernetes**: https://kubernetes.io/docs/
- **Docker**: https://docs.docker.com/
- **PostgreSQL**: https://www.postgresql.org/docs/

---

**Created for**: Academic/Professional IndiChess Microservices Project
**Date**: 2024
**Architecture**: Microservices + Kubernetes + Service Registry
