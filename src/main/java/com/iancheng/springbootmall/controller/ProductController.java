package com.iancheng.springbootmall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iancheng.springbootmall.constant.ProductCategory;
import com.iancheng.springbootmall.dto.ProductQueryParams;
import com.iancheng.springbootmall.dto.ProductRequest;
import com.iancheng.springbootmall.model.Product;
import com.iancheng.springbootmall.service.ProductService;
import com.iancheng.springbootmall.util.PageUtil;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;


@Validated
@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @Tag(name = "getProducts")
    @GetMapping("/products")
    public ResponseEntity<PageUtil<Product>> getProducts(
            // 查詢條件 Filtering
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) String search,

            // 排序 Sorting
            @RequestParam(defaultValue = "createdDate") String orderBy,
            @RequestParam(defaultValue = "DESC") String sort,

            // 分頁 Pagination
            @RequestParam(defaultValue = "5") @Max(1000) @Min(0) Integer size,
            @RequestParam(defaultValue = "0") @Min(0) Integer page
    ) {
        ProductQueryParams productQueryParams = new ProductQueryParams();
        productQueryParams.setCategory(category);
        productQueryParams.setSearch(search);
        productQueryParams.setOrderBy(orderBy);
        productQueryParams.setSort(sort);
        productQueryParams.setSize(size);
        productQueryParams.setPage(Math.max(page - 1, 0));

        // 取得 product list 分頁
        Page<Product> productListPage = productService.getProducts(productQueryParams);

        // 整理分頁
        PageUtil<Product> pageUtil = new PageUtil<>();
        pageUtil.setResults(productListPage.getContent());
        pageUtil.setSize(productListPage.getSize());
        pageUtil.setPage(productListPage.getPageable().getPageNumber());
        pageUtil.setTotal(productListPage.getTotalElements());
        pageUtil.setTotalPages(productListPage.getTotalPages());
        return ResponseEntity.status(HttpStatus.OK).body(pageUtil);
    }

    @Tag(name = "getProduct")
    @GetMapping("/products/{productId}")
    public ResponseEntity<Product> getProduct(
            @PathVariable Integer productId
    ) {
        Product product = productService.getProductById(productId);

        if (product != null) return ResponseEntity.status(HttpStatus.OK).body(product);
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Tag(name = "createProduct")
    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(
            @ModelAttribute ProductRequest productRequest
    ) {
        Product product = productService.createProduct(productRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @Tag(name = "updateProduct")
    @PutMapping("/products/{productId}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Integer productId,
            @ModelAttribute ProductRequest productRequest
    ) {
        // 檢查 product 是否存在
        Product product = productService.getProductById(productId);

        if (product == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        // 修改商品的數據
        productService.updateProduct(productId, productRequest);

        Product updatedProduct = productService.getProductById(productId);

        return ResponseEntity.status(HttpStatus.OK).body(updatedProduct);
    }

    @Tag(name = "deleteProduct")
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer productId) {
        productService.deleteProductById(productId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Tag(name = "getProductCategories")
    @GetMapping("/products/categories")
    public ResponseEntity<ProductCategory[]> getProductCategories() {
        ProductCategory[] ProductCategories = productService.getProductCategories();

        return ResponseEntity.status(HttpStatus.OK).body(ProductCategories);
    }
}