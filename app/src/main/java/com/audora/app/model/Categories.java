package com.audora.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Categories {
    @Id
    public String id = UUID.randomUUID().toString();
    public String categoryName;

    public Categories(String id, String categoryName){
        this.id=id;
        this.categoryName = categoryName;
    }

    @ManyToMany(mappedBy = "categoriesList", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @ToString.Exclude  // To prevent circular reference in toString()
    public List<Books> books;
}
