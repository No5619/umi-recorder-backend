package com.no5619.umirecorder.security.service;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class CaptchaService {
    @Autowired
    HttpServletRequest req;
    @Autowired
    HttpServletResponse resp;
    @Autowired
    HttpSession session;

    public String getCaptcha() {
        // 这里是创建验证码的长、宽、验证码字符数、干扰元素数量
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(160, 80, 4, 50);
        session.setAttribute("code", captcha.getCode());
        // 获取验证码图片base64
        String base64Img = captcha.getImageBase64();
        return base64Img;
    }

    public void checkCaptcha(String inputCode) {
        String captchaCode = (String) session.getAttribute("code");
        if (!inputCode.equals(captchaCode))
            throw new BadCredentialsException("驗證碼輸入錯誤");
    }
}
