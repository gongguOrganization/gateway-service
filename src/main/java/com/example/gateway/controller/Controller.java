package com.example.gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class Controller {
    private final Environment env;

    @GetMapping("/health_check")
    public String status(){
        return "It's Working in ApiGateway Service"
                + ", token secret=" + env.getProperty("jwt.secretKey")
                + ", token expiration time=" + env.getProperty("jwt.expTime")
                + ", test.test=" + env.getProperty("test.test");
    }
}
