# Task 07: Deployment & Documentation

## 🎯 Objective
Deploy the application to production and create comprehensive documentation.

---

## 🚀 Deployment Options

### Option 1: Traditional Server (VPS)
### Option 2: Cloud Platform (AWS, Azure, GCP)
### Option 3: Container (Docker)
### Option 4: Platform as a Service (Heroku, Railway)

---

## 🐳 Docker Deployment (Recommended)

### 1. Create Dockerfile

**File**: `Dockerfile`

```dockerfile
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY src ./src

# Build application
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Create uploads directory
RUN mkdir -p /app/uploads

# Expose port
EXPOSE 8080

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 2. Create docker-compose.yml

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: homeheaven-db
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_PASSWORD}
      MYSQL_DATABASE: homeheaven
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
    networks:
      - homeheaven-network

  app:
    build: .
    container_name: homeheaven-app
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/homeheaven
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
    ports:
      - "8080:8080"
    depends_on:
      - mysql
    volumes:
      - ./uploads:/app/uploads
    networks:
      - homeheaven-network

volumes:
  mysql-data:

networks:
  homeheaven-network:
    driver: bridge
```

### 3. Environment Variables

**File**: `.env`

```env
DB_PASSWORD=your_secure_password
JWT_SECRET=your_jwt_secret_key_min_256_bits
```

### 4. Deploy with Docker

```bash
# Build and start containers
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop containers
docker-compose down

# Rebuild after changes
docker-compose up -d --build
```

---

## ☁️ AWS Deployment

### Architecture

```
Internet
    ↓
Application Load Balancer (ALB)
    ↓
EC2 Instance (Spring Boot App)
    ↓
RDS MySQL Database
    ↓
S3 (for images)
```

### Steps

1. **Create RDS MySQL Instance**
```bash
# Configure RDS
- Engine: MySQL 8.0
- Instance: db.t3.micro (free tier)
- Storage: 20GB
- Public access: No
- VPC: Default
```

2. **Create EC2 Instance**
```bash
# Launch EC2
- AMI: Amazon Linux 2
- Instance type: t2.micro (free tier)
- Security group: Allow 8080, 22
```

3. **Deploy Application**
```bash
# SSH to EC2
ssh -i key.pem ec2-user@your-ec2-ip

# Install Java 21
sudo yum install java-21-amazon-corretto

# Upload JAR
scp -i key.pem target/*.jar ec2-user@your-ec2-ip:~/

# Run application
java -jar app.jar --spring.profiles.active=prod
```

4. **Setup S3 for Images**
```bash
# Create S3 bucket
aws s3 mb s3://homeheaven-images

# Update application to use S3
# Implement S3 file storage service
```

---

## 🌐 Domain & SSL

### 1. Domain Setup

```bash
# Purchase domain (e.g., homeheaven.com)
# Point DNS to your server IP

# A Record
homeheaven.com → your-server-ip
www.homeheaven.com → your-server-ip
```

### 2. SSL Certificate (Let's Encrypt)

```bash
# Install Certbot
sudo apt-get install certbot

# Get certificate
sudo certbot certonly --standalone -d homeheaven.com -d www.homeheaven.com

# Auto-renewal
sudo certbot renew --dry-run
```

### 3. Nginx Reverse Proxy

**File**: `/etc/nginx/sites-available/homeheaven`

```nginx
server {
    listen 80;
    server_name homeheaven.com www.homeheaven.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name homeheaven.com www.homeheaven.com;

    ssl_certificate /etc/letsencrypt/live/homeheaven.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/homeheaven.com/privkey.pem;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /uploads/ {
        alias /app/uploads/;
        expires 30d;
        add_header Cache-Control "public, immutable";
    }
}
```

---

## 📚 Documentation

### 1. API Documentation

**File**: `docs/API.md`

```markdown
# HomeHeaven API Documentation

## Base URL
```
https://api.homeheaven.com
```

## Authentication
All protected endpoints require JWT token in Authorization header:
```
Authorization: Bearer <token>
```

## Endpoints

### Authentication

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "phone": "9876543210",
  "password": "password123",
  "confirmPassword": "password123",
  "pin": "1234"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "username": "john_doe",
  "email": "john@example.com",
  "role": "USER"
}
```

### Properties

#### Search Properties
```http
GET /api/properties/search?city=Mumbai&minRent=10000&maxRent=50000&type=Flat

Response:
[
  {
    "id": 1,
    "name": "Cozy 2BHK Apartment",
    "city": "Mumbai",
    "rent": 25000,
    ...
  }
]
```

