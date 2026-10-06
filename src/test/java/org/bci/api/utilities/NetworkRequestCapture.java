package org.bci.api.utilities;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Request;

public class NetworkRequestCapture {

    /**
     * Captures the request payload (POST data) for a specific endpoint while executing an action.
     * 
     * @param page         Playwright Page instance
     * @param endpointPart The URL fragment to match (e.g., "/API/User/UserLogin")
     * @param action       The Runnable action that triggers the network request (e.g., clicking login)
     * @return The request payload as a String
     */
    public static String getRequestPayload(
            Page page,
            String endpointPart,
            Runnable action) {

        if (page == null) {
            throw new RuntimeException(
                    "Page cannot be null while capturing network request."
            );
        }

        if (endpointPart == null
                || endpointPart.trim().isEmpty()) {

            throw new RuntimeException(
                    "Endpoint name cannot be null or blank."
            );
        }

        if (action == null) {
            throw new RuntimeException(
                    "Action triggering the request cannot be null."
            );
        }

        try {
            // Playwright waits for the request matching the URL fragment while executing the action
            Request request = page.waitForRequest(
                    req -> req.url().contains(endpointPart),
                    action
            );

            if (request != null) {
                String postData = request.postData();
                if (postData != null && !postData.trim().isEmpty()) {
                    return postData;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Could not capture request payload for endpoint: " + endpointPart,
                    e
            );
        }

        throw new RuntimeException(
                "Could not capture request payload for endpoint: " + endpointPart
        );
    }
}