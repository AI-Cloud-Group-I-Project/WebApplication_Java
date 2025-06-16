package com.example.SalesForecast.domain.user.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    // 多対一,外部キー
    // 同一パッケージ内のRoleはimport不要
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String email;

    public User() {
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }

    public String getEmail() {
        return email;
    }
}
