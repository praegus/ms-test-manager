package io.componenttesting.testmanager.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.componenttesting.model.TestDataEvent;
import io.componenttesting.testmanager.event.KafkaConsumerService;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

public class EventSteps {

    @Autowired
    private KafkaConsumerService consumerService;

    @When("project {string} has received the following testdata:")
    public void sendTestData(String project, String testDataEvent) throws JsonProcessingException {
        sendInEvent(project, testDataEvent);
    }

    @When("project {string} has received {int} passing tests and {int} failing tests")
    public void sendTestDataList(String project, int passing, int failing) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        int iteration = 1;
        final TestDataEvent testData = new TestDataEvent();
        testData.setTestName("unit test A");
        testData.setProject(project);

        for (int i=0;i<passing;i++) {
            testData.setTestRunId(iteration++);
            testData.setResult("PASSED");
            sendInEvent(project, mapper.writeValueAsString(testData));
        }

        for (int i=0;i<failing;i++) {
            testData.setTestRunId(iteration++);
            testData.setResult("FAILED");
            sendInEvent(project, mapper.writeValueAsString(testData));
        }
    }

    private void sendInEvent(String projectName, String content) throws JsonProcessingException {
        consumerService.receive(content);
    }
}
