package com.iancheng.springbootmall.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.iancheng.springbootmall.repository.TokenRepository;
import com.iancheng.springbootmall.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{

	private JwtService jwtService;
	
	private UserDetailsService userDetailsService;
	
	private TokenRepository tokenRepository;
	
	@Autowired
	public JwtAuthenticationFilter(
			JwtService jwtService, 
			UserDetailsService userDetailsService,
			TokenRepository tokenRepository
	) {
		super();
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
		this.tokenRepository = tokenRepository;
	}


	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain
	)throws ServletException, IOException {
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String jwt;
		final String userEmail;
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			
			return;
		}
		
		jwt = authHeader.substring(7);
		userEmail = jwtService.extractUserEmail(jwt);
		
		if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
			boolean isTokenValid = tokenRepository.findByToken(jwt)
												  .map(token -> !token.isExpired() && !token.isRevoked())
												  .orElse(false);
			
			if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
		            userDetails,
		            null,
		            userDetails.getAuthorities()
		        );
		        authToken.setDetails(
		            new WebAuthenticationDetailsSource().buildDetails(request)
		        );
		        SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		} 
		filterChain.doFilter(request, response);
	}
}
