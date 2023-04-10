package com.iancheng.springbootmall.controller;

import com.iancheng.springbootmall.dto.UserLoginRequest;
import com.iancheng.springbootmall.dto.UserRegisterRequest;
import com.iancheng.springbootmall.dto.UserResetPasswordRequest;
import com.iancheng.springbootmall.dto.UserVerifyRequest;
import com.iancheng.springbootmall.model.User;
import com.iancheng.springbootmall.service.EmailService;
import com.iancheng.springbootmall.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private EmailService emailService;

    
    @Tag(name = "register")
    @PostMapping("/users/register")
    public ResponseEntity<User> register(
    		@RequestBody @Valid UserRegisterRequest userRegisterRequest
    ) {
    	User user = userService.register(userRegisterRequest);
   	       
        emailService.sendValidationLink(user);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Tag(name = "login")
    @PostMapping("/users/login")
    public ResponseEntity<User> login(
    		@RequestBody @Valid UserLoginRequest userLoginRequest
    ) {
        User user = userService.login(userLoginRequest);

        return ResponseEntity.status(HttpStatus.OK).body(user);
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
    	
    	userService.verify(userVerifyRequest);
    	
		return "註冊成功";
    }
    
    @Tag(name = "forgetPassword")
    @PostMapping("/users/forgetPassword")
    public String forgetPassword(@RequestBody String email) {
    	
    	User user = userService.checkIfUserExist(email);
    	
    	emailService.sendPasswordResetLink(user);
    	
		return "修改密碼郵件寄送成功";
    }
    
    @Tag(name = "resetPassword")
    @GetMapping("/users/reset_password")
    public String resetPassword(
    		@RequestParam String email,
    		@RequestParam String token
    ) {
    	UserResetPasswordRequest userResetPasswordRequest = new UserResetPasswordRequest();
    	userResetPasswordRequest.setEmail(email);
    	userResetPasswordRequest.setToken(token);
    	
    	userService.resetPassword(userResetPasswordRequest);
    	
		return "修改密碼成功";
    
    }
}
