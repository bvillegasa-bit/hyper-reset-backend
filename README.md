# Hyper Reset API

REST API backend for **Hyper Reset Performance** — a physical diagnosis and rehabilitation platform that connects deportistas (athletes) with coaches for test-based performance tracking.

## Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 22 |
| Framework | Spring Boot 3.4.3 |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| Database | MySQL 8.0 (via JPA / Hibernate 6) |
| Build | Maven |
| Tests | JUnit 5 + Spring Boot Test |

## Features

- **Auth**: Register, login, JWT token with role-based access (DEPORTISTA, COACH, ADMIN)
- **Deportistas**: CRUD management with coach assignment
- **Tests**: 8 physical test types with configurable scoring thresholds
- **Appointments**: Scheduling between deportistas and coaches
- **Dashboard**: Aggregated views for both deportista and coach roles
- **Messaging**: Communication between users
- **Materials**: Upload and manage training materials
- **Reports**: Generate performance reports (COACH/ADMIN)
- **Profile**: Update personal info and change password

## Getting Started

### Prerequisites

- Java 22+
- MySQL 8.0+
- Maven

### Configuration

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hyper_reset
    username: your_user
    password: your_password

app:
  jwt:
    secret: your-jwt-secret-here
    expiration: 86400000
```

### Run

```bash
# Build
./mvnw clean package -DskipTests

# Run
./mvnw spring-boot:run

# Or run the jar
java -jar target/api-0.0.1-SNAPSHOT.jar
```

The API starts at `http://localhost:8080`.

### Run tests

```bash
./mvnw test
```

## API Endpoints

### Auth
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/api/auth/register` | No | Register new user |
| POST | `/api/auth/login` | No | Login, returns JWT |
| GET | `/api/auth/profile` | JWT | Get current user profile |
| PUT | `/api/auth/profile` | JWT | Update profile (nombres, apellidos, correo, telefono, direccion, fechaNacimiento) |
| PATCH | `/api/auth/change-password` | JWT | Change password |

### Deportistas
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/api/deportistas` | COACH/ADMIN | List all |
| GET | `/api/deportistas/{id}` | Any | Get by ID |
| POST | `/api/deportistas` | COACH/ADMIN | Create |
| PUT | `/api/deportistas/{id}` | COACH/ADMIN | Update |
| DELETE | `/api/deportistas/{id}` | ADMIN | Delete |

### Appointments
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/api/citas` | Any | List (context-aware) |
| POST | `/api/citas` | Any | Create |
| PUT | `/api/citas/{id}` | COACH/ADMIN | Update |
| DELETE | `/api/citas/{id}` | ADMIN | Delete |

### Tests
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/api/test-fisicos` | Any | List test sessions |
| POST | `/api/test-fisicos` | COACH | Create session |
| GET | `/api/resultados/tipos-con-estado/{deportistaId}` | Any | Test types with status |

### Dashboard
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/api/dashboard/deportista/{id}` | DEPORTISTA/COACH/ADMIN | Deportista dashboard |
| GET | `/api/dashboard/coach/{id}` | COACH/ADMIN | Coach dashboard |
| GET | `/api/dashboard/actividad?page=0&size=20` | COACH/ADMIN | Paginated activity log |

Full API documentation is available via Swagger UI at `/swagger-ui.html` (if enabled).

## Project Structure

```
src/main/java/com/hyperreset/api/
├── auth/          # JWT filter + security config
├── config/        # CORS, WebMvc, scoring properties
├── controller/    # REST controllers
├── dto/           # Request/Response DTOs
├── entity/        # JPA entities (Usuario, Deportista, Coach, Cita, TestFisico, etc.)
├── exception/     # Global exception handler + custom exceptions
├── repository/    # Spring Data JPA repositories
└── service/       # Business logic layer

src/main/resources/
├── application.yml    # Main config
├── schema.sql         # Database schema
└── migration/         # Flyway migrations
```

## Database

The schema is defined in `schema.sql` and managed via Flyway migrations under `src/main/resources/migration/`.

Key tables: `usuario`, `deportista`, `coach`, `cita`, `test_fisico`, `resultado_test`, `material`, `mensaje`.

## License

MIT
