# Surf One PG

M-Pesa Payment Gateway that provisions Mikrotik hotspot access through RADIUS

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.2-green.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-336791.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Latest-2496ED.svg)](https://www.docker.com/)
[![Render](https://img.shields.io/badge/Render-Deployment-000000.svg)](https://render.com/)

## Overview

Surf One PG is a payment gateway system that:
- 💳 Integrates with **M-Pesa via KopoKopo** for payment processing
- 🔐 Provides **RADIUS provisioning** for WiFi access
- 📱 Works with **Mikrotik hotspots** for subscriber management
- 🌐 Deployed on **Render** with **Aiven PostgreSQL**
- 🚀 Uses **GitHub Actions** for CI/CD automation

## Key Features

- ✅ M-Pesa STK Push payment initiation
- ✅ Real-time payment status polling
- ✅ RADIUS user provisioning
- ✅ Automatic access expiry handling
- ✅ Webhook payment confirmations
- ✅ Data package management
- ✅ Production-ready with PostgreSQL
- ✅ Docker containerization
- ✅ Comprehensive logging and monitoring

## Quick Start

### Option 1: Local Development (Docker Compose)

```bash
# Start PostgreSQL + Application
docker-compose up -d

# Application available at http://localhost:8080

# View logs
docker-compose logs -f

# Stop
docker-compose down
```

See [DOCKER_GUIDE.md](./DOCKER_GUIDE.md) for detailed Docker instructions.

### Option 2: Local Development (Spring Boot)

```bash
# Start MySQL locally, then:
mvn clean compile
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Option 3: Production Deployment (Render + Aiven)

See [DEPLOYMENT_CHECKLIST.md](./DEPLOYMENT_CHECKLIST.md) for step-by-step setup.

## Project Structure

```
src/
├── main/java/com/surfonepg/
│   ├── controller/          # REST API endpoints
│   ├── dto/                 # Request/Response objects
│   ├── config/              # Spring configuration
│   ├── kopokopo/            # M-Pesa integration
│   ├── packages/            # Data package management
│   ├── radius/              # RADIUS provisioning
│   ├── transaction/         # Payment transaction handling
│   └── webhook/             # Webhook processing
└── resources/
    ├── application.yml      # Default configuration
    ├── application-prod.yml # Production configuration
    └── db/migration/        # Flyway database migrations
```

## API Endpoints

### Packages
- `GET /api/v1/packages` - List active data packages

### Payments
- `POST /api/v1/payments` - Initiate payment (M-Pesa STK push)
- `GET /api/v1/payments/{merchantReference}` - Check payment status

### Webhooks
- `POST /api/v1/webhooks/kopokopo` - KopoKopo payment callback

See [Postman Collection](./Surf_One_PG_API.postman_collection.json) for detailed examples.

## Configuration

### Environment Variables

#### Database
```
SPRING_DATASOURCE_URL=jdbc:postgresql://[host]:[port]/surfonepg
SPRING_DATASOURCE_USERNAME=surfonepg
SPRING_DATASOURCE_PASSWORD=your-password
SPRING_PROFILES_ACTIVE=prod
```

#### KopoKopo
```
KOPOKOPO_BASE_URL=https://api.kopokopo.com
KOPOKOPO_CLIENT_ID=your-client-id
KOPOKOPO_CLIENT_SECRET=your-client-secret
KOPOKOPO_TILL_NUMBER=your-till-number
KOPOKOPO_CALLBACK_URL=https://your-domain/api/v1/webhooks/kopokopo
KOPOKOPO_API_WEBHOOK_SECRET=your-webhook-secret
```

#### Application
```
NAS_IDENTIFIER=mikrotik-hotspot-01
PAYMENT_TIMEOUT_MINUTES=5
```

## Database

### Supported Databases
- **MySQL 8.0** (Production - via Aiven) ✅ Recommended
- **PostgreSQL** (Alternative)

### Migrations
Database migrations are handled by Flyway:
- `V1__radius_schema.sql` - FreeRADIUS base tables (MySQL)
- `V2__surfonepg_tables.sql` - Application-specific tables (MySQL)
- `V3__fix_radcheck_radreply_op_columns.sql` - Schema corrections (MySQL)
- `V2__postgresql_radius_schema.sql` - PostgreSQL alternative (skipped if using MySQL)

For MySQL configuration details, see [MYSQL_CONFIGURATION.md](./MYSQL_CONFIGURATION.md)

## Deployment

### Local Testing with Docker
```bash
docker-compose up -d
```

### Production Deployment to Render

**Quick Start:**
1. Follow [DEPLOYMENT_CHECKLIST.md](./DEPLOYMENT_CHECKLIST.md)
2. Create Render service with `render.yaml`
3. Set GitHub secrets (RENDER_API_KEY, RENDER_SERVICE_ID)
4. Push to main branch

**Automated:**
- GitHub Actions automatically builds and deploys
- Docker image built and pushed to registry
- Render pulls and runs latest image

**Detailed Setup:**
See [DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md) for comprehensive instructions.

## Technology Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 17 LTS |
| Framework | Spring Boot 3.3.2 |
| Build | Maven 3.9+ |
| Database | PostgreSQL 15 |
| Container | Docker |
| Orchestration | Docker Compose |
| CI/CD | GitHub Actions |
| Deployment | Render |
| Payment Gateway | KopoKopo (M-Pesa) |
| Authentication | RADIUS |

## Building

### Build with Maven
```bash
mvn clean package
```

### Build Docker Image
```bash
docker build -t surf-one-pg:latest .
```

## Testing

### Run Tests
```bash
mvn test
```

### Test API Endpoints
```bash
# Get packages
curl http://localhost:8080/api/v1/packages

# Initiate payment
curl -X POST http://localhost:8080/api/v1/payments \
  -H "Content-Type: application/json" \
  -d '{"phoneNumber":"0712345678","packageCode":"STARTER_500MB"}'

# Check status
curl http://localhost:8080/api/v1/payments/TXN-xxxxx
```

### Postman Testing
Import [Surf_One_PG_API.postman_collection.json](./Surf_One_PG_API.postman_collection.json)

## Documentation

| Document | Purpose |
|----------|---------|
| [SETUP_SUMMARY.md](./SETUP_SUMMARY.md) | Overview of setup files and architecture |
| [DEPLOYMENT_CHECKLIST.md](./DEPLOYMENT_CHECKLIST.md) | Step-by-step setup checklist |
| [DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md) | Comprehensive deployment guide |
| [DOCKER_GUIDE.md](./DOCKER_GUIDE.md) | Docker and Docker Compose usage |
| [POSTMAN_COLLECTION_README.md](./POSTMAN_COLLECTION_README.md) | API testing guide |

## GitHub Actions CI/CD

**Workflow**: `.github/workflows/build-deploy.yml`

**Triggers**:
- Push to `main` branch
- Push to `develop` branch
- Pull requests to `main`

**Steps**:
1. Build with Maven
2. Run tests
3. Build Docker image
4. Push to GitHub Container Registry
5. Deploy to Render

**Secrets Required**:
- `RENDER_API_KEY`
- `RENDER_SERVICE_ID`

## Monitoring & Logs

### Render Logs
```
Render Dashboard → Services → Your Service → Logs
```

### Docker Compose Logs
```bash
docker-compose logs -f
docker-compose logs -f app
docker-compose logs -f postgres
```

### Application Health
```bash
curl http://localhost:8080/actuator/health
```

## Troubleshooting

### Common Issues

**Build Fails**
- Check Maven output
- Verify Java 17 installed
- Run `mvn clean compile`

**Database Connection**
- Verify PostgreSQL running
- Check connection string
- Verify credentials
- Check firewall rules

**API Not Working**
- Check application logs
- Verify database migrations completed
- Test endpoint with curl/Postman
- Check network connectivity

**Webhook Signature Invalid**
- Verify webhook secret
- Check signature generation
- Review KopoKopo logs

See [DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md) for detailed troubleshooting.

## Contributing

1. Create feature branch: `git checkout -b feature/name`
2. Commit changes: `git commit -am "Add feature"`
3. Push to branch: `git push origin feature/name`
4. Submit pull request

## License

Private project - All rights reserved

## Support

- **Documentation**: See documentation files above
- **Issues**: GitHub Issues
- **Questions**: Check existing documentation first

## Roadmap

- [ ] Admin dashboard for package management
- [ ] Real-time analytics and reporting
- [ ] SMS notifications for customers
- [ ] Multiple payment gateway support
- [ ] Advanced RADIUS attributes
- [ ] Custom branding support
- [ ] Multi-tenant support

## Quick Links

- **Render Deployment**: https://render.com
- **Aiven Database**: https://aiven.io
- **KopoKopo API**: https://kopokopo.com
- **Spring Boot**: https://spring.io/projects/spring-boot
- **Docker**: https://www.docker.com

---

**Last Updated**: July 30, 2026
**Version**: 1.0.0
**Status**: Production Ready ✅
