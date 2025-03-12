package com.ecommerce.Shopping.service.impl;

import com.ecommerce.Shopping.model.Cart;
import com.ecommerce.Shopping.model.Product;
import com.ecommerce.Shopping.model.UserDtls;
import com.ecommerce.Shopping.repository.CartRepository;
import com.ecommerce.Shopping.repository.ProductRepository;
import com.ecommerce.Shopping.repository.UserRepository;
import com.ecommerce.Shopping.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Cart saveCart(Integer productId, Integer userId) {

        UserDtls userDtls = userRepository.findById(userId).get();
        Product product = productRepository.findById(productId).get();

        Cart cartStatus = cartRepository.findByProductIdAndUserId(productId, userId);

        Cart cart = null;

        if (ObjectUtils.isEmpty(cartStatus)) {
            cart = new Cart();
            cart.setProduct(product);
            cart.setUser(userDtls);
            cart.setQuantity(1);
            cart.setTotalPrice(1 * product.getDiscountPrice());
        } else {
            cart = cartStatus;
            cart.setQuantity(cart.getQuantity() + 1);
            cart.setTotalPrice(cart.getQuantity() * cart.getProduct().getDiscountPrice());
        }
        Cart saveCart = cartRepository.save(cart);

        return saveCart;
    }

    @Override
    public List<Cart> getCartByUser(Integer userId) {

        List<Cart> carts = cartRepository.findByUserId(userId);
        List<Cart> updateCarts = new ArrayList<>();

        Double totalOrderPrice = 0.0;

        for (Cart cart: carts) {
            Double totalPrice = (cart.getProduct().getDiscountPrice() * cart.getQuantity());
            cart.setTotalPrice(totalPrice);
            totalOrderPrice = totalOrderPrice + totalPrice;
            cart.setTotalOrderPrice(totalOrderPrice);
            updateCarts.add(cart);
        }
        return updateCarts;
    }

    @Override
    public Integer getCountCart(Integer userId) {
        Integer countByUserId = cartRepository.countByUserId(userId);
        return countByUserId;
    }

    @Override
    public void updateQuantity(String sy, Integer cid) {
        Cart cart = cartRepository.findById(cid).get();
        int updateQuantity;

        if (sy.equalsIgnoreCase("de")) {
            updateQuantity = cart.getQuantity() - 1;

            if (updateQuantity <= 0) {
                cartRepository.delete(cart);
            } else {
                cart.setQuantity(updateQuantity);
                cartRepository.save(cart);
            }

        } else {
            updateQuantity = cart.getQuantity() + 1;
            cart.setQuantity(updateQuantity);
            cartRepository.save(cart);
        }
    }
}
