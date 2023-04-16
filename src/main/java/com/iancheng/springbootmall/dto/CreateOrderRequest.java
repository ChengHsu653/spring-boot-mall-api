package com.iancheng.springbootmall.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class CreateOrderRequest {

    @NotEmpty
    private List<BuyItem> buyItems;

    public List<BuyItem> getBuyItems() {
        return buyItems;
    }

    public void setBuyItemList(List<BuyItem> buyItems) {
        this.buyItems = buyItems;
    }
}
