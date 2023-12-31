package com.no5619.umirecorder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    private String email;
    private String password;
    private String captchaCode;

    public LoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
