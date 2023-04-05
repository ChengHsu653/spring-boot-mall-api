package com.iancheng.springbootmall.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iancheng.springbootmall.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer>{

	Page<Order> findAllByUserId(Integer userId, Pageable pageable);
	
}
