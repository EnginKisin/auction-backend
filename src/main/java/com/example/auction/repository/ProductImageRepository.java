package com.example.auction.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auction.model.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long>{
    
}
