package com.iancheng.springbootmall.controller;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.iancheng.springbootmall.dto.OAuth20LoginParams;
import com.iancheng.springbootmall.model.User;
import com.iancheng.springbootmall.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class Oauth2Controller {

    @Value("${application.client-url}")
    private String CLIENT_URL;

    private final UserService userService;
    private final OAuth20Service googleOAuth20Service;


    @Autowired
    public Oauth2Controller(UserService userService, OAuth20Service googleOAuth20Service) {
        this.userService = userService;
        this.googleOAuth20Service = googleOAuth20Service;
    }

    @RequestMapping("/redirectToGoogle")
    public String redirectToGithub() {
        return "redirect:" + googleOAuth20Service.getAuthorizationUrl();
    }

    @RequestMapping("/google/callback")
    public void callback(@RequestParam String code, HttpServletResponse servletResponse) throws Exception {
        // 向 Google 取得 accessToken
        OAuth2AccessToken accessToken = googleOAuth20Service.getAccessToken(code);

        // 拿到 accessToken 之後，向 Google api 取得該 user 的 data
        OAuthRequest request = new OAuthRequest(Verb.GET, "https://www.googleapis.com/oauth2/v1/userinfo?alt=json");
        googleOAuth20Service.signRequest(accessToken, request);
        Response response = googleOAuth20Service.execute(request);

        JSONObject jsonObject = new JSONObject(response.getBody());
        OAuth20LoginParams oAuth20LoginParams = new OAuth20LoginParams();

        oAuth20LoginParams.setEmail(jsonObject.getString("email"));
        oAuth20LoginParams.setName(jsonObject.getString("name"));

        User user = userService.oauth20Login(oAuth20LoginParams);

        Cookie cookie = new Cookie("token", user.getAccessToken());
        cookie.setMaxAge(86400);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        servletResponse.addCookie(cookie);
        servletResponse.sendRedirect(CLIENT_URL +
                "/products?token=" + user.getAccessToken() + "&userId=" + user.getUserId());

    }
}
