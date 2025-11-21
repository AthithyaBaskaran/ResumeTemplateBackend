package com.example.Resumeproject.repositories;

import com.example.Resumeproject.models.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findByResumeId(Long resumeId);
    List<Skill> findByUserId(Long userId);
    List<Skill> findByUsername(String username);
}
