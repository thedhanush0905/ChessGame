# IndiChess Microservices Architecture + Kubernetes Deployment

## 🏗️ Architecture Overview

### Microservices Breakdown

```
┌─────────────────────────────────────────────────────────────┐
│                  FRONTEND (React)                           │
│              (Kubernetes Service: frontend)                 │
└──────────────────────────┬──────────────────────────────────┘
                           │
        ┌──────────────────┴──────────────────┐
        │                                     │
┌───────▼─────────────────┐      ┌───────────▼──────────┐
│   API GATEWAY           │      │  WebSocket Gateway   │
│ (Spring Cloud Gateway)  │      │ (WebSocket Service)  │
│ Port: 8080              │      │ Port: 8081           │
└───────┬─────────────────┘      └───────────┬──────────┘
        │                                     │
    ┌───┴───────────────┬───────────────┬────┴──────┐
    │                   │               │           │
┌───▼─────┐    ┌───────▼──┐   ┌──────▼───┐  ┌────▼──────┐
│ Auth    │    │  User    │   │  Game    │  │  Move    │
│ Service │    │ Service  │   │ Service  │  │ Service  │
│ :8081   │    │  :8082   │   │  :8083   │  │  :8084   │
└─────────┘    └──────────┘   └──────────┘  └──────────┘
     │              │              │            │
     └──────────────┴──────────────┴────────────┘
            Shared PostgreSQL Database
            (Multi-schema or separate DBs)
```

### Microservices Components

#### 1. **API Gateway** (Spring Cloud Gateway)
- Routes requests to appropriate microservices
- Handles authentication token validation
- Rate limiting and load balancing
- CORS handling
- Port: 8080

#### 2. **Auth Service**
- User login/registration
- JWT token generation
- Token validation
- Password management
- Replicas: 2
- Port: 8081

#### 3. **User Service**
- User profile management
- User statistics
- Leaderboard
- User preferences
- Replicas: 2
- Port: 8082

#### 4. **Game Service**
- Game creation and management
- Game state management
- Game history
- Replicas: 3
- Port: 8083

#### 5. **Move Service**
- Chess move validation
- Move storage
- Move suggestions/analysis
- Replicas: 2
- Port: 8084

#### 6. **WebSocket Service**
- Real-time move notifications
- Live game updates
- Chat functionality
- Replicas: 2
- Port: 8085

### Database Strategy

- **PostgreSQL** as primary database
- **Redis** for caching and sessions
- **Separate schemas** for each service or separate databases for complete isolation
- **Database migrations** per service

### Technologies Stack

**Backend:**
- Spring Boot 4.0.1
- Spring Cloud (Gateway, Config, Discovery)
- Spring Security with JWT
- Spring Data JPA
- PostgreSQL
- Redis
- RabbitMQ (for inter-service communication)

**Frontend:**
- React 19.x
- Axios for HTTP
- SockJS/STOMP for WebSocket

**Infrastructure:**
- Kubernetes 1.27+
- Docker
- Helm (optional, for package management)
- Nginx Ingress Controller
- Prometheus & Grafana (monitoring)

## 📦 Deployment Architecture

### Kubernetes Cluster Structure

```
Kubernetes Cluster
├── default Namespace
│   ├── Deployments (Services)
│   ├── Services (ClusterIP, LoadBalancer)
│   ├── ConfigMaps (Configuration)
│   ├── Secrets (Credentials)
│   ├── PersistentVolumeClaims (Database, Redis)
│   └── StatefulSets (PostgreSQL, Redis)
│
├── monitoring Namespace
│   ├── Prometheus
│   └── Grafana
│
└── ingress-nginx Namespace
    └── Nginx Ingress Controller
```

### Storage & Persistence

- **PostgreSQL StatefulSet** with PersistentVolume (10GB)
- **Redis StatefulSet** with PersistentVolume (2GB)
- **ConfigMaps** for application properties
- **Secrets** for sensitive data (DB credentials, JWT secret, API keys)

## 🔄 Inter-Service Communication

- **Synchronous**: REST APIs via API Gateway
- **Asynchronous**: RabbitMQ for events (game events, user notifications)
- **Caching**: Redis for session management and frequent queries

## 🔐 Security Features

- JWT Token-based authentication
- Role-based access control (RBAC)
- API Gateway authentication filter
- Kubernetes network policies
- TLS/SSL for HTTPS (via Ingress)
- Secrets management for credentials

## 📊 Scaling & High Availability

- Horizontal Pod Autoscaling (HPA) based on CPU/Memory
- Load balancing across replicas
- Database connection pooling
- Redis for caching
- Multi-zone deployment ready

## 🚀 Deployment Steps

1. **Prepare Docker images** for each microservice
2. **Create Kubernetes manifests** (Deployments, Services, ConfigMaps, Secrets)
3. **Deploy infrastructure** (PostgreSQL, Redis)
4. **Deploy API Gateway**
5. **Deploy microservices**
6. **Setup Ingress** for external access
7. **Configure monitoring** (Prometheus, Grafana)
8. **Setup CI/CD** (GitHub Actions/Jenkins)

## 📁 Project Structure

```
MicroServices/
├── api-gateway/                 # Spring Cloud Gateway
├── auth-service/                # Authentication Service
├── user-service/                # User Management Service
├── game-service/                # Game Logic Service
├── move-service/                # Chess Move Validation Service
├── websocket-service/           # Real-time Communication
├── frontend/                    # React Frontend
├── k8s/                         # Kubernetes Manifests
│   ├── namespaces/
│   ├── storage/
│   ├── services/
│   ├── deployments/
│   ├── configmaps/
│   ├── secrets/
│   ├── ingress/
│   └── monitoring/
├── docker/                      # Dockerfile templates
├── scripts/                     # Deployment scripts
└── docs/                        # Documentation
```

## 🎯 Next Steps

1. ✅ Architecture Planning (COMPLETED)
2. ⏳ Refactor monolithic backend into microservices
3. ⏳ Create Kubernetes manifests
4. ⏳ Update frontend API endpoints
5. ⏳ Setup local Kubernetes development environment
6. ⏳ Deploy to cloud provider (AWS/GCP/Azure)
