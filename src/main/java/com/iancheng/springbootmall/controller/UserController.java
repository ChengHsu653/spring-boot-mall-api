package com.iancheng.springbootmall.controller;

import java.io.IOException;

import com.iancheng.springbootmall.dto.UserQueryParams;
import com.iancheng.springbootmall.util.PageUtil;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.iancheng.springbootmall.dto.UserForgetRequest;
import com.iancheng.springbootmall.dto.UserLoginRequest;
import com.iancheng.springbootmall.dto.UserRegisterRequest;
import com.iancheng.springbootmall.model.User;
import com.iancheng.springbootmall.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserController {

	private final UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}


	@Tag(name = "register")
	@PostMapping("/users/register")
	public ResponseEntity<User> register(
			@RequestBody @Valid UserRegisterRequest userRegisterRequest
	) {
		User user = userService.register(userRegisterRequest);

		return ResponseEntity.status(HttpStatus.CREATED).body(user);
	}

	@Tag(name = "login")
	@PostMapping("/users/login")
	public ResponseEntity<User> login(
			@RequestBody @Valid UserLoginRequest userLoginRequest
	) {
		User user = userService.login(userLoginRequest);

		return ResponseEntity.status(HttpStatus.OK).body(user);
	}

	@Tag(name = "logout")
	@GetMapping("/users/logout")
	public ResponseEntity<?> logout() {
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@Tag(name = "forgetPassword")
	@PostMapping("/users/forget")
	public ResponseEntity<?> forgetPassword(
			@RequestBody UserForgetRequest userForgetRequest
	) {
		userService.forgetPassword(userForgetRequest);

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@Tag(name = "refreshToken")
	@PostMapping("/users/refresh_token")
	public void refreshToken(
			HttpServletRequest request,
			HttpServletResponse response
	) throws IOException {
		userService.refreshToken(request, response);
	}

	@Tag(name = "getUsers")
	@GetMapping("/users")
	public ResponseEntity<PageUtil<User>> getUsers(
			// 分頁 Pagination
			@RequestParam(defaultValue = "5") @Max(1000) @Min(0) Integer size,
			@RequestParam(defaultValue = "0") @Min(0) Integer page
	) {
		UserQueryParams userQueryParams = new UserQueryParams();
		userQueryParams.setSize(size);
		userQueryParams.setPage(Math.max(page - 1, 0));

		// 取得 order list 分頁
		Page<User> userListPage = userService.getUsers(userQueryParams);

		// 整理分頁
		PageUtil<User> pageUtil = new PageUtil<>();
		pageUtil.setResults(userListPage.getContent());
		pageUtil.setSize(userListPage.getSize());
		pageUtil.setPage(userListPage.getPageable().getPageNumber());
		pageUtil.setTotal(userListPage.getTotalElements());
		pageUtil.setTotalPages(userListPage.getTotalPages());

		return ResponseEntity.status(HttpStatus.OK).body(pageUtil);
	}
}