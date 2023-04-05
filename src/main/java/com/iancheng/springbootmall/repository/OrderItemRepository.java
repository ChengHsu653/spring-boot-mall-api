package com.iancheng.springbootmall.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iancheng.springbootmall.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer>{

	List<OrderItem> findAllByOrderId(Integer orderId);

}
