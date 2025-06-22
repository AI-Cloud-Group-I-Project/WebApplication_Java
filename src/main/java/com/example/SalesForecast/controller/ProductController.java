// // ビール銘柄管理ページ
// package com.example.SalesForecast.controller;

// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.PostMapping;

// import jakarta.servlet.http.HttpSession;

package com.example.SalesForecast.controller;

import com.example.SalesForecast.domain.product.entity.Product;
import com.example.SalesForecast.domain.product.entity.ProductStatus;
import com.example.SalesForecast.domain.product.repository.ProductRepository;
import com.example.SalesForecast.domain.product.repository.ProductStatusRepository;
import com.example.SalesForecast.domain.product.service.ProductService;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductStatusRepository productStatusRepository;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public String getProducts(
            Model model,
            HttpSession session,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productService.getProductsByPage(pageable);

        model.addAttribute("productList", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());

        model.addAttribute("current_username", session.getAttribute("username"));
        model.addAttribute("current_email", session.getAttribute("email"));

        return "admin-product-management";
    }

    @PostMapping("/products/update")
    public String updateProduct(@ModelAttribute Product updatedProduct) {
        try {
            productService.updateProduct(updatedProduct);
        } catch (Exception e) {
            e.printStackTrace(); // ← コンソールにエラーを出力！
            return "error"; // ← エラー用のテンプレートが無い場合はWhitelabelが出ます
        }
        return "redirect:/products";
    }

    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute Product product) {
        // ステータスがnullなら DBから取得して設定
        if (product.getStatus() == null) {
            ProductStatus defaultStatus = productStatusRepository
                    .findById(1)
                    .orElseThrow(() -> new IllegalStateException("Default status not found"));
            product.setStatus(defaultStatus);
        }

        productService.addProduct(product);
        return "redirect:/products";
    }

    @PostMapping("/products/toggleStatus/{id}")
    public ResponseEntity<Void> toggleStatus(@PathVariable Integer id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();

            ProductStatus currentStatus = product.getStatus();
            ProductStatus newStatus = new ProductStatus();

            if (currentStatus.getId() == 1) {
                newStatus.setId(2); // 「販売終了」
            } else {
                newStatus.setId(1); // 「販売中」
            }

            product.setStatus(newStatus);
            productRepository.save(product);

            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
