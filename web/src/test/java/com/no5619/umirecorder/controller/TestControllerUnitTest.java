package com.no5619.umirecorder.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TestControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void echo() throws Exception {
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.get("/echo?input=hello_world");
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk());
    }
}