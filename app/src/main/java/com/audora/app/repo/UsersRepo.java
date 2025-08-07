package com.audora.app.repo;

import com.audora.app.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepo extends JpaRepository<Users,String> {

    Optional<Users> findByEmail(String s);
}
