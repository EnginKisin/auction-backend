package com.example.auction.dto;

public class AuctionUpdateDTO {
    private Long id;
    private Double highestBid;
    private Long highestBidderId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getHighestBid() {
        return highestBid;
    }

    public void setHighestBid(Double highestBid) {
        this.highestBid = highestBid;
    }

    public Long getHighestBidderId() {
        return highestBidderId;
    }

    public void setHighestBidderId(Long highestBidderId) {
        this.highestBidderId = highestBidderId;
    }

    @Override
    public String toString() {
        return "AuctionUpdateDTO{" +
                "id=" + id +
                ", highestBid=" + highestBid +
                ", highestBidderId=" + highestBidderId +
                '}';
    }
}
