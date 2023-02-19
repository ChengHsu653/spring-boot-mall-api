package com.iancheng.springbootmall.dao;

import com.iancheng.springbootmall.model.Product;

public interface ProductDao {
    Product getProductById(Integer productId);
}
