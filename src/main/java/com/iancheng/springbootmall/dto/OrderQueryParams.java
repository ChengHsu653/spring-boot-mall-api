package com.iancheng.springbootmall.dto;

import lombok.Data;

@Data
public class OrderQueryParams {

    private Integer userId;
    private Integer size;
    private Integer page;
    
}
