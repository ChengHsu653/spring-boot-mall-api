package com.iancheng.springbootmall.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iancheng.springbootmall.model.Order;
import com.iancheng.springbootmall.model.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer>{

	List<OrderItem> findAllByOrder(Order order);

}
