package com.venkat.auth.service;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;

import com.venkat.auth.dto.AuthResponse;
import com.venkat.auth.dto.LoginRequest;
import com.venkat.auth.dto.RegisterRequest;
import com.venkat.auth.dto.UserDto;

public interface AuthService {

	AuthResponse register(RegisterRequest request);

	AuthResponse login(LoginRequest request);
	
	AuthResponse loginV2(LoginRequest request);

	AuthResponse refreshToken(String refreshToken);

	void logout(String refreshToken);

	UserDto getCurrentUser(Authentication authentication);
}
