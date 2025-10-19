package com.example.auction.validation;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ProductImageValidator {
    private static final long MAX_SIZE = 5 * 1024 * 1024;

    public void validate(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("En az bir resim yüklemelisiniz");
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Boş dosya yüklenemez");
            }
            if (!file.getContentType().startsWith("image/")) {
                throw new IllegalArgumentException("Sadece resim formatları yüklenebilir");
            }
            if (file.getSize() > MAX_SIZE) {
                throw new IllegalArgumentException("Dosya boyutu 5MB'ı aşamaz");
            }
        }
    }
}
