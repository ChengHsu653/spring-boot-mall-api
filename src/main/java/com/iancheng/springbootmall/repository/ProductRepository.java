package com.iancheng.springbootmall.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iancheng.springbootmall.constant.ProductCategory;
import com.iancheng.springbootmall.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>{

	Page<Product> findAllByProductNameContaining(String search, Pageable pageable);
	
	Page<Product> findAllByCategory(ProductCategory category, Pageable pageable);
	
	Page<Product> findAllByProductNameContainingAndCategory(String search, ProductCategory category, Pageable pageable);
}
