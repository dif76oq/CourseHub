package com.dif76oq.AuthMicroservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class RegistrationDTO {

    @NotEmpty(message="Username field should not be empty!")
    @Size(min=3, max=24, message="Username should contain from 3 to 24 letters!")
    private String username;

    @NotEmpty(message = "Email can not be empty!")
    @Email(message = "Email should be in format username@example.com")
    private String email;


    @NotEmpty(message="Password should not be empty!")
    private String password;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public RegistrationDTO() {

    }

    public RegistrationDTO(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

}
