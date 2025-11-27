package com.audora.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Books {

    @Id
    private String audiobookId = UUID.randomUUID().toString();
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
    private String image;

    private Long viewCount = 0L;

    @OneToMany(mappedBy = "books", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reviews> reviews;

    @OneToMany(mappedBy = "books", cascade = CascadeType.ALL , orphanRemoval = true)
    private List<Chapter> chapters;

    @Override
    public String toString() {
        return "Books{" +
                "audiobookId='" + audiobookId + '\'' +
                ", title='" + title + '\'' +
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
                ", image='" + image + '\'' +
                ", categoriesList=" + categoriesList +
                '}';
    }

    @ManyToMany
    @JoinTable(
            name = "book_categories",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @ToString.Exclude  // To prevent circular reference in toString()
    private List<Categories> categoriesList;

    @ManyToOne
    @JoinColumn(name = "author_Id")
    private Authors authors;

}
