package com.ecommerce.Shopping.service;

import com.ecommerce.Shopping.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    Product saveProduct(Product product);

    List<Product> getAllProduct();

    Boolean deleteProduct(Integer id);

    Product getProductById(Integer id);

    Product updateProduct(Product product, MultipartFile file) throws IOException;

    List<Product> getAllActiveProduct(String category);

    List<Product> searchProduct(String ch);

    Page<Product> getAllActiveProductPagination(Integer pageNo, Integer pageSize, String category);

    Page<Product> searchProductPagination(Integer pageNo, Integer pageSize, String ch);

    Page<Product> getAllProductsPagination(Integer pageNo, Integer pageSize);

    Page<Product> searchActiveProductPagination(Integer pageNo, Integer pageSize, String category, String ch);

}
