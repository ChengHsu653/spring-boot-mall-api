package com.iancheng.springbootmall.util;

import lombok.Data;
import java.util.List;

@Data
public class PageUtil<T> {
    private Integer size;
    private Integer page;
    private Integer totalPages;
    private Long total;
    private List<T> results;
}
