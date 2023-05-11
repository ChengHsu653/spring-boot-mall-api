package com.iancheng.springbootmall.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserResetPasswordRequest {
	@Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
    
    @NotBlank
    private String confirmPassword;

}
