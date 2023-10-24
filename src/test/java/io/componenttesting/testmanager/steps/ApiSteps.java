package io.componenttesting.testmanager.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.javacrumbs.jsonunit.core.Option;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

public class ApiSteps {
    @LocalServerPort
    protected int port;

    protected Response response;

    @When("I use {string} to send:")
    public void postMessage(String path, String message) {
        response = baseRequest().body(message).contentType(ContentType.JSON).when().post(path);
    }

    @Then("path {string} should exist and give me:")
    public void assertCallResponse(String path, String expectedBody) {
        response = baseRequest().when().get(path);
        assertThatJson(response.getBody().prettyPrint()).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(expectedBody);
    }

    @Then("I should receive a {int} status code")
    public void assertStatusCode(int expectedStatus) {
        response.then().assertThat().statusCode(expectedStatus);
    }

    @When("I delete {string}")
    public void delete(String path) {
        response = baseRequest().when().delete(path);
    }

    @Then("path {string} should receive a {int} status code")
    public void assertCallResponse(String path, int expectedStatus) {
        response = baseRequest().when().get(path);
        response.then().assertThat().statusCode(expectedStatus);
    }

    private RequestSpecification baseRequest() {
        return given().auth().oauth2("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.NHVaYe26MbtOYhSKkoKYdFVomg4i8ZJd8_-RU8VNbftc4TSMb4bXP3l3YlNWACwyXPGffz5aXHc6lty1Y2t4SWRqGteragsVdZufDn5BlnJl9pdR_kdVFUsra2rWKEofkZeIC4yWytE58sMIihvo9H1ScmmVwBcQP6XETqYd0aSHp1gOa9RdUPDvoXQ5oqygTqVtxaDr6wUFKrKItgBMzWIdNZ6y7O9E0DhEPTbE9rfBo6KTFsHAZnMg4k68CDp2woYIaXbmYTWcvbzIuHO7_37GT79XdIwkm95QJ7hYC9RiwrV7mesbY4PAahERJawntho0my942XheVLmGwLMBkQ").port(port);
    }
}
