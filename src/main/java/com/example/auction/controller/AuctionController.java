package com.example.auction.controller;

import java.util.Base64;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auction.common.response.ResponseHandler;
import com.example.auction.dto.AuctionDTO;
import com.example.auction.dto.BidDTO;
import com.example.auction.dto.ProductDTO;
import com.example.auction.dto.ProductImageDTO;
import com.example.auction.model.Auction;
import com.example.auction.model.Bid;
import com.example.auction.model.Product;
import com.example.auction.model.User;
import com.example.auction.service.AuctionService;
import com.example.auction.service.BidService;
import com.example.auction.service.ProductService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;
    private final BidService bidService;
    private final ProductService productService;

    public AuctionController(AuctionService auctionService, BidService bidService, ProductService productService) {
        this.auctionService = auctionService;
        this.bidService = bidService;
        this.productService = productService;
    }


    @GetMapping("/public/active")
    public ResponseEntity<?> listAllPublicActiveAuctions() {
        List<Auction> auctions = auctionService.getAllActiveAuctions();
        List<AuctionDTO> auctionDTOs = auctions.stream().map(this::convertToDTO).toList();
        return ResponseHandler.success(auctionDTOs, null, HttpStatus.OK);
    }

    @GetMapping("/active")
    public ResponseEntity<?> listAllActiveAuctions() {
        List<Auction> auctions = auctionService.getAllActiveAuctions();
        List<AuctionDTO> auctionDTOs = auctions.stream().map(this::convertToDTO).toList();
        return ResponseHandler.success(auctionDTOs, null, HttpStatus.OK);
    }

    @GetMapping("/my")
    public ResponseEntity<?> listAuctionsByOwner(HttpServletRequest request) {
        User owner = (User) request.getAttribute("authenticatedUser");
        
        List<Auction> auctions = auctionService.getAuctionsByOwner(owner);
        List<AuctionDTO> auctionDTOs = auctions.stream().map(this::convertToDTO).toList();
        return ResponseHandler.success(auctionDTOs, null, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<?> createAuction(@Valid @RequestBody AuctionDTO auctionDTO, HttpServletRequest request) {

        User owner = (User) request.getAttribute("authenticatedUser");
        Product product = productService.getProductById(auctionDTO.getProductId());

        Auction auction = new Auction();
        auction.setProduct(product);
        auction.setStartingPrice(auctionDTO.getStartingPrice());
        auction.setOwner(owner);

        String resultMessage = auctionService.createAuction(auction, auctionDTO.getDurationTypeId());
        return ResponseHandler.success(null, resultMessage, HttpStatus.CREATED);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<?> getPublicAuctionDetails(@PathVariable Long id) {
        Auction auction = auctionService.getAuctionById(id);
        AuctionDTO auctionDTO = convertToDTO(auction);
        return ResponseHandler.success(auctionDTO, null, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAuctionDetails(@PathVariable Long id) {
        Auction auction = auctionService.getAuctionById(id);
        AuctionDTO auctionDTO = convertToDTO(auction);
        return ResponseHandler.success(auctionDTO, null, HttpStatus.OK);
    }

    @PostMapping("/{id}/bids")
    public ResponseEntity<?> placeBid(@PathVariable Long id, @Valid @RequestBody BidDTO bidDTO, HttpServletRequest request) {

        User bidder = (User) request.getAttribute("authenticatedUser");

        Bid bid = new Bid();
        bid.setAmount(bidDTO.getAmount());
        bid.setBidder(bidder);

        String resultMessage = bidService.placeBid(id, bid);
        return ResponseHandler.success(null, resultMessage, HttpStatus.CREATED);
    }


    @PutMapping("/{id}/close")
    public ResponseEntity<?> closeAuction(@PathVariable Long id) {
        String resultMessage = auctionService.closeAuction(id);
        return ResponseHandler.success(null, resultMessage, HttpStatus.NO_CONTENT);
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
        }

        auctionDTO.setDurationTypeId(auction.getDurationType().getId());
        auctionDTO.setStartTime(auction.getStartTime());
        auctionDTO.setEndTime(auction.getEndTime());
        auctionDTO.setIsActive(auction.getIsActive());

        Product product = auction.getProduct();
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setOwnerId(product.getOwner().getId());

        List<ProductImageDTO> imageDTOs = product.getImages().stream().map(image -> {
            ProductImageDTO dto = new ProductImageDTO();
            dto.setId(image.getId());
            dto.setContentType(image.getContentType());
            dto.setBase64Data(Base64.getEncoder().encodeToString(image.getData()));
            return dto;
        }).toList();

        productDTO.setImages(imageDTOs);

        auctionDTO.setProduct(productDTO);

        return auctionDTO;
    }
}
