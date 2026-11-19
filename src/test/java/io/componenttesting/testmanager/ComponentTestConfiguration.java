package io.componenttesting.testmanager;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("cucumber")
@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "spring.kafka.bootstrap-servers")
public class ComponentTestConfiguration {

    @DynamicPropertySource
    static void properties(
            DynamicPropertyRegistry registry) {

        registry.add(
                "spring.security.oauth2.resourceserver.jwt.issuer-uri",
                AuthorizationServerWireMock::issuerUri);

        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
                AuthorizationServerWireMock::jwkSetUri);
    }
}
