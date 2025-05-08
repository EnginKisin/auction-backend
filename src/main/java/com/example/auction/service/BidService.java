package com.example.auction.service;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.auction.common.exception.NotFoundException;
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
                .orElseThrow(() -> new NotFoundException("Açık artırma bulunamadı."));

        if (!auction.getIsActive()) {
            throw new IllegalArgumentException("Açık artırma artık aktif değil.");
        }

        double currentHighest = auction.getHighestBid() == null
                ? auction.getStartingPrice()
                : auction.getHighestBid();

        if (bid.getAmount() <= currentHighest) {
            throw new IllegalArgumentException("Teklif mevcut en yüksek tekliften büyük olmalıdır.");
        }

        bid.setAuction(auction);
        bid.setBidTime(LocalDateTime.now());
        auction.setHighestBid(bid.getAmount());
        auction.setHighestBidder(bid.getBidder());

        bidRepository.save(bid);
        auctionRepository.save(auction);

        return "Teklif başarıyla oluşturuldu.";
    }

    // public Bid getHighestBid(Long auctionId) {
    //     List<Bid> bids = bidRepository.findByAuctionId(auctionId);
    //     if (bids == null || bids.isEmpty()) {
    //         throw new IllegalArgumentException("Açık artırmada teklif bulunamadı.");
    //     }
    //     return bids.stream()
    //             .max((b1, b2) -> Double.compare(b1.getAmount(), b2.getAmount()))
    //             .orElseThrow(() -> new NotFoundException("Açık artırmada en yüksek teklif bulunamadı."));
    // }

    // public List<Bid> getBidsForAuction(Long auctionId) {
    //     if (auctionId == null || auctionId <= 0) {
    //         throw new NotFoundException("Geçersiz açık artırma id.");
    //     }
    //     return bidRepository.findByAuctionId(auctionId);
    // }

}
