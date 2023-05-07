package com.iancheng.springbootmall.config;

import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.iancheng.springbootmall.repository.UserRepository;


@Configuration
public class ApplicationConfig {

	@Value("${spring.security.oauth2.client.registration.google.client-id}")
	private String CLIENT_ID;
	@Value("${spring.security.oauth2.client.registration.google.client-secret}")
	private String CLIENT_SECRET;

	@Value("${application.host-url}")
	private String HOST_URL;


	private final UserRepository userRepository;
	
	@Autowired
	public ApplicationConfig(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return username -> userRepository.findByEmail(username)
		        .orElseThrow(() -> new UsernameNotFoundException("該 Email 尚未註冊"));
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
	    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	    authProvider.setUserDetailsService(userDetailsService());
	    authProvider.setPasswordEncoder(passwordEncoder());
	    
	    return authProvider;
	  }

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
	    return config.getAuthenticationManager();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public OAuth20Service googleOAuth20Service() {
		return new ServiceBuilder(CLIENT_ID)
				.apiSecret(CLIENT_SECRET)
				.callback(HOST_URL + "/google/callback")
				.defaultScope("https://www.googleapis.com/auth/userinfo.profile " +
						"https://www.googleapis.com/auth/userinfo.email")
				.build(GoogleApi20.instance());
	}
}
