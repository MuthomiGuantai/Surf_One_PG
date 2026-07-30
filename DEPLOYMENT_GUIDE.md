 # GitHub Actions Deployment Guide - Render + Aiven DB

This guide explains how to set up CI/CD with GitHub Actions to deploy the Surf One PG application to Render with Aiven PostgreSQL database.

## Prerequisites

- GitHub account and repository
- Render account (https://render.com)
- Aiven account (https://aiven.io) or use Render's built-in PostgreSQL
- KopoKopo API credentials (production)
- RADIUS server configuration

## Architecture

```
GitHub Repository
  ↓ (on push to main/develop)
GitHub Actions
  ↓ (build & test)
Container Registry (GHCR)
  ↓ (push image)
Render
  ↓ (pull & deploy)
Aiven PostgreSQL Database
```

## Step 1: Set Up Aiven MySQL Database (Updated)

### Option A: Using Aiven MySQL (Recommended for this project)

1. Go to https://aiven.io and sign up
2. Create a new MySQL service
   - Region: Choose close to your Render region
   - Plan: Business or Developer tier
   - MySQL version: 8.0 (or latest)
3. Note the connection details:
   - Host: `your-service.a.aivencloud.com`
   - Port: `your-port`
   - Database: `radius` (create this)
   - Username: `avnadmin` (or custom)
   - Password: (auto-generated)
4. Create a database named `radius`
5. Create/configure a database user with appropriate permissions

**See [MYSQL_CONFIGURATION.md](./MYSQL_CONFIGURATION.md) for detailed MySQL setup!**

### Option B: Using Render's PostgreSQL (Alternative)

If you prefer PostgreSQL:
- Create database through Render dashboard
- Render provides the connection string automatically
- See DEPLOYMENT_GUIDE.md (PostgreSQL section)

## Step 2: GitHub Repository Setup

### Add GitHub Secrets

Go to Settings → Secrets and variables → Actions, add these secrets:

```
RENDER_API_KEY         # Get from Render Dashboard → API Keys
RENDER_SERVICE_ID      # Your Render service ID (after creating service)
```

Optional secrets (for manual deployment):
```
AIVEN_API_TOKEN        # Aiven API token
DOCKER_REGISTRY_TOKEN  # If using private registry
```

## Step 3: Create Render Service

### Method 1: Using render.yaml (Recommended)

1. Push code to GitHub with `render.yaml` included
2. Go to Render Dashboard → New → Web Service
3. Connect your GitHub repository
4. Select branch (main or develop)
5. Choose "Docker" as environment
6. Render will automatically:
   - Detect `render.yaml`
   - Create the web service
   - Provision the PostgreSQL database
   - Set up environment variables

### Method 2: Manual Creation

1. Go to Render Dashboard → New → Web Service
2. Connect GitHub repository
3. Configure:
   - Name: `surf-one-pg`
   - Runtime: Docker
   - Region: Choose based on your location
   - Branch: `main` or `develop`
4. Set environment variables (see Step 4)
5. Create the service

## Step 4: Configure Environment Variables

In Render Dashboard for your service, add these environment variables:

### Database Configuration
```
SPRING_DATASOURCE_URL          jdbc:mysql://[host]:[port]/radius?useSSL=true&serverTimezone=Africa/Nairobi
SPRING_DATASOURCE_USERNAME     [aiven_username]
SPRING_DATASOURCE_PASSWORD     [aiven_password]
SPRING_DATASOURCE_DRIVER_CLASS_NAME  com.mysql.cj.jdbc.Driver
SPRING_JPA_DATABASE_PLATFORM   org.hibernate.dialect.MySQL8Dialect
SPRING_JPA_HIBERNATE_DDL_AUTO  validate
SPRING_FLYWAY_ENABLED          true
```

**Note**: These are configured for **MySQL 8.0 on Aiven**. See [MYSQL_CONFIGURATION.md](./MYSQL_CONFIGURATION.md) for details.

### KopoKopo Configuration
```
KOPOKOPO_BASE_URL              https://api.kopokopo.com
KOPOKOPO_CLIENT_ID             [your-client-id]
KOPOKOPO_CLIENT_SECRET         [your-client-secret]
KOPOKOPO_TILL_NUMBER           [your-till-number]
KOPOKOPO_CALLBACK_URL          https://your-render-domain.onrender.com/api/v1/webhooks/kopokopo
KOPOKOPO_API_WEBHOOK_SECRET    [your-webhook-secret]
```

### Application Configuration
```
NAS_IDENTIFIER                 mikrotik-hotspot-01
PAYMENT_TIMEOUT_MINUTES        5
LOGGING_LEVEL_CO_KE_SURFONEPG  INFO
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK WARN
```

## Step 5: Update Application Configuration

### Update Flyway Migrations for MySQL

The project uses **MySQL 8.0** for production. Flyway automatically runs these migrations:

1. **V1__radius_schema.sql** - FreeRADIUS base tables
2. **V2__surfonepg_tables.sql** - Application-specific tables  
3. **V3__fix_radcheck_radreply_op_columns.sql** - Schema corrections

**Note**: The `V2__postgresql_radius_schema.sql` is for PostgreSQL and will be skipped.

For PostgreSQL deployments:
- Use the PostgreSQL migrations instead
- Update `render.yaml` to use PostgreSQL database service
- Refer to old documentation for PostgreSQL configuration

See [MYSQL_CONFIGURATION.md](./MYSQL_CONFIGURATION.md) for MySQL-specific details.

### Update KopoKopo Webhook URL

After Render provisions your service:
1. Copy your service URL (e.g., `https://surf-one-pg.onrender.com`)
2. Update in Render environment variables:
   ```
   KOPOKOPO_CALLBACK_URL=https://surf-one-pg.onrender.com/api/v1/webhooks/kopokopo
   ```
3. Also update in KopoKopo dashboard → Webhooks configuration

## Step 6: Enable GitHub Actions

1. Go to your GitHub repository
2. Click "Actions" tab
3. Click "I understand my workflows, go ahead and enable them"
4. The workflow file `.github/workflows/build-deploy.yml` will be active

## Step 7: Deploy

### Automatic Deployment
Simply push to `main` or `develop` branch:
```bash
git add .
git commit -m "Deploy to production"
git push origin main
```

GitHub Actions will:
1. Checkout code
2. Build with Maven
3. Run tests
4. Build Docker image
5. Push to container registry
6. Trigger Render deployment

### Manual Deployment
1. Go to GitHub Actions tab
2. Click "Build and Deploy to Render"
3. Click "Run workflow" → select branch → Run

### Monitor Deployment
1. GitHub Actions tab: Watch the workflow progress
2. Render Dashboard: Check "Logs" for deployment details
3. When complete, visit your service URL

## Step 8: Verify Deployment

1. Check Render logs for startup messages
2. Test the API:
   ```bash
   curl https://your-render-domain.onrender.com/api/v1/packages
   ```
3. Check database connection in logs
4. Monitor health endpoint:
   ```bash
   curl https://your-render-domain.onrender.com/actuator/health
   ```

## Troubleshooting

### Build Fails in GitHub Actions
- Check logs in GitHub Actions tab
- Common issues:
  - Java version mismatch
  - Missing Maven dependencies
  - Test failures

**Solution:**
```bash
# Test locally first
mvn clean compile
mvn test
mvn package
```

### Deployment Fails to Render
- Check Render API key in GitHub secrets
- Verify Render service ID
- Check Render logs for errors

**Solution:**
```bash
# Manually trigger deployment
curl https://api.render.com/deploy/srv-[SERVICE_ID]?key=[API_KEY] -X POST
```

### Database Connection Fails
- Verify PostgreSQL/MySQL service is running on Aiven
- Check connection string format
- Verify username and password
- Check firewall rules

**Solution:**
```bash
# Test connection locally
psql -h [host] -U [username] -d surfonepg
```

### Flyway Migration Fails
- Check migration file syntax for SQL dialect
- Verify migrations are in correct order (V1, V2, V3, etc.)
- Check for duplicate migration versions

**Solution:**
- Review migration files in `src/main/resources/db/migration/`
- Check Render logs for specific SQL errors
- Reset database if needed (dangerous - only in dev)

### KopoKopo Webhooks Not Working
- Verify callback URL is correct and public
- Check webhook signature validation
- Verify API credentials

**Solution:**
```bash
# Test webhook manually
curl -X POST https://your-domain/api/v1/webhooks/kopokopo \
  -H "Content-Type: application/json" \
  -H "X-KopoKopo-Signature: test-signature" \
  -d '{...}'
```

## Monitoring and Logs

### View Logs in Render
1. Render Dashboard → Your Service → Logs
2. Real-time logs appear as application runs
3. Check for errors related to:
   - Database connection
   - KopoKopo API calls
   - RADIUS provisioning

### View Logs in GitHub Actions
1. GitHub → Actions tab
2. Click workflow run
3. Expand "Build with Maven" to see test output
4. Check Docker build logs

## Scaling and Performance

### Optimize for Production
1. **Database**: Aiven provides multiple tiers
   - Developer: 1GB RAM, good for testing
   - Business: 8GB+ RAM, suitable for production
2. **Render Service**:
   - Standard tier: 0.5GB RAM
   - Pro tier: 2GB+ RAM
3. **Connection Pool**:
   - Increase `spring.datasource.hikari.maximum-pool-size` for high traffic
   - Default is 10, adjust based on load

### Monitor Performance
- Use Render's metrics dashboard
- Monitor database connections in Aiven
- Track API response times
- Monitor error rates

## Security Best Practices

1. **Secrets Management**:
   - Never commit `.env` files
   - Use GitHub Secrets for all credentials
   - Rotate API keys regularly

2. **Database Security**:
   - Use strong passwords
   - Restrict database access to Render IP
   - Enable SSL connections (included in Aiven)

3. **API Security**:
   - Validate webhook signatures
   - Use HTTPS only
   - Implement rate limiting
   - Keep dependencies updated

4. **Firewall Rules**:
   - Allow Render IPs for database access
   - Restrict administrative endpoints

## Updating the Application

To update the deployed application:

1. Make code changes locally
2. Test locally
3. Commit and push to GitHub
   ```bash
   git commit -am "Update feature"
   git push origin main
   ```
4. GitHub Actions automatically builds and deploys
5. Monitor Render logs for startup

## Rollback Deployment

If deployment causes issues:

1. **Render Dashboard**:
   - Go to your service
   - Check "Deploys" history
   - Click previous successful deploy
   - Click "Redeploy"

2. **Or push a fix**:
   - Fix the issue locally
   - Commit and push to trigger new deployment

## Useful Commands

```bash
# View Render logs locally
render logs --service-id srv-[ID] --follow

# Deploy manually
curl https://api.render.com/deploy/srv-[ID]?key=[KEY] -X POST

# Test database connection
psql -h [host] -U [user] -d surfonepg

# View Docker image layers
docker history [image-name]

# Build locally with Docker
docker build -t surf-one-pg:latest .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/surfonepg \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=password \
  surf-one-pg:latest
```

## Additional Resources

- [Render Documentation](https://render.com/docs)
- [Aiven PostgreSQL](https://aiven.io/postgresql)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Spring Boot Production Guide](https://spring.io/guides/gs/spring-boot/)
- [Flyway Documentation](https://flywaydb.org/documentation/)

## Support and Questions

For issues:
1. Check GitHub Actions workflow logs
2. Check Render service logs
3. Review application.yml configuration
4. Verify all environment variables are set
5. Test locally with Docker




