package com.audora.app.utils;

import com.audora.app.enumpackage.RoleType;
import com.audora.app.model.Users;
import com.audora.app.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;


@Component
public class SecurityUtils {

    @Autowired
    private UsersService usersService;

    public static String getUserEmail(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof OAuth2User){
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            return oAuth2User.getAttribute("email");
        } else {
            return authentication.getName();
        }
    }

    public boolean isAdmin() {
        Users user = usersService.getUser(getUserEmail());
        return user.getRole().stream()  // Changed from getRole() to getRoles()
                .anyMatch(role -> role.getRoles().equals(RoleType.ROLE_ADMIN));  // Changed to check role name
    }

}
