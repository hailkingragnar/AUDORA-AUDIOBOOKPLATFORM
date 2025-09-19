package com.audora.app.service;


import com.audora.app.dto.UserLogin;
import com.audora.app.dto.UserRegistration;
import com.audora.app.enumpackage.RoleType;
import com.audora.app.model.UserRoles;
import com.audora.app.model.Users;
import com.audora.app.repo.RolesRepo;
import com.audora.app.repo.UsersRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsersService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UsersRepo usersRepo;
    @Autowired
    private RolesRepo rolesRepo;

    public String ImagePath(UserLogin user) {
        Users user1 = usersRepo.findByEmail(user.getUserEmail()).get();
        return user1.getProfilePicture();

    }
    @Transactional
    public boolean registerUser(UserRegistration users, String fileName) {
        try {
            // 1️⃣ Check if user already exists
            Optional<Users> existingUserOpt = usersRepo.findByEmail(users.getEmail());
            if (existingUserOpt.isPresent()) {
                // User already exists
                return false;
            }

            // 2️⃣ Create new user entity
            Users newUser = new Users();
            newUser.setEmail(users.getEmail());
            newUser.setName(users.getName());
            newUser.setPassword(passwordEncoder.encode(users.getPassword()));
            newUser.setProfilePicture(fileName);

            // 3️⃣ Assign default ROLE_USER
            UserRoles defaultRole = rolesRepo.findByRoles(RoleType.ROLE_USER)
                    .orElseGet(() -> {
                        UserRoles role = new UserRoles();
                        role.setRoles(RoleType.ROLE_USER);
                        return rolesRepo.save(role);
                    });
            newUser.getRole().add(defaultRole);




            // Save user
            usersRepo.save(newUser);
            return true;

        } catch (Exception e) {
            // Log the exception in production
            e.printStackTrace();
            return false;
        }
    }


    public Users getUser(String username ) {
        return usersRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
