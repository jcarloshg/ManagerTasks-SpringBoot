# Development Docker Setup with Hot Reload

This guide explains how to use the development Docker Compose setup with **hot reload** - automatically apply code changes without restarting containers.

## Overview

The development setup uses Spring Boot DevTools to monitor source code changes and automatically restart the application. This allows you to:
- Modify Java files, controllers, services, etc.
- Save the file
- Application automatically restarts with your changes
- No manual container restart needed

## Quick Start

### 1. Start Development Environment

```bash
docker-compose -f docker-compose.dev.yml up -d
```

This will:
- Start PostgreSQL database
- Build and start Spring Boot API with DevTools enabled
- Mount source code for live changes
- Enable verbose logging for debugging

### 2. Verify Services are Running

```bash
# Check container status
docker-compose -f docker-compose.dev.yml ps

# View logs in real-time
docker-compose -f docker-compose.dev.yml logs -f api
```

Expected output when starting:
```
api_dev_1 | [INFO] Scanning for projects...
api_dev_1 | [INFO] Building ManagerTasks API 1.0.0
...
api_dev_1 | Started ManagerTasksApiApplication in X.XXX seconds
```

### 3. Test the Application

```bash
# Test sign up
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "StrongPass@123"
  }'
```

## Hot Reload Workflow

### How It Works

```
┌──────────────────────────┐
│  Edit Java File (.java)  │
│  in your IDE             │
└────────────┬─────────────┘
             │
             ▼
┌──────────────────────────┐
│ Save File (Ctrl+S)       │
└────────────┬─────────────┘
             │
             ▼
┌──────────────────────────────┐
│ IDE compiles to target/ dir  │
│ (Maven watches automatically)│
└────────────┬─────────────────┘
             │
             ▼
┌──────────────────────────────┐
│ DevTools detects changes     │
│ in compiled classes          │
└────────────┬─────────────────┘
             │
             ▼
┌──────────────────────────────┐
│ Application automatically    │
│ RESTARTS with new code       │
│ (No container restart needed)│
└────────────┬─────────────────┘
             │
             ▼
┌──────────────────────────────┐
│ Check logs for changes       │
│ Application ready to test    │
└──────────────────────────────┘
```

### Example: Adding a New Endpoint

**Step 1: Edit Controller**
```bash
# Edit the file in your IDE
nano ManagerTasks-Api/src/main/java/com/managertasks/api/controller/AuthController.java
```

**Step 2: Add New Endpoint**
```java
@PostMapping("/test")
public ResponseEntity<String> testEndpoint() {
    return ResponseEntity.ok("Test successful!");
}
```

**Step 3: Save File**
- Press Ctrl+S in your IDE
- DevTools detects the change
- Application automatically restarts (watch logs)

**Step 4: Test Immediately**
```bash
curl http://localhost:8080/api/v1/auth/test

# Response: "Test successful!"
```

No container restart needed! 🚀

## File Structure for Development

```
ManagerTasks-SpringBoot/
├── docker-compose.dev.yml        # Development compose file
├── Dockerfile.dev                 # Development Dockerfile with DevTools
├── .env.dev                       # Development environment variables
├── ManagerTasks-Api/
│   ├── pom.xml
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/             # ← Edit files here (hot reload enabled)
│   │   │   │   └── com/managertasks/api/
│   │   │   │       ├── controller/
│   │   │   │       ├── service/
│   │   │   │       ├── entity/
│   │   │   │       └── ...
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   └── target/                   # ← Compiled classes (mounted)
└── database/
    └── migrations/               # SQL files auto-executed
```

## Mounted Volumes Explained

The development docker-compose mounts three volumes:

```yaml
volumes:
  - ./ManagerTasks-Api/src:/app/src              # Source code
  - maven_cache_dev:/root/.m2/repository         # Maven dependencies
  - ./ManagerTasks-Api/target:/app/target        # Compiled classes
```

### Why These Mounts?

1. **Source Code Mount** (`./src:/app/src`)
   - Changes you make are immediately visible in container
   - DevTools watches this directory for changes
   - When you save, the file is changed in the running container

