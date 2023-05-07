package com.iancheng.springbootmall.service;

import java.io.IOException;

import com.iancheng.springbootmall.dto.*;
import com.iancheng.springbootmall.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
	boolean verify(UserVerifyRequest userVerifyRequest);

	boolean resetPassword(String email, String password, String confirmPassword);
	
	User register(UserRegisterRequest userRegisterRequest);

	User login(UserLoginRequest userLoginRequest);

	void forgetPassword(UserForgetRequest userForgetRequest);

	void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

    User oauth20Login(OAuth20LoginParams oAuth20LoginParams);
}
