package com.no5619.umirecorder.security.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.LineCaptcha;
import com.no5619.umirecorder.security.service.CaptchaService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.io.IOException;

@RestController
public class CaptchaController {
    @Autowired
    private CaptchaService captchaService;

    @GetMapping("/auth/api/getCode")
    @ResponseBody
    public String getCaptcha() {
        return captchaService.getCaptcha();
    }


}
