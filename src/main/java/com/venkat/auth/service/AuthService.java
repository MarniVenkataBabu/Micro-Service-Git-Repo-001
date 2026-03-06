package com.venkat.auth.service;

import com.venkat.auth.dto.AuthResponse;
import com.venkat.auth.dto.LoginRequest;
import com.venkat.auth.dto.RegisterRequest;

public interface AuthService {

	AuthResponse register(RegisterRequest request);

	AuthResponse login(LoginRequest request);
	
	AuthResponse loginV2(LoginRequest request);

	AuthResponse refreshToken(String refreshToken);

	void logout(String refreshToken);
}
