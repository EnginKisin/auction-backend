package com.example.auction.controller;

import com.example.auction.dto.ProductDTO;
import com.example.auction.model.Product;
import com.example.auction.model.User;
import com.example.auction.service.ProductService;
import com.example.auction.service.TokenService;
import com.example.auction.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<ProductDTO>> listProducts(@RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        User owner = userService.findUserByEmail(email);
        List<Product> products = productService.getProductsByOwner(owner);
        List<ProductDTO> productDTOs = products.stream().map(this::convertToDTO).toList();
        return ResponseEntity.ok(productDTOs);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody Product product, @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        User owner = userService.findUserByEmail(email);
        product.setOwner(owner);
        Product savedProduct = productService.saveProduct(product);
        ProductDTO savedProductDTO = convertToDTO(savedProduct);
        return ResponseEntity.status(201).body(savedProductDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody Product product, @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        User owner = userService.findUserByEmail(email);
        product.setId(id);
        product.setOwner(owner);
        Product updatedProduct = productService.saveProduct(product);
        ProductDTO updatedProductDTO = convertToDTO(updatedProduct);
        return ResponseEntity.ok(updatedProductDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String email = getEmailFromToken(token);
        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            Product product = productService.getProductById(id);

            if (product.getOwner().getEmail().equals(email)) {
                productService.deleteProduct(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(403).build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build();
        }
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

    private ProductDTO convertToDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setOwnerId(product.getOwner().getId());
        return productDTO;
    }
}
