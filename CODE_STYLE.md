# Code Style Guidelines

This document outlines the coding standards and best practices for the Spring Boot Clean Architecture project.

## Guidelines for New Contributors

If you're new to the project, here are some important guidelines to follow:

### First Contributions

1. **Start Small**: Begin with small fixes or improvements to familiarize yourself with the codebase
2. **Follow Existing Patterns**: Match the style and patterns used in the existing code
3. **Ask Questions**: When in doubt, ask instead of assuming
4. **Seek Feedback Early**: Share your approach before implementing major changes

### Code Review Etiquette

1. **Be Receptive to Feedback**: Treat code reviews as learning opportunities
2. **Explain Your Changes**: Provide context about what you changed and why
3. **Respond to All Comments**: Address each code review comment
4. **Show Appreciation**: Thank reviewers for their time and insights

### Common Pitfalls to Avoid

1. **Mixing Concerns**: Keep domain logic separate from adapters
2. **Exposing Domain Objects**: Always use DTOs at API boundaries
3. **Hardcoding Values**: Use configuration or constants for changeable values
4. **Inadequate Testing**: Write tests for all new functionality
5. **Insufficient Documentation**: Document public APIs and complex logic
6. **Magic Strings/Numbers**: Use named constants for better readability
7. **Ignoring Error Handling**: Properly handle all potential exceptions

## Java Code Style

### General Guidelines

- Use 2 spaces for indentation
- Maximum line length: 100 characters
- Use UTF-8 file encoding
- Always add curly braces for control statements (if, for, while, etc.), even for single-line blocks
- Use explicit type declarations instead of 'var' keyword for better readability
- Prefer interfaces over abstract classes when possible
- Composition over inheritance

### Naming Conventions

- **Classes**: PascalCase (e.g., `ProductController`)
- **Interfaces**: PascalCase (e.g., `ProductRepository`)
- **Methods**: camelCase (e.g., `getAllProducts()`)
- **Variables**: camelCase (e.g., `productList`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_RETRY_COUNT`)
- **Packages**: lowercase, no underscores (e.g., `com.techsolify.aquapure.domain`)
- **Files**: Match the name of the primary class or interface (e.g., `ProductController.java`)

### Project-Specific Conventions

- Use plural form for collection resources in REST endpoints (e.g., `/api/products` not `/api/product`)
- Controllers should delegate business logic to use cases/services
- Domain entities should be immutable when possible
- Value objects must be immutable
- Use static factory methods for complex object creation

## Clean Architecture Guidelines

### Layer Dependencies

- Domain layer should not depend on any other layer
- Use case layer can depend only on the domain layer
- Adapter layer can depend on domain and use case layers
- Framework layer can depend on all other layers

### Package Structure

- Package by feature, not by layer within the clean architecture boundaries
- Keep related classes together in packages

### Code Organization

- One primary class/interface per file
- Related utility functions should be grouped in appropriate utility classes
- Group fields, constructors, and methods in a logical order

## Exception Handling

- Use custom exceptions for domain-specific errors
- Always include informative error messages
- Never expose sensitive information in error messages
- Use `ApiResponse` for standardized error responses
- Handle exceptions at the appropriate level

## Documentation

- All public APIs should have Javadoc
- Add comments for complex logic or non-obvious implementations
- Use meaningful variable and method names instead of excessive comments
- Document all exception scenarios

### Javadoc Guidelines

- All public classes, interfaces, and methods must have Javadoc
- Document parameters, return values, and exceptions
- Include examples for complex methods

Example:

```java
/**
 * Retrieves a product by its unique identifier.
 *
 * @param id The product ID
 * @return The product if found
 * @throws ResourceNotFoundException if the product does not exist
 */
public Product getProductById(Long id) {
    // Implementation
}
```

## Testing

- Write unit tests for all business logic
- Use descriptive test names (e.g., `shouldReturnProductWhenValidIdProvided`)
- Each test should focus on a single behavior
- Use appropriate mocking strategies
- Follow the AAA pattern: Arrange, Act, Assert

## Git Workflow

### Commit Messages

Follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

Types:

- `feat`: A new feature
- `fix`: A bug fix
- `docs`: Documentation changes
- `style`: Changes that do not affect the code's meaning (formatting, etc.)
- `refactor`: Code changes that neither fix bugs nor add features
- `test`: Adding or fixing tests
- `chore`: Changes to the build process or auxiliary tools

Examples:

```
feat(product): add endpoint to retrieve products by category
fix(auth): resolve JWT token validation issue
docs: update README with setup instructions
```

## API Design

- Use RESTful principles
- Versioning should be in the URL (e.g., `/api/v1/products`)
- Use HTTP methods appropriately (GET, POST, PUT, DELETE)
- Use HTTP status codes correctly
- Always return standardized responses using the `ApiResponse` class

## IDE Configuration

### IntelliJ IDEA

- Use EditorConfig for basic formatting
- Import and use the project's code style settings
- Enable "Optimize imports on the fly"
- Enable "Add final modifier where possible"

### Eclipse

- Use EditorConfig plugin
- Import the project's code style settings
- Configure "Save Actions" to organize imports and format code

## Code Quality Tools

The project uses the following tools to maintain code quality:

- **Checkstyle**: Enforces code style
- **PMD**: Checks for potential bugs and suboptimal code
- **SpotBugs**: Identifies potential bugs
- **JaCoCo**: Measures test coverage

## Production Code Guidelines

- No unused code or commented-out code in production
- No debug logging in production code (use appropriate log levels)
- No hardcoded configuration values (use properties files or environment variables)
- Handle all exceptions appropriately
- Close all resources properly (use try-with-resources)

## Security Best Practices

- Never store sensitive information in code
- Always validate and sanitize user input
- Use prepared statements for database queries
- Apply least privilege principle
- Implement proper authentication and authorization
- Sanitize data before logging

## Performance Considerations

- Use appropriate data structures for operations
- Be mindful of database query performance
- Consider pagination for large data sets
- Use efficient algorithms
- Profile code when performance is critical
