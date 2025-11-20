package com.example.Resumeproject.services;

import com.example.Resumeproject.dto.LoginRequest;
import com.example.Resumeproject.dto.LoginResponse;
import com.example.Resumeproject.dto.RegisterRequest;
import com.example.Resumeproject.dto.RegisterResponse;
import com.example.Resumeproject.models.User;
import com.example.Resumeproject.repositories.UserRepository;
import com.example.Resumeproject.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;


    public RegisterResponse register(RegisterRequest request) {
        RegisterResponse response = new RegisterResponse();

        if (userRepository.existsByUsername(request.getUsername())) {
            response.setSuccess(false);
            response.setMessage("Username already exists");
            return response;
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            response.setSuccess(false);
            response.setMessage("Email already exists");
            return response;
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNo(request.getPhoneNo());
        user.setRole(request.getRole() != null ? request.getRole() : "USER");
        user.setCreatedAt(System.currentTimeMillis());

        User savedUser = userRepository.save(user);
        response.setSuccess(true);
        response.setMessage("User registered successfully");
        response.setUserId(savedUser.getId());
        response.setUsername(savedUser.getUsername());
        response.setEmail(savedUser.getEmail());


        return response;
    }

    public LoginResponse login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        
        if (userOptional.isEmpty()) {
            return new LoginResponse(false, "User not found", null, null);
        }
        
        User user = userOptional.get();
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new LoginResponse(false, "Invalid password", null, null);
        }
        
        String token = jwtUtil.generateToken(user.getEmail());
        return new LoginResponse(true, "Login successful", token, user.getEmail());
    }
}
