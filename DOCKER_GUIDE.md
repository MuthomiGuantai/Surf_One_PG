# Docker & Docker Compose Guide

This guide explains how to run the Surf One PG application locally using Docker and PostgreSQL (same as production).

## Prerequisites

- Docker Desktop installed (https://www.docker.com/products/docker-desktop)
- Docker Compose (included with Docker Desktop)

## Quick Start - Docker Compose (Recommended)

### MySQL Setup (Production-like)

```bash
# Build and start MySQL and the application
docker-compose up --build

# Application will be available at: http://localhost:8080
# MySQL will be available at: localhost:3306
```

The `docker-compose.yml` is configured for **MySQL 8.0** to match your Aiven production setup.

**Database credentials:**
- Username: `surfonepg`
- Password: `surfonepg_dev_password`
- Database: `radius`

### Stop Everything
```bash
docker-compose down

# Also remove volumes (delete database)
docker-compose down -v
```

### View Logs
```bash
# View all logs
docker-compose logs

# Follow logs in real-time
docker-compose logs -f

# View logs for specific service
docker-compose logs -f app
docker-compose logs -f postgres
```

## Testing the Application

Once running with Docker Compose:

### 1. Get Available Packages
```bash
curl http://localhost:8080/api/v1/packages
```

### 2. Initiate a Payment
```bash
curl -X POST http://localhost:8080/api/v1/payments \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "0712345678",
    "packageCode": "STARTER_500MB"
  }'
```

### 3. Check Payment Status
```bash
# Use the merchantReference from previous request
curl http://localhost:8080/api/v1/payments/TXN-20260730-001234
```

### 4. Health Check
```bash
curl http://localhost:8080/actuator/health
```

## Advanced Docker Usage

### Building Docker Image Only
```bash
# Build the image
docker build -t surf-one-pg:latest .

# List images
docker images | grep surf-one-pg
```

### Running Container Manually (Without Compose)

#### Step 1: Start MySQL
```bash
docker run -d \
  --name surf-one-pg-db \
  -e MYSQL_ROOT_PASSWORD=root_password \
  -e MYSQL_DATABASE=radius \
  -e MYSQL_USER=surfonepg \
  -e MYSQL_PASSWORD=surfonepg_dev_password \
  -p 3306:3306 \
  mysql:8.0-alpine
```

#### Step 2: Wait for Database
```bash
# Check if database is ready
docker exec surf-one-pg-db mysqladmin ping -u surfonepg -p surfonepg_dev_password
```

#### Step 3: Run Application
```bash
docker run -d \
  --name surf-one-pg-app \
  -p 8080:8080 \
  --link surf-one-pg-db:mysql \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/radius?useSSL=false&serverTimezone=Africa/Nairobi \
  -e SPRING_DATASOURCE_USERNAME=surfonepg \
  -e SPRING_DATASOURCE_PASSWORD=surfonepg_dev_password \
  -e SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver \
  -e SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.MySQL8Dialect \
  -e SPRING_PROFILES_ACTIVE=prod \
  surf-one-pg:latest
```

### Container Management Commands

```bash
# List running containers
docker ps

# List all containers
docker ps -a

# Stop a container
docker stop surf-one-pg-app

# Start a stopped container
docker start surf-one-pg-app

# Remove a container
docker rm surf-one-pg-app

# View container logs
docker logs surf-one-pg-app

# Follow logs in real-time
docker logs -f surf-one-pg-app

# Execute command in running container
docker exec surf-one-pg-app curl http://localhost:8080/actuator/health

# Access container shell
docker exec -it surf-one-pg-app /bin/sh
```

## Environment Variables

You can customize the application by setting environment variables.

### Using Docker Compose
Create a `.env` file in the project root:
```
KOPOKOPO_CLIENT_ID=your-client-id
KOPOKOPO_CLIENT_SECRET=your-client-secret
KOPOKOPO_TILL_NUMBER=your-till-number
NAS_IDENTIFIER=your-nas-identifier
```

Then run:
```bash
docker-compose up
```

### Using Docker Command Line
```bash
docker run -d \
  -p 8080:8080 \
  -e KOPOKOPO_CLIENT_ID=your-id \
  -e KOPOKOPO_CLIENT_SECRET=your-secret \
  surf-one-pg:latest
```

## PostgreSQL Database Access

### Connect to MySQL from Host
```bash
# Install mysql client (if not available)
# macOS: brew install mysql-client
# Ubuntu: sudo apt-get install mysql-client
# Windows: Use MySQL Workbench or mysql from MySQL installer

# Connect to database
mysql -h localhost -u surfonepg -p radius
# Password: surfonepg_dev_password

# Useful commands once connected:
SHOW TABLES;              # List tables
DESCRIBE radcheck;        # Describe table structure
SELECT * FROM radcheck;   # Query table
EXIT                      # Quit
```

### Using pgAdmin GUI (Optional)
1. Add PostgreSQL service to docker-compose.yml:
```yaml
  pgadmin:
    image: dpage/pgadmin4:latest
    environment:
      PGADMIN_DEFAULT_EMAIL: admin@example.com
      PGADMIN_DEFAULT_PASSWORD: admin
    ports:
      - "5050:80"
    networks:
      - surf-one-pg-network
```

2. Start: `docker-compose up`
3. Visit: http://localhost:5050
4. Login with admin@example.com / admin
5. Add server: host=postgres, user=surfonepg, password=surfonepg_dev_password

## Troubleshooting

### Container Won't Start
```bash
# Check logs
docker logs surf-one-pg-app

# Check if port is already in use
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows

# Try different port
docker run -p 9080:8080 surf-one-pg:latest
```

### Database Connection Failed
```bash
# Check if MySQL is running
docker ps | grep mysql

# Check MySQL logs
docker logs surf-one-pg-db

# Test connection from app container
docker exec surf-one-pg-app mysql -h mysql -u surfonepg -p surfonepg_dev_password -e "SELECT VERSION();"
```

### Out of Disk Space
```bash
# Clean up unused Docker resources
docker system prune

# Remove all containers and volumes
docker-compose down -v
docker system prune -a
```

### Performance Issues
```bash
# Check container resource usage
docker stats

# Increase memory for Docker (check Docker Desktop settings)
# Restart containers if needed
docker-compose restart
```

## Production Comparison

| Aspect | Local Docker | Production (Render + Aiven) |
|--------|-------------|---------------------------|
| Database | MySQL 8.0 (local) | Aiven MySQL 8.0 |
| App Java Version | 17 (same) | 17 (same) |
| Networking | Internal bridge | Public HTTPS |
| Environment | `prod` | `prod` |
| Persistence | Docker volume | Aiven managed |

The local setup mirrors production using MySQL, so what works here should work on Render with Aiven.

## Useful Commands Cheat Sheet

```bash
# Start everything
docker-compose up -d

# Stop everything
docker-compose down

# Rebuild and restart
docker-compose up --build -d

# View all logs
docker-compose logs -f

# Access database
psql -h localhost -U surfonepg -d surfonepg

# Access app shell
docker exec -it surf-one-pg-app /bin/sh

# Rebuild image only
docker build -t surf-one-pg:latest .

# Clean all Docker resources
docker system prune -a --volumes
```

## Next Steps

1. ✅ Run locally with Docker Compose
2. ✅ Test all API endpoints
3. ✅ Verify database schema created
4. ✅ Test KopoKopo webhooks
5. ✅ Deploy to production (see DEPLOYMENT_GUIDE.md)

## References

- Docker Documentation: https://docs.docker.com/
- Docker Compose: https://docs.docker.com/compose/
- PostgreSQL Docker: https://hub.docker.com/_/postgres
- Spring Boot Docker: https://spring.io/guides/gs/spring-boot-docker/






