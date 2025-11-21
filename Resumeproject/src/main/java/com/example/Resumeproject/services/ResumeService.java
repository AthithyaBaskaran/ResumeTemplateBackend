package com.example.Resumeproject.services;

import com.example.Resumeproject.dto.SkillExtractionResponse;
import com.example.Resumeproject.models.Resume;
import com.example.Resumeproject.models.Skill;
import com.example.Resumeproject.repositories.ResumeRepository;
import com.example.Resumeproject.repositories.SkillRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class ResumeService {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Value("${file.upload-dir:uploads/resumes}")
    private String uploadDir;

    private static final List<String> COMMON_SKILLS = Arrays.asList(
        "java", "python", "javascript", "c++", "c#", "ruby", "go", "rust", "typescript",
        "kotlin", "swift", "php", "scala", "r", "perl", "sql", "nosql", "mongodb",
        "mysql", "postgresql", "oracle", "dynamodb", "firebase", "cassandra",
        "spring", "hibernate", "django", "flask", "express", "nodejs", "react",
        "angular", "vue", "svelte", "webpack", "gulp", "gradle", "maven", "npm",
        "git", "docker", "kubernetes", "jenkins", "travis", "circleci", "linux",
        "windows", "macos", "aws", "azure", "gcp", "heroku", "digitalocean",
        "html", "css", "bootstrap", "tailwind", "sass", "less", "rest", "graphql",
        "soap", "microservices", "redis", "elasticsearch", "kafka", "rabbitmq",
        "junit", "pytest", "jest", "selenium", "postman", "jira", "confluence",
        "agile", "scrum", "kanban", "leadership", "teamwork", "communication",
        "problem solving", "critical thinking", "time management", "creativity",
        "tensorflow", "pytorch", "keras", "scikit-learn", "pandas", "numpy",
        "machine learning", "deep learning", "ai", "nlp", "computer vision",
        "hadoop", "spark", "hive", "pig", "spark sql", "data warehousing",
        "tableau", "power bi", "looker", "data analytics", "excel", "vba",
        "selenium webdriver", "cypress", "appium", "jmeter", "loadrunner",
        "figma", "adobe xd", "sketch", "ui design", "ux design", "wireframing",
        "api design", "database design", "system design", "architecture",
        "devops", "ci/cd", "deployment", "monitoring", "logging", "metrics"
    );

    public SkillExtractionResponse uploadAndExtractSkills(MultipartFile file, Long userId, String username) throws IOException {
        SkillExtractionResponse response = new SkillExtractionResponse();

        try {
            if (file.isEmpty()) {
                response.setSuccess(false);
                response.setMessage("File is empty");
                return response;
            }

            if (!file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
                response.setSuccess(false);
                response.setMessage("Only PDF files are allowed");
                return response;
            }

            String uploadPath = createUploadDirectory();
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadPath, fileName);

            Files.write(filePath, file.getBytes());

            String extractedText = extractTextFromPdf(filePath.toString());
            List<String> extractedSkills = extractSkillsFromText(extractedText);

            Resume resume = new Resume();
            resume.setUserId(userId);
            resume.setUsername(username);
            resume.setFileName(file.getOriginalFilename());
            resume.setFilePath(filePath.toString());
            resume.setCreatedAt(System.currentTimeMillis());
            resume.setUpdatedAt(System.currentTimeMillis());

            Resume savedResume = resumeRepository.save(resume);

            List<Skill> skills = new ArrayList<>();
            for (String skillName : extractedSkills) {
                Skill skill = new Skill();
                skill.setUserId(userId);
                skill.setUsername(username);
                skill.setSkillName(skillName);
                skill.setResume(savedResume);
                skill.setCreatedAt(System.currentTimeMillis());
                skills.add(skill);
            }

            skillRepository.saveAll(skills);

            response.setSuccess(true);
            response.setMessage("Resume uploaded and skills extracted successfully");
            response.setResumeId(savedResume.getId());
            response.setUserId(userId);
            response.setUsername(username);
            response.setExtractedSkills(extractedSkills);

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error processing resume: " + e.getMessage());
        }

        return response;
    }

    private String createUploadDirectory() throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        return uploadPath.toString();
    }

    private String extractTextFromPdf(String filePath) throws IOException {
        StringBuilder text = new StringBuilder();
        PDDocument document = PDDocument.load(new File(filePath));
        try {
            PDFTextStripper stripper = new PDFTextStripper();
            text.append(stripper.getText(document));
        } finally {
            document.close();
        }
        return text.toString();
    }

    private List<String> extractSkillsFromText(String text) {
        Set<String> foundSkills = new LinkedHashSet<>();
        String lowerText = text.toLowerCase();

        for (String skill : COMMON_SKILLS) {
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(skill) + "\\b");
            if (pattern.matcher(lowerText).find()) {
                foundSkills.add(skill);
            }
        }

        return new ArrayList<>(foundSkills);
    }

    public List<Resume> getUserResumes(Long userId) {
        return resumeRepository.findByUserId(userId);
    }

    public Resume getResumeById(Long resumeId, Long userId) {
        return resumeRepository.findByIdAndUserId(resumeId, userId).orElse(null);
    }

    public List<Skill> getResumeSkills(Long resumeId) {
        return skillRepository.findByResumeId(resumeId);
    }

    public List<Skill> getUserSkills(Long userId) {
        return skillRepository.findByUserId(userId);
    }

    public boolean deleteResume(Long resumeId, Long userId) {
        var resume = resumeRepository.findByIdAndUserId(resumeId, userId);
        if (resume.isPresent()) {
            try {
                Files.deleteIfExists(Paths.get(resume.get().getFilePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            resumeRepository.delete(resume.get());
            return true;
        }
        return false;
    }
}
