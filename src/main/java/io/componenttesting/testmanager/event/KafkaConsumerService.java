package io.componenttesting.testmanager.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.componenttesting.model.TestDataEvent;
import io.componenttesting.testmanager.dao.ProjectDao;
import io.componenttesting.testmanager.dao.ProjectEntity;
import io.componenttesting.testmanager.service.ProjectService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

@Component
@Log4j2
public class KafkaConsumerService {

    private CountDownLatch latch = new CountDownLatch(1);

    private final ObjectMapper objectMapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectDao projectDao;

    @KafkaListener(topics = "embedded-test-topic")
    public void receive(String consumerRecord) throws JsonProcessingException {
        log.info("received payload='{}'", consumerRecord);

        TestDataEvent event = objectMapper.readValue(consumerRecord, TestDataEvent.class);
        Optional<ProjectEntity> entity = projectDao.findByNameIgnoreCase(event.getProject());

        if (entity.isPresent()) {
            projectService.updateTeamBasedOnEvent(entity.get(), event);
        } else {
            log.info("project {} does not exist, please use our amazing api to create a new project before using the event platform", event.getProject());
        }

        latch.countDown();
    }

}
