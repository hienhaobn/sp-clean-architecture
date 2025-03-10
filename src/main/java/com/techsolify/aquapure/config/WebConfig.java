package com.techsolify.aquapure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    // Cấu hình trang mặc định khi truy cập đường dẫn gốc
    registry.addViewController("/").setViewName("forward:/index.html");
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // Đảm bảo tài nguyên tĩnh được xử lý đúng cách
    registry
        .addResourceHandler("/**")
        .addResourceLocations(
            "classpath:/static/",
            "classpath:/public/",
            "classpath:/resources/",
            "classpath:/META-INF/resources/")
        .setCachePeriod(3600);

    // Cấu hình cho Swagger UI
    registry
        .addResourceHandler("/swagger-ui/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/springdoc-openapi-ui/")
        .setCachePeriod(3600);
  }
}
