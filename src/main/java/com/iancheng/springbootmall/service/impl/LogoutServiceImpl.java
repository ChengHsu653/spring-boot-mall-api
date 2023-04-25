package com.iancheng.springbootmall.service.impl;

import org.springframework.stereotype.Service;

import com.iancheng.springbootmall.repository.TokenRepository;
import com.iancheng.springbootmall.service.LogoutService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Service
public class LogoutServiceImpl implements LogoutService, LogoutHandler{
	
	private final TokenRepository tokenRepository;
	
	@Autowired
	public LogoutServiceImpl(TokenRepository tokenRepository) {
		this.tokenRepository = tokenRepository;
	}

	
	@Override
	public void logout(
			HttpServletRequest request,
			HttpServletResponse response,
			Authentication authentication
	) {
		final String authHeader = request.getHeader("Authorization");
		final String jwt;
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) return;
		
		jwt = authHeader.substring(7);
		var storedToken = tokenRepository.findByToken(jwt)
										 .orElse(null);
		
		if (storedToken != null) {
			storedToken.setExpired(true);
			storedToken.setRevoked(true);
		
			tokenRepository.save(storedToken);
		}
	}
}
