package com.example.Resumeproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private boolean success;
    private String message;
    private Long userId;
    private String username;
    private String email;
    private String token;
    private String role;
}
