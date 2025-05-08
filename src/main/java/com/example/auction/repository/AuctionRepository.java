package com.example.auction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auction.model.Auction;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    List<Auction> findByIsActive(Boolean isActive);

    Optional<Auction> findByProductId(Long productId);
}