2. **Maven Cache Mount** (`~/.m2/repository`)
   - Speeds up dependency downloads
   - Prevents re-downloading the same dependencies
   - Persists across container restarts

3. **Target Mount** (`./target:/app/target`)
   - Compiled Java classes stored here
   - DevTools watches for changes in this directory
   - Allows you to see compiled output locally

## Development Workflow

### Option 1: Using IDE with Local Maven (Recommended)

```bash
# Start containers (database only)
docker-compose -f docker-compose.dev.yml up -d postgres

# Run Spring Boot in your IDE
# - Use IDE's built-in Spring Boot runner
# - DevTools works with IDE hot reload
# - Faster feedback loop
```

**Benefits:**
- Full IDE integration
- Immediate hot reload
- Better debugging
- No Docker compilation delays

### Option 2: Using Docker for Full Environment

```bash
# Start all services in containers
docker-compose -f docker-compose.dev.yml up -d

# Edit files locally
nano ManagerTasks-Api/src/main/java/.../YourFile.java

# Save file → Application restarts automatically
```

**Benefits:**
- Complete isolation
- Exactly matches production environment
- No local Java installation needed

## Watching Logs

### Real-time Logs (All Services)

```bash
docker-compose -f docker-compose.dev.yml logs -f
```

### API Logs Only

```bash
docker-compose -f docker-compose.dev.yml logs -f api
```

### PostgreSQL Logs Only

```bash
docker-compose -f docker-compose.dev.yml logs -f postgres
```

### Last 100 Lines of API Logs

```bash
docker-compose -f docker-compose.dev.yml logs --tail=100 api
```

### Watch for Specific Log Pattern

```bash
docker-compose -f docker-compose.dev.yml logs -f api | grep -i "error\|exception"
```

## DevTools Configuration

The development setup uses these DevTools settings (in docker-compose.dev.yml):

```yaml
environment:
  SPRING_DEVTOOLS_RESTART_ENABLED: "true"
  SPRING_DEVTOOLS_RESTART_POLL_INTERVAL: "1000"        # Check every 1 second
  SPRING_DEVTOOLS_RESTART_QUIET_PERIOD: "400"          # Wait 400ms after change
```

### What These Mean

- **RESTART_ENABLED**: Turns on automatic restart feature
- **POLL_INTERVAL**: How often to check for file changes (1000ms = 1 second)
- **QUIET_PERIOD**: Wait this long after changes stop before restarting (400ms)

## Common Development Tasks

### View Database

```bash
docker exec -it managertasks_postgres_db_dev psql -U admin -d managertasks_db

# Inside psql:
\dt                    # List tables
SELECT * FROM users;   # View users
\q                     # Exit
```

### Reset Database

```bash
# Keep containers running, reset data
docker-compose -f docker-compose.dev.yml exec postgres \
  psql -U admin -d managertasks_db -c "DROP TABLE todos; DROP TABLE users;"

# Or completely reset (stops containers, removes volumes)
docker-compose -f docker-compose.dev.yml down -v
docker-compose -f docker-compose.dev.yml up -d
```

### Rebuild Maven Cache

```bash
# Clear local Maven cache in container
docker-compose -f docker-compose.dev.yml exec api \
  mvn dependency:purge-local-repository

# Or rebuild everything
docker-compose -f docker-compose.dev.yml build api
docker-compose -f docker-compose.dev.yml up -d api
```

### Change Java Logging Level

Edit `docker-compose.dev.yml`:

```yaml
environment:
  LOGGING_LEVEL_COM_MANAGERTASKS_API: TRACE  # More verbose
  # or
  LOGGING_LEVEL_COM_MANAGERTASKS_API: WARN   # Less verbose
```

Then restart:
```bash
docker-compose -f docker-compose.dev.yml up -d api
```

### Change Database Credentials

Edit `.env.dev`:

```env
POSTGRES_USER=myuser
POSTGRES_PASSWORD=mypassword
```

Then restart:
```bash
docker-compose -f docker-compose.dev.yml down -v
docker-compose -f docker-compose.dev.yml up -d
```

