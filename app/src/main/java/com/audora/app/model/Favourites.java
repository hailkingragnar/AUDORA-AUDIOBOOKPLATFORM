package com.audora.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Favourites {

        @Id
        private String id = UUID.randomUUID().toString();

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        private Users user;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "book_id", nullable = false)
        private Books books;

        private LocalDateTime localDateTime = LocalDateTime.now();

    }
