package com.microservice.UserService.controller;
import com.microservice.UserService.config.CustomUserDetailService;
import com.microservice.UserService.dto.AuthRequest;
import com.microservice.UserService.dto.AuthResponse;
import com.microservice.UserService.entity.UserEntity;
import com.microservice.UserService.jwtUtils.JwtUtil;


import com.microservice.UserService.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailService userDetailService;
    private final JwtUtil jwtUtil;

    // ========== REGISTER ==========
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserEntity user) {
        UserEntity savedUser = userDetailService.saveUser(user);
        return ResponseEntity.ok(new AuthResponse("User registered successfully", savedUser.getUsername()));
    }

    // ========== LOGIN ==========
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            Optional<UserEntity> us = userRepository.findByUsername(request.getUsername());
            if (auth.isAuthenticated()) {
                String token = jwtUtil.generateToken(request.getUsername(), String.valueOf(us.get().getRole()));
                return ResponseEntity.ok(new AuthResponse("Login successful", token));
            } else {
                return ResponseEntity.status(401).body(new AuthResponse("Invalid credentials", null));
            }

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(new AuthResponse("Authentication failed", null));
        }
    }
}
