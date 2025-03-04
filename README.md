# Clean Architecture

## Layers

### 1. Enterprise Business Rules (Entities)
Chứa các business entities và business rule cốt lõi

### 2. Application Business Rules (Use Cases)
Chứa logic cụ thể của ứng dụng

### 3. Interface Adapters
Chuyển đổi dữ liệu giữa các layer và các framework bên ngoài (ví dụ: database, API, UI)

#### Controllers
- Nhận và xử lý các request từ bên ngoài (Ví dụ: HTTP requests, CLI commands, etc.)
- Chuyển đổi dữ liệu đầu vào thành format phù hợp cho use case
- Gọi các use case tương ứng để thực hiện logic nghiệp vụ
- Điều hướng luồng dữ liệu từ bên ngoài vào hệ thống
- Không chứa business logic, chỉ làm nhiệm vụ điều phối

#### Presenters
- Chuyển đổi dữ liệu đầu ra từ use cases thành format phù hợp để hiển thị
- Định dạng dữ liệu theo yêu cầu hiển thị cụ thể (Ví dụ: Web, Mobile, CLI, etc.)
- Tách biệt logic hiển thị khỏi business logic
- Đảm bảo nguyên tắc "Separation of Concerns" - tách biệt trách nhiệm

#### Gateways
- Định nghĩa interfaces cho việc tương tác với các nguồn dữ liệu bên ngoài
- Chuyển đổi dữ liệu từ domain entities và các cấu trúc dữ liệu bên ngoài
- Triển khai các repository interfaces được định nghĩa trong domain layer
- Cung cấp lớp trừu tượng để domain layer không phụ thuộc vào chi tiết triển khai

### 4. Frameworks & Drivers
Chứa các framework và drivers bên ngoài (ví dụ: Spring Boot, Hibernate, JPA, JDBC)

## Mô hình Clean Architecture trong Java Spring Boot

### Luồng xử lý request

Khi một request API được gửi đến, nó sẽ đi qua các layer như sau:

1. **Frameworks & Drivers**: 
   - Request HTTP đầu tiên được Spring Boot nhận và xử lý.

2. **Interface Adapters**:
   - ProductController nhận request và chuyển đổi dữ liệu từ DTO sang domain entity thông qua ProductMapper.
   - Controller gọi đến use case tương ứng.

3. **Application Business Rules**:
   - ProductUseCaseImpl thực hiện logic nghiệp vụ, kiểm tra các điều kiện, và gọi đến repository.
   - Use case không biết về các chi tiết triển khai của repository, nó chỉ biết interface.

4. **Enterprise Business Rules**:
   - Entity Product chứa các business rules cốt lõi như isInStock() và getTotalValue().

### Luồng xử lý response

Khi xử lý xong, response sẽ đi ngược lại các layer:

1. **Enterprise Business Rules**: 
   - Trả về entity Product.

2. **Application Business Rules**: 
   - Use case trả kết quả về controller.

3. **Interface Adapters**: 
   - Controller chuyển đổi entity thành DTO và trả về response.

4. **Frameworks & Drivers**: 
   - Spring Boot chuyển đổi response thành HTTP response và gửi về client.