package com.example.auction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auction.model.Auction;
import com.example.auction.model.User;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    List<Auction> findByIsActive(Boolean isActive);

    List<Auction> findByOwner(User owner);

    Optional<Auction> findByProductId(Long productId);
}
