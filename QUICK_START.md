# Developer Quick Start

Get up and running with Surf One PG in 5 minutes.

## Prerequisites

- ✅ Java 17+ (`java -version`)
- ✅ Maven 3.9+ (`mvn -version`)
- ✅ Docker & Docker Compose (for containerized setup)
- ✅ Git
- ✅ Your favorite IDE (IntelliJ IDEA, VS Code, etc.)

## 1. Clone Repository (2 min)

```bash
git clone https://github.com/[your-org]/Surf_One_PG.git
cd Surf_One_PG
```

## 2. Choose Your Setup

### Option A: Quickest (Docker Compose - 3 min)

```bash
# Start everything
docker-compose up -d

# Wait for startup
sleep 10

# Test API
curl http://localhost:8080/api/v1/packages
```

✅ Done! Application ready at http://localhost:8080

**Cleanup:**
```bash
docker-compose down
```

### Option B: Spring Boot (Maven - 5 min)

```bash
# Update application.yml to use local MySQL/PostgreSQL
# Then run:
mvn clean spring-boot:run

# In another terminal, test
curl http://localhost:8080/api/v1/packages
```

### Option C: IDE Debug Mode (IntelliJ)

1. Open project in IntelliJ
2. Configure Java SDK (Project Settings → SDK → Java 17)
3. Run → Edit Configurations → Add Spring Boot
4. Main class: `com.surfonepg.SurfOnePgApplication`
5. Environment variables: (set as needed)
6. Run → Debug

## 3. Test the Application (1 min)

```bash
# Get available packages
curl http://localhost:8080/api/v1/packages

# Expected response:
# [
#   {"id": 1, "packageCode": "STARTER_500MB", ...},
#   ...
# ]

# Check health
curl http://localhost:8080/actuator/health

# Expected: {"status":"UP"}
```

## 4. Import Postman Collection (1 min)

1. Open Postman
2. Click Import
3. Select `Surf_One_PG_API.postman_collection.json`
4. Select `Surf_One_PG_Environment.postman_environment.json`
5. Ready to test all endpoints!

## Project Layout

```
Surf_One_PG/
├── src/main/java/
│   └── com/surfonepg/
│       ├── controller/        ← REST endpoints
│       ├── dto/              ← Request/Response objects
│       ├── config/           ← Configuration
│       ├── kopokopo/         ← M-Pesa integration
│       ├── packages/         ← Package management
│       ├── radius/           ← RADIUS provisioning
│       └── transaction/      ← Payment handling
├── src/test/                 ← Unit tests
├── pom.xml                   ← Maven dependencies
└── docker-compose.yml        ← Local dev environment
```

## Common Tasks

### Run Tests
```bash
mvn test
```

### Build JAR
```bash
mvn clean package
```

### Build Docker Image
```bash
docker build -t surf-one-pg:latest .
```

### View Logs (Docker Compose)
```bash
docker-compose logs -f          # All services
docker-compose logs -f app      # Application only
docker-compose logs -f postgres # Database only
```

### Access Database (Docker Compose)
```bash
# PostgreSQL CLI
psql -h localhost -U surfonepg -d surfonepg -p 5432
# Password: surfonepg_dev_password

# Inside container
docker exec -it surf-one-pg-db psql -U surfonepg -d surfonepg
```

### Clean Everything
```bash
docker-compose down -v
mvn clean
```

## API Endpoints (Quick Reference)

### Get Packages
```bash
curl http://localhost:8080/api/v1/packages
```

### Initiate Payment
```bash
curl -X POST http://localhost:8080/api/v1/payments \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "0712345678",
    "packageCode": "STARTER_500MB"
  }'
```

### Check Payment Status
```bash
# Use merchantReference from previous response
curl http://localhost:8080/api/v1/payments/TXN-xxxxx
```

## Making Changes

### Development Workflow

1. **Create feature branch**
   ```bash
   git checkout -b feature/my-feature
   ```

2. **Make changes**
   - Edit Java files in `src/main/java/`
   - IDE will auto-compile
   - Or: `mvn compile`

3. **Test changes**
   ```bash
   mvn test
   
   # Or run application and test manually
   docker-compose up -d
   curl http://localhost:8080/api/v1/packages
   ```

4. **Commit and push**
   ```bash
   git add .
   git commit -m "feat: add new feature"
   git push origin feature/my-feature
   ```

5. **Create pull request**
   - GitHub Actions automatically runs tests
   - Review feedback
   - Merge when approved

