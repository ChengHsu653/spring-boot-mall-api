package com.iancheng.springbootmall.service.impl;


import com.iancheng.springbootmall.constant.Role;
import com.iancheng.springbootmall.dto.UserLoginRequest;
import com.iancheng.springbootmall.dto.UserRegisterRequest;
import com.iancheng.springbootmall.dto.UserResetPasswordRequest;
import com.iancheng.springbootmall.dto.UserVerifyRequest;
import com.iancheng.springbootmall.model.User;
import com.iancheng.springbootmall.repository.UserRepository;
import com.iancheng.springbootmall.service.JwtService;
import com.iancheng.springbootmall.service.UserService;


import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;
    
    
    @Override
    public User register(UserRegisterRequest userRegisterRequest) {
    	User user = userRepository.getUserByEmail(userRegisterRequest.getEmail());
    	
        // 檢查註冊的 email
        if (user != null) {
           log.warn("該 email {} 已經被註冊", userRegisterRequest.getEmail());
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // 創建帳號
        user = new User();

        user.setEmail(userRegisterRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        user.setUserName(userRegisterRequest.getUserName());
        user.setRole(Role.UNVERIFIED);
        
        Date now = new Date();

        user.setCreatedDate(now);
        user.setLastModifiedDate(now);

        String jwtToken = jwtService.generateToken(user);
        
        user.setToken(jwtToken);
        
        user = userRepository.save(user);
        
        return user;
    }

    @Override
    public User login(UserLoginRequest userLoginRequest) {
    	User user = userRepository.getUserByEmail(userLoginRequest.getEmail());
    	
        // 檢查 user 是否存在
        if (user == null) {
            log.warn("該 Email {} 尚未註冊", userLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // 比較密碼
        if (passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())) {
        	String jwtToken = jwtService.generateToken(user);
            
            user.setToken(jwtToken);
        	
            return user;
        } else {
            log.warn("Email {} 的密碼不正確", userLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

	@Override
	public void verify(UserVerifyRequest userVerifyRequest) {
		User user = userRepository.getUserByEmail(userVerifyRequest.getEmail());
    	
        // 比較驗證碼
        if (user.getPassword().equals(userVerifyRequest.getToken())) {
        	user.setRole(Role.MEMBER);
        	user = userRepository.save(user);
        } else {
            log.warn("email {} 的驗證碼不正確", userVerifyRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
	}

	@Override
	public User checkIfUserExist(String email) {
		User user = userRepository.getUserByEmail(email);
    	
        // 檢查 user 是否存在
        if (user == null) {
            log.warn("該 Email {} 尚未註冊", email);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        
        return user;
	}

	@Override
	public void resetPassword(UserResetPasswordRequest userResetPasswordRequest) {
		User user = userRepository.getUserByEmail(userResetPasswordRequest.getEmail());
    	
		// 比較驗證碼
        if (user.getPassword().equals(userResetPasswordRequest.getToken())) {
        	user = userRepository.save(user);
        } else {
            log.warn("email {} 的驗證碼不正確", userResetPasswordRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
		
	}
	
	
}
