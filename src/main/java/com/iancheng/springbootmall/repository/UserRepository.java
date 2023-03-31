package com.iancheng.springbootmall.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iancheng.springbootmall.model.User;

public interface UserRepository extends JpaRepository<User, Integer>{
	
	User getUserByEmail(String email);
}
