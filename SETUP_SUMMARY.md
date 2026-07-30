# GitHub Actions + Render + Aiven Setup - Summary

This document summarizes all files created for deploying the Surf One PG application to Render with Aiven PostgreSQL using GitHub Actions CI/CD.

## Files Created

### GitHub Actions Workflow
**File**: `.github/workflows/build-deploy.yml`
- **Purpose**: Automated CI/CD pipeline
- **Triggers**: Push to main/develop branches
- **Steps**:
  1. Checkout code
  2. Set up Java 17
  3. Build with Maven
  4. Run tests
  5. Build Docker image
  6. Push to container registry
  7. Deploy to Render
- **Secrets Required**: `RENDER_API_KEY`, `RENDER_SERVICE_ID`

### Docker Configuration
**File**: `Dockerfile`
- **Purpose**: Build container image for deployment
- **Base Image**: eclipse-temurin:17-jre-alpine (minimal, production-ready)
- **Features**:
  - Multi-stage build (reduces image size)
  - Health checks included
  - Runs JAR file directly
  - Exposes port 8080

### Docker Compose (Local Development)
**File**: `docker-compose.yml`
- **Purpose**: Run complete stack locally with PostgreSQL
- **Services**:
  - PostgreSQL 15 Alpine
  - Spring Boot application
- **Features**:
  - Health checks
  - Volume persistence for database
  - Network isolation
  - Environment variables for testing

### Render Configuration
**File**: `render.yaml`
- **Purpose**: Infrastructure as Code for Render deployment
- **Defines**:
  - Web service configuration
  - Docker build settings
  - PostgreSQL database (optional)
  - All environment variables
  - Health check settings

### Production Configuration
**File**: `src/main/resources/application-prod.yml`
- **Purpose**: Spring Boot production profile
- **Includes**:
  - PostgreSQL database settings
  - Connection pooling (HikariCP)
  - KopoKopo configuration
  - Logging levels
  - Actuator endpoints

### PostgreSQL Migrations
**File**: `src/main/resources/db/migration/V2__postgresql_radius_schema.sql`
- **Purpose**: Database schema for PostgreSQL
- **Tables**:
  - radcheck, radreply, radusergroup, radacct, nas
- **Indexes**: For optimal query performance

### Documentation

#### 1. **Deployment Guide**
**File**: `DEPLOYMENT_GUIDE.md`
- **Length**: ~500 lines
- **Contents**:
  - Step-by-step setup instructions
  - Database configuration (Aiven or Render)
  - Environment variables reference
  - Troubleshooting guide
  - Monitoring and logging
  - Security best practices
  - Useful commands

#### 2. **Deployment Checklist**
**File**: `DEPLOYMENT_CHECKLIST.md`
- **Purpose**: Checkoff list for completing setup
- **Phases**:
  1. Account Setup
  2. Database Setup
  3. GitHub Secrets
  4. Render Service
  5. Environment Variables
  6. Actions Verification
  7. First Deployment
  8. Verification
  9. Post-Deployment
  10. Maintenance
- **Includes**: Quick command reference

#### 3. **Docker Guide**
**File**: `DOCKER_GUIDE.md`
- **Purpose**: Local development with Docker
- **Sections**:
  - Docker Compose quick start
  - Advanced Docker usage
  - Container management commands
  - PostgreSQL access
  - Troubleshooting
  - Production comparison

## Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                   GitHub Repository                      │
│  • Source code                                           │
│  • Dockerfile                                            │
│  • .github/workflows/build-deploy.yml                    │
└────────────────┬────────────────────────────────────────┘
                 │ (push to main/develop)
                 ▼
┌─────────────────────────────────────────────────────────┐
│               GitHub Actions (CI/CD)                     │
│  • Build with Maven                                      │
│  • Run Tests                                             │
│  • Build Docker Image                                    │
│  • Push to GHCR                                          │
└────────────────┬────────────────────────────────────────┘
                 │ (trigger deployment)
                 ▼
┌─────────────────────────────────────────────────────────┐
│          Render (Container Platform)                    │
│  • Pull Docker Image                                    │
│  • Run Spring Boot Application                          │
│  • Manage scaling and monitoring                         │
└────────────────┬────────────────────────────────────────┘
                 │ (connects to)
                 ▼
