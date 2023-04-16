package com.iancheng.springbootmall.service;

import com.iancheng.springbootmall.dto.ProductQueryParams;
import com.iancheng.springbootmall.dto.ProductRequest;
import com.iancheng.springbootmall.model.Product;


import org.springframework.data.domain.Page;

public interface ProductService {
	Product createProduct(ProductRequest productRequest);

	Page<Product> getProducts(ProductQueryParams productQueryParams);
    
    Product getProductById(Integer productId);
    
    void updateProduct(Integer productId, ProductRequest productRequest);
    
    void deleteProductById(Integer productId);

}
