package com.ecommerce.Shopping.controller;

import com.ecommerce.Shopping.model.Cart;
import com.ecommerce.Shopping.model.Category;
import com.ecommerce.Shopping.model.UserDtls;
import com.ecommerce.Shopping.service.CartService;
import com.ecommerce.Shopping.service.CategoryService;
import com.ecommerce.Shopping.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CartService cartService;

    @GetMapping("/")
    public String home(){
        return "user/home";
    }

    @ModelAttribute
    public void getUserDetails(Principal p, Model m){
        if(p!=null){
            String email = p.getName();
            UserDtls userDtls = userService.getUserByEmail(email);
            m.addAttribute("user", userDtls);
            Integer countCart = cartService.getCountCart(userDtls.getId());
            m.addAttribute("countCart", countCart);
        }

        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
        m.addAttribute("categories", allActiveCategory);
    }

    @GetMapping("/addCart")
    public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session){
        Cart saveCart = cartService.saveCart(pid, uid);

        if(ObjectUtils.isEmpty(saveCart)){
            session.setAttribute("errorMsg", "Product add to cart failed");
        }else {
            session.setAttribute("succMsg", "Product added to cart");
        }
        return "redirect:/product/" + pid;
    }

    @GetMapping("/cart")
    public String loadCartPage(Principal p, Model model){
        UserDtls user = getLoggedInUserDetails(p);
        List<Cart> carts = cartService.getCartByUser(user.getId());
        model.addAttribute("carts", carts);

        if (carts.size() > 0){
            Double totalOrderPrice = carts.get(carts.size()-1).getTotalOrderPrice();
            model.addAttribute("totalOrderPrice", totalOrderPrice);
        }
        return "/user/cart";
    }

    @GetMapping("/cartQuantityUpdate")
    public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid){
        cartService.updateQuantity(sy,cid);
        return "redirect:/user/cart";
    }

    private UserDtls getLoggedInUserDetails(Principal p){
        String email = p.getName();
        UserDtls userDtls = userService.getUserByEmail(email);
        return userDtls;
    }

    @GetMapping("/orders")
    public String orderPage(){
        return "/user/order";
    }
}
