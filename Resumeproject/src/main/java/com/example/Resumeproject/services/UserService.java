package com.example.Resumeproject.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Resumeproject.models.User;
import com.example.Resumeproject.repositories.UserRespository;

@Service
public class UserService {

    @Autowired
    UserRespository userRepository;

    public List<User> getAllUsers(){
        return userRepository.findAll();
        
    }
}
