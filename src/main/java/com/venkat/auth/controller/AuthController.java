package com.venkat.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.venkat.auth.dto.AuthResponse;
import com.venkat.auth.dto.LoginRequest;
import com.venkat.auth.dto.RegisterRequest;
import com.venkat.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController

@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;
	
	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		AuthResponse authResponse = authService.register(request);
		return ResponseEntity.ok(authResponse);
	}
	
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
		AuthResponse authResponse = authService.login(request);
		return ResponseEntity.ok(authResponse);
		
	}
}
