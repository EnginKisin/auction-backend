package com.example.auction.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.auction.common.exception.NotFoundException;
import com.example.auction.common.message.MessageCode;
import com.example.auction.model.Auction;
import com.example.auction.model.DurationType;
import com.example.auction.repository.AuctionRepository;

@Service
public class AuctionService {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private DurationTypeService durationTypeService;

    public String createAuction(Auction auction, Long durationTypeId) {
        boolean exists = auctionRepository.findByProductId(auction.getProduct().getId()).isPresent();
        if (exists) {
            throw new IllegalStateException(MessageCode.AUCTION_ALREADY_EXISTS.getMessage());
        }

        auction.setStartTime(LocalDateTime.now());
        DurationType durationType = durationTypeService.getDurationTypeById(durationTypeId);
        auction.setDurationType(durationType);
        auction.setEndTime(auction.getStartTime().plusMinutes(durationType.getDurationInMinutes()));
        auction.setIsActive(true);

        auctionRepository.save(auction);
        return MessageCode.AUCTION_CREATED_SUCCESS.getMessage();
    }

    public List<Auction> getActiveAuctions() {
        return auctionRepository.findByIsActive(true);
    }

    public Auction getAuctionById(Long id) {
        return auctionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MessageCode.AUCTION_NOT_FOUND.getMessage()));
    }

    public String closeAuction(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new NotFoundException(MessageCode.AUCTION_NOT_FOUND.getMessage()));

        if (!auction.getIsActive()) {
            throw new IllegalStateException(MessageCode.AUCTION_INACTIVE.getMessage());
        }

        auction.setIsActive(false);
        auctionRepository.save(auction);
        return MessageCode.AUCTION_CLOSED_SUCCESS.getMessage();
    }
}
