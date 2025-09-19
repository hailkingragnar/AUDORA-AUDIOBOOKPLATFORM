package com.audora.app.dto;

import com.audora.app.model.Books;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorDTO {
    private String id = UUID.randomUUID().toString();
    private String name;
    private String bio;
    private MultipartFile image;
    private List<Books> booksList;
    private String penName;
    private String email;
}
