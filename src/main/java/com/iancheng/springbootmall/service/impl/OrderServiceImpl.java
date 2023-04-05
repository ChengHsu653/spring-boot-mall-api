package com.iancheng.springbootmall.service.impl;

import com.iancheng.springbootmall.dto.BuyItem;
import com.iancheng.springbootmall.dto.CreateOrderRequest;
import com.iancheng.springbootmall.dto.OrderQueryParams;
import com.iancheng.springbootmall.model.Order;
import com.iancheng.springbootmall.model.OrderItem;
import com.iancheng.springbootmall.model.Product;
import com.iancheng.springbootmall.model.User;
import com.iancheng.springbootmall.repository.OrderItemRepository;
import com.iancheng.springbootmall.repository.OrderRepository;
import com.iancheng.springbootmall.repository.ProductRepository;
import com.iancheng.springbootmall.repository.UserRepository;
import com.iancheng.springbootmall.service.OrderService;

import ecpay.payment.integration.AllInOne;
import ecpay.payment.integration.domain.AioCheckOutALL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;


    @Override
    public Page<Order> getOrders(OrderQueryParams orderQueryParams) {
    	Optional<User> optionalUser = userRepository.findById(orderQueryParams.getUserId());

        // 檢查 user 是否存在
        if (!optionalUser.isPresent()) {
            log.warn("該 userId {} 不存在", orderQueryParams.getUserId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        
    	Pageable pageable = PageRequest.of(
    			orderQueryParams.getPage(),
    			orderQueryParams.getSize(),
    			Sort.by("createdDate")
    	);
    	
    	Page<Order> orderList = orderRepository.findAllByUserId(orderQueryParams.getUserId(), pageable);

        for (Order order: orderList) {
            List<OrderItem> orderItemList = orderItemRepository.findAllByOrderId(order.getOrderId());

            order.setOrderItemList(orderItemList);
        }

        return orderList;
    }

    @Override
    public Order getOrderById(Integer orderId) {
        Order order = orderRepository.getReferenceById(orderId);

        List<OrderItem> orderItemList = orderItemRepository.findAllByOrderId(orderId);

        order.setOrderItemList(orderItemList);

        return order;
    }

    @Transactional
    @Override
    public Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest) {
        Optional<User> user = userRepository.findById(userId);

        // 檢查 user 是否存在
        if (!user.isPresent()) {
            log.warn("該 userId {} 不存在", userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        
        BigDecimal totalAmount = BigDecimal.valueOf(0);
        ArrayList<OrderItem> orderItemList = new ArrayList<OrderItem>();
        
        for (BuyItem buyItem: createOrderRequest.getBuyItemList()) {
            Product product = productRepository.getReferenceById(buyItem.getProductId());

            // 檢查 product 是否存在、庫存是否足夠
            if (product == null) {
                log.warn("商品 {} 不存在", buyItem.getProductId());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            } else if (product.getStock() < buyItem.getQuantity()) {
                log.warn("商品 {} 庫存數量不足，無法購買。剩餘庫存 {}，欲購買數量 {}",
                        buyItem.getProductId(), product.getStock(), buyItem.getQuantity());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            // 扣除商品庫存
            product.setStock(product.getStock() - buyItem.getQuantity());
            product = productRepository.save(product);
            
            // 計算總價錢
            BigDecimal amount = product.getPrice().multiply(BigDecimal.valueOf(buyItem.getQuantity()));
            totalAmount = totalAmount.add(amount);

            // 轉換 BuyItem to OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(buyItem.getProductId());
            orderItem.setQuantity(buyItem.getQuantity());
            orderItem.setAmount(amount);

            orderItemList.add(orderItem);
        }

        // 創建訂單
        Order order = new Order();
        
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        
        Date now = new Date();
        
        order.setCreatedDate(now);
        order.setLastModifiedDate(now);
        
        order = orderRepository.save(order);
        
        for (OrderItem orderItem: orderItemList) {
        	orderItem.setOrderId(order.getOrderId());
        }
        
        orderItemList = (ArrayList<OrderItem>) orderItemRepository.saveAll(orderItemList);
        
        return order.getOrderId();
    }

	@Override
	public String checkout(Integer userId, Integer orderId) {
		Optional<Order> optionalOrder = orderRepository.findById(orderId);
		Optional<User> optionalUser = userRepository.findById(userId);

        // 檢查 user 是否存在
        if (!optionalUser.isPresent()) {
            log.warn("該 userId {} 不存在", userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        
		// 檢查 order 是否存在
        if (!optionalOrder.isPresent()) {
            log.warn("該 orderId {} 不存在", orderId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        
        Order order = orderRepository.getReferenceById(orderId);

        // 檢查此 order 屬於此 user
        if (order.getUserId() != userId) {
            log.warn("該 orderId {} 不屬於該 userId {}", orderId, userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        
		AllInOne all = new AllInOne("");
		
		// 轉換為訂單格式
		String uuId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20);
	    String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
		String totalAmount = String.valueOf(order.getTotalAmount().intValue());
		
		StringBuilder orderItemsDetail = new StringBuilder();
		
		List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId);

		for (OrderItem orderItem: orderItems) {
			Integer productId = orderItem.getProductId();
			Product product = productRepository.getReferenceById(productId);
			
			String productName = product.getProductName();
			String quantity = String.valueOf(orderItem.getQuantity());
			String amount = String.valueOf(orderItem.getAmount().intValue());
			
			orderItemsDetail.append(String.format("[%s * %s = %s]", productName, quantity, amount));
		}
    	
		// 產生訂單
		AioCheckOutALL obj = new AioCheckOutALL();
		obj.setMerchantTradeNo(uuId);
		obj.setMerchantTradeDate(now);
		obj.setTotalAmount(totalAmount);		
		obj.setTradeDesc(orderId.toString());
		obj.setItemName(orderItemsDetail.toString());
		obj.setReturnURL("http://localhost:8080");
		obj.setNeedExtraPaidInfo("N");
		
		String form = all.aioCheckOut(obj, null);
		
		return form;
	}
}
