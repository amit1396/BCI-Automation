package org.bci.api.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ApiBase {

	private static final Logger logger = LogManager.getLogger(ApiBase.class);

    private final String baseUrl;

    public ApiBase(String baseUrl) {

        if (baseUrl == null
                || baseUrl.trim().isEmpty()) {

            throw new RuntimeException(
                    "API Base URL cannot be null or blank"
            );
        }

        this.baseUrl =
                baseUrl.trim();
    }

    protected RequestSpecification request() {

        return RestAssured
                .given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    protected RequestSpecification request(
            String accessToken) {

        if (accessToken == null
                || accessToken.trim().isEmpty()) {

            throw new RuntimeException(
                    "API Access Token is null or blank"
            );
        }

        String token = accessToken.trim();

        logger.info(
                "API Authorization:"
                        + " Token Present: ["
                        + !token.isEmpty()
                        + "]"
                        + " | Bearer Prefix Present: ["
                        + token.startsWith("Bearer ")
                        + "]"
        );

        return request()
                .header(
                        "access_Token",
                        token
                );
    }

    public Response post(
            String endpoint,
            Object requestBody) {

        return request()
                .body(requestBody)
                .when()
                .post(endpoint)
                .then()
                .extract()
                .response();
    }

    public Response post(
            String endpoint,
            Object requestBody,
            String accessToken) {

        return request(accessToken)
                .body(requestBody)
                .when()
                .post(endpoint)
                .then()
                .extract()
                .response();
    }
}