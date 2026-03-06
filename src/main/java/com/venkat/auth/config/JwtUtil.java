package com.venkat.auth.config;





import java.util.Date;

import org.springframework.stereotype.Component;

import com.venkat.auth.entity.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {

	private final String SECRET = "VENKAT_UNIVERSE_123";

	private final long ACCESS_EXPIRY = 1000 * 60 * 15;

	public String generateToken(String email, Role role) {

		return Jwts.builder()
				.setSubject(email)
				.claim("role", role)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRY))
				.signWith(SignatureAlgorithm.HS256, SECRET)
				.compact();
	}

	public String extractEmail(String token) {
		return getClaims(token).getSubject();
	}

	public Claims getClaims(String token) {
		return Jwts.parser()
				.setSigningKey(SECRET)
				.parseClaimsJws(token)
				.getBody();
	}
}
