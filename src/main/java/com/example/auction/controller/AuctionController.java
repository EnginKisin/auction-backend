package com.example.auction.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.auction.dto.AuctionDTO;
import com.example.auction.dto.BidDTO;
import com.example.auction.model.Auction;
import com.example.auction.model.Bid;
import com.example.auction.model.Product;
import com.example.auction.model.User;
import com.example.auction.service.AuctionService;
import com.example.auction.service.BidService;
import com.example.auction.service.ProductService;
import com.example.auction.service.TokenService;
import com.example.auction.service.UserService;

@Controller
@RequestMapping("/api/auctions")
public class AuctionController {
    
    private final AuctionService auctionService;
    private final BidService bidService;
    private final UserService userService;
    private final TokenService tokenService;
    private final ProductService productService;

    public AuctionController(AuctionService auctionService, BidService bidService, UserService userService, TokenService tokenService, ProductService productService) {
        this.auctionService = auctionService;
        this.bidService = bidService;
        this.userService = userService;
        this.tokenService = tokenService;
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<AuctionDTO>> listAuctions(@RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        List<Auction> auctions = auctionService.getActiveAuctions();
        List<AuctionDTO> auctionDTOs = auctions.stream().map(this::convertToDTO).toList();
        return ResponseEntity.ok(auctionDTOs);
    }

    // @PostMapping
    // public ResponseEntity<Auction> createAuction(@RequestBody Auction auction, @RequestHeader("Authorization") String token) {
    //     String email = getEmailFromToken(token);
    //     if (email == null) {
    //         return ResponseEntity.status(401).build();
    //     }

    //     User owner = userService.findUserByEmail(email);
    //     auction.setOwner(owner);

    //     Auction createdAuction = auctionService.createAuction(auction);
    //     return ResponseEntity.status(201).body(createdAuction);
    // }


    @PostMapping
    public ResponseEntity<AuctionDTO> createAuction(@RequestBody Map<String, Object> requestBody, @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        Long productId = Long.valueOf(requestBody.get("product_id").toString());
        Product product = productService.getProductById(productId);
        if (product == null) {
            return ResponseEntity.status(404).build();
        }

        Auction auction = new Auction();
        auction.setProduct(product);
        auction.setStartingPrice(Double.valueOf(requestBody.get("startingPrice").toString()));
        auction.setIsActive((Boolean) requestBody.get("isActive"));

        User owner = userService.findUserByEmail(email);
        auction.setOwner(owner);

        Auction createdAuction = auctionService.createAuction(auction);
        AuctionDTO createAuctionDTO = convertToDTO(createdAuction);
        return ResponseEntity.status(201).body(createAuctionDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionDTO> getAuctionDetails(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        Optional<Auction> auction = auctionService.getAuctionById(id);
        if (auction.isPresent()) {
            AuctionDTO auctionDTO = convertToDTO(auction.get());
            return ResponseEntity.ok(auctionDTO);
        }
        return ResponseEntity.status(404).build();
    }

    @PostMapping("/{id}/bids")
    public ResponseEntity<String> placeBid(@PathVariable Long id, @RequestBody Bid bid, @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        User bidder = userService.findUserByEmail(email);
        bid.setBidder(bidder);

        String response = bidService.placeBid(id, bid);
        if (response.equals("Bid placed successfully!")) {
            return ResponseEntity.status(201).body(response);
        } else {
            return ResponseEntity.status(400).body(response);
        }
    }

    @GetMapping("/{id}/bids")
    public ResponseEntity<List<BidDTO>> listBids(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        List<Bid> bids = bidService.getBidsForAuction(id);
        List<BidDTO> bidDTOs = bids.stream().map(this::convertToDTO).toList();
        return ResponseEntity.ok(bidDTOs);
    }


    @PutMapping("/{id}/close")
    public ResponseEntity<Void> closeAuction(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        auctionService.closeAuction(id);
        return ResponseEntity.noContent().build();
    }

    private String getEmailFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }

        String jwtToken = token.substring(7);
        if (tokenService.validateToken(jwtToken)) {
            return tokenService.getEmailFromToken(jwtToken);
        }
        return null;
    }

    private AuctionDTO convertToDTO(Auction auction) {
        AuctionDTO auctionDTO = new AuctionDTO();
        auctionDTO.setId(auction.getId());
        auctionDTO.setProductId(auction.getProduct().getId());
        auctionDTO.setOwnerId(auction.getOwner().getId());
        auctionDTO.setStartingPrice(auction.getStartingPrice());
        auctionDTO.setHighestBid(auction.getHighestBid());

        if (auction.getHighestBidder() != null) {
            auctionDTO.setHighestBidderId(auction.getHighestBidder().getId());
        } else {
            auctionDTO.setHighestBidderId(null);
        }

        auctionDTO.setStartTime(auction.getStartTime());
        auctionDTO.setEndTime(auction.getEndTime());
        auctionDTO.setIsActive(auction.getIsActive());
        return auctionDTO;
    }

    private BidDTO convertToDTO(Bid bid) {
        BidDTO bidDTO = new BidDTO();
        bidDTO.setId(bid.getId());

        if (bid.getAuction() != null) {
            bidDTO.setAuctionId(bid.getAuction().getId());
        } else {
            bidDTO.setAuctionId(null);
        }

        if (bid.getBidder() != null) {
            bidDTO.setBidderId(bid.getBidder().getId());
        } else {
            bidDTO.setBidderId(null);
        }

        bidDTO.setAmount(bid.getAmount());
        bidDTO.setBidTime(bid.getBidTime());
        return bidDTO;
    }
}
