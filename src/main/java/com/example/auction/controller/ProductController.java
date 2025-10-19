package com.example.auction.controller;

import com.example.auction.common.response.ResponseHandler;
import com.example.auction.dto.ProductDTO;
import com.example.auction.dto.ProductImageDTO;
import com.example.auction.model.Product;
import com.example.auction.model.User;
import com.example.auction.service.ProductService;
import com.example.auction.validation.ProductImageValidator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductImageValidator productImageValidator;

    public ProductController(ProductService productService, ProductImageValidator productImageValidator) {
        this.productService = productService;
        this.productImageValidator = productImageValidator;
    }

    @GetMapping
    public ResponseEntity<?> listProductsByOwner(HttpServletRequest request) {
        User owner = (User) request.getAttribute("authenticatedUser");
        List<Product> products = productService.getProductsByOwner(owner);
        List<ProductDTO> productDTOs = products.stream().map(this::convertToDTO).toList();
        return ResponseHandler.success(productDTOs, null, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO, HttpServletRequest request) {
        User user = (User) request.getAttribute("authenticatedUser");
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setOwner(user);

        String resultMessage = productService.saveProduct(product);
        return ResponseHandler.success(null, resultMessage, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO, HttpServletRequest request) {
        User user = (User) request.getAttribute("authenticatedUser");
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());

        String resultMessage = productService.updateProduct(id, product, user.getEmail());
        return ResponseHandler.success(null, resultMessage, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, HttpServletRequest request) {
        User user = (User) request.getAttribute("authenticatedUser");
        String resultMessage = productService.deleteProduct(id, user.getEmail());
        return ResponseHandler.success(null, resultMessage, HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<?> uploadImages(@PathVariable Long id, HttpServletRequest request, @RequestParam("images") MultipartFile[] files) {
        User user = (User) request.getAttribute("authenticatedUser");
        try {
            productImageValidator.validate(files);
        } catch (IllegalArgumentException e) {
            return ResponseHandler.error(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        String resultMessage = productService.addImagesToProduct(id, files, user.getEmail());
        return ResponseHandler.success(null, resultMessage, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/images/{imageId}")
    public ResponseEntity<?> deleteProductImage(@PathVariable Long id, @PathVariable Long imageId, HttpServletRequest request) {
        User user = (User) request.getAttribute("authenticatedUser");
        String resultMessage = productService.deleteImageFromProduct(id, imageId, user.getEmail());
        return ResponseHandler.success(null, resultMessage, HttpStatus.NO_CONTENT);
    }

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
