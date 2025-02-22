package com.example.auction.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auction.model.Product;
import com.example.auction.model.User;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByOwner(User owner);
}
