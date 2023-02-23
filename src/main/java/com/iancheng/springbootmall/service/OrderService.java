package com.iancheng.springbootmall.service;

import com.iancheng.springbootmall.dto.CreateOrderRequest;
import com.iancheng.springbootmall.model.Order;

public interface OrderService {

    Order getOrderById(Integer orderId);

    Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest);

}
