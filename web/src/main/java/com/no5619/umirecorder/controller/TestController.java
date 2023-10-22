package com.no5619.umirecorder.controller;

import com.no5619.umirecorder.dto.MsgDto;
import com.no5619.umirecorder.dto.PostTestDto;
import com.no5619.umirecorder.security.config.AuthedUser;
import com.no5619.umirecorder.service.TestService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    private TestService testService;
    @Autowired
    private HttpServletRequest request;

    @GetMapping("/echo")
    public MsgDto echo(AuthedUser authedUser, String input) {
        return new MsgDto(testService.echo(input), 200);
    }
    @PostMapping("/echo")
    public MsgDto echoPost(AuthedUser authedUser, @RequestBody PostTestDto dto) {
        return new MsgDto(testService.echo(dto.getInput()), 200);
    }
}
