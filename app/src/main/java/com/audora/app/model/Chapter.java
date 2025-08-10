package com.audora.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Chapter {
    @Id
    private String id = UUID.randomUUID().toString();
    private String title;
    private String duration; // in seconds
    private String audioFileKey; // S3 object key

    @ManyToOne
    @JoinColumn(name = "audiobookId")
    private Books books;
}
