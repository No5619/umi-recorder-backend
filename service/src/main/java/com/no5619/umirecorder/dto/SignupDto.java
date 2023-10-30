package com.no5619.umirecorder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupDto {
    private String email;
    private String password;
    private String name;
    private String captchaCode;

    public SignupDto(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
}
