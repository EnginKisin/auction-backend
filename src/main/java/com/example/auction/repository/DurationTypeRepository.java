package com.example.auction.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auction.model.DurationType;

public interface DurationTypeRepository extends JpaRepository<DurationType, Long> {
    
}
