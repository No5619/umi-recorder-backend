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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;
    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

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
            //.cors(Customizer.withDefaults())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

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

            //打"/oauth2/authorization/*"這個API，就可進行Oauth
            //ex: 有application.properties有設定google的Oauth，就會長出"/oauth2/authorization/google"這個API
            .oauth2Login(oath2 -> oath2
                //.loginPage("/auth/oauth2").permitAll() //若登入失敗為自動導頁(前後端不分離用)
                .successHandler(oAuth2LoginSuccessHandler)
            )

            //預設基本認證方式，每次請求時在HttpRequestHeader要加上Authorization header
            //不使用httpBasic，原本抱401錯誤會變成報403錯誤
            //.httpBasic(Customizer.withDefaults())

            //disable redirecting to the default login page
            .formLogin().disable()

            .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", configuration);
        return urlBasedCorsConfigurationSource;
    }

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
