package com.iancheng.springbootmall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iancheng.springbootmall.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>{
	
}
