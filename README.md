# Online Storefront Backend

A professional Spring Boot WebFlux backend with comprehensive authentication, authorization, and FakeStore API integration.

## Features

### Authentication & Authorization
- ✅ JWT-based authentication with access and refresh tokens
- ✅ Email verification for new users
- ✅ Forgot password and reset password functionality
- ✅ Role-based access control (RBAC)
- ✅ Secure password change
- ✅ Profile management

### Technical Stack
- **Framework**: Spring Boot 3.2.0 with WebFlux (Reactive)
- **Security**: Spring Security + JWT
- **Database**: PostgreSQL with R2DBC (Reactive)
- **Migration**: Liquibase
- **Email**: Spring Mail
- **External API**: FakeStore API integration
- **Containerization**: Docker & Docker Compose

## API Endpoints

### Authentication Endpoints
```
POST /api/auth/signup           - Register new user
POST /api/auth/login            - Login
POST /api/auth/refresh-token    - Refresh access token
POST /api/auth/verify-email     - Verify email address
POST /api/auth/forgot-password  - Request password reset
POST /api/auth/reset-password   - Reset password with token
```

### User Endpoints (Authenticated)
```
GET  /api/users/me              - Get current user profile
PUT  /api/users/profile         - Update profile
POST /api/users/change-password - Change password
```

### Product Endpoints (Authenticated)
```
GET /api/products               - Get all products
GET /api/products/{id}          - Get product by ID
GET /api/products/category/{category} - Get products by category
GET /api/products/categories    - Get all categories
```

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL 15+ (or use Docker)
- Gmail account for email (or other SMTP server)

### Local Development Setup

1. **Clone the repository**
```bash
git clone <repository-url>
cd online-storefront-backend
```

2. **Configure Database**

Create a PostgreSQL database:
```sql
CREATE DATABASE storefront_db;
```

3. **Configure Email**

Get a Gmail App Password:
- Go to Google Account settings
- Enable 2-Step Verification
- Generate App Password for "Mail"

4. **Update application.yml**

Edit `src/main/resources/application.yml`:
```yaml
spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/storefront_db
    username: your_db_username
    password: your_db_password
  
  liquibase:
    url: jdbc:postgresql://localhost:5432/storefront_db
    user: your_db_username
    password: your_db_password
  
  mail:
    username: your-email@gmail.com
    password: your-app-password

jwt:
  secret: your-256-bit-secret-key-here
```

5. **Run the application**
```bash
mvn spring-boot:run
```

The server will start at `http://localhost:8080`

### Docker Setup

1. **Set environment variables**

Create a `.env` file:
```env
JWT_SECRET=your-256-bit-secret-key
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

2. **Run with Docker Compose**
```bash
docker-compose up -d
```

This will start both PostgreSQL and the backend application.

## Database Schema

The application automatically creates the following tables via Liquibase:

- `users` - User accounts
- `roles` - Available roles (USER, ADMIN, MODERATOR)
- `user_roles` - User-Role mappings
- `refresh_tokens` - Refresh token storage
- `verification_tokens` - Email verification and password reset tokens

## Default Roles

The application comes with three pre-configured roles:
- `ROLE_USER` - Default role for all registered users
- `ROLE_ADMIN` - Administrator role
- `ROLE_MODERATOR` - Moderator role

## Security Features

1. **JWT Tokens**
    - Access Token: 15 minutes expiration
    - Refresh Token: 7 days expiration

2. **Password Security**
    - BCrypt hashing
    - Minimum 6 characters required

3. **Email Verification**
    - 24-hour token expiration
    - Automatic cleanup of expired tokens

4. **CORS Configuration**
    - Configurable allowed origins
    - Supports credentials

## Testing the API

### 1. Register a new user
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'
```

### 2. Verify email (check your email for token)
```bash
curl -X POST http://localhost:8080/api/auth/verify-email \
  -H "Content-Type: application/json" \
  -d '{
    "token": "your-verification-token"
  }'
```

### 3. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "testuser",
    "password": "password123"
  }'
```

### 4. Access protected endpoint
```bash
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer your-access-token"
```

## Project Structure

```
src/main/java/com/storefront/
├── config/              # Configuration classes
├── controller/          # REST controllers
├── dto/                 # Data Transfer Objects
│   ├── request/
│   └── response/
├── entity/              # Database entities
├── exception/           # Custom exceptions
├── repository/          # R2DBC repositories
├── security/            # Security components
├── service/             # Business logic
└── OnlineStorefrontApplication.java

src/main/resources/
├── db/changelog/        # Liquibase migrations
└── application.yml      # Configuration
```

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| JWT_SECRET | Secret key for JWT signing | (required) |
| MAIL_USERNAME | SMTP username | (required) |
| MAIL_PASSWORD | SMTP password | (required) |
| SPRING_R2DBC_URL | R2DBC connection URL | localhost:5432 |
| SPRING_R2DBC_USERNAME | Database username | postgres |
| SPRING_R2DBC_PASSWORD | Database password | postgres |

## Production Considerations

1. **Use strong JWT secrets** (at least 256 bits)
2. **Configure proper CORS origins**
3. **Use environment-specific profiles** (dev, prod)
4. **Enable HTTPS** in production
5. **Set up proper logging**
6. **Configure rate limiting**
7. **Use a production-grade SMTP server**
8. **Implement proper monitoring**

## Troubleshooting

### Database Connection Issues
- Ensure PostgreSQL is running
- Verify connection credentials
- Check if the database exists

### Email Not Sending
- Verify Gmail App Password is correct
- Check if 2-Step Verification is enabled
- Ensure SMTP settings are correct

### JWT Token Issues
- Verify JWT_SECRET is at least 256 bits
- Check token expiration settings
- Ensure proper Authorization header format

## License

This project is for educational purposes.

## Support

For issues and questions, please create an issue in the repository.