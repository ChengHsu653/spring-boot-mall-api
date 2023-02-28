package com.iancheng.springbootmall.dao;

import com.iancheng.springbootmall.dto.OrderQueryParams;
import com.iancheng.springbootmall.model.Order;
import com.iancheng.springbootmall.model.OrderItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public interface OrderDao {

    Integer countOrder(OrderQueryParams orderQueryParams);

    List<Order> getOrders(OrderQueryParams orderQueryParams);

    Order getOrderById(Integer orderId);

    List<OrderItem> getOrderItemsByOrderId(Integer orderId);

    Integer createOrder(Integer userId, BigDecimal totalAmount);

    void createOrderItems(Integer orderId, ArrayList<OrderItem> orderItemList);



}
