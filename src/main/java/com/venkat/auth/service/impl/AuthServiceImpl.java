package com.venkat.auth.service.impl;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.venkat.auth.config.JwtUtil;
import com.venkat.auth.dto.AuthResponse;
import com.venkat.auth.dto.LoginRequest;
import com.venkat.auth.dto.RegisterRequest;
import com.venkat.auth.dto.UserDto;
import com.venkat.auth.entity.Role;
import com.venkat.auth.entity.User;
import com.venkat.auth.repository.UserRepository;
import com.venkat.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse register(RegisterRequest request) {

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            throw new RuntimeException("User already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole());
        String refreshToken = UUID.randomUUID().toString();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean passwordMatch = passwordEncoder
                .matches(request.getPassword(), user.getPassword());

        if (!passwordMatch) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole());
        String refreshToken = UUID.randomUUID().toString();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
    @Override
    public AuthResponse loginV2(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean passwordMatch = passwordEncoder
                .matches(request.getPassword(), user.getPassword());

        if (!passwordMatch) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole());
        String refreshToken = UUID.randomUUID().toString();
        // Store refresh token in Redis
        redisTemplate.opsForValue().set(
                "refresh_token:" + user.getEmail(),
                refreshToken,
                Duration.ofDays(7)
        );
        
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(900)
                .user(userDto)
                .build();
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {

        // Normally we validate refresh token from DB or Redis

        String newAccessToken = jwtUtil.generateToken("user@email.com",Role.USER);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public void logout(String refreshToken) {

        // In production:
        // add refresh token to blacklist (Redis / DB)

        System.out.println("User logged out with refresh token: " + refreshToken);
    }

	@Override
	public UserDto getCurrentUser(Authentication authentication) {
		   String email = authentication.name();

	        User user = userRepository
	                .findByEmail(email)
	                .orElseThrow();

	        UserDto dto = UserDto.builder()
	                .id(user.getId())
	                .name(user.getName())
	                .email(user.getEmail())
	                .role(user.getRole())
	                .build();
		return dto;
	}
}