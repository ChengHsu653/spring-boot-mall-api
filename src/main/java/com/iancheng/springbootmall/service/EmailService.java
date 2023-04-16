package com.iancheng.springbootmall.service;

import com.iancheng.springbootmall.model.User;

public interface EmailService {
	
	void sendValidationLink(User user);

	void sendPasswordResetLink(User user);
	
}
