package com.srikanth.uaepass.apis;

public class TokenResponse {

    private String access_token;
    private String scope;
    private String token_type;
    private int expires_in;

    // Getters
    public String getAccessToken() {
        return access_token;
    }

    public String getScope() {
        return scope;
    }

    public String getTokenType() {
        return token_type;
    }

    public int getExpiresIn() {
        return expires_in;
    }

    // Optionally: toString() for logging
    @Override
    public String toString() {
        return "TokenResponse{" +
                "access_token='" + access_token + '\'' +
                ", scope='" + scope + '\'' +
                ", token_type='" + token_type + '\'' +
                ", expires_in=" + expires_in +
                '}';
    }
}
