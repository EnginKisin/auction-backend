package com.example.auction.controller;

import com.example.auction.common.message.MessageCode;
import com.example.auction.common.response.ResponseHandler;
import com.example.auction.dto.ProductDTO;
import com.example.auction.dto.ProductImageDTO;
import com.example.auction.model.Product;
import com.example.auction.model.ProductImage;
import com.example.auction.model.User;
import com.example.auction.service.ProductService;
import com.example.auction.service.TokenService;
import com.example.auction.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final UserService userService;
    private final TokenService tokenService;

    public ProductController(ProductService productService, UserService userService, TokenService tokenService) {
        this.productService = productService;
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @GetMapping
    public ResponseEntity<?> listProductsByOwner(@RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        if (email == null) {
            return ResponseHandler.error(MessageCode.INVALID_TOKEN.getMessage(), HttpStatus.UNAUTHORIZED);
        }

        User owner = userService.findUserByEmail(email);
        List<Product> products = productService.getProductsByOwner(owner);
        List<ProductDTO> productDTOs = products.stream().map(this::convertToDTO).toList();
        return ResponseHandler.success(productDTOs, null, HttpStatus.OK);
    }

    // @PostMapping
    // public ResponseEntity<?> createProduct(@RequestBody Product product, @RequestHeader("Authorization") String token) {
    //     String email = getEmailFromToken(token);
    //     if (email == null) {
    //         return ResponseHandler.error(MessageCode.INVALID_TOKEN.getMessage(), HttpStatus.UNAUTHORIZED);
    //     }

    //     User owner = userService.findUserByEmail(email);
    //     product.setOwner(owner);

    //     String resultMessage = productService.saveProduct(product);
    //     return ResponseHandler.success(null, resultMessage, HttpStatus.CREATED);
    // }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
        @RequestParam("name") String name,
        @RequestParam("description") String description,
        @RequestParam("price") Double price,
        @RequestPart(value = "images", required = false) List<MultipartFile> images,
        @RequestHeader("Authorization") String token) throws IOException {

        String email = getEmailFromToken(token);
        if (email == null) {
            return ResponseHandler.error(MessageCode.INVALID_TOKEN.getMessage(), HttpStatus.UNAUTHORIZED);
        }

        User owner = userService.findUserByEmail(email);

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setOwner(owner);

        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                ProductImage pi = new ProductImage();
                pi.setContentType(image.getContentType());
                pi.setData(image.getBytes());
                pi.setProduct(product);
                product.getImages().add(pi);
            }
        }

        String resultMessage = productService.saveProduct(product);
        return ResponseHandler.success(null, resultMessage, HttpStatus.CREATED);
    }
    

    // @PutMapping("/{id}")
    // public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product, @RequestHeader("Authorization") String token) {
    //     String email = getEmailFromToken(token);
    //     if (email == null) {
    //         return ResponseHandler.error(MessageCode.INVALID_TOKEN.getMessage(), HttpStatus.UNAUTHORIZED);
    //     }

    //     User owner = userService.findUserByEmail(email);
    //     product.setId(id);
    //     product.setOwner(owner);
    //     String resultMessage = productService.saveProduct(product);
    //     return ResponseHandler.success(null, resultMessage, HttpStatus.CREATED);
    // }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product, @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        if (email == null) {
            return ResponseHandler.error(MessageCode.INVALID_TOKEN.getMessage(), HttpStatus.UNAUTHORIZED);
        }

        String resultMessage = productService.updateProduct(id, product, email);
        return ResponseHandler.success(null, resultMessage, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        if (email == null) {
            return ResponseHandler.error(MessageCode.INVALID_TOKEN.getMessage(), HttpStatus.UNAUTHORIZED);
        }

        String resultMessage = productService.deleteProduct(id, email);
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

    // private ProductDTO convertToDTO(Product product) {
    //     ProductDTO productDTO = new ProductDTO();
    //     productDTO.setId(product.getId());
    //     productDTO.setName(product.getName());
    //     productDTO.setDescription(product.getDescription());
    //     productDTO.setPrice(product.getPrice());
    //     productDTO.setOwnerId(product.getOwner().getId());
    //     return productDTO;
    // }


    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setOwnerId(product.getOwner().getId());

        List<ProductImageDTO> imageDTOs = product.getImages().stream().map(image -> {
            ProductImageDTO imgDTO = new ProductImageDTO();
            imgDTO.setId(image.getId());
            imgDTO.setContentType(image.getContentType());
            imgDTO.setBase64Data(Base64.getEncoder().encodeToString(image.getData()));
            return imgDTO;
        }).toList();

        dto.setImages(imageDTOs);

        return dto;
    }
}
