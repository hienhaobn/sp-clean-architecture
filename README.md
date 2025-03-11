# Spring Boot Clean Architecture Project

This project demonstrates the implementation of Clean Architecture in a Spring Boot application, providing a template for building robust and maintainable microservices.

## Architecture Overview

The project follows the Clean Architecture principles, separating the application into distinct layers:

- **Domain Layer**: Contains business logic, entities, and business rules
- **Use Case Layer**: Implements application-specific business rules
- **Adapter Layer**: Connects the use cases with external systems
- **Framework Layer**: Provides frameworks and tools

### Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── techsolify/
│   │           └── aquapure/
│   │               ├── adapter/            # Adapters (Controllers, Repositories, DTOs)
│   │               │   ├── common/         # Common classes for adapters
│   │               │   ├── controller/     # REST API controllers
│   │               │   ├── dto/            # Data Transfer Objects
│   │               │   └── mapper/         # Object mappers
│   │               ├── aspect/             # Aspect-Oriented Programming components
│   │               ├── config/             # Application configuration
│   │               ├── domain/             # Domain model and business logic
│   │               │   ├── entity/         # Domain entities
│   │               │   ├── exception/      # Domain-specific exceptions
│   │               │   ├── repository/     # Repository interfaces
│   │               │   ├── usecase/        # Use case implementations
│   │               │   └── vo/             # Value Objects
│   │               └── infrastructure/     # Infrastructure implementations
│   └── resources/                          # Application resources
└── test/                                   # Test classes
```

## Features

- **RESTful API**: Comprehensive API for resource management with standardized responses
- **Clean Architecture**: Separation of concerns for maintainability and testability
- **Swagger/OpenAPI**: API documentation and testing interface
- **Exception Handling**: Global exception handling with standardized error responses
- **Validation**: Input validation with detailed error messages
- **Value Objects**: Immutable value objects for domain concepts
- **Aspect-Oriented Programming**: Cross-cutting concerns handled via AOP
- **Distributed Caching**: Redis-based caching for improved performance
- **Full-Text Search**: Elasticsearch integration for powerful search capabilities
- **Object Storage**: MinIO integration for file storage and management
- **Docker Support**: Complete containerization with Docker Compose
- **Health Checks**: Built-in health monitoring for all services
- **Environment-based Configuration**: Separate configurations for development and production

## Prerequisites

### For Testing/Running Only
- Docker and Docker Compose

### For Development
- Java 21 or higher
- Maven 3.6.x or higher (or use the included Maven wrapper)
- Docker and Docker Compose
- Git
- IDE (IntelliJ IDEA recommended)

### Optional Tools
- PostgreSQL client (for direct database access)
- Redis client (for cache inspection)
- Elasticsearch client (for search index management)
- MinIO client (for object storage management)
- Node.js and npm (for code formatting)

Note: When using Docker Compose for running the application (`docker compose up -d`), you don't need Java or Maven installed locally. However, for local development with hot reload and debugging capabilities, Java 21 is required.

## Getting Started

### Automated Setup (Recommended for New Developers)

We provide an automated setup script that will check your environment, install dependencies, and set up the project for you.

1. Clone the repository:

   ```bash
   git clone https://github.com/yourusername/sp-clean-architecture.git
   cd sp-clean-architecture
   ```

2. Run the setup script:

   ```bash
   ./scripts/setup.sh
   ```

   The script will:

   - Check if you have all required tools installed (Java, Maven, Docker, Git, Node.js)
   - Install npm dependencies (for code formatting)
   - Offer to set up PostgreSQL using Docker (recommended)
   - Configure Git hooks for code quality
   - Compile the project
   - Format the code

3. After setup completes, you can start the application:

   ```bash
   ./mvnw spring-boot:run
   ```

   Or using Docker:

   ```bash
   docker-compose up -d
   ```

4. Access the API documentation:
   ```
   http://localhost:8080/swagger-ui.html
   ```

### Manual Setup

If you prefer to set up the project manually instead of using the automated script:

1. Clone the repository:

   ```bash
   git clone https://github.com/yourusername/sp-clean-architecture.git
   cd sp-clean-architecture
   ```

2. Configure database connection in `application.properties`:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/yourdb
   spring.datasource.username=yourusername
   spring.datasource.password=yourpassword
   ```

3. Install npm dependencies (required for code formatting):

   ```bash
   npm install
   ```

