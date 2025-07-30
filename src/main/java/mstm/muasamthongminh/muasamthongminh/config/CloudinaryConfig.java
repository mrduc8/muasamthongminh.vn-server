package mstm.muasamthongminh.muasamthongminh.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary(){
       return new Cloudinary(ObjectUtils.asMap(
               "cloud_name", "df3je18mn",
               "api_key", "339559229962567",
               "api_secret", "hgyrhaELj3YpQFqzS-wOoOo9TWs",
               "secure", true
       ));
    }
}
