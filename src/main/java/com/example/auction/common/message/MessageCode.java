package com.example.auction.common.message;

public enum MessageCode {
    // Kullanıcı,token,login,register Mesajları
    USER_NOT_FOUND("Bu e-posta adresine sahip bir kullanıcı bulunamadı."),
    USER_REGISTRATION_SUCCESS("Kullanıcı başarıyla kaydedildi."),
    USER_LOGOUT_SUCCESS("Çıkış başarılı."),

    INVALID_TOKEN("Geçersiz veya süresi dolmuş token."),
    VALID_TOKEN("Token geçerli."),
    ACCESS_TOKEN_REFRESHED("Token yenilendi."),
    TOKEN_SUCCESS("Token başarıyla alındı."),
    TOKEN_EXPIRED_OR_INVALID("Token geçersiz veya süresi dolmuş."),
    EMAIL_NOT_FOUND_IN_TOKEN("Token'dan e-posta alınamadı."),

    INVALID_EMAIL("E-posta adresi geçersiz."),
    INCORRECT_PASSWORD("Şifre yanlış."),
    EMAIL_ALREADY_REGISTERED("Bu e-posta adresi zaten kayıtlı."),

    // Stripe Mesajları
    STRIPE_ERROR("Stripe işlemi başarısız oldu."),
    
    // Açık Artırma Mesajları
    AUCTION_NOT_FOUND("Açık artırma bulunamadı."),
    AUCTION_ALREADY_EXISTS("Bu ürüne ait zaten bir açık artırma mevcut."),
    AUCTION_INACTIVE("Açık artırma aktif değil."),
    AUCTION_CREATED_SUCCESS("Açık artırma başarıyla oluşturuldu."),
    AUCTION_CLOSED_SUCCESS("Açık artırma başarıyla kapatıldı."),
    INVALID_AUCTION_ID("Geçersiz açık artırma id."),

    // Teklif Mesajları
    BID_SUCCESS("Teklif başarıyla yapıldı."),
    BID_TOO_LOW("Teklif mevcut en yüksek tekliften büyük olmalıdır."),
    BID_NOT_FOUND("Açık artırmada teklif bulunamadı."),
    HIGHEST_BID_NOT_FOUND("Açık artırmada en yüksek teklif bulunamadı."),
    BIDS_LISTED("Teklifler listelendi."),
    BID_OWN_AUCTION_NOT_ALLOWED("Kendi oluşturduğunuz açık artırmaya teklif veremezsiniz."),

    // Ürün Mesajları
    PRODUCT_SUCCESS("Ürün başarıyla kaydedildi."),
    PRODUCT_NOT_FOUND("Ürün bulunamadı."),

    PRODUCT_DELETED("Ürün başarıyla silindi."),
    PRODUCT_DELETE_UNAUTHORIZED("Bu ürünü silme yetkiniz yok."),
    PRODUCT_DELETE_ACTIVE_AUCTION_ALREADY_EXISTS("Aktif açık artırması olan ürünler silinemez."),
    PRODUCT_DELETE_AUCTION_ALREADY_EXISTS("Aktif açık artırması olan ürünler silinemez."),

    PRODUCT_UPDATED("Ürün başarıyla düzenlendi."),
    PRODUCT_UPDATE_UNAUTHORIZED("Bu ürünü düzenleme yetkiniz yok."),
    PRODUCT_UPDATE_AUCTION_ALREADY_EXISTS("Açık artırması olan ürünler düzenlenemez."),

    PRODUCT_FILE_PROCESSING_SUCCESS("Ürün görselleri başarıyla işlendi."),
    PRODUCT_FILE_PROCESSING_ERROR("Ürün görselleri işlenirken bir hata oluştu."),
    PRODUCT_IMAGE_NOT_FOUND("Ürün görseli bulunamadı."),
    PRODUCT_IMAGE_DELETE_UNAUTHORIZED("Bu ürünü silme yetkiniz yok."),
    PRODUCT_IMAGE_DELETED("Ürün görseli silindi."),
    IMAGE_NOT_BELONG_TO_PRODUCT("Görsel bu ürüne ait değil."),
    PRODUCT_IMAGE_REQUIRED("En az bir görsel yüklenmelidir."),
    PRODUCT_UPDATE_IMAGE_AUCTION_ALREADY_EXISTS("Açık artırması olan ürünün görselleri düzenlenemez."),
    PRODUCT_DELETE_IMAGE_AUCTION_ALREADY_EXISTS("Açık artırması olan ürünün görselleri silinemez."),

    // Upload Mesajları
    EMPTY_FILE_NOT_UPLOAD("Boş dosya yüklenemez."),
    ONLY_IMAGE_FORMAT_UPLOAD("Sadece resim formatları yüklenebilir."),
    FILE_SIZE_NOT_EXCEED_5MB("Dosya boyutu 5mb'ı geçmemelidir."),
    
    // Süre Tipi Mesajları
    DURATION_TYPE_NOT_FOUND("Süre Tipi bulunamadı!");

    private final String message;

    MessageCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String formatMessage(Object... args) {
        return String.format(message, args);
    }
}

