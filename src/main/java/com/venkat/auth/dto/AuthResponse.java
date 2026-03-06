package com.venkat.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
	private String accessToken;
	private String refreshToken;
}
