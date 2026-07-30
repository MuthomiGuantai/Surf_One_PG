# GitHub Actions Deployment Setup - Quick Reference Card

Print this page or bookmark for quick reference during setup!

---

## 📋 Setup Overview (2-3 hours)

```
┌─────────────────────────────────────────────────────┐
│ PHASE 1: Prepare Accounts (15 min)                  │
│ ✓ GitHub account with repo                          │
│ ✓ Render account (render.com)                       │
│ ✓ Aiven account (aiven.io) - Optional               │
│ ✓ KopoKopo API credentials                          │
└─────────────────────────────────────────────────────┘
            ↓
┌─────────────────────────────────────────────────────┐
│ PHASE 2: Add GitHub Secrets (5 min)                 │
│ ✓ RENDER_API_KEY                                    │
│ ✓ RENDER_SERVICE_ID                                 │
└─────────────────────────────────────────────────────┘
            ↓
┌─────────────────────────────────────────────────────┐
│ PHASE 3: Create Render Service (30 min)             │
│ ✓ Connect GitHub repo                               │
│ ✓ Select render.yaml                                │
│ ✓ Auto-create PostgreSQL (optional)                 │
└─────────────────────────────────────────────────────┘
            ↓
┌─────────────────────────────────────────────────────┐
│ PHASE 4: Configure Variables (30 min)               │
│ ✓ Database connection                               │
│ ✓ KopoKopo credentials                              │
│ ✓ Application settings                              │
└─────────────────────────────────────────────────────┘
            ↓
┌─────────────────────────────────────────────────────┐
│ PHASE 5: First Deployment (30 min)                  │
│ ✓ Push to main branch                               │
│ ✓ Watch GitHub Actions                              │
│ ✓ Verify in Render                                  │
│ ✓ Test API endpoints                                │
└─────────────────────────────────────────────────────┘
```

---

## 🔑 Essential Credentials Checklist

### From Render
- [ ] API Key: _______________________
- [ ] Service ID: srv-_________________
- [ ] Service URL: https://________________.onrender.com

### From KopoKopo
- [ ] Client ID: _______________________
- [ ] Client Secret: _______________________
- [ ] Till Number: K_____________________
- [ ] Webhook Secret: _______________________

### Database (Aiven or Render)
- [ ] Host: _______________________
- [ ] Port: _______________________
- [ ] Username: _______________________
- [ ] Password: _______________________
- [ ] Database: surfonepg

---

## 🖥️ Local Testing (Before Deployment)

```bash
# Start local environment
docker-compose up -d

# Test API
curl http://localhost:8080/api/v1/packages

# View logs
docker-compose logs -f

# Stop
docker-compose down
```

**Expected**: Application runs, database connects, API responds

---

## 🔐 GitHub Secrets Setup

```
Path: GitHub Repo → Settings → Secrets and variables → Actions

Secret Name              | Value Source
─────────────────────────┼──────────────────────────
RENDER_API_KEY          | Render Dashboard → Account
RENDER_SERVICE_ID       | After creating service
```

---

## 🚀 Environment Variables in Render

### Database Connection
```
SPRING_DATASOURCE_URL     jdbc:postgresql://[host]:[port]/surfonepg
SPRING_DATASOURCE_USERNAME [username]
SPRING_DATASOURCE_PASSWORD [password]
SPRING_PROFILES_ACTIVE     prod
```

### KopoKopo Integration
```
KOPOKOPO_BASE_URL              https://api.kopokopo.com
KOPOKOPO_CLIENT_ID             [client-id]
KOPOKOPO_CLIENT_SECRET         [client-secret]
KOPOKOPO_TILL_NUMBER           [till-number]
KOPOKOPO_CALLBACK_URL          https://[service].onrender.com/api/v1/webhooks/kopokopo
KOPOKOPO_API_WEBHOOK_SECRET    [webhook-secret]
```

### Application Settings
```
NAS_IDENTIFIER                 mikrotik-hotspot-01
PAYMENT_TIMEOUT_MINUTES        5
LOGGING_LEVEL_CO_KE_SURFONEPG  INFO
```

---

## 📊 Deployment Workflow (What Happens)

