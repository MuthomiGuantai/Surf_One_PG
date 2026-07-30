# Quick Setup Checklist for Render + Aiven Deployment

Use this checklist to complete the GitHub Actions deployment setup.

## Phase 1: Account Setup ✅

- [ ] GitHub account with repository access
- [ ] Render account created (https://render.com)
- [ ] Aiven account created (https://aiven.io) OR using Render's PostgreSQL
- [ ] KopoKopo API credentials ready
- [ ] RADIUS configuration ready

## Phase 2: Database Setup ✅

### MySQL on Aiven (Recommended)
- [ ] Create Aiven MySQL service (v8.0+)
- [ ] Note: Host, Port, Username, Password
- [ ] Create database `radius`
- [ ] Create database user with appropriate permissions
- [ ] Test connection locally
- [ ] **See MYSQL_CONFIGURATION.md for detailed setup**

## Phase 3: GitHub Secrets Setup ✅

Go to: GitHub Repo → Settings → Secrets and variables → Actions

Add these secrets:
- [ ] `RENDER_API_KEY` - Get from Render Dashboard → Account → API Keys
- [ ] `RENDER_SERVICE_ID` - Get after creating Render service (format: srv-xxxxx)

Optional:
- [ ] `DOCKER_REGISTRY_TOKEN` - If using private registry

## Phase 4: Create Render Service ✅

### Quick Method (Recommended):
1. [ ] Go to Render Dashboard → New → Web Service
2. [ ] Connect GitHub repository
3. [ ] Select branch (main or develop)
4. [ ] Render will auto-detect `render.yaml`
5. [ ] Review and confirm settings
6. [ ] Click "Create Web Service"
7. [ ] Note your service ID (srv-xxxxx) and URL (https://xxxx.onrender.com)

### Manual Method:
1. [ ] Go to Render Dashboard → New → Web Service
2. [ ] Connect GitHub repository
3. [ ] Configure:
   - Name: `surf-one-pg`
   - Region: oregon (or your choice)
   - Branch: main
   - Runtime: Docker
4. [ ] Click "Create Web Service"
5. [ ] After creation, add environment variables (see Phase 5)

## Phase 5: Configure Environment Variables ✅

In Render Dashboard → Your Service → Environment:

### Database Settings (MySQL via Aiven)
```
SPRING_DATASOURCE_URL=jdbc:mysql://[host]:[port]/radius?useSSL=true&serverTimezone=Africa/Nairobi
SPRING_DATASOURCE_USERNAME=[username]
SPRING_DATASOURCE_PASSWORD=[password]
SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.MySQL8Dialect
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_FLYWAY_ENABLED=true
```

- [ ] `SPRING_DATASOURCE_URL` - from Aiven MySQL console
- [ ] `SPRING_DATASOURCE_USERNAME` - from Aiven
- [ ] `SPRING_DATASOURCE_PASSWORD` - from Aiven
- [ ] `SPRING_PROFILES_ACTIVE` = `prod`

**For detailed MySQL configuration, see MYSQL_CONFIGURATION.md**

### KopoKopo Settings
```
KOPOKOPO_BASE_URL=https://api.kopokopo.com
KOPOKOPO_CLIENT_ID=[your-client-id]
KOPOKOPO_CLIENT_SECRET=[your-client-secret]
KOPOKOPO_TILL_NUMBER=[your-till-number]
KOPOKOPO_CALLBACK_URL=https://[your-render-domain].onrender.com/api/v1/webhooks/kopokopo
KOPOKOPO_API_WEBHOOK_SECRET=[your-webhook-secret]
```

- [ ] `KOPOKOPO_BASE_URL` - production endpoint
- [ ] `KOPOKOPO_CLIENT_ID` - from KopoKopo dashboard
- [ ] `KOPOKOPO_CLIENT_SECRET` - from KopoKopo dashboard
- [ ] `KOPOKOPO_TILL_NUMBER` - from KopoKopo dashboard
- [ ] `KOPOKOPO_CALLBACK_URL` - your Render service URL + webhook path
- [ ] `KOPOKOPO_API_WEBHOOK_SECRET` - from KopoKopo dashboard

### Application Settings
```
NAS_IDENTIFIER=mikrotik-hotspot-01
PAYMENT_TIMEOUT_MINUTES=5
LOGGING_LEVEL_CO_KE_SURFONEPG=INFO
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK=WARN
```

- [ ] `NAS_IDENTIFIER` - your Mikrotik device identifier
- [ ] `PAYMENT_TIMEOUT_MINUTES` - payment expiry time
- [ ] Logging levels configured

## Phase 6: GitHub Actions Verification ✅

- [ ] Go to GitHub → Actions tab
- [ ] See "Build and Deploy to Render" workflow
- [ ] Workflow has green checkmark ✅

## Phase 7: First Deployment ✅

### Option A: Manual Trigger
1. [ ] Go to GitHub → Actions tab
2. [ ] Click "Build and Deploy to Render"
3. [ ] Click "Run workflow" dropdown
4. [ ] Select branch (main/develop)
5. [ ] Click "Run workflow" button
6. [ ] Wait for completion (5-10 minutes)

### Option B: Automatic on Push
1. [ ] Make a test commit and push to main/develop
2. [ ] GitHub Actions automatically triggers
3. [ ] Wait for completion

## Phase 8: Deployment Verification ✅

After deployment completes:

1. [ ] Check Render Dashboard:
   - Service status is "Live" (green)
   - No errors in Logs tab

2. [ ] Test API endpoints:
   ```bash
   # Get service URL from Render Dashboard
   curl https://[your-domain].onrender.com/api/v1/packages
   ```
   - [ ] Returns list of packages

3. [ ] Check health endpoint:
   ```bash
   curl https://[your-domain].onrender.com/actuator/health
   ```
   - [ ] Returns `{"status":"UP"}`

4. [ ] Monitor database:
   - [ ] Migrations completed (check logs)
   - [ ] Tables created in Aiven/Render PostgreSQL

5. [ ] Test payment endpoint:
   ```bash
   curl -X POST https://[your-domain].onrender.com/api/v1/payments \
     -H "Content-Type: application/json" \
     -d '{"phoneNumber":"0712345678","packageCode":"STARTER_500MB"}'
   ```
   - [ ] Returns valid response with merchantReference

## Phase 9: Post-Deployment Setup ✅

1. [ ] Update KopoKopo dashboard:
   - [ ] Set webhook URL to your Render service
   - [ ] Test webhook delivery

2. [ ] Configure domain (optional):
   - [ ] Buy domain or use subdomain
   - [ ] Add DNS CNAME to Render
   - [ ] Update `KOPOKOPO_CALLBACK_URL` to custom domain

3. [ ] Set up monitoring:
   - [ ] Enable Render error notifications
   - [ ] Set up log monitoring
   - [ ] Configure backup strategy

4. [ ] Security hardening:
   - [ ] Rotate API keys
   - [ ] Review firewall rules
   - [ ] Enable HTTPS (automatic with Render)

## Phase 10: Ongoing Maintenance ✅

- [ ] Set up automated backups for Aiven database
- [ ] Monitor application logs weekly
- [ ] Update dependencies regularly
- [ ] Test payment flow in production
- [ ] Document any custom configurations

## Troubleshooting

### Deployment Failed
- [ ] Check GitHub Actions logs for build errors
- [ ] Verify all environment variables are set
- [ ] Check Render logs for startup errors
- [ ] Test database connection

### API Not Responding
- [ ] Check Render service status
- [ ] Review service logs for errors
- [ ] Verify database is running
- [ ] Check network connectivity

### Database Migration Failed
- [ ] Review Flyway logs
- [ ] Check for SQL syntax errors
- [ ] Verify database user has permissions
- [ ] Check migration file naming (V1, V2, V3...)

### KopoKopo Webhooks Not Working
- [ ] Verify callback URL is correct
- [ ] Check webhook signature in Render logs
- [ ] Test webhook manually
- [ ] Verify KopoKopo API credentials

## Files Created/Modified

These files were created or need review:

- ✅ `.github/workflows/build-deploy.yml` - GitHub Actions workflow
- ✅ `Dockerfile` - Container configuration
- ✅ `render.yaml` - Render service configuration
- ✅ `src/main/resources/application-prod.yml` - Production configuration
- ✅ `DEPLOYMENT_GUIDE.md` - Detailed deployment documentation

## Command Reference

```bash
# View service logs
render logs --service-id srv-[ID] --follow

# Trigger deployment manually
curl https://api.render.com/deploy/srv-[ID]?key=[KEY] -X POST

# Test database connection
psql -h [host] -U [user] -d surfonepg

# Build Docker image locally
docker build -t surf-one-pg:latest .

# Run container locally
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod surf-one-pg:latest
```

## Next Steps

1. ✅ Complete all checklist items above
2. ✅ Trigger first deployment via GitHub Actions
3. ✅ Verify all endpoints working
4. ✅ Set up monitoring and alerting
5. ✅ Test payment flow with KopoKopo
6. ✅ Plan backup and disaster recovery

## Support

For issues or questions:
- Check `DEPLOYMENT_GUIDE.md` for detailed troubleshooting
- Review Render documentation: https://render.com/docs
- Check GitHub Actions logs: https://github.com/[repo]/actions
- Aiven support: https://aiven.io/support

---

**Status**: [ ] Setup Complete - Ready for Production



