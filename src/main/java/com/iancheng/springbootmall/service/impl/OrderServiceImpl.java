package com.iancheng.springbootmall.service.impl;

import com.iancheng.springbootmall.constant.PaymentStatus;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    @Value("${application.host-url}")
    private String hostUrl;
    
    @Value("${application.client-back-url}")
    private String clientBackUrl;

    @Override
    public Page<Order> getOrders(OrderQueryParams orderQueryParams) {
    	// 檢查 user 是否存在
        if (!userExists(orderQueryParams.getUserId())) {
            log.warn("該 userId {} 不存在", orderQueryParams.getUserId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        
    	Pageable pageable = PageRequest.of(
    			orderQueryParams.getPage(),
    			orderQueryParams.getSize(),
    			Sort.by("createdDate")
    	);
    	
    	User user = userRepository.findById(orderQueryParams.getUserId()).orElseThrow();
    	
    	Page<Order> orderList = orderRepository.findAllByUser(user, pageable);
    	
        return orderList;
    }

    @Override
    public Order getOrderById(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        
        return order;
    }

    @Transactional
    @Override
    public Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest) {
        // 檢查 user 是否存在
        if (!userExists(userId)) {
            log.warn("該 userId {} 不存在", userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        
        BigDecimal totalAmount = BigDecimal.valueOf(0);
        List<OrderItem> orderItems = new ArrayList<OrderItem>();
        
        for (BuyItem buyItem: createOrderRequest.getBuyItems()) {
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

            orderItems.add(orderItem);
        }

        // 創建訂單
        Order order = new Order();
        
        User user = userRepository.getReferenceById(userId);
        
        order.setUser(user);
        order.setTotalAmount(totalAmount);
        
        Date now = new Date();
        
        order.setCreatedDate(now);
        order.setLastModifiedDate(now);
        order.setPaymentStatus(PaymentStatus.UNPAY);

        for (OrderItem orderItem: orderItems) {
        	orderItem.setOrder(order);
        }
        order.setOrderItems(orderItems);
        
        order = orderRepository.save(order);
        
        orderItems = orderItemRepository.saveAll(orderItems);
        
        return order.getOrderId();
    }

	@Override
	public String checkout(Integer userId, Integer orderId) {
		// 檢查 user 是否存在
        if (!userExists(userId)) {
            log.warn("該 userId {} 不存在", userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        
		// 檢查 order 是否存在
        if (!orderExists(orderId)) {
            log.warn("該 orderId {} 不存在", orderId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
       
        User user = userRepository.findById(userId).orElseThrow();
        
        // 檢查此 order 屬於此 user
        if (!orderRepository.existsByUser(user)) {
            log.warn("該 orderId {} 不屬於該 userId {}", orderId, userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        
		AllInOne all = new AllInOne("");
		Order order = orderRepository.findById(orderId).orElseThrow();
		
		String uuId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20);
		
		// 產生訂單
		String form = generateCheckOutForm(all, order, uuId);
		
		order.setUuid(uuId);
		orderRepository.save(order);
		
		return form;
	}
	
	private String generateCheckOutForm(AllInOne all, Order order, String uuId) {
		// 轉換為綠界訂單格式
		
	    String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
		String totalAmount = String.valueOf(order.getTotalAmount().intValue());
		
		StringBuilder orderDetail = new StringBuilder();
		
		List<OrderItem> orderItems = orderItemRepository.findAllByOrder(order);

		// 建立綠界訂單內容
		for (OrderItem orderItem: orderItems) {
			Integer productId = orderItem.getProductId();
			Product product = productRepository.findById(productId).orElseThrow();
			
			String productName = product.getProductName();
			String quantity = String.valueOf(orderItem.getQuantity());
			String amount = String.valueOf(orderItem.getAmount().intValue());
			
			orderDetail.append(String.format("[%s * %s = %s]", productName, quantity, amount));
		}
    	
		// 產生訂單
		AioCheckOutALL obj = new AioCheckOutALL();
		obj.setMerchantTradeNo(uuId);
		obj.setMerchantTradeDate(now);
		obj.setTotalAmount(totalAmount);		
		obj.setTradeDesc(order.getOrderId().toString());
		obj.setItemName(orderDetail.toString());
		obj.setReturnURL(hostUrl + "/callback");
		obj.setNeedExtraPaidInfo("N");
		// 商店轉跳網址
		obj.setClientBackURL(clientBackUrl);
		
		return all.aioCheckOut(obj, null); 
		
	}


	@Override
	public void callback(MultiValueMap<String, String> formData) {
		String rtnCode = formData.get("RtnCode").get(0);
		String merchantTradeNo = formData.get("MerchantTradeNo").get(0);
		String paymentDate = formData.get("PaymentDate").get(0);
		
		Order order = orderRepository.findOrderByUuid(merchantTradeNo);
		
		if (rtnCode.equals("1")) order.setPaymentStatus(PaymentStatus.PAID);
		else order.setPaymentStatus(PaymentStatus.FAIL);
		
		order.setPaymentDate(paymentDate);
		
		orderRepository.save(order);
	}
	
	private boolean userExists(Integer userId) {
		return userRepository.existsById(userId);
	}
	
	private boolean orderExists(Integer orderId) {
		return orderRepository.existsById(orderId);
	}
}