```
Developer pushes code to main
          ↓
GitHub Actions workflow triggers
  ├─ Checkout code ✓
  ├─ Setup Java 17 ✓
  ├─ Maven build ✓
  ├─ Run tests ✓
  ├─ Docker build ✓
  └─ Push to registry ✓
          ↓
Render detects new image
  ├─ Pull Docker image ✓
  ├─ Run container ✓
  ├─ Execute migrations ✓
  └─ Start application ✓
          ↓
Application ready
  ├─ Health check passes ✓
  ├─ Database connected ✓
  └─ API endpoints live ✓
          ↓
Service available at: https://your-service.onrender.com
```

---

## ✅ Verification Checklist

After deployment, verify:

- [ ] Service status in Render is "Live" (green)
- [ ] No errors in Render logs
- [ ] GitHub Actions workflow passed
- [ ] API responds: `curl https://[domain]/api/v1/packages`
- [ ] Health check: `curl https://[domain]/actuator/health`
- [ ] Database connected (check logs)
- [ ] All 14 environment variables set

---

## 🆘 Common Issues & Fixes

| Issue | Solution |
|-------|----------|
| Port 8080 in use | `lsof -i :8080` then `kill -9 [PID]` |
| Database won't connect | Check SPRING_DATASOURCE_URL format |
| GitHub Actions fails | Check Maven build: `mvn clean package` |
| Docker build fails | Ensure Java 17 installed: `java -version` |
| Render deploy fails | Check service logs in Render dashboard |
| API returns 404 | Check service URL, might not be ready yet |
| Webhook signature invalid | Verify KOPOKOPO_API_WEBHOOK_SECRET |

---

## 🔗 Quick Links

| Resource | URL |
|----------|-----|
| Render Dashboard | https://dashboard.render.com |
| GitHub Actions | https://github.com/[user]/Surf_One_PG/actions |
| Render Docs | https://render.com/docs |
| Aiven Console | https://console.aiven.io |
| KopoKopo Dev | https://developer.kopokopo.com |

---

## 📚 Documentation Files (In Priority Order)

1. **START_HERE.md** ← You are here!
2. **DEPLOYMENT_CHECKLIST.md** ← Follow this step-by-step
3. **DEPLOYMENT_GUIDE.md** ← Reference as needed
4. **QUICK_START.md** ← For local development
5. **DOCKER_GUIDE.md** ← Docker specifics
6. **SETUP_SUMMARY.md** ← Detailed overview

---

## 🎯 Success Milestones

- [ ] Accounts created (GitHub, Render, Aiven)
- [ ] GitHub Secrets added
- [ ] Render service created
- [ ] Environment variables configured
- [ ] First deployment triggered
- [ ] API responds with data
- [ ] Payment endpoint works
- [ ] Webhook signature validates
- [ ] Database migrations complete
- [ ] Monitoring active

---

## ⏱️ Time Estimate

| Phase | Duration | Status |
|-------|----------|--------|
| Accounts & Setup | 30 min | |
| Database Config | 30 min | |
| Render Service | 30 min | |
| Env Variables | 30 min | |
| First Deploy | 15 min | |
| Verification | 15 min | |
| **TOTAL** | **~2-3 hrs** | |

---

## 💾 Files to Remember

Keep these accessible:
- `.github/workflows/build-deploy.yml` - The automation
- `Dockerfile` - Container definition
- `render.yaml` - Render config
- `docker-compose.yml` - Local testing
- `DEPLOYMENT_CHECKLIST.md` - Setup guide

---

## 🚀 Next Step

→ Open **DEPLOYMENT_CHECKLIST.md**
→ Follow Phase 1-10 step by step
→ Copy this reference card to desktop for quick access

---

## 📞 Quick Troubleshooting Flowchart

```
Problem?
  │
  ├─ Build fails in GitHub Actions?
  │  └─ Check Maven: mvn clean package
  │
  ├─ Render deployment fails?
  │  └─ Check Render logs & Dockerfile
  │
  ├─ Database connection error?
  │  └─ Verify SPRING_DATASOURCE_URL
  │
  ├─ API returns 404?
  │  └─ Check service is Live, wait 30 sec
  │
  ├─ Webhook not working?
  │  └─ Verify signature secret & URL
  │
  └─ Still stuck?
     └─ Check DEPLOYMENT_GUIDE.md Troubleshooting
```

---

**Print this card and keep it handy during setup!**

**Bookmark START_HERE.md and DEPLOYMENT_CHECKLIST.md**

---

*Version: 1.0 | Created: July 30, 2026 | Status: Production Ready*