## Troubleshooting

### Application Not Restarting After Code Changes

**Symptoms**: Saved code changes aren't reflected

**Solutions**:

1. Check if DevTools is enabled:
   ```bash
   docker-compose -f docker-compose.dev.yml logs api | grep -i devtools
   ```

2. Verify file is actually saved in container:
   ```bash
   docker exec managertasks_api_dev ls -la /app/src/main/java/com/managertasks/api/controller/
   ```

3. Restart container:
   ```bash
   docker-compose -f docker-compose.dev.yml restart api
   ```

### Port Already in Use

```bash
# Change in .env.dev
API_PORT=8081
POSTGRES_PORT=5433

# Or kill existing process
lsof -i :8080
kill -9 <PID>
```

### Maven Dependencies Not Downloading

```bash
# Clear cache and rebuild
docker-compose -f docker-compose.dev.yml down -v
docker-compose -f docker-compose.dev.yml build --no-cache api
docker-compose -f docker-compose.dev.yml up -d
```

### Container Keeps Restarting

**Cause**: Compilation errors in your code

**Solution**:

1. Check logs for errors:
   ```bash
   docker-compose -f docker-compose.dev.yml logs api | tail -50
   ```

2. Fix syntax errors in IDE
3. Save file
4. Container will restart automatically once fixed

### IDE Not Syncing with Container

**If using IDE hot reload AND Docker containers:**

1. Disable IDE hot reload
2. Use only Docker's DevTools
3. Or run Spring Boot in IDE only, Docker for DB only

## Performance Tips

### Faster Rebuilds

1. **Use IDE + Docker for DB**
   ```bash
   # Only start PostgreSQL in Docker
   docker-compose -f docker-compose.dev.yml up -d postgres

   # Run Spring Boot in IDE (faster)
   ```

2. **Limit DevTools Poll Interval**
   - Increase `SPRING_DEVTOOLS_RESTART_POLL_INTERVAL` to 2000 (2 seconds)
   - Reduces CPU usage, slightly slower feedback

3. **Cache Maven Dependencies**
   - First run downloads all dependencies (~200MB)
   - Subsequent runs are instant (cached)

### Disk Space

```bash
# Check Docker volume usage
docker system df

# Remove unused volumes
docker volume prune

# Remove unused images
docker image prune
```

## Stop Development Environment

```bash
# Stop all containers (keeps volumes/data)
docker-compose -f docker-compose.dev.yml down

# Stop and remove all data
docker-compose -f docker-compose.dev.yml down -v

# Stop only API
docker-compose -f docker-compose.dev.yml stop api
```

## Switching Between Development and Production

### Run Development
```bash
docker-compose -f docker-compose.dev.yml up -d
```

### Switch to Production
```bash
docker-compose -f docker-compose.dev.yml down -v
docker-compose up -d
```

### Run Both (Different Ports)
```bash
# Terminal 1: Development
docker-compose -f docker-compose.dev.yml -p managertasks-dev up

# Terminal 2: Production (if needed)
API_PORT=8081 docker-compose -p managertasks-prod up
```

## Next Steps

1. **Start development environment**: `docker-compose -f docker-compose.dev.yml up -d`
2. **Edit a file**: Modify `AuthController.java` or any other file
3. **Watch logs**: `docker-compose -f docker-compose.dev.yml logs -f api`
4. **Save and test**: Changes automatically applied!

## References

- [Spring Boot DevTools Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.devtools)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Maven Spring Boot Plugin](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/)

## Common Commands Cheat Sheet

```bash
# Start
docker-compose -f docker-compose.dev.yml up -d

# Logs
docker-compose -f docker-compose.dev.yml logs -f api

# Stop
docker-compose -f docker-compose.dev.yml down

# Restart API (force restart)
docker-compose -f docker-compose.dev.yml restart api

# View database
docker exec -it managertasks_postgres_db_dev psql -U admin -d managertasks_db

# Execute command in API container
docker exec managertasks_api_dev bash

# Remove everything
docker-compose -f docker-compose.dev.yml down -v
```

Happy developing! 🚀
