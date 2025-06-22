package com.example.SalesForecast.domain.inventory.service;

import org.springframework.stereotype.Service;

import com.example.SalesForecast.domain.inventory.entity.ProductReceipt;
import com.example.SalesForecast.domain.inventory.repository.ProductReceiptRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProductReceiptService {

    private final ProductReceiptRepository productReceiptRepository;

    public ProductReceiptService(ProductReceiptRepository productReceiptRepository) {
        this.productReceiptRepository = productReceiptRepository;
    }

    public List<ProductReceipt> findByReceivedDate(LocalDate receivedDate) {
        return productReceiptRepository.findByReceivedDateOrderByEditedDateDesc(receivedDate);
    }
}