┌─────────────────────────────────────────────────────────┐
│   Aiven PostgreSQL (Database as a Service)             │
│  • surfonepg database                                   │
│  • Automated backups                                    │
│  • SSL encrypted connections                            │
└─────────────────────────────────────────────────────────┘
```

## Deployment Flow

1. **Local Development**
   - Developer makes changes locally
   - Tests with `docker-compose up`
   - Commits and pushes to GitHub

2. **GitHub Actions Triggers**
   - Workflow automatically runs
   - Maven builds the application
   - Tests execute
   - Docker image builds

3. **Container Registry**
   - Image pushed to GHCR (GitHub Container Registry)
   - Versioning via git tags

4. **Render Deployment**
   - Render detects new image
   - Pulls and runs on platform
   - Performs health checks
   - Serves traffic via HTTPS

5. **Database Operations**
   - Application connects to Aiven
   - Flyway migrations run
   - Database is ready

## Environment Variables Reference

### Database (Production)
```
SPRING_DATASOURCE_URL          PostgreSQL connection string
SPRING_DATASOURCE_USERNAME     Database user
SPRING_DATASOURCE_PASSWORD     Database password
SPRING_JPA_HIBERNATE_DDL_AUTO  validate (production)
SPRING_PROFILES_ACTIVE         prod
```

### KopoKopo
```
KOPOKOPO_BASE_URL              API endpoint (sandbox/production)
KOPOKOPO_CLIENT_ID             OAuth client ID
KOPOKOPO_CLIENT_SECRET         OAuth secret
KOPOKOPO_TILL_NUMBER           Merchant till number
KOPOKOPO_CALLBACK_URL          Webhook receiver URL
KOPOKOPO_API_WEBHOOK_SECRET    Signature verification secret
```

### Application
```
NAS_IDENTIFIER                 Mikrotik device identifier
PAYMENT_TIMEOUT_MINUTES        Payment expiry time
LOGGING_LEVEL_*                Log level configuration
```

## GitHub Secrets Required

Create these in GitHub Settings → Secrets → Actions:

| Secret | Description | Where to Get |
|--------|-------------|--------------|
| `RENDER_API_KEY` | Render API authentication | Render Dashboard → Account → API Keys |
| `RENDER_SERVICE_ID` | Render service identifier | Displayed after creating service (srv-xxxxx) |

## Getting Started (Quick Steps)

1. **Prepare Accounts**
   ```bash
   # 1. Ensure GitHub, Render, Aiven accounts are ready
   # 2. Get KopoKopo credentials
   ```

2. **Add GitHub Secrets**
   - RENDER_API_KEY
   - RENDER_SERVICE_ID

3. **Create Render Service**
   - Connect GitHub repository
   - Select branch (main/develop)
   - Render uses render.yaml automatically

4. **Configure Environment Variables**
   - Set database connection (Aiven or Render)
   - Set KopoKopo credentials
   - Set application settings

5. **Deploy**
   - Push to main branch
   - GitHub Actions automatically builds and deploys
   - Monitor Render logs

6. **Verify**
   - Test API endpoints
   - Check database
   - Monitor in Render dashboard

## Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Java | 17 LTS |
| Framework | Spring Boot | 3.3.2 |
| Build | Maven | 3.9+ |
| Database | PostgreSQL | 15 |
| Container | Docker | Latest |
| Container OS | Alpine Linux | Latest |
| CI/CD | GitHub Actions | Latest |
| Platform | Render | Latest |
| Database Service | Aiven | Latest |

## File Structure

```
Surf_One_PG/
├── .github/
│   └── workflows/
│       └── build-deploy.yml           # GitHub Actions workflow
├── src/
│   └── main/
│       └── resources/
│           ├── application.yml        # Default config
│           ├── application-prod.yml   # Production config
│           └── db/migration/
│               ├── V1__*              # MySQL migrations
│               ├── V2__*              # PostgreSQL migrations
│               └── V3__*              # Additional migrations
├── Dockerfile                         # Container build
├── docker-compose.yml                 # Local dev setup
├── render.yaml                        # Render deployment config
├── DEPLOYMENT_GUIDE.md                # Detailed setup guide
├── DEPLOYMENT_CHECKLIST.md            # Quick checklist
├── DOCKER_GUIDE.md                    # Docker usage guide
├── pom.xml                            # Maven config
└── README.md                          # Project overview
```

## Benefits of This Setup

✅ **Automated Deployment** - No manual steps after push
✅ **Production-Ready** - Uses same stack locally as production
✅ **Database as Service** - Aiven handles backups, scaling
✅ **Containerized** - Consistent environment everywhere
✅ **Scalable** - Render auto-scales based on traffic
✅ **Monitored** - Built-in health checks and logging
✅ **Documented** - Comprehensive guides for all operations
✅ **Secure** - Environment variables, HTTPS, database encryption

## Common Operations

### Deploy Update
```bash
git commit -am "feature: add new endpoint"
git push origin main
# Automatically deploys via GitHub Actions
```

### View Logs
```bash
# GitHub Actions
# https://github.com/[user]/Surf_One_PG/actions

# Render
# https://dashboard.render.com → Services → Logs
```

### Rollback Deployment
```
Render Dashboard → Deploys → Select previous → Redeploy
```

### Scale Application
```
Render Dashboard → Instance Type → Upgrade to higher tier
```

## Monitoring and Alerts

Recommended setup:
- Render: Enable error notifications
- Aiven: Set up backup alerts
- GitHub: Action notifications on failures
- Application: Use Render's log viewer

## Security Considerations

1. ✅ Secrets stored in GitHub (not in code)
2. ✅ Database encrypted in transit and at rest (Aiven)
3. ✅ HTTPS enforced by Render
4. ✅ Docker image scanned for vulnerabilities
5. ✅ Regular dependency updates via Maven
6. ✅ Webhook signature verification
7. ✅ API key rotation strategy

## Support Resources

- **Deployment Guide**: `DEPLOYMENT_GUIDE.md` (detailed, troubleshooting)
- **Quick Checklist**: `DEPLOYMENT_CHECKLIST.md` (step-by-step)
- **Docker Guide**: `DOCKER_GUIDE.md` (local testing)
- **Render Docs**: https://render.com/docs
- **Aiven Docs**: https://aiven.io/docs
- **GitHub Actions**: https://docs.github.com/en/actions

## Next Steps

1. Follow `DEPLOYMENT_CHECKLIST.md` to complete setup
2. Test locally with `docker-compose.yml`
3. Set GitHub secrets
4. Create Render service
5. Configure environment variables
6. Trigger deployment
7. Monitor and maintain

---

**Setup Status**: Ready for Implementation
**Last Updated**: July 30, 2026
**Files Created**: 6 configuration files + 3 documentation files

