package com.no5619.umirecorder.security.controller;

import java.util.Collections;

import com.no5619.umirecorder.dto.MsgDto;
import com.no5619.umirecorder.security.config.AuthedUser;
import com.no5619.umirecorder.dto.LoginDto;
import com.no5619.umirecorder.dto.SignupDto;
import com.no5619.umirecorder.entity.Role;
import com.no5619.umirecorder.entity.UserEntity;
import com.no5619.umirecorder.repository.RoleRepository;
import com.no5619.umirecorder.repository.UserRepository;
import com.no5619.umirecorder.security.service.AuthService;
import com.no5619.umirecorder.security.service.CaptchaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

/**
 * 懶得寫AuthService，把他一起寫在Controller上
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	private AuthService authService;
	@Autowired
	private CaptchaService captchaService;

	@PostMapping("/login")
	public MsgDto login(@RequestBody LoginDto loginDto){
		captchaService.checkCaptcha(loginDto.getCaptchaCode());
		return authService.login(loginDto);
	}

    @PostMapping("/signup")
    public ResponseEntity<MsgDto> signup(@RequestBody SignupDto signupDto) {
		captchaService.checkCaptcha(signupDto.getCaptchaCode());
		return authService.signup(signupDto);
    }

	@GetMapping("/nologin")
	public ResponseEntity<MsgDto> noLogin() {
		return authService.noLogin();
	}
}
