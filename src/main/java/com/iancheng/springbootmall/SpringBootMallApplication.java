package com.iancheng.springbootmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(info = @Info(title = "Spring Boot Mall"))
@SpringBootApplication
public class SpringBootMallApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootMallApplication.class, args);
    }
    
}
