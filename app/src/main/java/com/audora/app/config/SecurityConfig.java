package com.audora.app.config;

import com.audora.app.service.MyUserDetailService;
import com.audora.app.wrapper.JwtFilter;
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
    private MyUserDetailService myUserDetailService;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(customizer->customizer.disable())
                .authorizeHttpRequests(request->request
                        .requestMatchers("/register", "/login", "/","/authenticate","/api/v1/authenticate").permitAll()
                        .anyRequest().authenticated())
                .formLogin(formLogin ->
                        formLogin.loginPage("/login")
                                .failureUrl("/login?error=true"))
                .httpBasic(Customizer.withDefaults())
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
