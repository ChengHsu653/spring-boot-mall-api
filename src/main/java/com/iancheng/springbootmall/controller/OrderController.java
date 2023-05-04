package com.iancheng.springbootmall.controller;

import com.iancheng.springbootmall.dto.CreateOrderRequest;
import com.iancheng.springbootmall.dto.OrderQueryParams;
import com.iancheng.springbootmall.model.Order;
import com.iancheng.springbootmall.service.OrderService;
import com.iancheng.springbootmall.util.PageUtil;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

	private final OrderService orderService;

	@Autowired
    public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}


    @Tag(name = "getOrders")
    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<PageUtil<Order>> getOrders(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "5") @Max(1000) @Min(0) Integer size,
            @RequestParam(defaultValue = "0") @Min(0) Integer page
    ) {
        OrderQueryParams orderQueryParams = new OrderQueryParams();
        orderQueryParams.setUserId(userId);
        orderQueryParams.setSize(size);
        orderQueryParams.setPage(Math.max(page - 1, 0));

        // 取得 order list 分頁
        Page<Order> orderListPage = orderService.getOrders(orderQueryParams);

        // 整理分頁
        PageUtil<Order> pageUtil = new PageUtil<>();
        pageUtil.setResults(orderListPage.getContent());
        pageUtil.setSize(orderListPage.getSize());
        pageUtil.setPage(orderListPage.getPageable().getPageNumber());
        pageUtil.setTotal(orderListPage.getTotalElements());
        pageUtil.setTotalPages(orderListPage.getTotalPages());
        
        return ResponseEntity.status(HttpStatus.OK).body(pageUtil);
    }

    @Tag(name = "createOrder")
    @PostMapping("/users/{userId}/orders")
    public ResponseEntity<Order> createOrder(
    		@PathVariable Integer userId,
            @RequestBody @Valid CreateOrderRequest createOrderRequest
    ) {
        Integer orderId = orderService.createOrder(userId, createOrderRequest);

        Order order = orderService.getOrderById(orderId);
 
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
    
    @Tag(name = "checkout")
    @PostMapping("/users/{userId}/orders/{orderId}")
    public ResponseEntity<String> checkout(
    		@PathVariable Integer userId,
    		@PathVariable Integer orderId				
    ) {
        String allPayAPIForm = orderService.checkout(userId, orderId);
    	
        return ResponseEntity.status(HttpStatus.OK).body(allPayAPIForm);
    }
    
    @Tag(name = "callback")
	@RequestMapping(value="/callback",
    				method=RequestMethod.POST,
    				consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public void callback(
			@RequestBody MultiValueMap<String, String> formData
	) {
    	orderService.callback(formData);
	}

    @Tag(name = "getAllOrders")
    @GetMapping("/users/all/orders")
    public ResponseEntity<PageUtil<Order>> getAllOrders(
            @RequestParam(defaultValue = "5") @Max(1000) @Min(0) Integer size,
            @RequestParam(defaultValue = "0") @Min(0) Integer page
    ) {
        OrderQueryParams orderQueryParams = new OrderQueryParams();
        orderQueryParams.setSize(size);
        orderQueryParams.setPage(Math.max(page - 1, 0));

        // 取得 order list 分頁
        Page<Order> orderListPage = orderService.getAllOrders(orderQueryParams);

        // 整理分頁
        PageUtil<Order> pageUtil = new PageUtil<>();
        pageUtil.setResults(orderListPage.getContent());
        pageUtil.setSize(orderListPage.getSize());
        pageUtil.setPage(orderListPage.getPageable().getPageNumber());
        pageUtil.setTotal(orderListPage.getTotalElements());
        pageUtil.setTotalPages(orderListPage.getTotalPages());

        return ResponseEntity.status(HttpStatus.OK).body(pageUtil);
    }
}
