package com.iancheng.springbootmall.model;

import com.iancheng.springbootmall.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "`order`")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer orderId;

	private BigDecimal totalAmount;

	private Date createdDate;

	private Date lastModifiedDate;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "fk_user_id")
	private User user;

	@OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
	private List<OrderItem> orderItems;

	private String uuid;

	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;

	private String PaymentDate;

}
