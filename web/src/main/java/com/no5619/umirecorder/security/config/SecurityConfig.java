package com.no5619.umirecorder.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            //SpringSecurity-csrf防護: 登入成功後會送一個XSRF-TOKEN的cookie到client
            //.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())

            .csrf(csrf -> {
                csrf.disable();
                csrf.ignoringRequestMatchers(toH2Console());
            })

            //The cors() method will add the Spring-provided CorsFilter to the application context,
            //bypassing the authorization checks for OPTIONS requests.
            .cors(Customizer.withDefaults())

            .headers(headers -> headers.frameOptions().disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                //.requestMatchers("/actuator/**").permitAll()
                .anyRequest().hasAnyRole("USER", "ADMIN")
            )
            .logout(logoutCustomizer -> logoutCustomizer
                    .logoutUrl("/auth/logout")
                    .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                    .deleteCookies("JSESSIONID")
            )

            //預設基本認證方式，每次請求時在HttpRequestHeader要加上Authorization header
            //不使用httpBasic，原本抱401錯誤會變成報403錯誤
            //.httpBasic(Customizer.withDefaults())

            //disable redirecting to the default login page
            .formLogin().disable()

            .build();
    }

//    @Bean
//    public WebSecurityCustomizer ignoringCustomizer() {
//        return (web) -> {
//            web.ignoring().requestMatchers("/h2-console");
//        };
//    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider);
        return authenticationManagerBuilder.build();
    }
}
