package com.example.Login_Spring_Boot.model;

import jakarta.persistence.*;

@Entity
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username")
    private String userName;

    @Column(name = "password_hash")
    private String passwordHash;

    public Users() {
    }

    public Users(String userName, String passwordHash) {
        this.userName = userName;
        this.passwordHash = passwordHash;
    }

    public Integer getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

}