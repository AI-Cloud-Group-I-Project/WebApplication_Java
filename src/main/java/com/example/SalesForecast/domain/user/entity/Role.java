package com.example.SalesForecast.domain.user.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    public Role() {
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
