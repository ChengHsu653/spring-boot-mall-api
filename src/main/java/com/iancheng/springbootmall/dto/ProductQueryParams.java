package com.iancheng.springbootmall.dto;

import com.iancheng.springbootmall.constant.ProductCategory;
import lombok.Data;

@Data
public class ProductQueryParams {

    private ProductCategory category;

    private String search;

    private String orderBy;

    private String sort;

    private Integer size;

    private Integer page;
    
}
