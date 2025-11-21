package com.example.Resumeproject.controllers;

import com.example.Resumeproject.dto.SkillExtractionResponse;
import com.example.Resumeproject.models.Resume;
import com.example.Resumeproject.models.Skill;
import com.example.Resumeproject.response.SuccessResponse;
import com.example.Resumeproject.services.ResumeService;
import com.example.Resumeproject.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/upload")
    public SuccessResponse<Object> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String token) {
        try {
            String cleanToken = token.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(cleanToken);
            Long userId = jwtUtil.extractUserId(cleanToken);

            if (userId == null) {
                return new SuccessResponse<>(HttpStatus.UNAUTHORIZED, "Invalid token: userId not found. Please login again.", null);
            }

            SkillExtractionResponse response = resumeService.uploadAndExtractSkills(file, userId, username);

            if (response.isSuccess()) {
                Map<String, Object> data = new HashMap<>();
                data.put("resumeId", response.getResumeId());
                data.put("userId", response.getUserId());
                data.put("username", response.getUsername());
                data.put("extractedSkills", response.getExtractedSkills());
                return new SuccessResponse<>(HttpStatus.CREATED, response.getMessage(), data);
            } else {
                return new SuccessResponse<>(HttpStatus.BAD_REQUEST, response.getMessage(), null);
            }
        } catch (Exception e) {
            return new SuccessResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "Error: " + e.getMessage(), null);
        }
    }

    @GetMapping("/user-resumes")
    public ResponseEntity<?> getUserResumes(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
            List<Resume> resumes = resumeService.getUserResumes(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("resumes", resumes);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{resumeId}")
    public ResponseEntity<?> getResume(
            @PathVariable Long resumeId,
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
            Resume resume = resumeService.getResumeById(resumeId, userId);

            if (resume != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("resume", resume);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Resume not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{resumeId}/skills")
    public ResponseEntity<?> getResumeSkills(
            @PathVariable Long resumeId,
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
            Resume resume = resumeService.getResumeById(resumeId, userId);

            if (resume != null) {
                List<Skill> skills = resumeService.getResumeSkills(resumeId);
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("skills", skills);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Resume not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/user-skills/all")
    public ResponseEntity<?> getUserSkills(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
            List<Skill> skills = resumeService.getUserSkills(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("skills", skills);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{resumeId}")
    public ResponseEntity<?> deleteResume(
            @PathVariable Long resumeId,
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
            boolean deleted = resumeService.deleteResume(resumeId, userId);

            Map<String, Object> response = new HashMap<>();
            if (deleted) {
                response.put("success", true);
                response.put("message", "Resume deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Resume not found or unauthorized");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
