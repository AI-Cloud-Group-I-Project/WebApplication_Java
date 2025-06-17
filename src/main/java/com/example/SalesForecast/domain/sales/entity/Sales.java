package com.example.SalesForecast.domain.sales.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import com.example.SalesForecast.domain.product.entity.Product;
import com.example.SalesForecast.domain.user.entity.User;
import com.example.SalesForecast.domain.weather.entity.Weather;

@Entity
@Table(name = "sales")
public class Sales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "weather_id", nullable = false)
    private Weather weather;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "sales_date", nullable = false)
    private LocalDate salesDate;

    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;

    @Column(name = "edited_date", nullable = false)
    private LocalDate editedDate;

    public Sales() {
    }

    public Integer getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public User getUser() {
        return user;
    }

    public Weather getWeather() {
        return weather;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public LocalDate getSalesDate() {
        return salesDate;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public LocalDate getEditedDate() {
        return editedDate;
    }
}
