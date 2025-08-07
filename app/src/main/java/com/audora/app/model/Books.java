package com.audora.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    private Date releaseDate;
    private String isbn;
    private String fileFormat;
    private Double price;
    private String availability;
    private String image;


    @ManyToMany
    @JoinTable(
            name = "book_categories",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @ToString.Exclude  // To prevent circular reference in toString()
    private List<Categories> categoriesList;

}
