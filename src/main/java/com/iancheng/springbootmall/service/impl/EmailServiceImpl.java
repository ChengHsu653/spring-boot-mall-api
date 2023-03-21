package com.iancheng.springbootmall.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.iancheng.springbootmall.model.User;
import com.iancheng.springbootmall.service.EmailService;

import jakarta.mail.internet.MimeMessage;


@Component
public class EmailServiceImpl implements EmailService{
	
	private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

	@Autowired
	private JavaMailSender javaMailSender;
	
	@Value("${spring.mail.username}") 
	private String sender;
	
	@Override
	public void sendValidationLink(User user) {
		try {
            var link = String.format(
            		"http://localhost:8080/users/verify?email=%s&token=%s",
            		user.getEmail(),
            		user.getPassword());
            
            var anchor = String.format(
            		"<a href='%s'>驗證郵件</a>",
            		link);
            
            var html = String.format(
            		"請按 %s 啟用帳戶或複製鏈結至網址列:<br><br> %s", 
            		anchor,
            		link);
            
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            
            helper.setFrom(sender);
            helper.setTo(user.getEmail());
            helper.setText(html, true);
            helper.setSubject("Spring Boot Mall 驗證郵件(請勿回傳)");

            javaMailSender.send(mimeMessage);
            
            log.info("Email {} 寄送成功!", user.getEmail());
        } catch (Exception e) {
        	log.error("Email {} 寄送失敗!", user.getEmail());
        }
	}

}
