package com.audora.app.config;

import com.audora.app.enumpackage.RoleType;
import com.audora.app.model.UserRoles;
import com.audora.app.model.Users;
import com.audora.app.repo.RolesRepo;
import com.audora.app.repo.UsersRepo;
import com.audora.app.service.JWTService;
import com.audora.app.wrapper.JwtFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private RolesRepo rolesRepo;

    private final UsersRepo usersRepo;
    private OAuth2SuccessHandler(UsersRepo usersRepo){
        this.usersRepo=usersRepo;
    }
    @Autowired
    private JWTService jwtService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name =  oAuth2User.getAttribute("name");

        Users user = usersRepo.findByEmail(email)
                .orElseGet(() -> {
                    Users newUser = new Users();
                    newUser.setName(name);
                    newUser.setEmail(email);

                    // Assign USER role
                    UserRoles userRole = rolesRepo.findByRoles(RoleType.ROLE_USER)
                            .orElseGet(() -> {
                                UserRoles role = new UserRoles();
                                role.setRoles(RoleType.ROLE_USER);
                                return rolesRepo.save(role);
                            });
                    newUser.getRole().add(userRole);

                    // Assign ADMIN role for specific email
                    if (email != null && email.equalsIgnoreCase("dhunganahemant07@gmail.com")) {
                        UserRoles adminRole = rolesRepo.findByRoles(RoleType.ROLE_ADMIN)
                                .orElseGet(() -> {
                                    UserRoles role = new UserRoles();
                                    role.setRoles(RoleType.ROLE_ADMIN);
                                    return rolesRepo.save(role);
                                });

                        newUser.getRole().add(adminRole);
                        // Create new authentication with your application roles
                    }

                    return usersRepo.save(newUser);
                });


        // Convert your application roles to GrantedAuthority
        Set<GrantedAuthority> updatedAuthorities = user.getRole().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoles().name()))
                .collect(Collectors.toSet());

        // Create updated authentication with new authorities
        OAuth2AuthenticationToken updatedAuth = new OAuth2AuthenticationToken(
                oAuth2User,
                updatedAuthorities,
                ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId()
        );

        // Set the updated authentication in security context

        SecurityContextHolder.getContext().setAuthentication(updatedAuth);// This was missing!


        List<String> roleList = user.getRole()
                .stream()
                .map(r -> r.getRoles().name())
                .toList();

        String jwtToken = jwtService.generateToken(email,roleList);

        // 3️⃣ Clear any old JWT cookie
        Cookie clearCookie = new Cookie("jwtToken", null);
        clearCookie.setHttpOnly(true);
        clearCookie.setSecure(true); // if using HTTPS
        clearCookie.setPath("/");
        clearCookie.setMaxAge(0); // expires immediately
        response.addCookie(clearCookie);

        if (jwtToken != null) {
            Cookie cookie = new Cookie("jwtToken", jwtToken);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 30);
            response.addCookie(cookie);

        }  if (roleList.contains("ROLE_ADMIN")) {
            response.sendRedirect("/admin/home");
        } else {
            response.sendRedirect("/user/home");

        }

    }
}
