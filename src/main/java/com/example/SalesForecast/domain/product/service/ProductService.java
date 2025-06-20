package com.example.SalesForecast.domain.product.service;


import com.example.SalesForecast.domain.inventory.service.InventoryService;
import com.example.SalesForecast.domain.product.entity.Product;
import com.example.SalesForecast.domain.product.entity.ProductStatus;
import com.example.SalesForecast.domain.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;


import java.util.List;
import java.util.NoSuchElementException;


@Service
public class ProductService {


    @Autowired
    private ProductRepository productRepository;


    @Autowired
    private InventoryService inventoryService;


    // すべての商品を取得
    public List<Product> getAllProducts() {
        return productRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }


    // 商品情報を更新（Edit/Submit用）
    public void updateProduct(Product updatedProduct) {
        if (updatedProduct.getStatus() == null) {
            ProductStatus defaultStatus = new ProductStatus();
            defaultStatus.setId(1); // 'available' のIDを仮で設定
            updatedProduct.setStatus(defaultStatus);
        }
        productRepository.save(updatedProduct);
    }


    // 商品追加時に在庫も1で初期化
    public void addProduct(Product product) {
        // 商品を保存してIDを取得
        Product savedProduct = productRepository.save(product);


        // 在庫を初期化（1個で）
        inventoryService.addInitialInventory(savedProduct);
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

}
