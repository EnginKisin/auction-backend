package com.example.auction.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.auction.common.exception.NotFoundException;
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
        return "Ürün başarıyla kaydedildi.";
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ürün bulunamadı."));
    }

    public String deleteProduct(Long id, String email) {
        Product product = getProductById(id);
    
        if (!product.getOwner().getEmail().equals(email)) {
            throw new IllegalStateException("Bu ürünü silme yetkiniz yok.");
        }
    
        productRepository.delete(product);
        return "Ürün başarıyla silindi.";
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
