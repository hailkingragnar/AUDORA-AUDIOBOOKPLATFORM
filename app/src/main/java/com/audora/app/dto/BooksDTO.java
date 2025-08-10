package com.audora.app.dto;

import com.audora.app.model.Chapter;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BooksDTO {

    private String title;
    private String author;
    private String narrator;
    private String duration;
    private String language;
    private String genre;
    private String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;
    private String isbn;
    private String fileFormat;
    private Double price;
    private String availability;
    private MultipartFile image;
    private List<String> categoryNames;
    private List<ChaptersDTO> chapters;
}
