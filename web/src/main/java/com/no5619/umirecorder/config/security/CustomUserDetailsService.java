package com.no5619.umirecorder.config.security;

import com.no5619.umirecorder.entity.UserEntity;
import com.no5619.umirecorder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service("userDetailService")
public class CustomUserDetailsService implements UserDetailsService {
	@Autowired
	UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		//透過DAO、Repository向DB查出帳密
		UserEntity userEntity = userRepository.findByEmail(email)
                                              .orElseThrow(() -> new UsernameNotFoundException("Email not found"));

		//因為在SecurityConfig中定義的HttpSecurity是用"hasRole"條件，所以要加"ROLE_"前綴。 (改用"has_Authority"就不用加"ROLE_"前綴)
		Collection<GrantedAuthority> roles = userEntity.getRoles()
														.stream()
														.map( role -> new SimpleGrantedAuthority("ROLE_"+role.getName()) )
														.collect( Collectors.toList() );
		User user = new User(userEntity.getEmail(), userEntity.getPassword(), roles);

		System.out.println("CustomUserDetailsService.loadUserByUsername##############################");
		System.out.println("CustomUserDetailsService.loadUserByUsername被AuthenticationManager調用!!!!");
		System.out.println("roles: " + roles);
		System.out.println("user: " + user);
		System.out.println("#########################################################################");

		return user;
	}
	
}
