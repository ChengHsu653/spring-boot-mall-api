package com.iancheng.springbootmall.model;

import com.iancheng.springbootmall.constant.ProductCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private Integer productId;

	private String productName;

	@Enumerated(EnumType.STRING)
	private ProductCategory category;

	@Lob @Basic(fetch=FetchType.LAZY)
	@Column(name = "image", columnDefinition="LONGBLOB")
	private byte[] image;

	private BigDecimal price;

	private Integer stock;

	private String description;

	private Date createdDate;

	private Date lastModifiedDate;

}
