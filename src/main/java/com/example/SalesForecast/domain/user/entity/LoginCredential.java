package com.example.SalesForecast.domain.user.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "login_credentials")
public class LoginCredential {

    @Id
    private Integer userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    // jasonignoreでjson出力で出力できないようにする
    @JsonIgnore
    @Column(nullable = false, name = "password_hash")
    private String passwordHash;

    @Column(nullable = false, name = "created_date")
    private LocalDate createdDate;

    @Column(nullable = false, name = "edited_date")
    private LocalDate editedDate;

    public LoginCredential() {
    }

    public Integer getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public LocalDate getEditedDate() {
        return editedDate;
    }

}
