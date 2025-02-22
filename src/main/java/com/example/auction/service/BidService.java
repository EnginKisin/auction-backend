package com.example.auction.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.auction.model.Auction;
import com.example.auction.model.Bid;
import com.example.auction.repository.AuctionRepository;
import com.example.auction.repository.BidRepository;

@Service
public class BidService {
    
    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    public String placeBid(Long auctionId, Bid bid) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Auction not found"));

        if (!auction.getIsActive()) {
            return "Auction is no longer active.";
        }

        if (bid.getAmount() <= (auction.getHighestBid() == null ? auction.getStartingPrice() : auction.getHighestBid())) {
            return "Bid must be higher than the current highest bid.";
        }

        bid.setAuction(auction);
        bid.setBidTime(LocalDateTime.now());
        auction.setHighestBid(bid.getAmount());
        auction.setHighestBidder(bid.getBidder());

        bidRepository.save(bid);
        auctionRepository.save(auction);
        return "Bid placed successfully!";
    }

    public List<Bid> getBidsForAuction(Long auctionId) {
        return bidRepository.findByAuction_Id(auctionId);
    }

    public Bid getHighestBid(Long auctionId) {
        return bidRepository.findByAuction_Id(auctionId).stream()
                .max((b1, b2) -> Double.compare(b1.getAmount(), b2.getAmount()))
                .orElse(null);
    }

}
