package com.no5619.umirecorder.security.filter;

import com.no5619.umirecorder.security.config.AuthedUser;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class AfterLoggedinFilter extends GenericFilter {
    private static final int EXPIRED_TIME = 10 * 60;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if ( auth !=null && !(auth instanceof AnonymousAuthenticationToken) ) {
            Cookie cookie = new Cookie("logged-in", "true");
            cookie.setPath("/");
            cookie.setMaxAge(EXPIRED_TIME);
            resp.addCookie(cookie);
        }

        chain.doFilter(req, resp);
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
