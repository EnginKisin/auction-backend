package com.example.auction.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.auction.common.response.ResponseHandler;
import com.example.auction.dto.AuctionDTO;
//import com.example.auction.dto.BidDTO;
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

    public AuctionController(AuctionService auctionService, BidService bidService, UserService userService,
            TokenService tokenService, ProductService productService) {
        this.auctionService = auctionService;
        this.bidService = bidService;
        this.userService = userService;
        this.tokenService = tokenService;
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<?> listAuctions(@RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        if (email == null) {
            return ResponseHandler.error("Geçersiz token.", HttpStatus.UNAUTHORIZED);
        }

        List<Auction> auctions = auctionService.getActiveAuctions();
        List<AuctionDTO> auctionDTOs = auctions.stream().map(this::convertToDTO).toList();
        return ResponseHandler.success(auctionDTOs, null, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createAuction(@RequestBody Map<String, Object> requestBody,
            @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        if (email == null) {
            return ResponseHandler.error("Geçersiz token.", HttpStatus.UNAUTHORIZED);
        }

        Long productId = Long.valueOf(requestBody.get("product_id").toString());
        Product product = productService.getProductById(productId);

        Auction auction = new Auction();
        auction.setProduct(product);
        auction.setStartingPrice(Double.valueOf(requestBody.get("startingPrice").toString()));
        Long durationTypeId = Long.valueOf(requestBody.get("durationTypeId").toString());

        User owner = userService.findUserByEmail(email);
        auction.setOwner(owner);

        String resultMessage = auctionService.createAuction(auction, durationTypeId);
        return ResponseHandler.success(null, resultMessage, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAuctionDetails(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        if (email == null) {
            return ResponseHandler.error("Geçersiz token.", HttpStatus.UNAUTHORIZED);
        }

        Auction auction = auctionService.getAuctionById(id);
        AuctionDTO auctionDTO = convertToDTO(auction);
        return ResponseHandler.success(auctionDTO, null, HttpStatus.OK);
    }

    @PostMapping("/{id}/bids")
    public ResponseEntity<?> placeBid(@PathVariable Long id, @RequestBody Bid bid,
            @RequestHeader("Authorization") String token) {

        String email = getEmailFromToken(token);
        if (email == null) {
            return ResponseHandler.error("Geçersiz token.", HttpStatus.UNAUTHORIZED);
        }

        User bidder = userService.findUserByEmail(email);
        bid.setBidder(bidder);

        String resultMessage = bidService.placeBid(id, bid);
        return ResponseHandler.success(null, resultMessage, HttpStatus.CREATED);
    }

    // @GetMapping("/{id}/bids")
    // public ResponseEntity<?> listBids(@PathVariable Long id,
    // @RequestHeader("Authorization") String token) {
    // String email = getEmailFromToken(token);
    // if (email == null) {
    // return ResponseHandler.error("Geçersiz token.", HttpStatus.UNAUTHORIZED);
    // }

    // List<Bid> bids = bidService.getBidsForAuction(id);
    // List<BidDTO> bidDTOs = bids.stream().map(this::convertToDTO).toList();
    // return ResponseHandler.success(bidDTOs, "Teklifler listelendi.",
    // HttpStatus.OK);
    // }

    @PutMapping("/{id}/close")
    public ResponseEntity<?> closeAuction(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        if (email == null) {
            return ResponseHandler.error("Geçersiz token.", HttpStatus.UNAUTHORIZED);
        }

        String resultMessage = auctionService.closeAuction(id);
        return ResponseHandler.success(null, resultMessage, HttpStatus.NO_CONTENT);
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

        auctionDTO.setDurationTypeId(auction.getDurationType().getId());
        auctionDTO.setStartTime(auction.getStartTime());
        auctionDTO.setEndTime(auction.getEndTime());
        auctionDTO.setIsActive(auction.getIsActive());
        return auctionDTO;
    }

    // private BidDTO convertToDTO(Bid bid) {
    //     BidDTO bidDTO = new BidDTO();
    //     bidDTO.setId(bid.getId());

    //     if (bid.getAuction() != null) {
    //         bidDTO.setAuctionId(bid.getAuction().getId());
    //     } else {
    //         bidDTO.setAuctionId(null);
    //     }

    //     if (bid.getBidder() != null) {
    //         bidDTO.setBidderId(bid.getBidder().getId());
    //     } else {
    //         bidDTO.setBidderId(null);
    //     }

    //     bidDTO.setAmount(bid.getAmount());
    //     bidDTO.setBidTime(bid.getBidTime());
    //     return bidDTO;
    // }
}
