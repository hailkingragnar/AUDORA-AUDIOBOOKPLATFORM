package com.audora.app.model;

import com.audora.app.enumpackage.RoleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserRoles {
    @Id
    @Column(name = "role_id")
    private String roleId = UUID.randomUUID().toString();
    @Enumerated(EnumType.STRING)
    private RoleType roles;
    @ManyToMany(mappedBy = "role")
    private List<Users> usersList;
}
