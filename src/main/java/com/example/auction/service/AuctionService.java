package com.example.auction.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.auction.model.Auction;
import com.example.auction.repository.AuctionRepository;

@Service
public class AuctionService {
    
    @Autowired
    private AuctionRepository auctionRepository;

    public Auction createAuction(Auction auction) {
        auction.setStartTime(LocalDateTime.now());
        auction.setEndTime(auction.getStartTime().plusMinutes(auction.getIsActive() ? 30 : 1440));
        auction.setIsActive(true);

        return auctionRepository.save(auction);
    }

    public List<Auction> getActiveAuctions() {
        return auctionRepository.findByIsActive(true);
    }

    public Optional<Auction> getAuctionById(Long id) {
        return auctionRepository.findById(id);
    }

    public void closeAuction(Long auctionId) {
        Optional<Auction> auctionOpt = auctionRepository.findById(auctionId);
        if (auctionOpt.isPresent()) {
            Auction auction = auctionOpt.get();
            auction.setIsActive(false);
            auctionRepository.save(auction);
        }
    }
}
