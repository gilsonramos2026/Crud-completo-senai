package com.crud.fullstack.service;

import com.crud.fullstack.dto.request.LoginRequest;
import com.crud.fullstack.dto.request.RegisterRequest;
import com.crud.fullstack.dto.response.AuthResponse;
 // IMPORT CORRIGIDO: Usando a entidade que você criou
import com.crud.fullstack.model.Role;
import com.crud.fullstack.model.User;
import com.crud.fullstack.repository.RoleRepository;
import com.crud.fullstack.repository.UserRepository;
import com.crud.fullstack.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Nome de usuário já está em uso");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("E-mail já está em uso");
        }

        Role roleUser = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("ROLE_USER não encontrada — verifique a migration V1"));

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password())) // NUNCA salvar senha em texto puro
                .roles(Set.of(roleUser)) // todo registro público sempre entra como ROLE_USER
                .build();

        userRepository.save(user);

        return authenticateAndBuildResponse(request.username(), request.password());
    }

    public AuthResponse login(LoginRequest request) {
        return authenticateAndBuildResponse(request.username(), request.password());
    }

    private AuthResponse authenticateAndBuildResponse(String username, String password) {
        // uma única chamada ao AuthenticationManager: ele já valida usuário+senha
        // e devolve um Authentication populado com o UserDetails no principal
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String token = jwtService.generateToken(userDetails);
        return AuthResponse.of(token, userDetails.getUsername(), roles);
    }
}
