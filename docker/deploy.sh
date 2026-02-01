#!/bin/bash

# Docker Deployment Script for Enterprise Management System

set -e

echo "=========================================="
echo "Enterprise Management System - Docker Deploy"
echo "=========================================="
echo ""

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
 echo "Error: Docker is not installed"
 exit 1
fi

if ! command -v docker-compose &> /dev/null; then
 echo "Error: Docker Compose is not installed"
 exit 1
fi

echo "Docker and Docker Compose are installed ✓"
echo ""

# Function to clean up
cleanup() {
 echo ""
 echo "Cleaning up..."
 docker-compose down 2>/dev/null || true
}

# Handle Ctrl+C
trap cleanup INT

# Build and start services
echo "Building and starting services..."
docker-compose build --no-cache || {
 echo "Error: Failed to build Docker images"
 exit 1
}

docker-compose up -d || {
 echo "Error: Failed to start services"
 exit 1
}

echo ""
echo "=========================================="
echo "Deployment Complete!"
echo "=========================================="
echo ""
echo "Access Points:"
echo "  - Web Interface: http://localhost"
echo "  - API: http://localhost:8080"
echo "  - MySQL: localhost:3306"
echo "  - Redis: localhost:6379"
echo ""
echo "Default Login:"
echo "  - Username: admin"
echo "  - Password: 123456"
echo ""
echo "View logs: docker-compose logs -f"
echo "Stop: docker-compose down"
echo ""
echo "Waiting for services to be healthy..."
docker-compose ps
