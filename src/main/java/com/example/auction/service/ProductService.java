package com.example.auction.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.auction.common.exception.FileProcessingException;
import com.example.auction.common.exception.NotFoundException;
import com.example.auction.common.message.MessageCode;
import com.example.auction.model.Product;
import com.example.auction.model.ProductImage;
import com.example.auction.model.User;
import com.example.auction.repository.ProductImageRepository;
import com.example.auction.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    public List<Product> getProductsByOwner(User owner) {
        return productRepository.findByOwner(owner);
    }

    // public String saveProduct(Product product) {
    //     productRepository.save(product);
    //     return MessageCode.PRODUCT_SUCCESS.getMessage();
    // }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public String updateProduct(Long id, Product product, String email) {
        Product existingProduct = getProductById(id);

        if (!existingProduct.getOwner().getEmail().equals(email)) {
            throw new IllegalStateException(MessageCode.PRODUCT_UPDATE_UNAUTHORIZED.getMessage());
        }

        if (existingProduct.getAuction() != null) {
            throw new IllegalStateException(MessageCode.PRODUCT_UPDATE_AUCTION_ALREADY_EXISTS.getMessage());
        }

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());

        productRepository.save(existingProduct);
        return MessageCode.PRODUCT_UPDATED.getMessage();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MessageCode.PRODUCT_NOT_FOUND.getMessage()));
    }

    public String deleteProduct(Long id, String email) {
        Product existingProduct = getProductById(id);
    
        if (!existingProduct.getOwner().getEmail().equals(email)) {
            throw new IllegalStateException(MessageCode.PRODUCT_DELETE_UNAUTHORIZED.getMessage());
        }

        if (existingProduct.getAuction() != null && existingProduct.getAuction().getIsActive()) {
            throw new IllegalStateException(MessageCode.PRODUCT_DELETE_ACTIVE_AUCTION_ALREADY_EXISTS.getMessage());
        }
    
        productRepository.delete(existingProduct);
        return MessageCode.PRODUCT_DELETED.getMessage();
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }


    //ProductImage process
    public String addImagesToProduct(Long id, MultipartFile[] files, String email) {
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException(MessageCode.PRODUCT_IMAGE_REQUIRED.getMessage());
        }

        Product existingProduct = getProductById(id);

        if (!existingProduct.getOwner().getEmail().equals(email)) {
            throw new IllegalStateException(MessageCode.PRODUCT_UPDATE_UNAUTHORIZED.getMessage());
        }

        if (existingProduct.getAuction() != null) {
            throw new IllegalStateException(MessageCode.PRODUCT_UPDATE_IMAGE_AUCTION_ALREADY_EXISTS.getMessage());
        }

        for (MultipartFile file : files) {
            try {
                ProductImage image = new ProductImage();
                image.setProduct(existingProduct);
                image.setContentType(file.getContentType());
                image.setData(file.getBytes());
                productImageRepository.save(image);
            } catch (IOException e) {
                throw new FileProcessingException(MessageCode.PRODUCT_FILE_PROCESSING_ERROR.getMessage());
            }
        }

        return MessageCode.PRODUCT_FILE_PROCESSING_SUCCESS.getMessage();
    }


    public String deleteImageFromProduct(Long id, Long imageId, String email) {
        Product existingProduct = getProductById(id);
    
        if (!existingProduct.getOwner().getEmail().equals(email)) {
            throw new IllegalStateException(MessageCode.PRODUCT_DELETE_UNAUTHORIZED.getMessage());
        }

        if (existingProduct.getAuction() != null) {
            throw new IllegalStateException(MessageCode.PRODUCT_DELETE_AUCTION_ALREADY_EXISTS.getMessage());
        }

        ProductImage image = productImageRepository.findById(imageId)
                    .orElseThrow(() -> new RuntimeException(MessageCode.PRODUCT_IMAGE_NOT_FOUND.getMessage()));

        if (!image.getProduct().getId().equals(existingProduct.getId())) {
            throw new IllegalStateException(MessageCode.IMAGE_NOT_BELONG_TO_PRODUCT.getMessage());
        }
        productImageRepository.delete(image);

        return MessageCode.PRODUCT_IMAGE_DELETED.getMessage();
    }
}
