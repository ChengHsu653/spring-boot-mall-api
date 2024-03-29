package com.iancheng.springbootmall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.iancheng.springbootmall.dto.UserVerifyRequest;
import com.iancheng.springbootmall.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@RequestMapping("/api")
public class ViewController {
	
	private final UserService userService;
	
	@Autowired
	public ViewController(UserService userService) {
		this.userService = userService;
	}


	@Tag(name = "verify")
	@GetMapping("/users/verify")
	public String verify(
			@RequestParam String email,
			@RequestParam String token
	) {
		UserVerifyRequest userVerifyRequest = new UserVerifyRequest();
		userVerifyRequest.setEmail(email);
		userVerifyRequest.setToken(token);

		return userService.verify(userVerifyRequest) ? "registerSuccess" : "registerFail";
	}
	
	
	@Tag(name = "resetForm")
	@GetMapping("/users/reset_form")
	public String resetForm() {
		return "resetForm";
	}

	@Tag(name = "resetPassword")
	@PostMapping("/users/reset")
	public String resetPassword(
			@RequestParam String email, 
			@RequestParam String password,
			@RequestParam String confirmPassword
	) {
		return userService.resetPassword(email, password, confirmPassword) ? "resetSuccess":"resetFail";
	}
}
