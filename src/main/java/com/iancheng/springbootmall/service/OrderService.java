package com.iancheng.springbootmall.service;

import com.iancheng.springbootmall.dto.CreateOrderRequest;
import com.iancheng.springbootmall.dto.OrderQueryParams;
import com.iancheng.springbootmall.model.Order;

import java.util.List;

public interface OrderService {

    List<Order> getOrders(OrderQueryParams orderQueryParams);

    Integer countOrder(OrderQueryParams orderQueryParams);

    Order getOrderById(Integer orderId);

    Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest);
}
