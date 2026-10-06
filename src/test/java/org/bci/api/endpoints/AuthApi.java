package org.bci.api.endpoints;

import org.testng.Assert;

import org.bci.api.auth.ApiAuthContext;
import org.bci.api.base.ApiBase;
import org.bci.api.models.request.AuthenticateRequestPojo;
import org.bci.api.models.response.AuthenticateResponsePojo;

import io.restassured.response.Response;

public class AuthApi extends ApiBase {

    private static final String AUTHENTICATE_ENDPOINT = "/api/TokenAuth/Authenticate";

    public AuthApi(String baseUrl) {
        super(baseUrl);
    }

    public AuthenticateResponsePojo authenticate(String usernameOrEmail, String password) {
        AuthenticateRequestPojo request = new AuthenticateRequestPojo(usernameOrEmail, password);

        Response response = post(AUTHENTICATE_ENDPOINT, request);

        Assert.assertEquals(
                response.getStatusCode(),
                200,
                "Authentication API HTTP status mismatch."
        );

        AuthenticateResponsePojo authResponse = response.as(AuthenticateResponsePojo.class);

        Assert.assertTrue(
                authResponse.isStatus(),
                "Authentication failed: " + authResponse.getMessage()
        );

        Assert.assertNotNull(
                authResponse.getResult(),
                "Auth response result payload is null."
        );

        Assert.assertNotNull(
                authResponse.getResult().getAccessToken(),
                "Access Token is null in authentication response."
        );

        // Prepend "Bearer " if needed and set into thread-local context
        String rawToken = authResponse.getResult().getAccessToken().trim();
        String bearerToken = rawToken.startsWith("Bearer ") ? rawToken : "Bearer " + rawToken;
        
        ApiAuthContext.setAccessToken(bearerToken);

        return authResponse;
    }
}