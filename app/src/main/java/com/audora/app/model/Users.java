package com.audora.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Users {
    @Id
    @Column(name = "user_id")
    private String userId = UUID.randomUUID().toString();
    private String name;
    private String email;
    private String password;
    private String profilePicture;


    @Override
    public String toString() {
        return "Users{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                '}';
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles_join",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<UserRoles> role = new HashSet<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reviews> reviews;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Subscriptions> subscriptions;
}
