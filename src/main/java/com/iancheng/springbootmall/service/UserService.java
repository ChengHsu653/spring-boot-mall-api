package com.iancheng.springbootmall.service;

import com.iancheng.springbootmall.dto.UserLoginRequest;
import com.iancheng.springbootmall.dto.UserRegisterRequest;
import com.iancheng.springbootmall.dto.UserResetPasswordRequest;
import com.iancheng.springbootmall.dto.UserVerifyRequest;
import com.iancheng.springbootmall.model.User;

public interface UserService {
	User register(UserRegisterRequest userRegisterRequest);

	User login(UserLoginRequest userLoginRequest);

	boolean verify(UserVerifyRequest userVerifyRequest);

	User getUserByEmail(String email);

	boolean resetPassword(String email, String password, String confirmPassword);
    
}
