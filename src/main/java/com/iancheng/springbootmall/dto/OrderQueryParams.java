package com.iancheng.springbootmall.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderQueryParams {
    private Integer userId;
    private Integer size;
    private Integer page;

}
