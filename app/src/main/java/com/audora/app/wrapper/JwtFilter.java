
package com.audora.app.wrapper;

import com.audora.app.service.JWTService;
import com.audora.app.service.MyUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ApplicationContext applicationContext;

    //
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
//        String authHeader = request.getHeader("Authorization");
//        String userName = null;
//        String token = null;
//
//        if(authHeader != null && authHeader.startsWith("Bearer ")){
//            token = authHeader.substring(7);
//            userName = jwtService.extractUsername(token);
//        }
//
//        if(userName!=null && SecurityContextHolder.getContext().getAuthentication() == null){
//           UserDetails userDetails = applicationContext.getBean(MyUserDetailService.class).loadUserByUsername(userName);
//
//            if(jwtService.isTokenValid(token,userDetails)){
//                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
//                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
//                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//            }
//        }
//        filterChain.doFilter(request,response);
//    }
//}
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = null;
        String userName = null;

        // First check Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            userName = jwtService.extractUsername(token);
        }

        // If no header found, check the cookies
        if (token == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("jwtToken")) {
                    token = cookie.getValue();
                    userName = jwtService.extractUsername(token);
                    break;
                }
            }
        }

        // Validate and set authentication
        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = applicationContext
                    .getBean(MyUserDetailService.class)
                    .loadUserByUsername(userName);

            if (jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}