4. Set up Git hooks (for code quality checks):

   ```bash
   git config core.hooksPath .githooks
   chmod +x .githooks/pre-commit
   ```

5. Build the project:

   ```bash
   mvn clean install
   ```

6. Format the code:

   ```bash
   ./scripts/format.sh
   ```

7. Run the application:
   ```bash
   mvn spring-boot:run
   ```

### Using Docker

The project includes Docker configuration for easy setup of all required services:

1. Ensure Docker and Docker Compose are installed on your system

2. Start all services:

   ```bash
   docker compose up -d
   ```

   This will start the following services:
   - Application (Spring Boot) on port 8089
   - PostgreSQL database on port 5438
   - Elasticsearch on ports 9200 and 9300
   - MinIO (Object Storage) on ports 9000 (API) and 9001 (Console)
   - Redis on port 6379

3. Access the services:
   - Application API: http://localhost:8089
   - MinIO Console: http://localhost:9001
   - Elasticsearch: http://localhost:9200
   - PostgreSQL: localhost:5438
   - Redis: localhost:6379

4. Stop all services:
   ```bash
   docker compose down
   ```

5. View service logs:
   ```bash
   docker compose logs -f [service_name]
   ```
   Available services: app, db, elasticsearch, minio, redis

6. Rebuild and restart a specific service:
   ```bash
   docker compose up -d --build [service_name]
   ```

### Environment Variables

The application uses environment variables for configuration. Create a `.env.dev` file for development:

```properties
# Application
APP_ENV=dev
SERVER_PORT=8089

# Database
POSTGRES_DB=aquapure
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

# MinIO
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin
MINIO_BUCKET_NAME=aquapure

# Redis
REDIS_PASSWORD=redis
```

For production, create a `.env.prod` file with secure values.

## Onboarding for New Developers

If you're new to this project, follow these steps to get started quickly:

### Environment Setup

