package com.iancheng.springbootmall.dao;

import com.iancheng.springbootmall.dto.ProductRequest;
import com.iancheng.springbootmall.model.Product;

import java.util.List;

public interface ProductDao {
    List<Product> getProducts();

    Product getProductById(Integer productId);

    Integer createProduct(ProductRequest productRequest);

    void updateProduct(Integer productId, ProductRequest productRequest);

    void deleteProductById(Integer productId);

}
