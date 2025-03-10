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
@RequestMapping("/admin")
public class AdminController {

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
        return "admin/index";
    }

    // Categories Controllers

    @GetMapping("/loadAddProduct")
    public String loadAddProduct(Model model){
        List<Category> categories = categoryService.getAllCategory();
        model.addAttribute("categories",categories);
        return "admin/add_product";
    }

    @GetMapping("/category")
    public String category(Model model){
        model.addAttribute("categories",categoryService.getAllCategory());
        return "admin/category";
    }

    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category,
                               @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {

        String imageName = file !=null ? file.getOriginalFilename(): "default.jpg";
        category.setImageName(imageName);

        Boolean existCategory = categoryService.existCategory(category.getName());
        if (existCategory){
            session.setAttribute("errorMsg","Category name already exist");
        }else{
            Category saveCategory = categoryService.saveCategory(category);

            if (ObjectUtils.isEmpty(saveCategory)){
                session.setAttribute("errorMsg", "Not save, Internal server error");
            }else{

                File saveFile =  new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+"category_img"+File.separator+file.getOriginalFilename());

//                 System.out.println(path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                session.setAttribute("succMsg", "Saved Successfully");
            }

        }
        return "redirect:/admin/category";
    }

    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable int id, HttpSession session){

        Boolean deleteCategory = categoryService.deleteCategory(id);

        if(deleteCategory){
            session.setAttribute("succMsg", "Category deleted Successfully");
        }else{
            session.setAttribute("errorMsg", "Something went wrong");
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/loadEditCategory/{id}")
    public String loadEditCategory(@PathVariable int id, Model model){
        model.addAttribute("category", categoryService.getCategoryById(id));
        return "admin/edit_category";
    }

    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category,
                                 @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {

        Category oldCategory = categoryService.getCategoryById(category.getId());
        String imageName = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename();

        if (!ObjectUtils.isEmpty(category)){
            oldCategory.setName(category.getName());
            oldCategory.setIsActive(category.getIsActive());
            oldCategory.setImageName(imageName);
        }

        Category updateCategory = categoryService.saveCategory(oldCategory);

        if (!ObjectUtils.isEmpty(updateCategory)){

            if(!file.isEmpty()){
                File saveFile =  new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+"category_img"+File.separator+file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }

            session.setAttribute("succMsg", "Category updated Successfully");
        }else{
            session.setAttribute("errorMsg", "Something went wrong");
        }

        return "redirect:/admin/loadEditCategory/"+category.getId();
    }


    // Product Controllers

    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product,
                              @RequestParam("file") MultipartFile image,  HttpSession session) throws IOException {

        String imageName = image.isEmpty()? "default.jpg" : image.getOriginalFilename();

        product.setImage(imageName);
        product.setDiscount(0);
        product.setDiscountPrice(product.getPrice());
        Product saveProduct = productService.saveProduct(product);

        if(!ObjectUtils.isEmpty(saveProduct)){

            File saveFile =  new ClassPathResource("static/img").getFile();
            Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+"product_img"+File.separator+image.getOriginalFilename());

            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            session.setAttribute("succMsg", "Product saved Successfully");
        }else {
            session.setAttribute("errorMsg", "Something went wrong");
        }

        return "redirect:/admin/loadAddProduct";
    }

    @GetMapping("/products")
    public String loadViewProduct(Model model){
        model.addAttribute("products", productService.getAllProduct());
        return "admin/products";
    }

    @GetMapping("/deleteProduct/{id}")
    public String DeleteProduct(@PathVariable int id, HttpSession session){
        Boolean deleteProduct = productService.deleteProduct(id);

        if (deleteProduct){
            session.setAttribute("succMsg", "Product deleted successfully");
        }else {
            session.setAttribute("errorMsg", "Something went wrong");
        }

        return "redirect:/admin/products";
    }

    @GetMapping("/editProduct/{id}")
    public String editProduct(@PathVariable int id,  Model model){

        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("categories", categoryService.getAllCategory());
        return "admin/edit_product";
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
                                HttpSession session) throws IOException {


        if (product.getDiscount()<0 || product.getDiscount()>100){
            session.setAttribute("errorMsg", "Invalid Discount");
        }else {
            Product updateProduct = productService.updateProduct(product,image);

            if (!ObjectUtils.isEmpty(updateProduct)){
                session.setAttribute("succMsg", "Product updated successfully");
            }else {
                session.setAttribute("errorMsg", "Something went wrong");
            }
        }
        return "redirect:/admin/editProduct/"+product.getId();
    }

    @GetMapping("/users")
    public String getAllUsers(Model m){

        List<UserDtls> users = userService.getUsers("ROLE_USER");
        m.addAttribute("users",users);
        return "/admin/users";
    }

    @GetMapping("/updateSts/")
    public String updateUserAccountStatus(@RequestParam Boolean status, @RequestParam Integer id,
                                          HttpSession session){
        Boolean f = userService.updateAccountStatus(id, status);

        if (f){
            session.setAttribute("succMsg", "Account Status updated");
        }else {
            session.setAttribute("errorMsg", "Something went wrong");
        }
        return "redirect:/admin/users";
    }

}
