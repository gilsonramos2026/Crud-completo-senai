package com.crud.fullstack.controller;

import com.crud.fullstack.dto.request.LoginRequest;
import com.crud.fullstack.dto.request.RegisterRequest;
import com.crud.fullstack.dto.response.AuthResponse;
import com.crud.fullstack.exception.ApiError;
import com.crud.fullstack.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints públicos para registro e login de usuários")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Registrar um novo usuário",
            description = "Cria uma nova conta no sistema. Por padrão, todo registro público recebe a permissão ROLE_USER."
    )
    @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso e autenticado")
    @ApiResponse(
            responseCode = "409",
            description = "Conflito: Nome de usuário ou e-mail já estão em uso",
            content = @Content(schema = @Schema(implementation = ApiError.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Dados de requisição inválidos ou malformados",
            content = @Content(schema = @Schema(implementation = ApiError.class))
    )
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Autenticar usuário",
            description = "Valida as credenciais enviadas e retorna um token JWT válido por 1 hora caso estejam corretas."
    )
    @ApiResponse(responseCode = "200", description = "Login efetuado com sucesso, retorna o token")
    @ApiResponse(
            responseCode = "401",
            description = "Usuário ou senha inválidos",
            content = @Content(schema = @Schema(implementation = ApiError.class))
    )
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
