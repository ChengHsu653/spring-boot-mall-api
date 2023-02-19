package com.iancheng.springbootmall.dao;

import com.iancheng.springbootmall.dto.ProductRequest;
import com.iancheng.springbootmall.model.Product;

public interface ProductDao {
    Product getProductById(Integer productId);
    Integer createProduct(ProductRequest productRequest);
    void updateProduct(Integer productId, ProductRequest productRequest);
}