## IDE Setup

### IntelliJ IDEA
1. Open project folder
2. Maven automatically detected
3. Configure SDK: Project Settings → SDK
4. Run Application: Right-click `SurfOnePgApplication.java` → Run
5. Debug: Right-click → Debug

### VS Code
1. Install extensions:
   - Extension Pack for Java
   - Spring Boot Extension Pack
   - Docker
2. Open folder
3. Java language server starts automatically
4. Run: Debug → Run and Debug → Spring Boot App

### Eclipse
1. Import as Maven project
2. File → Import → Existing Maven Projects
3. Right-click project → Run As → Maven Build
4. Configure Run Configuration for Spring Boot

## Environment Variables

For local development, you can set in:

### Docker Compose
```bash
# Create .env file
KOPOKOPO_CLIENT_ID=sandbox_client_id
KOPOKOPO_CLIENT_SECRET=sandbox_client_secret
KOPOKOPO_TILL_NUMBER=K000000
```

### Maven
```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/radius
mvn spring-boot:run
```

### IDE
- IntelliJ: Run → Edit Configurations → Environment variables
- VS Code: .vscode/launch.json

## Debugging

### Debug Mode
```bash
# Maven
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"

# Then attach IDE debugger to localhost:5005
```

### View Logs
```bash
# Application logs
docker-compose logs -f app

# Database logs
docker-compose logs -f postgres

# Test logs
mvn test -X
```

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

## Troubleshooting

### "Port 8080 already in use"
```bash
# Find process
lsof -i :8080

# Kill process
kill -9 [PID]

# Or use different port
docker-compose up -p 9080:8080
```

### "Database connection refused"
```bash
# Check if PostgreSQL running
docker ps | grep postgres

# Start if not running
docker-compose up -d postgres
```

### "Maven build fails"
```bash
# Clean cache
mvn clean install -U

# Clear local repository
rm -rf ~/.m2/repository
mvn clean install
```

### "Tests failing"
```bash
# Run single test
mvn test -Dtest=YourTestClass

# Skip tests temporarily
mvn install -DskipTests
```

## Useful Commands

| Command | Purpose |
|---------|---------|
| `mvn clean compile` | Compile source |
| `mvn test` | Run unit tests |
| `mvn package` | Build JAR |
| `mvn spring-boot:run` | Run application |
| `docker-compose up` | Start all services |
| `docker-compose logs -f` | View logs |
| `docker ps` | List containers |
| `git status` | Show changes |
| `git diff` | Show detailed changes |

## Documentation

When stuck, check:
1. **Quick Questions**: This file
2. **Setup Issues**: [DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md)
3. **Docker Issues**: [DOCKER_GUIDE.md](./DOCKER_GUIDE.md)
4. **Production Deploy**: [DEPLOYMENT_CHECKLIST.md](./DEPLOYMENT_CHECKLIST.md)
5. **API Testing**: [POSTMAN_COLLECTION_README.md](./POSTMAN_COLLECTION_README.md)

## Next Steps

- ✅ Setup complete
- 📖 Read [README.md](./README.md) for overview
- 🧪 Run tests: `mvn test`
- 📦 Make a change and test it
- 🚀 When ready, check deployment docs

## Tips & Tricks

### IntelliJ Shortcuts
- `Ctrl+Shift+F10` - Run current class
- `Ctrl+D` - Duplicate line
- `Ctrl+/` - Toggle comment
- `Ctrl+Alt+L` - Format code
- `Shift+F6` - Rename

### Maven Shortcuts
```bash
# Quick test
mvn test -q

# Skip tests
mvn package -DskipTests

# Update dependencies
mvn versions:display-dependency-updates

# Clean and fresh install
mvn clean install -U
```

### Docker Compose
```bash
# Rebuild image
docker-compose up --build

# View specific service logs
docker-compose logs -f postgres

# Execute command in container
docker-compose exec app curl http://localhost:8080/health
```

## Getting Help

1. **Check existing documentation** - Most answers are there
2. **Search GitHub Issues** - Problem might be known
3. **Check logs** - Application tells you what's wrong
4. **Test in isolation** - Narrow down the problem
5. **Read error messages carefully** - They're usually helpful

## Happy Coding! 🚀

---

**Questions?** Check the documentation files or open an issue on GitHub.

**Ready to deploy?** See [DEPLOYMENT_CHECKLIST.md](./DEPLOYMENT_CHECKLIST.md)

