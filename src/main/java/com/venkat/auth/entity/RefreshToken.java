package com.venkat.auth.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@ManyToOne
	private User user;

	private String token;

	private Date expiryDate;
}