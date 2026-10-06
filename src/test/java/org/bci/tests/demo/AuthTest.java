package org.bci.tests.demo;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import org.bci.api.auth.ApiAuthContext;
import org.bci.api.endpoints.AuthApi;
import org.bci.api.models.response.AuthenticateResponsePojo;

public class AuthTest {

    private final String BASE_URL = "http://98.70.13.78:92";

    @Test
    public void testAuthentication() {
        AuthApi authApi = new AuthApi(BASE_URL);

        // Performs post call, verifies assertions, and sets ApiAuthContext automatically
        AuthenticateResponsePojo response = authApi.authenticate("B1351", "Bcil@123456789");

        System.out.println("Authenticated User: " + response.getResult().getName());
        System.out.println("Plant Code: " + response.getResult().getPlantCode());
        System.out.println("Captured Token in Context: " + ApiAuthContext.getAccessToken().substring(0, 35) + "...");
    }

    @AfterMethod 
    public void tearDown() {
        // Clear thread local context after each test execution
        ApiAuthContext.clear();
    }
}