1. **Development Tools**:

   - Install [IntelliJ IDEA](https://www.jetbrains.com/idea/) (recommended) or Eclipse
   - Install [Postman](https://www.postman.com/) for API testing
   - Install [pgAdmin](https://www.pgadmin.org/) for PostgreSQL management

2. **Project Configuration**:

   - Configure your IDE with the project's code style settings
   - Enable annotation processing in your IDE for Lombok
   - Set up EditorConfig plugin

3. **Database Setup**:
   - Create a local PostgreSQL database named `aquapure`
   - Run the SQL scripts in `src/main/resources/db/migration` to initialize the schema
   - Load sample data using `src/main/resources/db/data.sql` (optional)

### Understanding the Project

1. **Key Files to Review**:

   - `src/main/java/com/techsolify/aquapure/AquapureApplication.java`: Main application entry point
   - `src/main/resources/application.properties`: Application configuration
   - `src/main/java/com/techsolify/aquapure/adapter/common/ApiResponse.java`: Standard response format
   - `src/main/java/com/techsolify/aquapure/adapter/controller/ProductController.java`: Example API controller

2. **Architecture Exploration**:
   - Understand domain entities in `src/main/java/com/techsolify/aquapure/domain/entity`
   - Review use cases in `src/main/java/com/techsolify/aquapure/domain/usecase`
   - Explore value objects in `src/main/java/com/techsolify/aquapure/domain/vo`
   - Examine exception handling in `src/main/java/com/techsolify/aquapure/config/CustomExceptionResolver.java`

### Development Workflow

1. **Running the Application**:

   - **Option 1: Full Docker Environment** (recommended for testing):
     ```bash
     docker compose up -d    # Start all services including the application
     docker compose logs -f  # Watch logs
     ```

   - **Option 2: Local Development with IDE** (recommended for development):
     ```bash
     # First, start required services in Docker (database, cache, search, storage)
     docker compose up -d db elasticsearch minio redis

     # Then run the application either through:
     # a) Your IDE: Run AquapureApplication.java (recommended for development)
     # b) Maven: mvn spring-boot:run
     ```

     Benefits of local development:
     - Hot reload capabilities
     - Better debugging experience
     - Breakpoints support
     - Direct log viewing in IDE
     - Code completion and navigation

   Note: For local development, ensure you have Java 21 installed and properly configured in your IDE.

2. **Working with Services**:

   - **Database (PostgreSQL)**:
     - Connect using: localhost:5438, database: aquapure
     - Default credentials: postgres/postgres
     - View logs: `docker compose logs -f db`

   - **Elasticsearch**:
     - Access: http://localhost:9200
     - View indices: http://localhost:9200/_cat/indices
     - View logs: `docker compose logs -f elasticsearch`

   - **MinIO**:
     - Console: http://localhost:9001
     - Default credentials: minioadmin/minioadmin
     - View logs: `docker compose logs -f minio`

   - **Redis**:
     - Connect using: localhost:6379
     - Default password: redis
     - View logs: `docker compose logs -f redis`

3. **Making Changes**:

   - Create a feature branch from `main`: `git checkout -b feature/your-feature-name`
   - Implement your changes following the project's code style
   - Write unit tests for your changes
   - Verify code quality with `mvn verify`
   - Test with all required services using Docker Compose
   - Commit with descriptive messages following conventional commits

4. **Testing Your Changes**:

   - Run unit tests: `mvn test`
   - Run integration tests: `mvn verify -P integration-test`
   - Test the API using Swagger UI at http://localhost:8089/swagger-ui.html
   - Verify service integration using health endpoints at http://localhost:8089/actuator/health

5. **Debugging**:

   - View application logs: `docker compose logs -f app`
   - Access service-specific logs using `docker compose logs -f [service_name]`
   - Use your IDE's debugger with the application running locally
   - Monitor service health at http://localhost:8089/actuator/health

### Common Development Tasks

1. **Adding a New Entity**:

   - Create a domain entity in `domain/entity`
   - Create corresponding value objects in `domain/vo`
   - Define repository interface in `domain/repository`
   - Implement use cases in `domain/usecase`
   - Create DTOs in `adapter/dto`
   - Implement mappers in `adapter/mapper`
   - Create controller in `adapter/controller`

2. **Implementing a New API Endpoint**:

   - Define the endpoint in an existing or new controller
   - Implement required use cases
   - Add Swagger documentation
   - Write tests for the endpoint

3. **Handling Exceptions**:
   - Create domain-specific exceptions in `domain/exception`
   - Update exception handlers if needed

### Troubleshooting

- **Application Won't Start**: Check database connection, port availability
- **Compilation Errors**: Verify dependencies, Java version compatibility
- **Test Failures**: Check test data setup, database state
- **Bean Creation Issues**: Review dependency injection, component scanning

### Getting Help

- Consult the project documentation in the `docs` directory
- Review existing code for examples and patterns
- Reach out to the team lead or senior developers for guidance

## API Endpoints

The API provides the following endpoints:

- **Products API**:

  - `GET /api/products`: Get all products
  - `GET /api/products/{id}`: Get product by ID
  - `POST /api/products`: Create a new product
  - `PUT /api/products/{id}`: Update an existing product
  - `DELETE /api/products/{id}`: Delete a product

- **API Status**:

  - `GET /api/status`: Get API status
  - `GET /api/health`: Get service health status

- **Test Endpoints** (for testing exception handling):
  - `GET /api/test-exceptions/resource-not-found`: Test resource not found exceptions
  - `GET /api/test-exceptions/bad-request`: Test bad request exceptions
  - `GET /api/test-exceptions/missing-parameter`: Test missing parameter errors
  - `GET /api/test-exceptions/invalid-parameter-type`: Test invalid parameter type errors

## Response Format

All API responses follow a standardized format:

```json
{
  "success": true,
  "message": "Operation successful",
  "code": null,
  "data": { ... },
  "timestamp": "2023-06-01 12:34:56"
}
```

Error responses:

```json
{
  "success": false,
  "message": "Error message",
  "code": "ERROR_CODE",
  "errors": ["Detailed error 1", "Detailed error 2"],
  "timestamp": "2023-06-01 12:34:56"
}
```

## Exception Handling

The application implements comprehensive exception handling:

- **ResourceNotFoundException**: Thrown when a requested resource cannot be found
- **BadRequestException**: Thrown for invalid request parameters
- **DataIntegrityViolationException**: Thrown for data integrity violations
- **MethodArgumentNotValidException**: Thrown for validation errors
- **ConstraintViolationException**: Thrown for constraint violations

## Contributing

Please read the [CODE_STYLE.md](CODE_STYLE.md) for details on our code style and contribution process.

## License

This project is licensed under the MIT License - see the LICENSE file for details.
