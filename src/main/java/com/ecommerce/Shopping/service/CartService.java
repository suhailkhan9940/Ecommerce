package com.ecommerce.Shopping.service;

import com.ecommerce.Shopping.model.Cart;

import java.util.List;

public interface CartService {

    public Cart saveCart(Integer productId, Integer userId);

    public List<Cart> getCartByUser(Integer userId);

    public Integer getCountCart(Integer userId);

    public void updateQuantity(String sy, Integer cid);
}
