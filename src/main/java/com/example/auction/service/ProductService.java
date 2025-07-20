package com.example.auction.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.auction.common.exception.NotFoundException;
import com.example.auction.common.message.MessageCode;
import com.example.auction.model.Product;
import com.example.auction.model.User;
import com.example.auction.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getProductsByOwner(User owner) {
        return productRepository.findByOwner(owner);
    }

    public String saveProduct(Product product) {
        productRepository.save(product);
        return MessageCode.PRODUCT_SUCCESS.getMessage();
    }

    public String updateProduct(Long id, Product product, String email) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MessageCode.PRODUCT_NOT_FOUND.getMessage()));

        if (!existingProduct.getOwner().getEmail().equals(email)) {
            throw new IllegalStateException(MessageCode.PRODUCT_UPDATE_UNAUTHORIZED.getMessage());
        }

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());

        productRepository.save(existingProduct);
        return MessageCode.PRODUCT_UPDATED.getMessage();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MessageCode.PRODUCT_NOT_FOUND.getMessage()));
    }

    public String deleteProduct(Long id, String email) {
        Product product = getProductById(id);
    
        if (!product.getOwner().getEmail().equals(email)) {
            throw new IllegalStateException(MessageCode.PRODUCT_DELETE_UNAUTHORIZED.getMessage());
        }
    
        productRepository.delete(product);
        return MessageCode.PRODUCT_DELETED.getMessage();
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
