package com.crud.fullstack.dto.response;

import java.util.List;

public record AuthResponse(String token, String tokenType, String username, List<String> roles) {
    public static AuthResponse of(String token, String username, List<String> roles) {
        return new AuthResponse(token, "Bearer", username, roles);
    }
}
