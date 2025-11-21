package com.example.Resumeproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillExtractionResponse {
    private boolean success;
    private String message;
    private Long resumeId;
    private Long userId;
    private String username;
    private List<String> extractedSkills;
}
