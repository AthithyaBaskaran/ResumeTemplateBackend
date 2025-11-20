package com.example.Resumeproject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Resumeproject.models.User;

@Repository
public interface UserRespository extends JpaRepository<User, Long> {
    
}
