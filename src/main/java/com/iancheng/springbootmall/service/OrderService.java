package com.iancheng.springbootmall.service;

import com.iancheng.springbootmall.dto.CreateOrderRequest;
import com.iancheng.springbootmall.dto.OrderQueryParams;
import com.iancheng.springbootmall.model.Order;


import org.springframework.data.domain.Page;

public interface OrderService {

    Page<Order> getOrders(OrderQueryParams orderQueryParams);

    Order getOrderById(Integer orderId);

    Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest);

	String checkout(Integer userId, Integer orderId);
}
