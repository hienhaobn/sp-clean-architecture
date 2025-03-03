# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.4.3/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.4.3/maven-plugin/build-image.html)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/3.4.3/reference/using/devtools.html)
* [Spring Web](https://docs.spring.io/spring-boot/3.4.3/reference/web/servlet.html)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

# Giải thích
Khi một request API được gửi đến, nó sẽ đi qua các layer như sau:

1. Frameworks & Drivers : Request HTTP đầu tiên được Spring Boot nhận và xử lý.
2. Interface Adapters :
   
   - ProductController nhận request và chuyển đổi dữ liệu từ DTO sang domain entity thông qua ProductMapper .
   - Controller gọi đến use case tương ứng.
3. Application Business Rules :
   
   - ProductUseCaseImpl thực hiện logic nghiệp vụ, kiểm tra các điều kiện, và gọi đến repository.
   - Use case không biết về các chi tiết triển khai của repository, nó chỉ biết interface.
4. Enterprise Business Rules :
   
   - Entity Product chứa các business rules cốt lõi như isInStock() và getTotalValue() .
Khi xử lý xong, response sẽ đi ngược lại các layer:

1. Enterprise Business Rules : Trả về entity Product .
2. Application Business Rules : Use case trả kết quả về controller.
3. Interface Adapters : Controller chuyển đổi entity thành DTO và trả về response.
4. Frameworks & Drivers : Spring Boot chuyển đổi response thành HTTP response và gửi về client.