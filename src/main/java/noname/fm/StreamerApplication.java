package noname.fm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.ResourceRegionHttpMessageConverter;

@SpringBootApplication
public class StreamerApplication {

  public static void main(String[] args) {
    SpringApplication.run(StreamerApplication.class, args);
  }

    //need this so I can retturn the stuff from the controller
    @Bean
    public HttpMessageConverters customConverter() {
        return new HttpMessageConverters(new ResourceRegionHttpMessageConverter());
    }
}