package com.no5619.umirecorder.security.config;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component("AuthenticationProvider")
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    CustomUserDetailsService customUserDetailsService;
    @Autowired
    private HttpServletRequest request;

    //避免securityConfig、AuthenticationProvider循環依賴，改用ApplicationContext取得依賴
    @Autowired
    private ApplicationContext appContext;
    PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (StringUtils.isBlank(username))
            throw new UsernameNotFoundException("username用戶名不可為空");
        if(StringUtils.isBlank(password))
            throw new BadCredentialsException("password密碼不可為空");

        UserDetails user = customUserDetailsService.loadUserByUsername(username);
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        // TODO: !password.equals(user.getPassword()) 此段不確定是否安全
        // 因Oauth"註冊"，密碼使用UUID，再使用Oauth"登入"，直接抓出那筆email的密碼，
        // 但直接抓出的密碼為密文，passwordEncoder.matches(密文, 密文) => 會等於false
        passwordEncoder = appContext.getBean(PasswordEncoder.class);
        if (!passwordEncoder.matches(password, user.getPassword()) && !password.equals(user.getPassword()))
            throw new BadCredentialsException("password密碼錯誤");

        return new AuthedUser(username, password, authorities, request.getRequestedSessionId());
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}