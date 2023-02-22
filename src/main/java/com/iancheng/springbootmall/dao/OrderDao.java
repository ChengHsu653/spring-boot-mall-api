package com.iancheng.springbootmall.dao;

import com.iancheng.springbootmall.model.OrderItem;

import java.math.BigDecimal;
import java.util.ArrayList;

public interface OrderDao {
    Integer createOrder(Integer userId, BigDecimal totalAmount);

    void createOrderItems(Integer orderId, ArrayList<OrderItem> orderItemList);
}
