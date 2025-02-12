package com.ecommerce.Shopping.controller;

import com.ecommerce.Shopping.model.Category;
import com.ecommerce.Shopping.model.Product;
import com.ecommerce.Shopping.repository.ProductRepository;
import com.ecommerce.Shopping.service.CategoryService;
import com.ecommerce.Shopping.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/register")
    public String register(){
        return "register";
    }

    @GetMapping("/products")
    public String products(Model model){
        List<Category> categories = categoryService.getAllActiveCategory();
        List<Product> products = productService.getAllActiveProduct();

        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        return "product";
    }

    @GetMapping("/product")
    public String product(){
        return "view_product";
    }
}
