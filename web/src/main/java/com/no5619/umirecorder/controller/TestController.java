package com.no5619.umirecorder.controller;

import com.no5619.umirecorder.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    private TestService testService;

    @GetMapping("/echo")
    public String echo(String input) {
        return testService.echo(input);
    }
}
