package com.ecommerce.Shopping.service;

import com.ecommerce.Shopping.model.OrderRequest;
import com.ecommerce.Shopping.model.ProductOrder;

import java.util.List;

public interface OrderService {

    public void saveOrder(Integer userId, OrderRequest orderRequest) throws Exception;

    public List<ProductOrder> getOrdersByUser(Integer userId);

    public ProductOrder updateOrderStatus(Integer id, String status);

    public List<ProductOrder> getAllOrders();
}
