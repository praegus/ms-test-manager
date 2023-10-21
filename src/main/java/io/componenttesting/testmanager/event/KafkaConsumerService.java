package io.componenttesting.testmanager.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.componenttesting.model.TestDataEvent;
import io.componenttesting.testmanager.service.ProjectService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class KafkaConsumerService {

    private final ObjectMapper objectMapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    @Autowired
    private ProjectService projectService;

    @KafkaListener(topics = "testdata", groupId = "my-group")
    public void receive(String consumerRecord) throws JsonProcessingException {
        log.info("received testdata='{}'", consumerRecord);

        TestDataEvent event = objectMapper.readValue(consumerRecord, TestDataEvent.class);
        projectService.triggerNewTestData(event);
    }

}
