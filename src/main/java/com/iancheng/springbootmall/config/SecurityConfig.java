package com.iancheng.springbootmall.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import com.iancheng.springbootmall.filter.JwtAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private JwtAuthenticationFilter jwtAuthFilter;
	
	@Autowired
	private AuthenticationProvider authenticationProvider;
	
	@Autowired
	private LogoutHandler logoutHandler;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
        .csrf().disable()
        .authorizeHttpRequests()
        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
        .requestMatchers("/users/*/orders").hasAnyRole("MEMBER", "ADMIN")
        .requestMatchers("/users/*").permitAll()
        .requestMatchers("/callback").permitAll()
        .requestMatchers(HttpMethod.GET, "/products").permitAll()
        .requestMatchers(HttpMethod.POST, "/products").permitAll()//.hasRole("ADMIN")
        .requestMatchers(HttpMethod.PUT, "/products/*").hasRole("ADMIN")
        .requestMatchers(HttpMethod.DELETE, "/products/*").hasRole("ADMIN")
        .anyRequest().authenticated()
        .and()
        .sessionManagement()
        	.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
	    .logout()
	    	.logoutUrl("/users/logout")
	    	.addLogoutHandler(logoutHandler)
	    	.logoutSuccessHandler((request, response, authentication) 
	    			-> SecurityContextHolder.clearContext());
		
	    return http.build();
	}
}
