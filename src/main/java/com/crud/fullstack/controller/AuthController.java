package com.crud.fullstack.controller;

import com.crud.fullstack.dto.request.LoginRequest;
import com.crud.fullstack.dto.request.RegisterRequest;
import com.crud.fullstack.dto.response.AuthResponse;
import com.crud.fullstack.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ---- PÚBLICO: qualquer visitante pode criar conta, sempre como ROLE_USER ----
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    // ---- PÚBLICO: login de qualquer usuário já cadastrado (USER ou ADMIN) ----
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}