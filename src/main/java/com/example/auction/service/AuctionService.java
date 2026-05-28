package com.example.auction.service;

import java.time.Instant;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.auction.common.exception.NotFoundException;
import com.example.auction.common.message.MessageCode;
import com.example.auction.model.Auction;
import com.example.auction.model.DurationType;
import com.example.auction.model.User;
import com.example.auction.repository.AuctionRepository;
import com.stripe.exception.StripeException;

@Service
public class AuctionService {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private DurationTypeService durationTypeService;

    @Autowired
    private StripeService stripeService;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void closeExpiredAuctions() {
        List<Auction> activeAuctions = auctionRepository.findByIsActive(true);
        Instant now = Instant.now();

        for (Auction auction : activeAuctions) {
            if (now.isAfter(auction.getEndTime())) {
                auction.setIsActive(false);
                auctionRepository.save(auction);

                if (auction.getHighestBidder() != null && auction.getHighestBid() != null) {
                    try {
                        stripeService.chargeCustomerWithPaymentIntent(
                            auction.getHighestBidder().getStripeCustomerId(),
                            auction.getHighestBidder().getStripePaymentMethodId(),
                            auction.getHighestBid(),
                            "usd"
                        );
                    } catch (StripeException e) {
                        System.err.println("Stripe ödeme hatası: " + e.getMessage());
                    }
                }
            }
        }
    }

    public String createAuction(Auction auction, Long durationTypeId) {
        boolean exists = auctionRepository.findByProductId(auction.getProduct().getId()).isPresent();
        if (exists) {
            throw new IllegalStateException(MessageCode.AUCTION_ALREADY_EXISTS.getMessage());
        }

        Instant now = Instant.now().truncatedTo(ChronoUnit.MINUTES).plus(1, ChronoUnit.MINUTES);
        DurationType durationType = durationTypeService.getDurationTypeById(durationTypeId);
        auction.setStartTime(now);
        auction.setEndTime(now.plus(Duration.ofMinutes(durationType.getDurationInMinutes())));

        auction.setDurationType(durationType);
        auction.setIsActive(true);

        auctionRepository.save(auction);
        return MessageCode.AUCTION_CREATED_SUCCESS.getMessage();
    }

    public List<Auction> getAllActiveAuctions() {
        return auctionRepository.findByIsActive(true);
    }

    public List<Auction> getAuctionsByOwner(User owner) {
        return auctionRepository.findByOwner(owner);
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
