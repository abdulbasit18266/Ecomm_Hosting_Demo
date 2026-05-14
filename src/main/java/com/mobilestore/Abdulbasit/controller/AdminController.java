package com.mobilestore.Abdulbasit.controller;



import com.mobilestore.Abdulbasit.entity.Order;

import com.mobilestore.Abdulbasit.entity.Product;

import com.mobilestore.Abdulbasit.entity.User;

import com.mobilestore.Abdulbasit.service.OrderService;

import com.mobilestore.Abdulbasit.service.ProductFirestoreService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.stream.Collectors;



// ... baaki imports same rahenge ...

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private ProductFirestoreService productService;
    @Autowired private OrderService orderService;

    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && "ADMIN".equals(user.getRole());
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        try {
            List<Product> products = productService.getAllProducts();
            model.addAttribute("products", products);
            model.addAttribute("totalProducts", products != null ? products.size() : 0);

            List<Order> orders = orderService.getAllOrders();
            model.addAttribute("totalOrders", orders != null ? orders.size() : 0);
            model.addAttribute("orders", orders);

            // Chart data calculation
            double revenue = (orders != null) ? orders.stream().filter(o -> o.getTotalAmount() != null).mapToDouble(Order::getTotalAmount).sum() : 0;
            model.addAttribute("totalRevenue", revenue);

            return "admin_dashboard";
        } catch (Exception e) { return "error"; }
    }

    // ✅ ADD PRODUCT FORM
    @GetMapping("/add-product")
    public String showAddProductForm(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("product", new Product());
        return "add_product";
    }

    // ✅ EDIT PRODUCT FORM (Missing was here)
    @GetMapping("/edit-product/{id}")
    public String showEditProductForm(@PathVariable String id, Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        try {
            Product product = productService.getProductById(id);
            model.addAttribute("product", product);
            return "add_product"; // Add aur Edit ke liye ek hi form use kar sakte hain
        } catch (Exception e) {
            return "redirect:/admin/dashboard?error=NotFound";
        }
    }

    // ✅ SAVE / UPDATE PRODUCT
    @PostMapping("/save-product")
    public String saveProduct(@ModelAttribute("product") Product product, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        try {
            productService.saveProduct(product);
            return "redirect:/admin/dashboard?success=true";
        } catch (Exception e) {
            return "redirect:/admin/dashboard?error=failed";
        }
    }

    // ✅ DELETE PRODUCT (Missing was here)
    @GetMapping("/delete-product/{id}")
    public String deleteProduct(@PathVariable String id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        try {
            productService.deleteProduct(id);
            return "redirect:/admin/dashboard?deleted=true";
        } catch (Exception e) {
            return "redirect:/admin/dashboard?error=deleteFailed";
        }
    }

    @GetMapping("/orders")
    public String viewOrders(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin_orders";
    }

    @PostMapping("/update-order-status")
    @ResponseBody
    public String updateOrderStatus(@RequestParam String orderId, @RequestParam String status, HttpSession session) {
        if (!isAdmin(session)) return "Unauthorized";
        try {
            orderService.updateStatus(orderId, status);
            return "Success";
        } catch (Exception e) { return "Error: " + e.getMessage(); }
    }
}