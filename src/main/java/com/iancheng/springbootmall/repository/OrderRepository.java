package com.iancheng.springbootmall.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iancheng.springbootmall.model.Order;
import com.iancheng.springbootmall.model.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer>{

	Page<Order> findAllByUser(User user, Pageable pageable);
	
	boolean existsByUser(User user);
	
	@Query("SELECT o FROM Order o JOIN FETCH o.orderItems")
	Order getOrderById(Integer id);
}
