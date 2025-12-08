package com.audora.app.service;

import com.audora.app.dto.AuthorDTO;
import com.audora.app.model.Authors;
import com.audora.app.repo.AuthorsRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorsRepo authorsRepo;

    @Autowired
    public AuthorService(AuthorsRepo authorsRepo) {
        this.authorsRepo = authorsRepo;
    }

    @Transactional
    public boolean addAuthors(AuthorDTO authorDTO, String fileName) {
        // Validate inputs
        if (authorDTO == null || fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("AuthorDTO or filename cannot be null/empty");
        }

        try {
            Authors author = new Authors();
            author.setBio(authorDTO.getBio());
            author.setName(authorDTO.getName());
            author.setPenName(authorDTO.getPenName());
            author.setImage(fileName);
            author.setEmail(authorDTO.getEmail());

            authorsRepo.save(author);
            return true;
        } catch (DataIntegrityViolationException e) {
            // Handle database constraints violations
            throw new RuntimeException("Failed to save author due to data constraints: " + e.getMessage());
        } catch (Exception e) {
            // Log the error for debugging
            throw new RuntimeException("Failed to save author: " + e.getMessage());
        }
    }

    public List<Authors> getAuthors() {
        return authorsRepo.findAll();
    }

    public Authors getAuthorsById(String id) {
        return authorsRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with ID: " + id));
    }
}
