package com.ecommerce.Shopping.service;

import com.ecommerce.Shopping.model.OrderRequest;
import com.ecommerce.Shopping.model.ProductOrder;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {

    void saveOrder(Integer userId, OrderRequest orderRequest) throws Exception;

    List<ProductOrder> getOrdersByUser(Integer userId);

    ProductOrder updateOrderStatus(Integer id, String status);

    List<ProductOrder> getAllOrders();

    ProductOrder getOrdersByOrderId(String orderId);

    Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize);
}
