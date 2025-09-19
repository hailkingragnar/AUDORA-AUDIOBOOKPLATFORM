package com.audora.app.dto;

import com.audora.app.model.Books;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChaptersDTO {
    private String id;
    private String title;
    private String duration;
    private MultipartFile audioFile;
    private Books books;
    private String audioFileUrl;


}
