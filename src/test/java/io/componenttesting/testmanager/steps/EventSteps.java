package io.componenttesting.testmanager.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.componenttesting.model.TestDataEvent;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class EventSteps {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    private static BlockingQueue<ConsumerRecord<String, String>> records;

    private static Producer<String, String> producer;

    private static boolean hasStarted = false;
    @Before
    public void setUp() {
        if (!hasStarted) {
            hasStarted = true;
            Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("consumer", "false", embeddedKafkaBroker));
            DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(configs, new StringDeserializer(), new StringDeserializer());
            ContainerProperties containerProperties = new ContainerProperties("project");
            KafkaMessageListenerContainer<String, String> container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
            records = new LinkedBlockingQueue<>();
            container.setupMessageListener((MessageListener<String, String>) records::add);
            container.start();
            ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());

            Map<String, Object> producerConfigs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
            producer = new DefaultKafkaProducerFactory<>(producerConfigs, new StringSerializer(), new StringSerializer()).createProducer();
        }
    }

    @After
    public void cleanUp() {
        records.clear();
    }

    @When("the following testdata was received on topic {string}:")
    public void sendTestData(String topic, String testDataEvent) {
        sendInEvent(topic, testDataEvent);
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
            sendInEvent("testdata", mapper.writeValueAsString(testData));
        }

        for (int i=0;i<failing;i++) {
            testData.setTestRunId(iteration++);
            testData.setResult("FAILED");
            sendInEvent("testdata", mapper.writeValueAsString(testData));
        }
    }

    @Then("an event with message {string} will be published to the topic {string}")
    public void checkPublish(String message, String topic) throws InterruptedException {
        ConsumerRecord<String, String> singleRecord = records.poll(100, TimeUnit.MILLISECONDS);
        assertThat(singleRecord).isNotNull();
        assertThat(singleRecord.topic()).isEqualTo(topic);
        assertThat(singleRecord.value()).isEqualTo(message);
    }

    private void sendInEvent(String topic, String content) {
        producer.send(new ProducerRecord<>(topic, "my-aggregate-id", content));
        producer.flush();
    }
}
