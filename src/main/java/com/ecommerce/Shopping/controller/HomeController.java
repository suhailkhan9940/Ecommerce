package com.ecommerce.Shopping.controller;

import com.ecommerce.Shopping.model.Category;
import com.ecommerce.Shopping.model.Product;
import com.ecommerce.Shopping.model.UserDtls;
import com.ecommerce.Shopping.service.CartService;
import com.ecommerce.Shopping.service.CategoryService;
import com.ecommerce.Shopping.service.ProductService;
import com.ecommerce.Shopping.service.UserService;
import com.ecommerce.Shopping.util.CommomUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommomUtil commomUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private CartService cartService;

    @ModelAttribute
    public void getUserDetails(Principal p, Model m){
        if(p!=null){
            String email = p.getName();
            UserDtls userDtls = userService.getUserByEmail(email);
            m.addAttribute("user", userDtls);
            Integer countCart = cartService.getCountCart(userDtls.getId());
            m.addAttribute("countCart", countCart);
        }else{
            m.addAttribute("user", null);
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
    public String products(Model m, @RequestParam(value = "category", defaultValue = "") String category,
                           @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                           @RequestParam(name = "pageSize", defaultValue = "9") Integer pageSize,
                           @RequestParam(defaultValue = "") String ch) {

        List<Category> categories = categoryService.getAllActiveCategory();
        m.addAttribute("paramValue", category);
        m.addAttribute("categories", categories);

//		List<Product> products = productService.getAllActiveProducts(category);
//		m.addAttribute("products", products);
        Page<Product> page = null;
        if (StringUtils.isEmpty(ch)) {
            page = productService.getAllActiveProductPagination(pageNo, pageSize, category);
        } else {
            page = productService.searchActiveProductPagination(pageNo, pageSize, category, ch);
        }

        List<Product> products = page.getContent();
        m.addAttribute("products", products);
        m.addAttribute("productsSize", products.size());

        m.addAttribute("pageNo", page.getNumber());
        m.addAttribute("pageSize", pageSize);
        m.addAttribute("totalElements", page.getTotalElements());
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());

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

    //Forget Password

    @GetMapping("/forget-password")
    public String showForgetPassword(){
        return "forget_password.html";
    }

    @PostMapping("/forget-password")
    public String showProcessPassword(@RequestParam String email, HttpSession session, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {

        UserDtls userNyEmail = userService.getUserByEmail(email);

        if(ObjectUtils.isEmpty(userNyEmail)){
            session.setAttribute("errorMsg", "invalid email");
        }else{

            String resetToken = UUID.randomUUID().toString();
            userService.updateUserResetToken(email,resetToken);

            // Generate URL :
            // http://localhost:8080/reset-password?token=sfgdbgfswegfbdgfewgvsrg

            String url = CommomUtil.generateUrl(request) + "/reset-password?token"+resetToken;




           Boolean sendMail =  commomUtil.sendMail(url,email);

           if(sendMail){
               session.setAttribute("succMsg", "Please check your email...Password Rest link sent");
           }else{
               session.setAttribute("errorMsg", "Something went wrong || Email not sent");
           }
        }
        return "redirect:/forget-password";
    }

    @GetMapping("/reset-password")
    public String showResetPassword(@RequestParam String token, HttpSession session, Model model){

        UserDtls userByToken = userService.getUserByToken(token);

        if(userByToken == null){
            model.addAttribute("msg", "Your link is invalid or expired");
            return "message";
        }
        model.addAttribute("token", token);
        return "reset_password.html";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestParam String password,  HttpSession session, Model model){

        UserDtls userByToken = userService.getUserByToken(token);

        if(userByToken == null){
            model.addAttribute("errorMsg", "Your link is invalid or expired");
            return "message";
        }else{
            userByToken.setPassword(passwordEncoder.encode(password));
            userByToken.setResetToken(null);
            userService.updateUser(userByToken);
            session.setAttribute("succMsg", "Password change successfully");
            model.addAttribute("msg", "Password change successfully");

            return "message";
        }
    }

    @GetMapping("/search")
    public String searchProduct(@RequestParam String ch, Model model){
        List<Product> searchProduct = productService.searchProduct(ch);
        model.addAttribute("products", searchProduct);

        List<Category> categories = categoryService.getAllActiveCategory();
        model.addAttribute("categories", categories);

        return "product";
    }
}
