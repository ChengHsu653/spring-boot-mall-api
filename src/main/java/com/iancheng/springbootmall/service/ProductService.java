package com.iancheng.springbootmall.service;

import org.springframework.data.domain.Page;

import com.iancheng.springbootmall.constant.ProductCategory;
import com.iancheng.springbootmall.dto.ProductQueryParams;
import com.iancheng.springbootmall.dto.ProductRequest;
import com.iancheng.springbootmall.model.Product;

public interface ProductService {
	Product createProduct(ProductRequest productRequest);

	Page<Product> getProducts(ProductQueryParams productQueryParams);
    
    Product getProductById(Integer productId);
    
    void updateProduct(Integer productId, ProductRequest productRequest);
    
    void deleteProductById(Integer productId);

    ProductCategory[] getProductCategories();

}
