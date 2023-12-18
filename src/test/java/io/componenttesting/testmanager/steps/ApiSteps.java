package io.componenttesting.testmanager.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.javacrumbs.jsonunit.core.Option;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.awaitility.Awaitility.await;

public class ApiSteps {
    @LocalServerPort
    protected int port;

    protected Response response;

    enum Tokens {
        WRITE("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMiwic2NvcGUiOiJ3cml0ZSIsInJvbGVzIjpbInBlcm1pc3Npb246d3JpdGUiXX0.QJ7kRXFMpBBbokrIcqCUbgaZny9AfbYQOd0DxQtfiAW--MmX88vNhcB783-6QtaWiWQJERoWSade1kGpM4-knhcmdnAxRMqT-KqqSmHPJVIwmYUE1EyMBuZhivnWoEARcYWdOCRKVuXCcUMncNI03gnEDMRjnc-IAlCw2-iesTOj6W92y0LAMHRYWAUe_p7duoMHGjFyIFd8lZbt6vfL4qLBX5fyx64C8M3w9jxjDs4jbZj1vCgvdCGoXbLKkMUNXmmUirMa6vr5tQlVrIFsZnF9R6SS2PR1uQDNB9Ge4GTcTgKO8tS-fq7UPSki14Y20U8MEzRNuRBXuoR6kLRL6A"),
        READ("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMiwic2NvcGUiOiJyZWFkIn0.oKqn5Ojv8S28xexWufKPXjmu-hDyUq8fEtINL8jOenfeDOfjGUNbirmFLiNrqRe-WUYROL0Xz_abVOi2FtXxw2PU4gdZy7MQDpmpYT1upciOWhgKckRU69Y2ZKy9pmdQrLBcG78cYxW9YSTrLo7od0R95jXHKOTrqixGguMSqBfbTvHUGBuONLFHx97lwiDLMCxqifTyqXvUdh3HL-rFHVo9SgNw46Fvnsek6AbDwNt47VsENpEMbZ3i39zRG0gNOllPtqRyqtOeU6isy_OUjaeda4MscBHlHfjkd_CklT5HAiai2ZnhkmTcdwBB7-TGjKlGIRhIUJ9TkATw-rPYrg"),
        INVALID("eyMan");
        public final String jwtKey;
        Tokens(String jwtKey) {
            this.jwtKey = jwtKey;
        }
    }

    @Given("I use {string} with a {word} token to send:") public void postMessageWithCustomToken(String path, String tokenType, String message) { response = baseRequest() .auth().oauth2(Tokens.valueOf(tokenType.toUpperCase()).jwtKey) .body(message).contentType(ContentType.JSON) .when().post(path); }

    @When("I use {string} to send:")
    public void postMessage(String path, String message) {
        response = baseRequest().body(message).contentType(ContentType.JSON).when().post(path);
    }

    @Then("path {string} should exist and give me:")
    public void assertCallResponse(String path, String expectedBody) {
        await().atMost(5, SECONDS).untilAsserted(() -> {
            response = baseRequest().when().get(path);
            assertThatJson(response.getBody().prettyPrint()).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(expectedBody);
        });
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
        return given()
                .auth().oauth2("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMiwic2NvcGUiOiJ3cml0ZSJ9.KmOzPmGQd_SaCqP2mCzARUcWVUuuWNyJK9JL6mvH5LqEZJjUJrWb-0nF-uUaJAbquXUG5CMNt9Ycmlr8sPHXvTMnyXdWPcDjurQ0Yb7_bVPbVwoFKz-_d3T2leuM5AX-VeRWMXFfI6HbenJvVmR6iAwm0vk1ea9448iUmwDnUloGXrECiNUgM6ZHphDiwNOIA8puuhUBb0bMKm0_OtBymjhnYG1q7urxqRhoiFtTcMZDTAxKcYnSnk_4qjQX1UpjVWBBFt6d3-oL7wM3WQaijQyEwQ8K64skCHstZq4yMxsV1qTFOsgE5QBE4ZUhJEc9-f0On03aRZGrC-BNiSoXgA")
                .port(port);
    }
}
