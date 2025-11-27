package com.audora.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reviews {
    @Id
    private String id = UUID.randomUUID().toString();

    public Reviews(String id, Integer rating, String comment, LocalDate createdAt, LocalDate updatedDate) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
        this.updatedDate = updatedDate;
    }

    @Column(nullable = false)
    private float rating;

    @Column(columnDefinition = "TEXT") // For longer text content
    private String comment;

    @CreationTimestamp
    private LocalDate createdAt;

    @UpdateTimestamp
    private  LocalDate updatedDate;

    @ManyToOne(fetch = FetchType.LAZY , optional = false)
    @JoinColumn(name = "userId")
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY , optional = false)
    @JoinColumn(name = "bookId" )
    private Books books;

}


