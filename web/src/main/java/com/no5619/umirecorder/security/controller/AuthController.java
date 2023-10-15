package com.no5619.umirecorder.security.controller;

import java.util.Collections;

import com.no5619.umirecorder.security.config.AuthenticatedUser;
import com.no5619.umirecorder.dto.LoginDto;
import com.no5619.umirecorder.dto.RegisterDto;
import com.no5619.umirecorder.entity.Role;
import com.no5619.umirecorder.entity.UserEntity;
import com.no5619.umirecorder.repository.RoleRepository;
import com.no5619.umirecorder.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 懶得寫AuthService，把他一起寫在Controller上
 */
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



	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginDto loginDto){
		//如果login帳密打錯，在authenticationManager.authenticate內會拋出錯誤
		//authenticationManager會透過動態代理的方式，調用UserDetailsService (被自己寫的CustomerUserDetailsService繼承)
		Authentication authentication = authenticationManager.authenticate(
				new AuthenticatedUser(loginDto.email, loginDto.password, request.getRequestedSessionId())
		);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		System.out.println("login controller##########################################");
		System.out.println("authentication: " + authentication);
		System.out.println("UserName: " + ((UserDetails)authentication.getPrincipal()).getUsername() );
		System.out.println("##########################################################");
		System.out.println();
		return new ResponseEntity<>("User signed sucess!", HttpStatus.OK);
	}

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            return new ResponseEntity<>("Email is taken!", HttpStatus.BAD_REQUEST);
        }

        UserEntity user = new UserEntity();
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode((registerDto.getPassword())));
        user.setName(null);
        Role role = roleRepository.findByName("USER").orElseThrow(() -> new SecurityException("找不到role"));
        user.setRoles(Collections.singletonList(role));
        userRepository.save(user);

        return new ResponseEntity<>("User registered success!", HttpStatus.OK);
    }
	
}
