package com.iancheng.springbootmall.service.impl;

import com.iancheng.springbootmall.dao.OrderDao;
import com.iancheng.springbootmall.dao.ProductDao;
import com.iancheng.springbootmall.dto.BuyItem;
import com.iancheng.springbootmall.dto.CreateOrderRequest;
import com.iancheng.springbootmall.model.OrderItem;
import com.iancheng.springbootmall.model.Product;
import com.iancheng.springbootmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

@Component
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;
    
    @Autowired
    private ProductDao productDao;

    @Transactional
    @Override
    public Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest) {
        BigDecimal totalAmount = BigDecimal.valueOf(0);
        var orderItemList = new ArrayList<OrderItem>();

        for (BuyItem buyItem: createOrderRequest.getBuyItemList()) {
            Product product = productDao.getProductById(buyItem.getProductId());

            // 計算總價錢
            BigDecimal amount = product.getPrice().multiply(BigDecimal.valueOf(buyItem.getQuantity()));
            totalAmount.add(amount);

            // 轉換 BuyItem to OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(buyItem.getProductId());
            orderItem.setQuantity(buyItem.getQuantity());
            orderItem.setAmount(amount);

            orderItemList.add(orderItem);
        }

        // 創建訂單
        Integer orderId = orderDao.createOrder(userId, totalAmount);

        orderDao.createOrderItems(orderId, orderItemList);

        return orderId;
    }
}
