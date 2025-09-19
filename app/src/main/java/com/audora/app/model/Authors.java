package com.audora.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Authors {
    @Id
    private String id = UUID.randomUUID().toString();
    private String name;
    private String bio;
    private String image;
    private String penName;
    private String email;

    @OneToMany(mappedBy = "author")
    private List<Books> booksList;
}
