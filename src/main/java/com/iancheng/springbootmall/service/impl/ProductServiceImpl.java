package com.iancheng.springbootmall.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.iancheng.springbootmall.constant.ProductCategory;
import com.iancheng.springbootmall.dto.ProductQueryParams;
import com.iancheng.springbootmall.dto.ProductRequest;
import com.iancheng.springbootmall.model.Product;
import com.iancheng.springbootmall.repository.ProductRepository;
import com.iancheng.springbootmall.service.ProductService;
import com.iancheng.springbootmall.util.ImageUtil;

@Service
public class ProductServiceImpl implements ProductService {

	private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
	 
	private ProductRepository productRepository;

	@Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

    
    @Override
    public Page<Product> getProducts(ProductQueryParams productQueryParams) {   	
    	
    	Pageable pageable = PageRequest.of(
    			productQueryParams.getPage(),
    			productQueryParams.getSize(),
    			Sort.by(productQueryParams.getOrderBy())
    	);
    	
    	ProductCategory category = productQueryParams.getCategory();
    	
    	String search = productQueryParams.getSearch();
    	
    	if (category == null && search == null) return productRepository.findAll(pageable);
    	
    	if (category == null) return productRepository.findAllByProductNameContaining(search, pageable);    		
    	
    	if (search == null) return productRepository.findAllByCategory(category, pageable);    		
    	
    	return productRepository.findAllByProductNameContainingAndCategory(search, category, pageable);  	
    }

    @Override
    public Product getProductById(Integer productId) {
    	Optional<Product> optionalProduct = productRepository.findById(productId);
        
    	if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
//            product.setImage(ImageUtil.decompressImage(product.getImage()));
            
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
    	
    	try {
//			product.setImage(ImageUtil.compressImage(productRequest.getImage().getBytes()));
			product.setImage(productRequest.getImage().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
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
            
            try {
//    			product.setImage(ImageUtil.compressImage(productRequest.getImage().getBytes()));
            	product.setImage(productRequest.getImage().getBytes());
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
            
            product.setPrice(productRequest.getPrice());
            product.setStock(productRequest.getStock());
            product.setDescription(productRequest.getDescription());
            product.setLastModifiedDate(new Date());
            
            productRepository.save(product);
        } else {
        	log.warn("該商品 id: {} 不存在", productId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void deleteProductById(Integer productId) {
    	productRepository.deleteById(productId);
    }


	@Override
	public ProductCategory[] getProductCategories() {
		ProductCategory[] categories = ProductCategory.values();
		
		return categories;
	}
}
