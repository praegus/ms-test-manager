package io.componenttesting.testmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
//@EnableKafkaStreams
public class TestManagerApplication {

    public static final Logger LOGGER = LoggerFactory.getLogger(TestManagerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TestManagerApplication.class, args);
        LOGGER.info("The test manager Application has started...");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

