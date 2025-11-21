package com.example.Resumeproject.services;

import com.example.Resumeproject.dto.LoginRequest;
import com.example.Resumeproject.dto.RegisterRequest;
import com.example.Resumeproject.models.User;
import com.example.Resumeproject.repositories.UserRespository;
import com.example.Resumeproject.response.SuccessResponse;
import com.example.Resumeproject.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;

@Service
public class AuthService {

    @Autowired
    private UserRespository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;


    public SuccessResponse<Object> register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return new SuccessResponse<>(HttpStatus.BAD_REQUEST, "Username already exists", null);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return new SuccessResponse<>(HttpStatus.BAD_REQUEST, "Email already exists", null);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNo(request.getPhoneNo());
        user.setRole(request.getRole() != null ? request.getRole() : "USER");
        user.setCreatedAt(System.currentTimeMillis());

        User savedUser = userRepository.save(user);
        
        Map<String, Object> data = new HashMap<>();
        data.put("userId", savedUser.getId());
        data.put("username", savedUser.getUsername());
        data.put("email", savedUser.getEmail());
        
        return new SuccessResponse<>(HttpStatus.CREATED, "User registered successfully", data);
    }

    public SuccessResponse<Object> login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        
        if (userOptional.isEmpty()) {
            return new SuccessResponse<>(HttpStatus.UNAUTHORIZED, "User not found", null);
        }
        
        User user = userOptional.get();
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new SuccessResponse<>(HttpStatus.UNAUTHORIZED, "Invalid password", null);
        }
        
        String token = jwtUtil.generateTokenWithUserId(user.getEmail(), user.getId());
        
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("email", user.getEmail());
        
        return new SuccessResponse<>(HttpStatus.OK, "Login successful", data);
    }

    public SuccessResponse<Object> getAllUsers(){
        List<User> users = userRepository.findAll();
        return new SuccessResponse<>(HttpStatus.OK, "Users retrieved successfully", users);
    }
    
}
