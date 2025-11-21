package com.example.Resumeproject.controllers;

import com.example.Resumeproject.dto.LoginRequest;
import com.example.Resumeproject.dto.RegisterRequest;
import com.example.Resumeproject.response.SuccessResponse;
import com.example.Resumeproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("register")
    public SuccessResponse<Object> getRegister(@RequestBody RegisterRequest request) {
            return userService.register(request);
    }

    @PostMapping("login")
    public SuccessResponse<Object> login(@RequestBody LoginRequest request) {
            return userService.login(request);
    }

}
