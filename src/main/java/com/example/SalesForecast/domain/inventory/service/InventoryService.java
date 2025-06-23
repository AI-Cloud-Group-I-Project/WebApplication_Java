package com.example.SalesForecast.domain.inventory.service;

import com.example.SalesForecast.domain.inventory.entity.Inventory;
import com.example.SalesForecast.domain.inventory.entity.ProductReceipt;
import com.example.SalesForecast.domain.inventory.repository.InventoryRepository;
import com.example.SalesForecast.domain.inventory.repository.ProductReceiptRepository;
import com.example.SalesForecast.domain.product.entity.Product;
import com.example.SalesForecast.domain.product.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductReceiptRepository productReceiptRepository;

    @Autowired
    private ProductRepository productRepository;

    // status_id = 1（販売中）の商品だけにフィルタ
    public List<Inventory> getAllInventories() {
        return inventoryRepository.findByProduct_Status_IdOrderByProductIdDesc(1);
    }

    public void processStockEntry(String janCode, int quantity) {
        // JANコードから商品を取得
        Product product = productRepository.findByJanCode(janCode);
        if (product == null) {
            throw new IllegalArgumentException("JANコードに該当する商品が見つかりません: " + janCode);
        }

        Integer productId = product.getId();

        if (productReceiptRepository.existsByProductIdAndReceivedDate(productId, LocalDate.now())) {
            throw new IllegalStateException("この商品の入荷情報は本日すでに登録されています。");
        }

        // 1. 入荷履歴に追加
        ProductReceipt receipt = new ProductReceipt();
        receipt.setProduct(product);
        receipt.setQuantity(quantity);
        receipt.setReceivedDate(LocalDate.now());
        receipt.setEditedDate(LocalDateTime.now());
        productReceiptRepository.save(receipt);

        // 2. 在庫更新
        Inventory inventory = inventoryRepository.findByProductId(productId);
        if (inventory == null) {
            // 在庫がまだ存在しない場合
            inventory = new Inventory(productId, quantity, LocalDateTime.now());
        } else {
            inventory.setStockQuantity(inventory.getStockQuantity() + quantity);
            inventory.setUpdatedDate(LocalDateTime.now());
        }
        inventoryRepository.save(inventory);
    }

    public void updateTodayInventory(Integer productId, Integer newQuantity) {
        // 今日の日付を取得
        LocalDate today = LocalDate.now();

        // 今日の入荷データ（ProductReceipt）を取得
        Optional<ProductReceipt> optionalReceipt = productReceiptRepository
                .findByProductIdAndReceivedDate(productId, today);

        if (optionalReceipt.isPresent()) {
            ProductReceipt receipt = optionalReceipt.get();

            // 在庫の増減を反映させるために、変更前の数量を保持
            int oldQuantity = receipt.getQuantity();
            int delta = newQuantity - oldQuantity;

            // 入荷データを更新
            receipt.setQuantity(newQuantity);
            receipt.setEditedDate(LocalDateTime.now());
            productReceiptRepository.save(receipt);

            // 在庫データを更新
            Inventory inventory = inventoryRepository.findByProductId(productId);
            inventory.setStockQuantity(inventory.getStockQuantity() + delta);
            inventory.setUpdatedDate(LocalDateTime.now());
            inventoryRepository.save(inventory);
        }
    }

    public void deductStock(Integer productId, Integer qty) {
        Inventory inv = inventoryRepository.findByProductId(productId);
        if (inv.getStockQuantity() < qty) {
            throw new IllegalStateException("在庫不足です: productId=" + productId);
        }
        inv.setStockQuantity(inv.getStockQuantity() - qty);
        inv.setUpdatedDate(LocalDateTime.now());
        inventoryRepository.save(inv);
    }

    public void addStock(Integer productId, Integer qty) {
        Inventory inv = inventoryRepository.findByProductId(productId);
        if (inv == null) {
            // もともと在庫レコードがなければ新規作成
            inv = new Inventory(productId, qty, LocalDateTime.now());
        } else {
            inv.setStockQuantity(inv.getStockQuantity() + qty);
            inv.setUpdatedDate(LocalDateTime.now());
        }
        inventoryRepository.save(inv);
    }

    // 商品登録時に初期在庫0件を自動追加（履歴は残す）
    public void addInitialInventory(Product product) {

        // 1. 入荷履歴（ProductReceipt）を 0 個で登録
        ProductReceipt receipt = new ProductReceipt();
        receipt.setProduct(product);
        receipt.setQuantity(0); // ← 数量を 0 に変更
        receipt.setReceivedDate(LocalDate.now());
        receipt.setEditedDate(LocalDateTime.now());
        productReceiptRepository.save(receipt);

        // 2. Inventory（在庫）も 0 個で初期化
        Inventory inventory = inventoryRepository.findByProductId(product.getId());
        if (inventory == null) {
            inventory = new Inventory(product.getId(), 0, LocalDateTime.now()); // ← 0 に変更
        } else {
            inventory.setStockQuantity(0); // ← ここも 0
            inventory.setUpdatedDate(LocalDateTime.now());
        }
        inventoryRepository.save(inventory);
    }

    /** ProductId で在庫エンティティを取得 */
    public Inventory getInventoryByProductId(Integer productId) {
        return inventoryRepository.findByProductId(productId);
    }
}
