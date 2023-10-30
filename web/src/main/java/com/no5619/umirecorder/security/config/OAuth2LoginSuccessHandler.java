package com.no5619.umirecorder.security.config;

import com.no5619.umirecorder.dto.LoginDto;
import com.no5619.umirecorder.dto.SignupDto;
import com.no5619.umirecorder.repository.UserRepository;
import com.no5619.umirecorder.security.controller.AuthController;
import com.no5619.umirecorder.security.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
//    @Value("${frontend.url}")
//    private String frontendUrl;
    @Autowired
    private UserRepository userRepository;

    //避免循環依賴，
    @Autowired
    private ApplicationContext appContext;
    private AuthService authService;

    private static final int EXPIRED_TIME = 10 * 60;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        this.setAlwaysUseDefaultTargetUrl(true);
        this.setDefaultTargetUrl("http://localhost:3000/#/VideosListPage");

        authService = appContext.getBean(AuthService.class);
        OAuth2AuthenticationToken oauth = (OAuth2AuthenticationToken) authentication;
        Map oauthAttributes = oauth.getPrincipal().getAttributes();
        userRepository.findByEmail((String) oauthAttributes.get("email"))
            .ifPresentOrElse(
                userEntity -> {
                    authService.login(new LoginDto(userEntity.getEmail(),userEntity.getPassword()));
                },
                () -> {
                    authService.signup(new SignupDto(
                        (String) oauthAttributes.get("email"),
                        UUID.randomUUID().toString(),
                        (String) oauthAttributes.get("name")));
                });

        // TODO: Oauth登入後好像不會被AfterLoggedinFilter攔截? 先暫時在此加cookie (原本AfterLoggedinFilter就有負責加cookie了)
        Cookie cookie = new Cookie("logged-in", "true");
        cookie.setPath("/");
        cookie.setMaxAge(EXPIRED_TIME);
        response.addCookie(cookie);

        super.onAuthenticationSuccess(request, response, authentication);
    }
}