package com.example.SalesForecast.domain.product.repository;

import com.example.SalesForecast.domain.product.entity.Product;
import com.example.SalesForecast.domain.product.entity.ProductPriceHistory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductPriceHistoryRepository extends JpaRepository<ProductPriceHistory, Integer> {
    Optional<ProductPriceHistory> findByProductAndEffectiveToIsNull(Product product);

    @Query("""
              SELECT h
                FROM ProductPriceHistory h
               WHERE h.product.id IN :productIds
                 AND h.effectiveFrom <= :maxDate
                 AND (h.effectiveTo IS NULL OR h.effectiveTo >= :minDate)
            """)
    List<ProductPriceHistory> findByProductIdsAndDateRange(
            @Param("productIds") List<Integer> productIds,
            @Param("minDate") LocalDate minDate,
            @Param("maxDate") LocalDate maxDate);
}
