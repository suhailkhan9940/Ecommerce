package com.ecommerce.Shopping.service;

import com.ecommerce.Shopping.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    public Product saveProduct(Product product);

    public List<Product> getAllProduct();

    public Boolean deleteProduct(Integer id);

    public Product getProductById(Integer id);

    public Product updateProduct(Product product, MultipartFile file) throws IOException;

    public List<Product> getAllActiveProduct(String category);
}
