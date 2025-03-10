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

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6.x or higher
- PostgreSQL database

### Installation

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

3. Build the project:
   ```bash
   mvn clean install
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

5. Access the API documentation:
   ```
   http://localhost:8080/swagger-ui.html
   ```

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
   - Run with IDE: Execute the main class `AquapureApplication.java`
   - Run with Maven: `mvn spring-boot:run`
   - Run with debug mode: Add breakpoints and use your IDE's debug configuration

2. **Making Changes**:
   - Create a feature branch from `main`: `git checkout -b feature/your-feature-name`
   - Implement your changes following the project's code style
   - Write unit tests for your changes
   - Verify code quality with `mvn verify`
   - Commit with descriptive messages following conventional commits

3. **Testing Your Changes**:
   - Run unit tests: `mvn test`
   - Test the API using Swagger or Postman
   - Verify exception handling with the test endpoints

4. **Code Review Process**:
   - Push your branch and create a pull request
   - Address review comments
   - Ensure all checks pass before merging

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