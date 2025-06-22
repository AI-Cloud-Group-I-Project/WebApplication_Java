package com.example.SalesForecast.domain.inventory.repository;

import com.example.SalesForecast.domain.inventory.entity.Inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    Inventory findByProductId(Integer productId);

    @Query("SELECT i FROM Inventory i JOIN FETCH i.product")
    List<Inventory> findAllWithProduct();

    List<Inventory> findByProduct_Status_IdOrderByProductIdDesc(int statusId);
}
