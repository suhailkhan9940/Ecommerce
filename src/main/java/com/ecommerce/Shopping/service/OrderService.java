package com.ecommerce.Shopping.service;

import com.ecommerce.Shopping.model.OrderRequest;
import com.ecommerce.Shopping.model.ProductOrder;

public interface OrderService {

    public void saveOrder(Integer userId, OrderRequest orderRequest);
}
