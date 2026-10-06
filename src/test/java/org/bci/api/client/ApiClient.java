package org.bci.api.client;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ApiClient {

    private final String baseUrl;

    public ApiClient(String baseUrl) {

        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new RuntimeException(
                    "API Base URL cannot be null or blank"
            );
        }

        this.baseUrl = baseUrl.trim();
    }

    private RequestSpecification request() {

        return RestAssured
                .given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    private RequestSpecification authenticatedRequest(
            String accessToken) {

        RequestSpecification request =
                request();

        if (accessToken == null
                || accessToken.trim().isEmpty()) {

            throw new RuntimeException(
                    "API access token is null or blank"
            );
        }

        /*
         * access_Token already contains:
         *
         * Bearer eyJ...
         */
        return request.header(
                "Authorization",
                accessToken.trim()
        );
    }

    public Response post(
            String endpoint,
            Object body) {

        return request()
                .body(body)
                .when()
                .post(endpoint)
                .then()
                .extract()
                .response();
    }

    public Response post(
            String endpoint,
            Object body,
            String accessToken) {

        return authenticatedRequest(accessToken)
                .body(body)
                .when()
                .post(endpoint)
                .then()
                .extract()
                .response();
    }

    public Response get(
            String endpoint,
            String accessToken) {

        return authenticatedRequest(accessToken)
                .when()
                .get(endpoint)
                .then()
                .extract()
                .response();
    }
}