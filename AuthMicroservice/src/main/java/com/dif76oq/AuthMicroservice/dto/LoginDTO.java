package com.dif76oq.AuthMicroservice.dto;

import jakarta.validation.constraints.NotEmpty;

public class LoginDTO {
    @NotEmpty(message = "Email or Username can not be empty!")
    private String identifier;

    @NotEmpty(message="Password should not be empty!")
    private String password;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
