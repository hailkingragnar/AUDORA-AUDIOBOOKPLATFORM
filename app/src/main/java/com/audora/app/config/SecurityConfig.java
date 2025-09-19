package com.audora.app.config;

import com.audora.app.service.MyUserDetailService;
import com.audora.app.wrapper.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private MyUserDetailService myUserDetailService;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(customizer -> customizer.disable())
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/register", "/login", "/", "/authenticate", "/api/v1/authenticate","/logout", "/login?logout","/css/**", "/js/**", "/Images/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Admin-only access
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN") // User access
                        .anyRequest().authenticated())
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        }));

        // For OAuth2

        http.oauth2Login(oauth2Login -> {
            oauth2Login.loginPage("/login");
            oauth2Login.successHandler(oAuth2SuccessHandler);
        });

             http   //For form Login
                .formLogin(formLogin ->
                        formLogin.loginPage("/login")
                                .failureUrl("/login?error=true"))


                     .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
        )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);




        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){

        return new BCryptPasswordEncoder();
    }

//    @Bean
//    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
//
//        authenticationManagerBuilder.userDetailsService(myUserDetailService).passwordEncoder(passwordEncoder());
//    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(myUserDetailService); // ✅ still okay to use even if marked deprecated
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean

    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }



}
