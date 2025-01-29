package com.ecommerce.Shopping.controller;

import com.ecommerce.Shopping.model.Category;
import com.ecommerce.Shopping.service.CategoryService;
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

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public String index(){
        return "admin/index";
    }

    @GetMapping("/loadAddProduct")
    public String loadAddProduct(){
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

}
