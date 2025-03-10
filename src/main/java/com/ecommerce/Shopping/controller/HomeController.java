package com.ecommerce.Shopping.controller;

import com.ecommerce.Shopping.model.Category;
import com.ecommerce.Shopping.model.Product;
import com.ecommerce.Shopping.model.UserDtls;
import com.ecommerce.Shopping.service.CategoryService;
import com.ecommerce.Shopping.service.ProductService;
import com.ecommerce.Shopping.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void getUserDetails(Principal p, Model m){
        if(p!=null){
            String email = p.getName();
            UserDtls userDtls = userService.getUserByEmail(email);
            m.addAttribute("user", userDtls);
        }

        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
        m.addAttribute("categories", allActiveCategory);
    }

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/signin")
    public String login(){
        return "login";
    }

    @GetMapping("/register")
    public String register(){
        return "register";
    }

    @GetMapping("/products")
    public String products(Model model, @RequestParam(value = "category", defaultValue = "") String category){
        List<Category> categories = categoryService.getAllActiveCategory();
        List<Product> products = productService.getAllActiveProduct(category);

        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        model.addAttribute("paramValue", category);
        return "product";
    }

    @GetMapping("/product/{id}")
    public String product(@PathVariable int id, Model model){
        Product productById = productService.getProductById(id);

        model.addAttribute("product", productById);
        return "view_product";
    }


    @PostMapping("/saveUser")
    public String saveUSer(@ModelAttribute UserDtls user, @RequestParam("img")
            MultipartFile file, HttpSession session) throws IOException {

        String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();

        user.setProfileImage(imageName);
        UserDtls saveUser = userService.saveUser(user);

        if (!ObjectUtils.isEmpty(saveUser)) {
            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();

                // Ensure profile_img directory exists
                File profileImgDir = new File(saveFile.getAbsolutePath() + File.separator + "profile_img");
                if (!profileImgDir.exists()) {
                    profileImgDir.mkdirs(); // Create the directory if it does not exist
                }

                Path path = Paths.get(profileImgDir.getAbsolutePath(), file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("succMsg", "Registered successfully");
        } else {
            session.setAttribute("errorMsg", "something wrong on server");
        }

        return "redirect:/register";
    }


}
