package com.example.auction.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.auction.common.exception.NotFoundException;
import com.example.auction.common.message.MessageCode;
import com.example.auction.model.DurationType;
import com.example.auction.repository.DurationTypeRepository;

@Service
public class DurationTypeService {

    @Autowired
    private DurationTypeRepository durationTypeRepository;

    public DurationType getDurationTypeById(Long id) {
        return durationTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MessageCode.DURATION_TYPE_NOT_FOUND.getMessage()));
    }
}
