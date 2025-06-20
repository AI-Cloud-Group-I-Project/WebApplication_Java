package com.example.SalesForecast.controller;

import com.example.SalesForecast.domain.inventory.entity.Inventory;
import com.example.SalesForecast.domain.inventory.entity.ProductReceipt;
import com.example.SalesForecast.domain.inventory.repository.ProductReceiptRepository;
import com.example.SalesForecast.domain.inventory.service.InventoryService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ProductReceiptRepository productReceiptRepository;

    // 在庫一覧ページを表示
    @GetMapping("/inventories")
    public String showInventoryPage(Model model, HttpSession session) {
        model.addAttribute("current_username", session.getAttribute("username"));
        model.addAttribute("current_email", session.getAttribute("email"));

        List<Inventory> inventoryList = inventoryService.getAllInventories();
        model.addAttribute("inventoryList", inventoryList);

        List<ProductReceipt> todayReceipts = productReceiptRepository.findByReceivedDate(LocalDate.now());
        model.addAttribute("todayReceipts", todayReceipts);

        return "admin-inventories";
    }

   // 商品入荷の登録（複数行対応）
    @PostMapping("/inventories/add")
    public String addInventories(
        @RequestParam("janCodes") List<String> janCodes,
        @RequestParam("quantities") List<Integer> quantities
    ) {
        for (int i = 0; i < janCodes.size(); i++) {
            String janCode = janCodes.get(i);
            Integer quantity = quantities.get(i);

            if (quantity < 0) {
                continue;
            }

            try {
                inventoryService.processStockEntry(janCode, quantity);
            } catch (IllegalArgumentException e) {
                // JANコードが不正な場合はスキップ
                continue;
             } catch (IllegalStateException e) {
                System.out.println("【ログ】入荷済み商品の重複入力: " + janCode);
                return "redirect:/inventories?error=alreadyExists";
            }
        }

        return "redirect:/inventories";
    }


    // 入荷済み商品の編集（当日分のみ）
    @PostMapping("/inventories/update")
    public String updateInventory(
            @RequestParam("productId") Integer productId,
            @RequestParam("quantity") Integer quantity
    ) {
        if (quantity < 0) {
            return "redirect:/inventories?error=negative";
        }

        inventoryService.updateTodayInventory(productId, quantity);
        return "redirect:/inventories";
    }
}
