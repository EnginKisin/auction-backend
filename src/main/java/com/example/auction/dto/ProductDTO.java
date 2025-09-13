package com.example.auction.dto;

import java.util.List;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ProductDTO {
    private Long id;

    @NotBlank(message = "Ürün adı boş olamaz")
    @Size(min = 3, max = 100, message = "Ürün 3-100 karakter arasında olmalı")
    private String name;

    @NotBlank(message = "Açıklama boş olamaz")
    @Size(min = 10, max = 2000, message = "Açıklama 10-2000 karakter arasında olmalı")
    private String description;

    @NotNull(message = "Fiyat boş olamaz")
    @Positive(message = "Fiyat 0’dan büyük olmalıdır")
    @Digits(integer = 8, fraction = 2, message = "Fiyat formatı hatalı (max 8 hane ve 2 ondalık olmalı)")
    private Double price;
    
    private Long ownerId;
    private List<ProductImageDTO> images;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public List<ProductImageDTO> getImages() {
        return images;
    }

    public void setImages(List<ProductImageDTO> images) {
        this.images = images;
    }
}