[Continue with all endpoints...]
```

### 2. Setup Guide

**File**: `docs/SETUP.md`

```markdown
# HomeHeaven Setup Guide

## Prerequisites
- Java 21
- MySQL 8.0+
- Maven 3.9+

## Local Development

1. Clone repository
```bash
git clone https://github.com/yourusername/homeheaven.git
cd homeheaven
```

2. Configure database
```bash
mysql -u root -p
CREATE DATABASE homeheaven;
```

3. Update application.properties
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/homeheaven
spring.datasource.username=root
spring.datasource.password=your_password
```

4. Run application
```bash
mvn spring-boot:run
```

5. Access application
```
http://localhost:8080
```

[Continue with detailed setup...]
```

### 3. User Guide

**File**: `docs/USER_GUIDE.md`

```markdown
# HomeHeaven User Guide

## Getting Started

### 1. Registration
1. Visit https://homeheaven.com/register.html
2. Fill in your details
3. Set a 4-digit PIN (for password recovery)
4. Click "Create Account"

### 2. Login
1. Visit https://homeheaven.com/login.html
2. Enter username and password
3. Click "Sign In"

### 3. Search Properties
1. On dashboard, use search filters
2. Enter city, rent range, or property type
3. Click "Search"
4. Browse results

[Continue with all features...]
```

---

## 🔧 Production Configuration

### application-prod.properties

```properties
# Server
server.port=8080
server.compression.enabled=true

# Database
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.hikari.maximum-pool-size=10

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

# Logging
logging.level.root=WARN
logging.level.com.homeheaven=INFO
logging.file.name=/var/log/homeheaven/application.log

# Security
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

# File Upload
file.upload-dir=/app/uploads
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=50MB

# Actuator (monitoring)
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
```

---

## 📊 Monitoring & Logging

### 1. Application Monitoring

```java
// Add Spring Boot Actuator
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### 2. Log Aggregation

```bash
# Use ELK Stack or CloudWatch
# Configure log shipping
```

### 3. Health Checks

```bash
# Health endpoint
curl https://homeheaven.com/actuator/health

# Metrics endpoint
curl https://homeheaven.com/actuator/metrics
```

---

## 🔄 CI/CD Pipeline

### GitHub Actions

**File**: `.github/workflows/deploy.yml`

```yaml
name: Deploy to Production

on:
  push:
    branches: [ main ]

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 21
      uses: actions/setup-java@v3
      with:
        java-version: '21'
        distribution: 'temurin'
    
    - name: Build with Maven
      run: mvn clean package -DskipTests
    
    - name: Deploy to server
      uses: appleboy/scp-action@master
      with:
        host: ${{ secrets.SERVER_HOST }}
        username: ${{ secrets.SERVER_USER }}
        key: ${{ secrets.SSH_KEY }}
        source: "target/*.jar"
        target: "/app"
    
    - name: Restart application
      uses: appleboy/ssh-action@master
      with:
        host: ${{ secrets.SERVER_HOST }}
        username: ${{ secrets.SERVER_USER }}
        key: ${{ secrets.SSH_KEY }}
        script: |
          sudo systemctl restart homeheaven
```

---

## ✅ Deployment Checklist

### Pre-Deployment
- [ ] All tests passing
- [ ] Code review completed
- [ ] Security audit done
- [ ] Performance testing done
- [ ] Documentation updated
- [ ] Backup database
- [ ] Environment variables configured

### Deployment
- [ ] Build application
- [ ] Deploy to staging
- [ ] Test on staging
- [ ] Deploy to production
- [ ] Verify deployment
- [ ] Monitor logs
- [ ] Test critical flows

### Post-Deployment
- [ ] Monitor application health
- [ ] Check error logs
- [ ] Verify all features working
- [ ] Monitor performance metrics
- [ ] Update status page
- [ ] Notify team

---

## 🆘 Rollback Plan

```bash
# If deployment fails, rollback to previous version

# Stop current version
sudo systemctl stop homeheaven

# Restore previous JAR
cp /app/backup/app.jar /app/app.jar

# Restore database (if needed)
mysql homeheaven < /backup/homeheaven_backup.sql

# Start application
sudo systemctl start homeheaven

# Verify
curl http://localhost:8080/actuator/health
```

---

**Status**: ⏳ Pending
**Estimated Time**: 6 hours
**Dependencies**: TASK_06 (Testing)
**Completion**: Final Task
