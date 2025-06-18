// // ビール銘柄管理ページ
// package com.example.SalesForecast.controller;

// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.PostMapping;

// import jakarta.servlet.http.HttpSession;


package com.example.SalesForecast.controller;

import com.example.SalesForecast.domain.product.entity.Product;
import com.example.SalesForecast.domain.product.entity.ProductStatus;
import com.example.SalesForecast.domain.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public String showProductManagementPage(Model model) {
        List<Product> productList = productService.getAllProducts();
        model.addAttribute("productList", productList);
        return "product-management";
    }

    @PostMapping("/products/update")
    public String updateProduct(@ModelAttribute Product updatedProduct) {
        try {
            productService.updateProduct(updatedProduct);
        } catch (Exception e) {
            e.printStackTrace(); // ← コンソールにエラーを出力！
            return "error";      // ← エラー用のテンプレートが無い場合はWhitelabelが出ます
        }
        return "redirect:/products";
    }

    
    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute Product product) {
    // ステータスがnullなら "available" を仮設定
        if (product.getStatus() == null) {
            ProductStatus defaultStatus = new ProductStatus();
            defaultStatus.setId(1); // 'available' の ID
            product.setStatus(defaultStatus);
        }

        productService.addProduct(product);
        return "redirect:/products";
    }


    @PostMapping("/products/delete")
    public String deleteProduct(@RequestParam("id") Integer id) {
        productService.deleteProductById(id);
        return "redirect:/products";
    }
}

