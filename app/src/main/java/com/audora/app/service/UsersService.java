package com.audora.app.service;


import com.audora.app.dto.UserLogin;
import com.audora.app.dto.UserRegistration;
import com.audora.app.model.Users;
import com.audora.app.repo.RolesRepo;
import com.audora.app.repo.UsersRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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
           Users users1 = new Users();
           users1.setEmail(users.getEmail());
           users1.setName(users.getName());
           users1.setPassword(passwordEncoder.encode(users.getPassword()));
           users1.setProfilePicture(fileName);


//           Set<UserRoles> userRolesSet = new HashSet<>();
//           for(String roles : users.getRoles()){

//               RoleType roleEnum;
//               try {
//                   roleEnum = RoleType.valueOf(roles);  // Converts "USER" -> RoleType.USER
//               } catch (IllegalArgumentException e) {
//                   System.out.println("Invalid role: " + roles);
//                   continue; // skip invalid role
//               }

//               Optional<UserRoles> userRoles = rolesRepo.findByRoles(roleEnum);
//               UserRoles userRole = userRoles.orElseGet(() -> {
//                   UserRoles userRoles1 = new UserRoles();
//                   userRoles1.setRoles(roleEnum);
//                 return  rolesRepo.save(userRoles1);
//                   }); // save if not present
//                   userRolesSet.add(userRole);
//                   System.out.println("User Roles are :" + userRoles);
//           }

//           users1.setRole(userRolesSet);
           usersRepo.save(users1);
           return true;
       } catch(Exception e){
           return false;
       }

    }


    public Users getUser(String username ) {
        Users user1 = usersRepo.findByEmail(username).get();
        return user1;
    }
}
