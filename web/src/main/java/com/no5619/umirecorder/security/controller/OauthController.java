package com.no5619.umirecorder.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OauthController {
    @GetMapping("/auth/oauth2")
    public String getLoginPage() {
        return "login";
    }
}
