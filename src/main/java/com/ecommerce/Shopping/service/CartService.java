package com.ecommerce.Shopping.service;

import com.ecommerce.Shopping.model.Cart;

import java.util.List;

public interface CartService {

    Cart saveCart(Integer productId, Integer userId);

    List<Cart> getCartByUser(Integer userId);

    Integer getCountCart(Integer userId);

    void updateQuantity(String sy, Integer cid);
}
