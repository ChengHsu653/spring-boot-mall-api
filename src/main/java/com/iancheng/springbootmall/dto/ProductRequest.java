package com.iancheng.springbootmall.dto;

import com.iancheng.springbootmall.constant.ProductCategory;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data

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

}
