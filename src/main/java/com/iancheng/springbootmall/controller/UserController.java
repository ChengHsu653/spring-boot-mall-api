package com.iancheng.springbootmall.controller;

import com.iancheng.springbootmall.dto.UserForgetRequest;
import com.iancheng.springbootmall.dto.UserLoginRequest;
import com.iancheng.springbootmall.dto.UserRegisterRequest;
import com.iancheng.springbootmall.dto.UserVerifyRequest;
import com.iancheng.springbootmall.model.User;
import com.iancheng.springbootmall.service.EmailService;
import com.iancheng.springbootmall.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private EmailService emailService;

	@Tag(name = "register")
	@PostMapping("/users/register")
	public ResponseEntity<User> register(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {
		User user = userService.register(userRegisterRequest);

		emailService.sendValidationLink(user);

		return ResponseEntity.status(HttpStatus.CREATED).body(user);
	}

	@Tag(name = "login")
	@PostMapping("/users/login")
	public ResponseEntity<User> login(@RequestBody @Valid UserLoginRequest userLoginRequest) {
		User user = userService.login(userLoginRequest);

		return ResponseEntity.status(HttpStatus.OK).body(user);
	}
	
	@Tag(name = "logout")
	@GetMapping("/users/logout")
	public ResponseEntity<?> logout() {
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@Tag(name = "verify")
	@GetMapping("/users/verify")
	public String verify(@RequestParam String email, @RequestParam String token) {
		UserVerifyRequest userVerifyRequest = new UserVerifyRequest();
		userVerifyRequest.setEmail(email);
		userVerifyRequest.setToken(token);

		return userService.verify(userVerifyRequest) == true ? "registerSuccess" : "registerFail";
	}

	@Tag(name = "forgetPassword")
	@PostMapping("/users/forget")
	public ResponseEntity<?> forgetPassword(@RequestBody UserForgetRequest userForgetRequest) {
		User user = userService.getUserByEmail(userForgetRequest.getEmail());

		emailService.sendPasswordResetLink(user);

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@Tag(name = "resetForm")
	@GetMapping("/users/reset_form")
	public String resetForm() {
		
		return "resetForm";
	}

	@Tag(name = "resetPassword")
	@PostMapping("/users/reset")
	public String resetPassword(
			@RequestParam String email, @RequestParam String password,
			@RequestParam String confirmPassword) {
		
		return userService.resetPassword(email, password, confirmPassword) == true ? "resetSuccess":"resetFail";
	}
	
	
}
