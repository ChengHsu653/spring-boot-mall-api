package com.iancheng.springbootmall.service.impl;

import com.iancheng.springbootmall.dao.ProductDao;
import com.iancheng.springbootmall.dto.ProductQueryParams;
import com.iancheng.springbootmall.dto.ProductRequest;
import com.iancheng.springbootmall.model.Product;
import com.iancheng.springbootmall.repository.ProductRepository;
import com.iancheng.springbootmall.service.ProductService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

	private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
	
    @Autowired
    private ProductDao productDao;
    
    @Autowired
    private ProductRepository productRepository;

    @Override
    public Integer countProduct(ProductQueryParams productQueryParams) {
        return productDao.countProduct(productQueryParams);
    }

    @Override
    public List<Product> getProducts(ProductQueryParams productQueryParams) {
        return productDao.getProducts(productQueryParams);
    }

    @Override
    public Product getProductById(Integer productId) {
    	Optional<Product> optionalProduct = productRepository.findById(productId);
        
    	if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            
            return product;
        } else {
        	log.warn("該商品 id: {} 不存在", productId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Product createProduct(ProductRequest productRequest) {
    	Product product = new Product();
    	
    	product.setProductName(productRequest.getProductName());
    	product.setCategory(productRequest.getCategory());
    	product.setImageUrl(productRequest.getImageUrl());
    	product.setPrice(productRequest.getPrice());
    	product.setStock(productRequest.getStock());
    	product.setDescription(productRequest.getDescription());
    
        Date now = new Date();
        
    	product.setCreatedDate(now);
    	product.setLastModifiedDate(now);
    	
    	product = productRepository.save(product);
        
    	return product;
    }

    @Override
    public void updateProduct(Integer productId, ProductRequest productRequest) {
    	Optional<Product> optionalProduct = productRepository.findById(productId);
        
    	if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            
            product.setProductName(productRequest.getProductName());
            product.setCategory(productRequest.getCategory());
            product.setImageUrl(productRequest.getImageUrl());
            product.setPrice(productRequest.getPrice());
            product.setStock(productRequest.getStock());
            product.setDescription(productRequest.getDescription());
            
            productRepository.save(product);
        } else {
        	log.warn("該商品 id: {} 不存在", productId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void deleteProductById(Integer productId) {
        productDao.deleteProductById(productId);
    }
}
