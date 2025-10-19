package com.example.auction.service;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public String placeBid(Long auctionId, Bid bid) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new NotFoundException(MessageCode.AUCTION_NOT_FOUND.getMessage()));

        if (auction.getOwner().getId() == bid.getBidder().getId()) {
            throw new IllegalArgumentException(MessageCode.BID_OWN_AUCTION_NOT_ALLOWED.getMessage());
        }

        if (!auction.getIsActive() || LocalDateTime.now().isAfter(auction.getEndTime())) {
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

}
