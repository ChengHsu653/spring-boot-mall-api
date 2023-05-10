package com.iancheng.springbootmall.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer orderItemId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "fk_order_id")
	@JsonIgnore
	private Order order;

	private Integer quantity;

	private BigDecimal amount;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_product_id", referencedColumnName = "product_id")
	private Product product;

}
