package com.example.auction.service;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.auction.common.exception.NotFoundException;
import com.example.auction.common.message.MessageCode;
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
                .orElseThrow(() -> new NotFoundException(MessageCode.AUCTION_NOT_FOUND.getMessage()));

        if (!auction.getIsActive()) {
            throw new IllegalArgumentException(MessageCode.AUCTION_INACTIVE.getMessage());
        }

        double currentHighest = auction.getHighestBid() == null
                ? auction.getStartingPrice()
                : auction.getHighestBid();

        if (bid.getAmount() <= currentHighest) {
            throw new IllegalArgumentException(MessageCode.BID_TOO_LOW.getMessage());
        }

        bid.setAuction(auction);
        bid.setBidTime(LocalDateTime.now());
        auction.setHighestBid(bid.getAmount());
        auction.setHighestBidder(bid.getBidder());

        bidRepository.save(bid);
        auctionRepository.save(auction);

        return MessageCode.BID_SUCCESS.getMessage();
    }

    // public Bid getHighestBid(Long auctionId) {
    //     List<Bid> bids = bidRepository.findByAuctionId(auctionId);
    //     if (bids == null || bids.isEmpty()) {
    //         throw new IllegalArgumentException(MessageCode.BID_NOT_FOUND.getMessage());
    //     }
    //     return bids.stream()
    //             .max((b1, b2) -> Double.compare(b1.getAmount(), b2.getAmount()))
    //             .orElseThrow(() -> new NotFoundException(MessageCode.HIGHEST_BID_NOT_FOUND.getMessage()));
    // }

    // public List<Bid> getBidsForAuction(Long auctionId) {
    //     if (auctionId == null || auctionId <= 0) {
    //         throw new NotFoundException(MessageCode.INVALID_AUCTION_ID.getMessage());
    //     }
    //     return bidRepository.findByAuctionId(auctionId);
    // }

}
