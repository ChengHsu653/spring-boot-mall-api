package com.iancheng.springbootmall.service;

import com.iancheng.springbootmall.model.User;
import com.iancheng.springbootmall.util.*;

public interface EmailService {
	
	void sendValidationLink(User user);
	
}
