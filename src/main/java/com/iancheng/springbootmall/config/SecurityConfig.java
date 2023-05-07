package com.iancheng.springbootmall.config;

import com.iancheng.springbootmall.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	private final JwtAuthenticationFilter jwtAuthFilter;
	private final AuthenticationProvider authenticationProvider;
	private final LogoutHandler logoutHandler;

	
	@Autowired
	public SecurityConfig(
			JwtAuthenticationFilter jwtAuthFilter,
			AuthenticationProvider authenticationProvider,
			LogoutHandler logoutHandler
	) {
		this.jwtAuthFilter = jwtAuthFilter;
		this.authenticationProvider = authenticationProvider;
		this.logoutHandler = logoutHandler;
	}

	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
		.csrf().disable()
        .authorizeHttpRequests()
			.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/redirectToGoogle", "/google/callback/**", "/api/callback").permitAll()
			.requestMatchers("/api/users/*/orders").hasAnyRole("MEMBER", "ADMIN")
			.requestMatchers("/api/users/**").permitAll()
			.requestMatchers(HttpMethod.GET, "/api/products", "/api/products/*").permitAll()
			.requestMatchers(HttpMethod.POST, "/api/products").hasRole("ADMIN")
			.requestMatchers(HttpMethod.PUT, "/api/products/*").hasRole("ADMIN")
			.requestMatchers(HttpMethod.DELETE, "/api/products/*").hasRole("ADMIN")
			.anyRequest().authenticated()
		.and()
        .cors(Customizer.withDefaults())
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
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

	@Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
