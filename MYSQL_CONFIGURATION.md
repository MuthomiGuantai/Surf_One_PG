# Aiven MySQL Deployment Configuration

Since you're using **Aiven MySQL** (not PostgreSQL), here are the specific configurations needed.

## Connection String Format

```
jdbc:mysql://[host]:[port]/[database]?useSSL=true&serverTimezone=Africa/Nairobi
```

Example with Aiven:
```
jdbc:mysql://surf-one-pg-mysql-xxxxx.a.aivencloud.com:12345/radius?useSSL=true&serverTimezone=Africa/Nairobi
```

## Environment Variables for Render

Set these in your Render Dashboard → Environment:

```
SPRING_DATASOURCE_URL=jdbc:mysql://[aiven-host]:[aiven-port]/radius?useSSL=true&serverTimezone=Africa/Nairobi
SPRING_DATASOURCE_USERNAME=[aiven-user]
SPRING_DATASOURCE_PASSWORD=[aiven-password]
SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.MySQL8Dialect
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_FLYWAY_ENABLED=true
```

## Database Migrations Used

The following migrations will run automatically with Flyway:

1. **V1__radius_schema.sql** - FreeRADIUS tables (radcheck, radreply, etc.)
2. **V2__surfonepg_tables.sql** - Application-specific tables
3. **V3__fix_radcheck_radreply_op_columns.sql** - Schema corrections

**Note**: The `V2__postgresql_radius_schema.sql` file is for PostgreSQL only and will be skipped.

## Aiven MySQL Connection Details

Get these from your Aiven console:

1. Go to https://console.aiven.io
2. Select your MySQL service
3. Connection Information tab
4. Copy:
   - **Host**: `surf-one-pg-xxxxx.a.aivencloud.com`
   - **Port**: Usually `12345` or `5432`
   - **Database**: `radius`
   - **Username**: `avnadmin` or custom user
   - **Password**: Your password

## Local Development with MySQL

The `docker-compose.yml` has been updated to use MySQL 8.0.

```bash
# Start local MySQL + app
docker-compose up -d

# Connect to local MySQL
mysql -h localhost -u surfonepg -p radius
# Password: surfonepg_dev_password

# View logs
docker-compose logs -f

# Stop
docker-compose down
```

## MySQL vs PostgreSQL Differences

| Feature | MySQL | PostgreSQL |
|---------|-------|-----------|
| Driver | `com.mysql.cj.jdbc.Driver` | `org.postgresql.Driver` |
| Dialect | `MySQL8Dialect` | `PostgreSQL10Dialect` |
| Port | 3306 (local) | 5432 (local) |
| Connection String | `jdbc:mysql://...` | `jdbc:postgresql://...` |
| SSL | `useSSL=true` | Built-in |
| Timezone | `serverTimezone=...` | Auto |

## Configuration Files Updated

✅ `docker-compose.yml` - Now uses MySQL 8.0
✅ `application-prod.yml` - MySQL8Dialect configured
✅ `render.yaml` - MySQL connection variables (no database creation)

## Important Notes

### Flyway Migrations
- Existing migrations (`V1__radius_schema.sql`, `V2__surfonepg_tables.sql`) are MySQL
- They will run automatically on first deployment
- Flyway validates schema on each startup

### SSL/TLS
- Aiven MySQL requires SSL by default
- Connection string includes `useSSL=true`
- If you see SSL errors, ensure certificate is valid

### Character Set
- Use UTF-8 (default in Aiven MySQL 8)
- Connection string includes proper timezone handling

### Timezone
- Set to `Africa/Nairobi` (Kenya timezone)
- Adjust if needed for your region

## Testing Connection

### Local
```bash
# Test MySQL connection
docker-compose exec mysql mysql -u surfonepg -p -e "SELECT VERSION();"
# Password: surfonepg_dev_password
```

### Production
```bash
# Test from command line (after deployment)
mysql -h [aiven-host] -P [port] -u [user] -p[password] -e "SELECT VERSION();" radius
```

## Troubleshooting

### "Access denied for user"
- Verify username and password
- Check Aiven console for correct credentials

### "SSL connection error"
- Ensure `useSSL=true` in connection string
- Aiven MySQL 8 requires SSL

### "Unknown database 'radius'"
- Create database `radius` in Aiven console
- Or let Flyway migrations create required tables

### "Can't connect from Render"
- Check Aiven firewall rules
- Add Render IP to allowed IPs in Aiven console

### Migrations fail to run
- Check Flyway logs in Render
- Verify database user has CREATE TABLE permissions
- Review migration file syntax for MySQL compatibility

## MySQL-Specific Configuration

### Connection Pool
```
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2
```

### Hibernate
```
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=validate
```

### Flyway
```
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

## Production Checklist

- [ ] Aiven MySQL service created
- [ ] Database `radius` exists
- [ ] User created with permissions
- [ ] Firewall allows Render IP
- [ ] SSL certificates valid
- [ ] SPRING_DATASOURCE_URL set in Render
- [ ] SPRING_DATASOURCE_USERNAME set in Render
- [ ] SPRING_DATASOURCE_PASSWORD set in Render
- [ ] All other environment variables set
- [ ] GitHub Actions workflow triggered
- [ ] Deployment successful
- [ ] API endpoints respond
- [ ] Database migrations completed

## Documentation Files to Reference

- **DEPLOYMENT_CHECKLIST.md** - Use Phase 5 for MySQL-specific setup
- **DOCKER_GUIDE.md** - Local testing updated for MySQL
- **DEPLOYMENT_GUIDE.md** - General deployment help

## Need Help?

See the main documentation files:
1. Start with **DEPLOYMENT_CHECKLIST.md**
2. Follow Phase 4-5 with MySQL variables
3. Reference this file for MySQL specifics

---

**Status**: MySQL configuration updated
**Last Updated**: July 30, 2026
**Database**: Aiven MySQL 8.0 (Production)

