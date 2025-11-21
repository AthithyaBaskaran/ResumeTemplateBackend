package com.example.Resumeproject.controllers;

import com.example.Resumeproject.dto.LoginRequest;
import com.example.Resumeproject.dto.RegisterRequest;
import com.example.Resumeproject.models.User;
import com.example.Resumeproject.response.SuccessResponse;
import com.example.Resumeproject.services.AuthService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("register")
    public SuccessResponse<Object> getRegister(@RequestBody RegisterRequest request) {
            return authService.register(request);
    }

    @PostMapping("login")
    public SuccessResponse<Object> login(@RequestBody LoginRequest request) {
            return authService.login(request);
    }


    @GetMapping("/getAllUsers")
    public SuccessResponse<Object> getAllUsers(){
        return authService.getAllUsers();
    }
}
