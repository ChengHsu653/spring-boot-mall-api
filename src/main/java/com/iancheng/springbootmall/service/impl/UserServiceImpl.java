package com.iancheng.springbootmall.service.impl;


import com.iancheng.springbootmall.dao.UserDao;
import com.iancheng.springbootmall.dto.UserLoginRequest;
import com.iancheng.springbootmall.dto.UserRegisterRequest;
import com.iancheng.springbootmall.model.User;
import com.iancheng.springbootmall.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.server.ResponseStatusException;

@Component
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Override
    public User getUserById(Integer userId) {
        return userDao.getUserById(userId);
    }

    @Override
    public Integer register(UserRegisterRequest userRegisterRequest) {
        User user = userDao.getUserByEmail(userRegisterRequest.getEmail());

        // Check if email is registered
        if (user != null) {
           log.warn("Email {} is already registered", userRegisterRequest.getEmail());
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // Generate hashed password using MD5
        String hashedPassword = DigestUtils.md5DigestAsHex(userRegisterRequest.getPassword().getBytes());
        userRegisterRequest.setPassword(hashedPassword);

        return userDao.createUser(userRegisterRequest);
    }

    @Override
    public User login(UserLoginRequest userLoginRequest) {
        User user = userDao.getUserByEmail(userLoginRequest.getEmail());

        // Check if user is exist
        if (user == null) {
            log.warn("Email {} isn't registered", userLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // Generate hashed password using MD5
        String hashedPassword = DigestUtils.md5DigestAsHex(userLoginRequest.getPassword().getBytes());

        if (user.getPassword().equals(hashedPassword)) return user;
        else {
            log.warn("Email {}'s password is wrong", userLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
