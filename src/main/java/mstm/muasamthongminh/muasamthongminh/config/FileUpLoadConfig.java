package mstm.muasamthongminh.muasamthongminh.config;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Configuration
public class FileUpLoadConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(10));     // mỗi file tối đa 10MB
        factory.setMaxRequestSize(DataSize.ofMegabytes(20));  // tổng dung lượng request 20MB
        return factory.createMultipartConfig();
    }
}
