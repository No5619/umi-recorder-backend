package com.no5619.umirecorder.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
public class SecurityConfig {
    @Autowired
    MvcRequestMatcher.Builder mvcMatcherBuilder;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //http.authorizeRequests().antMatchers("/**").hasRole("USER").and().formLogin(); //=>使用預設畫面登入
        http
            .csrf(csrf -> csrf.disable()) //disables the CSRF filter

            //設定切面
            .authorizeRequests(auth -> auth
                .requestMatchers(
                        //設定哪些URL會觸發切面 (範圍較小，故放前面)
                        mvcMatcherBuilder.pattern("/auth/register/**"),
                        mvcMatcherBuilder.pattern("\"/auth/login/**\"")
                )
                .permitAll() //所有權限的人都放行

                .anyRequest()   //所有url都會觸發 (範圍較大，故放後面)
                .hasAnyRole("USER")  //只有"USER"權限的人能放行
            )

            .httpBasic(Customizer.withDefaults()); // 預設基本認證方式，每次請求時在HttpRequestHeader要加上Authorization參數

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
