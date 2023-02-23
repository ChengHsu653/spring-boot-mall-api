package com.iancheng.springbootmall.dao;

import com.iancheng.springbootmall.model.Order;
import com.iancheng.springbootmall.model.OrderItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public interface OrderDao {

    Order getOrderById(Integer orderId);

    List<OrderItem> getOrderItemsByOrderId(Integer orderId);

    Integer createOrder(Integer userId, BigDecimal totalAmount);

    void createOrderItems(Integer orderId, ArrayList<OrderItem> orderItemList);


}
