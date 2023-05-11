package com.iancheng.springbootmall.dto;

import com.iancheng.springbootmall.constant.ProductCategory;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
