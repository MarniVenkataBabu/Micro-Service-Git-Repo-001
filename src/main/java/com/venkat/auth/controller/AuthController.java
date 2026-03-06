package com.venkat.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.venkat.auth.dto.AuthResponse;
import com.venkat.auth.dto.LoginRequest;
import com.venkat.auth.dto.RegisterRequest;
import com.venkat.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // REGISTER USER
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse response = authService.register(request);

        return ResponseEntity.ok(response);
    }

    // LOGIN USER
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }
    // LOGIN USER VERSION 2
    @PostMapping("/login/v2")
    public ResponseEntity<AuthResponse> loginV2(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.loginV2(request);

        return ResponseEntity.ok(response);
    }
    // REFRESH TOKEN
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestParam String refreshToken) {

        AuthResponse response = authService.refreshToken(refreshToken);

        return ResponseEntity.ok(response);
    }

    // LOGOUT USER
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestParam String refreshToken) {

        authService.logout(refreshToken);

        return ResponseEntity.ok("Logged out successfully");
    }
}