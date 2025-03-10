# Domain Entity Creation Patterns

Tài liệu này mô tả các pattern khác nhau để khởi tạo domain entities và khi nào nên sử dụng chúng.

## 1. Abstract Builder Pattern

Sử dụng khi bạn có nhiều entities với các validation logic tương tự nhau.

```java
// Base abstract builder
public abstract class BaseBuilder<T, B extends BaseBuilder<T, B>> {
    protected abstract T build();
    protected abstract B self();

    // Common validation methods
    protected void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }
}

// Sử dụng cho Product
public class Product {
    public static class Builder extends BaseBuilder<Product, Builder> {
        private ProductId id;
        private ProductName name;
        private Money price;

        @Override
        protected Builder self() {
            return this;
        }

        public Builder name(String name) {
            this.name = ProductName.of(name);
            return self();
        }

        @Override
        public Product build() {
            validateNotNull(name, "name");
            validateNotNull(price, "price");
            return new Product(this);
        }
    }
}
```

### Ưu điểm:

- Tái sử dụng validation logic
- Type-safe
- Dễ mở rộng

### Nhược điểm:

- Phức tạp hơn các approaches khác
- Có thể overkill cho các entities đơn giản

## 2. Record Pattern (Java 16+)

Phù hợp cho các entities đơn giản với validation đơn giản.

```java
// Value Objects
public record ProductName(String value) {
    public ProductName {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }
}

// Domain Entity using Record
public record Product(
    ProductId id,
    ProductName name,
    Money price,
    Quantity quantity
) {
    public Product {
        Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(price, "Price cannot be null");
        Objects.requireNonNull(quantity, "Quantity cannot be null");
    }

    // Factory method
    public static Product create(String name, BigDecimal price, int quantity) {
        return new Product(
            null,
            ProductName.of(name),
            Money.of(price),
            Quantity.of(quantity)
        );
    }

    // Business methods
    public boolean isInStock() {
        return quantity.isInStock();
    }
}
```

### Ưu điểm:

- Ngắn gọn, dễ đọc
- Immutable by default
- Tự động tạo equals(), hashCode(), toString()

### Nhược điểm:

- Yêu cầu Java 16+
- Ít linh hoạt trong validation phức tạp
- Không thể kế thừa

## 3. Factory Method Pattern

Phù hợp khi có nhiều cách khởi tạo object khác nhau.

```java
public class Product {
    private final ProductId id;
    private final ProductName name;
    private final Money price;
    private final Quantity quantity;

    private Product(ProductId id, ProductName name, Money price, Quantity quantity) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.price = Objects.requireNonNull(price, "Price cannot be null");
        this.quantity = Objects.requireNonNull(quantity, "Quantity cannot be null");
    }

    // Factory methods
    public static Product createNew(String name, BigDecimal price, int quantity) {
        return new Product(
            null,
            ProductName.of(name),
            Money.of(price),
            Quantity.of(quantity)
        );
    }

    public static Product from(ProductId id, String name, BigDecimal price, int quantity) {
        return new Product(
            id,
            ProductName.of(name),
            Money.of(price),
            Quantity.of(quantity)
        );
    }
}
```

### Ưu điểm:

- Rõ ràng về ý định sử dụng
- Dễ đặt tên có ý nghĩa cho các methods
- Linh hoạt trong việc tạo objects

### Nhược điểm:

- Có thể dẫn đến nhiều factory methods
- Khó maintain khi có nhiều parameters

## 4. Constructor Pattern

Phù hợp cho các entities đơn giản với ít fields.

```java
public class Product {
    private final ProductId id;
    private final ProductName name;
    private final Money price;
    private final Quantity quantity;

    public Product(String name, BigDecimal price, int quantity) {
        this(null, name, price, quantity);
    }

    public Product(ProductId id, String name, BigDecimal price, int quantity) {
        this.id = id;
        this.name = ProductName.of(name);
        this.price = Money.of(price);
        this.quantity = Quantity.of(quantity);

        validateProduct();
    }

    private void validateProduct() {
        // Additional validation logic
    }
}
```

### Ưu điểm:

- Đơn giản, dễ hiểu
- Ít boilerplate code
- Dễ maintain

### Nhược điểm:

- Khó xử lý optional parameters
- Có thể dẫn đến constructor overloading
- Khó đọc khi có nhiều parameters

## Hướng dẫn Sử dụng

### 1. Chọn Pattern Phù hợp

- **Sử dụng Builder khi**:

  - Object có nhiều optional parameters
  - Cần validation phức tạp
  - Cần type safety cao

- **Sử dụng Record khi**:

  - Entity đơn giản
  - Cần immutability
  - Không cần kế thừa

- **Sử dụng Factory khi**:

  - Có nhiều cách tạo object
  - Cần clear intent trong việc tạo object
  - Có logic phức tạp trong việc tạo object

- **Sử dụng Constructor khi**:
  - Entity đơn giản với ít fields
  - Validation đơn giản
  - Không cần optional parameters

### 2. Ví dụ Thực tế

```java
// Simple value object - Use Record
public record Address(String street, String city, String country) {}

// Complex business logic - Use Factory
public class Order {
    public static Order createNewOrder(Customer customer, List<OrderItem> items) {
        // Complex creation logic
    }
}

// Many optional fields - Use Builder
public class Customer {
    public static class Builder {
        // Many optional fields
    }
}

// Basic entity - Use Constructor
public class Category(String name, String description) {
    // Simple validation
}
```

### 3. Best Practices

1. **Consistency**:

   - Chọn một approach chính và sử dụng nhất quán
   - Document rõ lý do chọn approach đó

2. **Validation**:

   - Luôn validate input
   - Throw exceptions có ý nghĩa
   - Sử dụng Value Objects cho các field quan trọng

3. **Immutability**:

   - Ưu tiên immutable objects
   - Sử dụng final fields
   - Tránh setters không cần thiết

4. **Documentation**:
   - Comment rõ ràng về cách sử dụng
   - Giải thích các validation rules
   - Cung cấp ví dụ sử dụng

## Kết luận

Không có một pattern "đúng" cho mọi trường hợp. Việc lựa chọn pattern phụ thuộc vào:

- Độ phức tạp của entity
- Yêu cầu về validation
- Số lượng fields
- Cách sử dụng entity trong codebase

Hãy chọn pattern đơn giản nhất có thể mà vẫn đáp ứng được yêu cầu của bạn.
