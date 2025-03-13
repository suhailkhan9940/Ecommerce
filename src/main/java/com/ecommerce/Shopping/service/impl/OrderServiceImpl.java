package com.ecommerce.Shopping.service.impl;

import com.ecommerce.Shopping.model.Cart;
import com.ecommerce.Shopping.model.OrderAddress;
import com.ecommerce.Shopping.model.OrderRequest;
import com.ecommerce.Shopping.model.ProductOrder;
import com.ecommerce.Shopping.repository.CartRepository;
import com.ecommerce.Shopping.repository.ProductOrderRepository;
import com.ecommerce.Shopping.service.OrderService;
import com.ecommerce.Shopping.util.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductOrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Override
    public void saveOrder(Integer userId, OrderRequest orderRequest) {

        List<Cart> carts = cartRepository.findByUserId(userId);

        for (Cart cart : carts){
            ProductOrder order = new ProductOrder();

            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderDate(new Date());

            order.setProduct(cart.getProduct());
            order.setPrice(cart.getProduct().getDiscountPrice());

            order.setQuantity(cart.getQuantity());
            order.setUser(cart.getUser());

            order.setStatus(OrderStatus.IN_PROGRESS.getName());
            order.setPaymentType(orderRequest.getPaymentType());

            OrderAddress address = new OrderAddress();
            address.setFirstName(orderRequest.getFirstName());
            address.setLastName(orderRequest.getLastName());
            address.setEmail(orderRequest.getEmail());
            address.setMobileNo(orderRequest.getMobileNo());
            address.setAddress(orderRequest.getAddress());
            address.setCity(orderRequest.getCity());
            address.setState(orderRequest.getState());
            address.setPincode(orderRequest.getPincode());

            order.setOrderAddress(address);
            orderRepository.save(order);

        }
    }
}
