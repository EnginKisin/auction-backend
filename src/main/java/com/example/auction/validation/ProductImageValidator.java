package com.example.auction.validation;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.auction.common.message.MessageCode;

@Component
public class ProductImageValidator {
    private static final long MAX_SIZE = 5 * 1024 * 1024;

    public void validate(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException(MessageCode.PRODUCT_IMAGE_REQUIRED.getMessage());
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new IllegalArgumentException(MessageCode.EMPTY_FILE_NOT_UPLOAD.getMessage());
            }
            if (!file.getContentType().startsWith("image/")) {
                throw new IllegalArgumentException(MessageCode.ONLY_IMAGE_FORMAT_UPLOAD.getMessage());
            }
            if (file.getSize() > MAX_SIZE) {
                throw new IllegalArgumentException(MessageCode.FILE_SIZE_NOT_EXCEED_5MB.getMessage());
            }
        }
    }
}
