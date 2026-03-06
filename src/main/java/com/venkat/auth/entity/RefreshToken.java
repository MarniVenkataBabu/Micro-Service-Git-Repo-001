package com.venkat.auth.entity;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RefreshToken {
	private String id;
	private UUID userId;
	private String expiresAt;
}
