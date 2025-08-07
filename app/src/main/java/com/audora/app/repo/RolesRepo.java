package com.audora.app.repo;


import com.audora.app.enumpackage.RoleType;
import com.audora.app.model.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepo extends JpaRepository<UserRoles,String> {

     Optional<UserRoles> findByRoles(RoleType role);


}
