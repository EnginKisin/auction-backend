package com.example.auction.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class BidDTO {
    private Long id;
    private Long auctionId;
    private Long bidderId;

    @NotNull(message = "Teklif miktarı boş olamaz")
    @Positive(message = "Teklif miktarı 0’dan büyük olmalıdır")
    @Digits(integer = 8, fraction = 2, message = "Teklif miktarı hatalı formatta (max 8 hane ve 2 ondalık olmalı)")
    private Double amount;

    private LocalDateTime bidTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }

    public Long getBidderId() {
        return bidderId;
    }

    public void setBidderId(Long bidderId) {
        this.bidderId = bidderId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getBidTime() {
        return bidTime;
    }

    public void setBidTime(LocalDateTime bidTime) {
        this.bidTime = bidTime;
    }
}
