package com.iancheng.springbootmall.model;

import com.iancheng.springbootmall.constant.TokenType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Token {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer tokenId;

	private String token;

	@Enumerated(EnumType.STRING)
	private TokenType tokenType = TokenType.BEARER;

	private boolean revoked;

	private boolean expired;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_user_id")
	private User user;

}
