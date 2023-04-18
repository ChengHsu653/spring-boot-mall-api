package com.iancheng.springbootmall.controller;

import com.iancheng.springbootmall.dto.CreateOrderRequest;
import com.iancheng.springbootmall.dto.OrderQueryParams;
import com.iancheng.springbootmall.model.Order;
import com.iancheng.springbootmall.service.OrderService;
import com.iancheng.springbootmall.util.PageResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Tag(name = "getOrders")
    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<PageResponse<Order>> getOrders(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "5") @Max(1000) @Min(0) Integer size,
            @RequestParam(defaultValue = "0") @Min(0) Integer page
    ) {
        OrderQueryParams orderQueryParams = new OrderQueryParams();
        orderQueryParams.setUserId(userId);
        orderQueryParams.setSize(size);
        orderQueryParams.setPage(page);

        // 取得 order list 分頁
        Page<Order> orderListPage = orderService.getOrders(orderQueryParams);

        // 整理分頁
        PageResponse<Order> pageResponse = new PageResponse<>();
        pageResponse.setResults(orderListPage.getContent());
        pageResponse.setSize(orderListPage.getSize());
        pageResponse.setPage(orderListPage.getPageable().getPageNumber());
        pageResponse.setTotal(orderListPage.getTotalElements());
        
        return ResponseEntity.status(HttpStatus.OK).body(pageResponse);
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
}
