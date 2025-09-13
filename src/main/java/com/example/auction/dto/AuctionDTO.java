package com.example.auction.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class AuctionDTO {

    private Long id;

    @NotNull(message = "Ürün ID boş olamaz")
    @JsonProperty("product_id")
    private Long productId;
    private Long ownerId;

    @NotNull(message = "Başlangıç fiyatı boş olamaz")
    @Positive(message = "Başlangıç fiyatı 0’dan büyük olmalıdır")
    @Digits(integer = 8, fraction = 2, message = "Başlangıç fiyatı formatı hatalı (max 8 hane ve 2 ondalık olmalı)")
    private Double startingPrice;

    private Double highestBid;
    private Long highestBidderId;

    @NotNull(message = "Süre tipi ID boş olamaz")
    private Long durationTypeId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isActive;
    private ProductDTO product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Double getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(Double startingPrice) {
        this.startingPrice = startingPrice;
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

    public Long getDurationTypeId() {
        return durationTypeId;
    }

    public void setDurationTypeId(Long durationTypeId) {
        this.durationTypeId = durationTypeId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }
}