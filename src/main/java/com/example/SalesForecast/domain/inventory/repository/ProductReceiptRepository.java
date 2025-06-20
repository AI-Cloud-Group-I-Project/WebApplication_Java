package com.example.SalesForecast.domain.inventory.repository;

import com.example.SalesForecast.domain.inventory.entity.ProductReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductReceiptRepository extends JpaRepository<ProductReceipt, Integer> {

    boolean existsByProductIdAndReceivedDate(Integer productId, LocalDate receivedDate);
    
    Optional<ProductReceipt> findByProductIdAndReceivedDate(Integer productId, LocalDate receivedDate);

    List<ProductReceipt> findByReceivedDate(LocalDate receivedDate);
}
