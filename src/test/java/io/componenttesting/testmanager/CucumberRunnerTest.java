package io.componenttesting.testmanager;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.cucumber.java.BeforeAll;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "io.componenttesting.testmanager")
public class CucumberRunnerTest {
    private static final WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8143));

    @BeforeAll
    public static void beforeClass() {
        wireMockServer.start();
        configureFor("localhost", wireMockServer.port());
    }
}
