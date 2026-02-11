# Docker Setup for ManagerTasks API

This document explains how to run the ManagerTasks API and PostgreSQL database using Docker Compose.

## Prerequisites

- Docker Engine 20.10+
- Docker Compose 2.0+
- Git

## Project Structure

```
.
├── Dockerfile                 # Multi-stage build for Spring Boot API
├── docker-compose.yml         # Docker Compose configuration
├── .env                       # Development environment variables
├── .env.prod                  # Production environment variables
├── ManagerTasks-Api/          # Spring Boot application source code
├── database/
│   ├── Dockerfile            # PostgreSQL Dockerfile (legacy, not used in main docker-compose)
│   ├── docker-compose.yml    # Legacy PostgreSQL-only compose file
│   └── migrations/           # SQL migration files for database initialization
└── README.md
```

## Quick Start (Development)

### 1. Start the Services

From the project root directory, run:

```bash
docker-compose up -d
```

This will:
- Build the Spring Boot API image
- Start PostgreSQL database container
- Start the API container
- Initialize the database with migration scripts
- Wait for PostgreSQL health check before starting the API

### 2. Verify Services are Running

```bash
# Check container status
docker-compose ps

# View logs
docker-compose logs -f

# View API logs only
docker-compose logs -f api

# View PostgreSQL logs only
docker-compose logs -f postgres
```

### 3. Access the Application

- **API Base URL**: `http://localhost:8080`
- **PostgreSQL**: `localhost:5432`

### 4. Stop the Services

```bash
# Stop all containers
docker-compose down

# Stop and remove volumes (deletes database data)
docker-compose down -v
```

## API Endpoints

### Authentication

- **Sign Up**: `POST http://localhost:8080/api/v1/auth/signup`
  ```json
  {
    "name": "John Doe",
    "email": "john@example.com",
    "password": "StrongPass@123"
  }
  ```

- **Login**: `POST http://localhost:8080/api/v1/auth/login`
  ```json
  {
    "email": "john@example.com",
    "password": "StrongPass@123"
  }
  ```

## Configuration

### Development Environment (.env)

The `.env` file contains default values for development:

```env
POSTGRES_DB=managertasks_db
POSTGRES_USER=admin
POSTGRES_PASSWORD=admin123456
POSTGRES_PORT=5432
API_PORT=8080
JWT_SECRET=defaultSecretKeyForDevelopmentOnlyChangeInProduction
```

### Production Environment (.env.prod)

For production deployments, use `.env.prod`:

```bash
docker-compose --env-file .env.prod up -d
```

**Important**: Before deploying to production, update:
- `POSTGRES_PASSWORD` - Use a strong password
- `JWT_SECRET` - Use a secure random string
- `POSTGRES_USER` - Consider renaming from default `admin`

## Database Management

### View Database

Connect to PostgreSQL directly:

```bash
docker exec -it managertasks_postgres_db psql -U admin -d managertasks_db
```

Common PostgreSQL commands:
```sql
-- List tables
\dt

-- View users table
SELECT * FROM users;

-- View todos table
SELECT * FROM todos;

-- Exit
\q
```

### Database Migrations

SQL migration files are located in `database/migrations/`:
- `2025-02-10.01.table-for-user.sql` - Creates users table
- `2025-02-10.02.create-table-for-todo.sql` - Creates todos table

These are automatically executed when PostgreSQL starts.

### Reset Database

To reset the database and remove all data:

```bash
docker-compose down -v
docker-compose up -d
```

## Troubleshooting

### Port Already in Use

If port 8080 or 5432 are already in use:

```bash
# Change ports in .env file
API_PORT=8081
POSTGRES_PORT=5433

# Or specify via command line
docker-compose -e API_PORT=8081 -e POSTGRES_PORT=5433 up -d
```

### Container Won't Start

Check logs for errors:

```bash
docker-compose logs api

# Or for detailed PostgreSQL logs
docker-compose logs postgres
```

### Database Connection Errors

Ensure PostgreSQL is healthy before API connects:

```bash
# Check health status
docker-compose ps

# Wait for PostgreSQL health check to pass (status: healthy)
# API will automatically wait and retry
```

### Rebuild Application

If you've made changes to the source code:

```bash
# Rebuild the API image
docker-compose build api

# Start with rebuilt image
docker-compose up -d api
```

### Clear Everything

To completely remove all containers, images, and volumes:

```bash
docker-compose down -v
docker rmi managertasks-api:latest
```

## Environment Variables Reference

| Variable | Default | Description |
|----------|---------|-------------|
| `POSTGRES_DB` | managertasks_db | Database name |
| `POSTGRES_USER` | admin | Database user |
| `POSTGRES_PASSWORD` | admin123456 | Database password |
| `POSTGRES_PORT` | 5432 | PostgreSQL port |
| `API_PORT` | 8080 | API port |
| `JWT_SECRET` | development key | JWT signing secret |
| `LOGGING_LEVEL_ROOT` | INFO | Root logging level |
| `LOGGING_LEVEL_COM_MANAGERTASKS_API` | DEBUG | Application logging level |

## Services Details

### PostgreSQL Service

- **Image**: `postgres:16-alpine`
- **Container Name**: `managertasks_postgres_db`
- **Port**: 5432
- **Volume**: `postgres_data` (persists data)
- **Health Check**: Verifies database readiness every 10 seconds

### Spring Boot API Service

- **Image**: Built from `Dockerfile`
- **Container Name**: `managertasks_api`
- **Port**: 8080
- **Depends On**: PostgreSQL (waits for health check)
- **Health Check**: HTTP endpoint check every 30 seconds

## Network

Both services communicate via a bridge network named `managertasks_network`. This allows:
- API to connect to PostgreSQL using hostname `postgres`
- Service isolation from host network
- Easy service discovery

## Logs and Debugging

### Real-time Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f api

# Last 100 lines
docker-compose logs --tail=100 api
```

### Access API Logs

Logs are printed to the container stdout in ECS format (Spring Boot 4 feature).

## Development Workflow

1. **Make code changes** in `ManagerTasks-Api/`
2. **Rebuild the API image**: `docker-compose build api`
3. **Restart the API**: `docker-compose up -d api`
4. **Check logs**: `docker-compose logs -f api`

## Production Deployment

For production deployment:

1. Use `.env.prod` with secure credentials
2. Set `SPRING_JPA_HIBERNATE_DDL_AUTO=validate` (read-only mode)
3. Update JWT_SECRET to a strong random value
4. Consider using environment-specific databases
5. Set up proper monitoring and logging

Example production command:
```bash
docker-compose --env-file .env.prod -p managertasks-prod up -d
```

## Next Steps

- Review the API documentation in `DOCS/01.init.md`
- Check database schema in `database/migrations/`
- Test API endpoints using curl or Postman
- Configure your JWT_SECRET for authentication

## Support

For issues:
1. Check logs: `docker-compose logs`
2. Verify PostgreSQL is healthy: `docker-compose ps`
3. Ensure ports are available
4. Review application properties: `ManagerTasks-Api/src/main/resources/application.properties`