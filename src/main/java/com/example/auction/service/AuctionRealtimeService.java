package com.example.auction.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.auction.dto.AuctionUpdateDTO;

@Service
public class AuctionRealtimeService {

    private final SimpMessagingTemplate messagingTemplate;

    public AuctionRealtimeService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastAuctionUpdate(AuctionUpdateDTO dto) {

        messagingTemplate.convertAndSend(
                "/topic/auctions",
                dto
        );
    }
}