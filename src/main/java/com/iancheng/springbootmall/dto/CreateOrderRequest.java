package com.iancheng.springbootmall.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {

    @NotEmpty
    private List<BuyItem> buyItems;

}
