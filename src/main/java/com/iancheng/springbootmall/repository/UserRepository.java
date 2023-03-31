package com.iancheng.springbootmall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iancheng.springbootmall.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
	
	User getUserByEmail(String email);
}
