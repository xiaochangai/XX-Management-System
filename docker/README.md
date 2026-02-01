# Docker Deployment Guide

Complete Docker deployment configuration for the Enterprise Management System.

## Quick Start

```bash
cd docker
docker-compose up -d
```

## Services

| Service | Port | Description |
|---------|------|-------------|
| MySQL | 3306 | Database |
| Redis | 6379 | Cache |
| Backend | 8080 | Spring Boot API |
| Frontend | 80 | Vue 3 Web App |

## Prerequisites

- Docker 20.10+
- Docker Compose 2.0+

## Usage

### Start all services

```bash
docker-compose up -d
```

### View logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
```

### Stop services

```bash
docker-compose down
```

### Stop and remove volumes (⚠️ deletes all data)

```bash
docker-compose down -v
```

### Rebuild after code changes

```bash
docker-compose build --no-cache
docker-compose up -d
```

## Access Points

- Web UI: http://localhost
- API: http://localhost:8080
- Default Login:
  - Username: `admin`
  - Password: `123456`

## Configuration

### Environment Variables

Backend configuration in `docker-compose.yml`:

| Variable | Default | Description |
|----------|---------|-------------|
| SPRING_DATASOURCE_URL | jdbc:mysql://mysql:3306/smart_admin | Database URL |
| SPRING_DATASOURCE_USERNAME | admin | Database username |
| SPRING_DATASOURCE_PASSWORD | admin123 | Database password |
| SPRING_REDIS_HOST | redis | Redis hostname |

### Database

- MySQL data is persisted in Docker volume `mysql_data`
- Initial schema will be loaded from `init-scripts/` folder

### Redis

- Data is persisted in Docker volume `redis_data`
- AOF persistence is enabled

## Troubleshooting

### Health checks failing

Wait for services to be ready:

```bash
docker-compose ps
```

All services should show `(healthy)`.

### Reset everything

```bash
docker-compose down -v
docker-compose up -d
```

### Port conflicts

If ports are already in use, change them in `docker-compose.yml`:

```yaml
ports:
  - "8081:8080"  # Use 8081 instead of 8080
```

## Production Notes

- Change default passwords
- Use external reverse proxy (nginx/traefik) with SSL
- Configure backups for MySQL and Redis volumes
- Enable log rotation
- Set up proper monitoring
