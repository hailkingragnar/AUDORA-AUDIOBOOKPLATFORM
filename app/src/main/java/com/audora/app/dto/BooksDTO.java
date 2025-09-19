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
    private String audiobookId;
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

    @Override
    public String toString() {
        return "BooksDTO{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", narrator='" + narrator + '\'' +
                ", duration='" + duration + '\'' +
                ", language='" + language + '\'' +
                ", genre='" + genre + '\'' +
                ", description='" + description + '\'' +
                ", releaseDate=" + releaseDate +
                ", isbn='" + isbn + '\'' +
                ", fileFormat='" + fileFormat + '\'' +
                ", price=" + price +
                ", availability='" + availability + '\'' +
                ", image=" + image +
                ", categoryNames=" + categoryNames +
                '}';
    }

    public BooksDTO(String audiobookId,String title, String author, String narrator, String duration, String language, String genre, String description, Date releaseDate, String isbn, String fileFormat, Double price, String availability, List<String> categoryNames, String imageUrl,List<ChaptersDTO> chapters) {
       this.audiobookId=audiobookId;
        this.title = title;
        this.author = author;
        this.narrator = narrator;
        this.duration = duration;
        this.language = language;
        this.genre = genre;
        this.description = description;
        this.releaseDate = releaseDate;
        this.isbn = isbn;
        this.fileFormat = fileFormat;
        this.price = price;
        this.availability = availability;
        this.categoryNames = categoryNames;
        this.chapters = chapters;
        this.imageUrl = imageUrl;

    }

    private MultipartFile image;
    private List<String> categoryNames;
    private List<ChaptersDTO> chapters;
    private String imageUrl;
    public List<String> audioUrl;
}
