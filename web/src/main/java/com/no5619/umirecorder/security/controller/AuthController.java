package com.no5619.umirecorder.security.controller;

import java.util.Collections;

import com.no5619.umirecorder.dto.MsgDto;
import com.no5619.umirecorder.security.config.AuthedUser;
import com.no5619.umirecorder.dto.LoginDto;
import com.no5619.umirecorder.dto.RegisterDto;
import com.no5619.umirecorder.entity.Role;
import com.no5619.umirecorder.entity.UserEntity;
import com.no5619.umirecorder.repository.RoleRepository;
import com.no5619.umirecorder.repository.UserRepository;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 懶得寫AuthService，把他一起寫在Controller上
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private HttpServletResponse response;

	private SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();


	/**
	 * 使用 AuthedUser extends UsernamePasswordAuthenticationToken 後， <br>
	 * 就無法將Authentication，轉型成 UserDetails，只能轉成AuthedUser <br>
	 * 不確定會因此產生其他issue
	 */
	@PostMapping("/login")
	public MsgDto login(@RequestBody LoginDto loginDto){
		//如果login帳密打錯，在authenticationManager.authenticate內會拋出錯誤
		//authenticationManager會透過動態代理的方式，調用UserDetailsService (被自己寫的CustomerUserDetailsService繼承)
		Authentication authentication = authenticationManager.authenticate(
				//Spring Document 建議 new UsernamePasswordAuthenticationToken
				new AuthedUser(loginDto.getEmail(), loginDto.getPassword(), request.getRequestedSessionId())
		);

		//SecurityContextHolder.getContext().setAuthentication(authentication);
		var context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
		securityContextRepository.saveContext(context, request, response);

		log.info("authentication:{}", authentication);
		log.info("UserName:{}", ((AuthedUser)authentication).getUsername());
		log.info("SessionId:{}", request.getRequestedSessionId());

		return new MsgDto("logged in successfully!", HttpStatus.OK.value());
	}

    @PostMapping("/signup")
    public ResponseEntity<MsgDto> signup(@RequestBody RegisterDto signupDto) {
        if (userRepository.existsByEmail(signupDto.getEmail())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new MsgDto("Email has been taken!", HttpStatus.BAD_REQUEST.value()));
        }

		/* save UserEntity into DB */
        UserEntity user = new UserEntity();
        user.setEmail(signupDto.getEmail());
        user.setPassword(passwordEncoder.encode((signupDto.getPassword())));
        user.setName(null);
        Role role = roleRepository.findByName("USER").orElseThrow(() -> new SecurityException("帳號無對應權限(查無role)"));
        user.setRoles(Collections.singletonList(role));
        userRepository.save(user);

		this.login(new LoginDto(signupDto.getEmail(), signupDto.getPassword()));

		log.info("SessionId:{}", request.getRequestedSessionId());
		return ResponseEntity.status(HttpStatus.OK)
				.body(new MsgDto("signed in successfully!",  HttpStatus.OK.value()));
    }
}
