package com.example.SalesForecast.domain.product.service;

import com.example.SalesForecast.domain.inventory.service.InventoryService;
import com.example.SalesForecast.domain.product.entity.Product;
import com.example.SalesForecast.domain.product.entity.ProductPriceHistory;
import com.example.SalesForecast.domain.product.entity.ProductStatus;
import com.example.SalesForecast.domain.product.repository.ProductPriceHistoryRepository;
import com.example.SalesForecast.domain.product.repository.ProductRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final InventoryService inventoryService;
    private final ProductPriceHistoryRepository historyRepository;

    public ProductService(ProductRepository productRepository,
            InventoryService inventoryService,
            ProductPriceHistoryRepository historyRepository) {
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
        this.historyRepository = historyRepository;
    }

    // すべての商品を取得
    public List<Product> getAllProducts() {
        return productRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    // 商品情報を更新（Edit/Submit用）
    @Transactional
    public void updateProduct(Product updatedProduct) {
        // status デフォルト設定
        if (updatedProduct.getStatus() == null) {
            ProductStatus defaultStatus = new ProductStatus();
            defaultStatus.setId(1);
            updatedProduct.setStatus(defaultStatus);
        }

        // 価格変更チェック
        Product existing = productRepository.findById(updatedProduct.getId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        Double oldPriceDouble = existing.getPrice();
        BigDecimal oldPriceDecimal = BigDecimal.valueOf(oldPriceDouble);
        Double newPriceDouble = updatedProduct.getPrice();
        BigDecimal newPriceDecimal = BigDecimal.valueOf(newPriceDouble);

        if (newPriceDecimal != null && oldPriceDecimal != null && newPriceDecimal.compareTo(oldPriceDecimal) != 0) {
            LocalDate today = LocalDate.now();

            // 旧履歴の終了日をセット
            historyRepository.findByProductAndEffectiveToIsNull(existing)
                    .ifPresent(hist -> {
                        hist.setEffectiveTo(today);
                        historyRepository.save(hist);
                    });

            // 新しい履歴を INSERT
            ProductPriceHistory newHist = new ProductPriceHistory();
            newHist.setProduct(existing);
            newHist.setPrice(newPriceDecimal);
            newHist.setEffectiveFrom(today);
            newHist.setEffectiveTo(null);
            newHist.setChangedAt(LocalDateTime.now());
            historyRepository.save(newHist);
        }

        // 商品本体を保存
        productRepository.save(updatedProduct);
    }

    @Transactional
    public void addProduct(Product product) {
        Product saved = productRepository.save(product);

        inventoryService.addInitialInventory(saved);

        Double priceDouble = saved.getPrice();
        BigDecimal priceDecimal = BigDecimal.valueOf(priceDouble);

        ProductPriceHistory hist = new ProductPriceHistory();
        hist.setProduct(saved);
        hist.setPrice(priceDecimal);
        hist.setEffectiveFrom(LocalDate.now()); // today as from
        hist.setEffectiveTo(null); // 現在有効中
        hist.setChangedAt(LocalDateTime.now());
        historyRepository.save(hist);
    }

    // 商品削除
    public void deleteProductById(Integer id) {
        productRepository.deleteById(id);
    }

    // 状態ごとの商品取得
    public List<Product> getProductsByStatusId(int statusId) {
        return productRepository.findAllByStatus_Id(statusId);
    }

    public Product getById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found: " + id));
    }

    public List<Product> getAvailableProducts() {
        return productRepository.findByStatusId(1); // status_id = 1（販売中）
    }

    public Page<Product> getProductsByPage(Pageable pageable) {
        return productRepository.findAllByOrderByIdDesc(pageable);
    }

}
