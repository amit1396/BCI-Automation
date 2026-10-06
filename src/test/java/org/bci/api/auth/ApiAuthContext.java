package org.bci.api.auth;

public final class ApiAuthContext {

    private static final ThreadLocal<String> ACCESS_TOKEN_HOLDER = new ThreadLocal<>();

    private ApiAuthContext() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static void setAccessToken(String token) {
        if (token != null && !token.trim().isEmpty()) {
            ACCESS_TOKEN_HOLDER.set(token.trim());
        } else {
            ACCESS_TOKEN_HOLDER.remove();
        }
    }

    public static String getAccessToken() {
        String token = ACCESS_TOKEN_HOLDER.get();
        
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("API access token is not available in the current context.");
        }
        
        return token;
    }

    public static void clear() {
        ACCESS_TOKEN_HOLDER.remove();
    }
}