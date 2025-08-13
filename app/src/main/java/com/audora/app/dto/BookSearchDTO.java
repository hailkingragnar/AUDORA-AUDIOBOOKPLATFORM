package com.audora.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookSearchDTO {
    private String audiobookId;
    private String title;
    private String author;
    private String narrator;
    private String duration;
    private String language;
    private String genre;
    private String description;

    public BookSearchDTO(String audiobookId, String title, String author, String image) {
        this.audiobookId = audiobookId;
        this.title = title;
        this.author = author;
        this.image = image;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;
    private String isbn;
    private String fileFormat;
    private Double price;
    private String availability;
    private String image;


}
