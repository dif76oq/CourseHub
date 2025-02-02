package com.dif76oq.AuthMicroservice.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name="verification_token")
public class VerificationToken {
    @Id
    @Column(name="token")
    private String token;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "expiration_date")
    private Date expirationDate;

    public VerificationToken(String token, User user, Date expirationDate) {
        this.token = token;
        this.user = user;
        this.expirationDate = expirationDate;
    }




    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public VerificationToken() {}
}
