package com.audora.app.service;


import com.audora.app.model.Users;
import com.audora.app.repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UsersRepo usersRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> user = usersRepo.findByEmail(username);
        if(user.isPresent()){
            return new org.springframework.security.core.userdetails.User(
                    user.get().getEmail(),
                    user.get().getPassword(),
                    user.get().getRole().stream()
                            .map(role -> new SimpleGrantedAuthority(role.getRoles().name()))
                            .collect(Collectors.toSet())
            );
        } else {
              throw new UsernameNotFoundException("User not found");
        }
    }
}
