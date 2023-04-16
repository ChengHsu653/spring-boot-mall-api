package com.iancheng.springbootmall.service;

import java.io.IOException;

import com.iancheng.springbootmall.dto.UserForgetRequest;
import com.iancheng.springbootmall.dto.UserLoginRequest;
import com.iancheng.springbootmall.dto.UserRegisterRequest;
import com.iancheng.springbootmall.dto.UserVerifyRequest;
import com.iancheng.springbootmall.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
	boolean verify(UserVerifyRequest userVerifyRequest);

	boolean resetPassword(String email, String password, String confirmPassword);
	
	User register(UserRegisterRequest userRegisterRequest);

	User login(UserLoginRequest userLoginRequest);

	User forgetPassword(UserForgetRequest userForgetRequest);

	void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
    
}
