package com.example.Resumeproject.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Resumeproject.models.User;
import com.example.Resumeproject.services.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired

     UserService userService;
    
    @GetMapping("/getAllUsers")
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }
}
