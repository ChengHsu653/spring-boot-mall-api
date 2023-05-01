package com.iancheng.springbootmall.dto;

import com.iancheng.springbootmall.constant.ProductCategory;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

public class ProductRequest {

    @NotNull
    private String productName;

    @NotNull
    private ProductCategory category;

    @NotNull
    private MultipartFile image;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Integer stock;

    private String description;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public MultipartFile getImage() {
		return image;
	}

	public void setImage(MultipartFile image) {
		this.image = image;
	}

	public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
