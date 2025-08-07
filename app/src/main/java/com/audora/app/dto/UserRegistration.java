package com.audora.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistration {
    private String name;
    private String email;
    private String password;
    private MultipartFile profilePicture;
    private String subscription;
    private Date membershipStartDate;
    private Date membershipEndDate;

}
