package com.example.SalesForecast.domain.product.service;

import com.example.SalesForecast.domain.product.entity.Product;
import com.example.SalesForecast.domain.product.entity.ProductStatus;
import com.example.SalesForecast.domain.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // すべての商品を取得
    public List<Product> getAllProducts() {
        return productRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    // 商品情報を更新（Edit/Submit用）
    public void updateProduct(Product updatedProduct) {
        if (updatedProduct.getStatus() == null) {
            ProductStatus defaultStatus = new ProductStatus();
            defaultStatus.setId(1);  // 'available' のIDを仮で設定
            updatedProduct.setStatus(defaultStatus);
        }

    productRepository.save(updatedProduct);
}


    public void addProduct(Product product) {
    productRepository.save(product);
    }

    public void deleteProductById(Integer id) {
    productRepository.deleteById(id);
}

    public List<Product> getAvailableProducts() {
        return productRepository.findByStatusId(1); // status_id = 1（販売中）
}


}



