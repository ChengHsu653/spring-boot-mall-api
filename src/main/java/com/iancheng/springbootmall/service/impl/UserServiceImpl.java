package com.iancheng.springbootmall.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.iancheng.springbootmall.constant.Role;
import com.iancheng.springbootmall.constant.TokenType;
import com.iancheng.springbootmall.dto.*;
import com.iancheng.springbootmall.model.Token;
import com.iancheng.springbootmall.model.User;
import com.iancheng.springbootmall.repository.TokenRepository;
import com.iancheng.springbootmall.repository.UserRepository;
import com.iancheng.springbootmall.service.EmailService;
import com.iancheng.springbootmall.service.JwtService;
import com.iancheng.springbootmall.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtService jwtService;
    
    @Autowired
    public UserServiceImpl(UserRepository userRepository, TokenRepository tokenRepository,
			PasswordEncoder passwordEncoder, JwtService jwtService, EmailService emailService) {
		this.userRepository = userRepository;
		this.tokenRepository = tokenRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
        this.emailService = emailService;
	}


	@Override
    public User register(UserRegisterRequest userRegisterRequest) {
    	
    	// 檢查註冊的 email
        if (emailExists(userRegisterRequest.getEmail())) {
           log.warn("該 email {} 已經被註冊", userRegisterRequest.getEmail());
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // 創建帳號
        User user = new User();

        user.setEmail(userRegisterRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        user.setUserName(userRegisterRequest.getUserName());
        user.setRole(Role.ROLE_UNVERIFIED);
        
        Date now = new Date();

        user.setCreatedDate(now);
        user.setLastModifiedDate(now);

        User savedUser = userRepository.save(user);
        
        // 產生 JWT
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        saveUserToken(savedUser, jwtToken);
        
        user.setAccessToken(jwtToken);
        user.setRefreshToken(refreshToken);

        // 寄送驗證連結
        emailService.sendValidationLink(user);

        return user;
    }

    private boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
    
    private void saveUserToken(User user, String jwtToken) {
    	Token token = new Token();
        
        token.setUser(user);
        token.setToken(jwtToken);
        token.setTokenType(TokenType.BEARER);
        token.setExpired(false);
        token.setRevoked(false);
        
        tokenRepository.save(token);
	}
    
    @Override
    public User login(UserLoginRequest userLoginRequest) {
    	
    	// 檢查註冊的 email
        if (!emailExists(userLoginRequest.getEmail())) {
            log.warn("該 Email {} 尚未註冊", userLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.getByEmail(userLoginRequest.getEmail());
        
        // 比較密碼
        if (passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())) {
        	String jwtToken = jwtService.generateToken(user);
        	String refreshToken = jwtService.generateRefreshToken(user);

            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);
        	
            user.setAccessToken(jwtToken);
            user.setRefreshToken(refreshToken);
            
            return user;
        } else {
            log.warn("Email {} 的密碼不正確", userLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private void revokeAllUserTokens(User user) {
    	List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getUserId());
    	
        if (validUserTokens.isEmpty()) return;
        
        validUserTokens.forEach(token -> {
          token.setExpired(true);
          token.setRevoked(true);
        });
        
        tokenRepository.saveAll(validUserTokens);
    }

    @Override
	public boolean verify(UserVerifyRequest userVerifyRequest) {
		User user = userRepository.getByEmail(userVerifyRequest.getEmail());

        // 比較驗證碼
        if (user.getPassword().equals(userVerifyRequest.getToken())) {
        	user.setRole(Role.ROLE_MEMBER);
        	user.setLastModifiedDate(new Date());
        	userRepository.save(user);
        	
        	return true;
        } else {
            log.warn("email {} 的驗證碼不正確", userVerifyRequest.getEmail());

            return false;
        }
	}

	@Override
	public boolean resetPassword(String email, String password, String confirmPassword) {
		User user = userRepository.getByEmail(email);
    	
		// 比較再次確認密碼及先前密碼
        if (password.equals(confirmPassword) &&
        	!passwordEncoder.matches(password, user.getPassword())
        ) {
        	user.setLastModifiedDate(new Date());
        	user.setPassword(passwordEncoder.encode(password));
        	userRepository.save(user);
        	
        	return true;
        } else {
            log.warn("欲修改密碼與確認密碼不同或與先前密碼相同");

            return false;
        }
	}

	@Override
	public void forgetPassword(UserForgetRequest userForgetRequest) {
		String email = userForgetRequest.getEmail();
		
		// 檢查註冊的 email
        if (!emailExists(email)) {
            log.warn("該 Email {} 尚未註冊", email);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        User user = userRepository.getByEmail(email);

        emailService.sendPasswordResetLink(user);
	}

	@Override
	public void refreshToken(
			HttpServletRequest request,
			HttpServletResponse response
	) throws IOException {
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String refreshToken;
		final String userEmail;
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) return;
		
		refreshToken = authHeader.substring(7);
		userEmail = jwtService.extractUserEmail(refreshToken);
		
		if(userEmail != null) {
            User user = userRepository.findByEmail(userEmail).orElseThrow();

            if (jwtService.isTokenValid(refreshToken, user)) {
				String accessToken = jwtService.generateToken(user);
				
				revokeAllUserTokens(user);
	            saveUserToken(user, accessToken);
				
	            user.setLastModifiedDate(new Date());
				user.setRefreshToken(refreshToken);
				user.setAccessToken(accessToken);
				
				response.setContentType("application/json");
				
				new ObjectMapper().writeValue(response.getOutputStream(), user);
			}
		}
	}

    @Override
    public User oauth20Login(OAuth20LoginParams oAuth20LoginParams) {
        User user = null;
        // 檢查註冊的 email
        if (!emailExists(oAuth20LoginParams.getEmail())) {
            // 創建帳號
            user = new User();

            user.setEmail(oAuth20LoginParams.getEmail());
            user.setUserName(oAuth20LoginParams.getName());
            user.setRole(Role.ROLE_MEMBER);

            Date now = new Date();

            user.setCreatedDate(now);
            user.setLastModifiedDate(now);

            userRepository.save(user);
        } else {
            user = userRepository.getByEmail(oAuth20LoginParams.getEmail());
            user.setLastModifiedDate(new Date());
            userRepository.save(user);
        }

        // 產生 JWT
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        saveUserToken(user, jwtToken);

        user.setAccessToken(jwtToken);
        user.setRefreshToken(refreshToken);

        return user;
    }

    @Override
    public Page<User> getUsers(UserQueryParams userQueryParams) {
        Pageable pageable = PageRequest.of(
                userQueryParams.getPage(),
                userQueryParams.getSize(),
                Sort.by(Sort.Direction.DESC, "createdDate")
        );

        return userRepository.findAll(pageable);
    }



}
