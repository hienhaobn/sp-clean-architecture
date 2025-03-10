# Contributing Guidelines

## Code Style và Conventions

### Domain Entity Creation

Chúng tôi có các quy ước cụ thể về cách tạo domain entities. Xem chi tiết tại [Domain Entity Creation Patterns](docs/patterns/domain-entity-creation-patterns.md).

Một số điểm chính:

1. **Sử dụng Factory Methods** cho hầu hết các domain entities:

```java
public class Product {
    public static Product createNew(String name, Money price) {
        return new Product(ProductId.generate(), name, price);
    }
}
```

2. **Sử dụng Records** cho value objects:

```java
public record Money(BigDecimal amount) {
    public Money {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
    }
}
```

3. **Sử dụng Builder** chỉ khi thực sự cần thiết:

```java
Customer customer = Customer.builder()
    .name("John Doe")
    .email("john@example.com")
    .address(address)
    .build();
```

### Validation

- Luôn validate input trong constructors hoặc factory methods
- Sử dụng Value Objects thay vì primitive types cho các business concepts
- Throw meaningful exceptions với clear messages

### Immutability

- Tất cả domain entities và value objects phải immutable
- Sử dụng final fields
- Tránh setters trong domain entities

### Testing

- Unit tests bắt buộc cho tất cả domain entities
- Test các validation rules
- Test các business methods

Xem thêm chi tiết và ví dụ trong [Domain Entity Creation Patterns](docs/patterns/domain-entity-creation-patterns.md).
