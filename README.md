# 🧩 Auction Backend

## 📝 Proje Hakkında
**Auction Backend**, açık artırma (auction) mantığıyla çalışan bir **RESTful API** uygulamasıdır.  
Kullanıcılar kayıt olabilir, ürün ekleyebilir, açık arttırma oluşturabilir, teklif verebilir ve JWT ile güvenli şekilde API istekleri yapabilirler.  

---

## ⚙️ Teknolojiler
- **Java 21**
- **Spring Boot 3.4.3**
  - Spring Web  
  - Spring Data JPA  
  - Spring Security (JWT)  
  - Spring Validation  
  - Spring Boot Actuator  
- **Microsoft SQL Server (mcr.microsoft.com/mssql/server:2022-latest)**
- **Stripe API** (ödeme işlemleri için)
- **BCrypt** (şifreleme)
- **Docker & Docker Compose**
- **Maven**

---

## 🚀 Kurulum ve Çalıştırma

### 1️⃣ Gereksinimler
Projeyi çalıştırmak için aşağıdakilerin sisteminizde kurulu olması gerekir:
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)
- (İsteğe bağlı) IDE: IntelliJ IDEA, VS Code veya Eclipse

---

### 2️⃣ Depoyu Klonla
```bash
git clone https://github.com/EnginKisin/auction-backend.git
cd auction-backend
```

---

### 3️⃣ application.properties Dosyasını Oluştur ve Düzenle
Projede `src/main/resources` dizinine bir `application.properties` dosyası oluştur. İçeriğini aşağıdaki gibi doldur:
```
spring.application.name=auction
spring.datasource.url=jdbc:sqlserver://sqlserver:1433;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=YOUR_DB_PASSWORD
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
stripe.api.key=YOUR_STRIPE_API_KEY
jwt.secret-key=YOUR_SECRET_KEY
jwt.access-token.expiration-ms=3600000
jwt.refresh-token-expiration-ms=604800000
```

---

### 4️⃣ docker-compose.yml Dosyasını Düzenle
```
services:
  sqlserver:
    image: mcr.microsoft.com/mssql/server:2022-latest
    container_name: sqlserver
    environment:
      - ACCEPT_EULA=Y
      - SA_PASSWORD=YOUR_DB_PASSWORD
    ports:
      - "1434:1433"
    healthcheck:
      test: ["CMD-SHELL", "/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P YOUR_DB_PASSWORD -Q 'SELECT 1' || exit 0"]
      interval: 10s
      timeout: 5s
      retries: 10
      start_period: 15s

  auction-app:
    build: .
    container_name: auction-app
    ports:
      - "8080:8080"
    depends_on:
      sqlserver:
        condition: service_healthy
    environment:
      SPRING_DATASOURCE_URL: jdbc:sqlserver://sqlserver:1433;encrypt=true;trustServerCertificate=true
      SPRING_DATASOURCE_USERNAME: sa
      SPRING_DATASOURCE_PASSWORD: YOUR_DB_PASSWORD
```

---

## Base URL: ## http://localhost:8080/api

## 🔐 Authentication Endpoints (`/auth`)

### 1️⃣ POST `/auth/register`
Yeni bir kullanıcı kaydı oluşturur.

**Query Param:**  
- `paymentMethodId` — Ödeme yöntemi ID’si

**Body (JSON):**
```json
{
  "email": "user@example.com",
  "password": "123456",
  "name": "John Doe"
}


Response:

{
  "success": true,
  "message": "User registered successfully"
}

2️⃣ POST /auth/login

Kullanıcı girişi yapar, JWT access ve refresh token döner.

Body (JSON):

{
  "email": "user@example.com",
  "password": "123456"
}


Response:

{
  "success": true,
  "message": "Token generated successfully",
  "data": {
    "accessToken": "<jwt_token>",
    "refreshToken": "<refresh_token>"
  }
}

3️⃣ POST /auth/refresh-token

Yeni bir access token oluşturur.

Query Param:

refreshToken=<refresh_token>


Response:

{
  "data": {
    "accessToken": "<new_access_token>"
  },
  "message": "Access token refreshed successfully"
}

4️⃣ POST /auth/logout

Aktif token’ı kara listeye alır ve kullanıcıyı çıkış yaptırır.

Header:

Authorization: Bearer <access_token>


Response:

{
  "message": "User logged out successfully"
}

📦 Product Endpoints (/products)
5️⃣ GET /products

Giriş yapmış kullanıcının sahip olduğu ürünleri listeler.

Header:

Authorization: Bearer <access_token>


Response:

{
  "data": [
    {
      "id": 1,
      "name": "Laptop",
      "description": "Gaming laptop",
      "price": 1200.0,
      "ownerId": 2,
      "images": []
    }
  ]
}

6️⃣ POST /products

Yeni ürün oluşturur.

Header:

Authorization: Bearer <access_token>
Content-Type: application/json


Body:

{
  "name": "Smartphone",
  "description": "New model phone",
  "price": 850.0
}


Response: 201 Created

7️⃣ PUT /products/{id}

Belirtilen ürünü günceller.

Body:

{
  "name": "Updated Product",
  "description": "Updated desc",
  "price": 950.0
}


Response: 200 OK

8️⃣ DELETE /products/{id}

Bir ürünü siler.

Response: 204 No Content

9️⃣ POST /products/{id}/images

Ürüne bir veya birden fazla resim yükler.

Header:

Authorization: Bearer <access_token>
Content-Type: multipart/form-data


Form Data:

images: [file1, file2, file3]


Response: 201 Created

🔟 DELETE /products/{id}/images/{imageId}

Belirtilen ürüne ait bir resmi siler.

Response: 204 No Content

🏷️ Auction Endpoints (/auctions)
1️⃣1️⃣ GET /auctions/public/active

Herkese açık tüm aktif açık artırmaları listeler.

Response:

{
  "data": [
    {
      "id": 5,
      "product": {
        "id": 2,
        "name": "Camera",
        "price": 500.0
      },
      "startingPrice": 300.0,
      "highestBid": 450.0,
      "isActive": true
    }
  ]
}

1️⃣2️⃣ GET /auctions/active

Sistemdeki tüm aktif açık artırmaları döner (authenticated kullanıcılar için).

1️⃣3️⃣ GET /auctions/my

Giriş yapmış kullanıcının oluşturduğu açık artırmaları listeler.

Header:

Authorization: Bearer <access_token>


Response:

{
  "data": [
    {
      "id": 1,
      "productId": 3,
      "startingPrice": 150.0,
      "highestBid": 220.0
    }
  ]
}

1️⃣4️⃣ POST /auctions

Yeni bir açık artırma oluşturur.

Header:

Authorization: Bearer <access_token>
Content-Type: application/json


Body:

{
  "productId": 3,
  "startingPrice": 100.0,
  "durationTypeId": 2
}


Response: 201 Created

1️⃣5️⃣ POST /auctions/{id}/bids

Belirtilen açık artırmaya teklif (bid) verir.

Header:

Authorization: Bearer <access_token>


Body:

{
  "amount": 200.0
}


Response: 201 Created

1️⃣6️⃣ GET /auctions/public/{id}

Public erişilebilir bir açık artırmanın detaylarını getirir.

1️⃣7️⃣ PUT /auctions/{id}/close

Açık artırmayı manuel olarak kapatır.

Response: 204 No Content

🧾 Ortak HTTP Header Örnekleri
Authorization: Bearer <access_token>
Content-Type: application/json
Accept: application/json

⚙️ HTTP Status Codes
Status Code	Anlamı
200 OK	İstek başarıyla işlendi
201 Created	Kaynak başarıyla oluşturuldu
204 No Content	Kaynak başarıyla silindi/güncellendi
400 Bad Request	Geçersiz istek parametreleri
401 Unauthorized	Geçersiz veya eksik token
404 Not Found	Kaynak bulunamadı
500 Internal Server Error	Sunucu hatası
🧱 Notlar

Tüm /api/products ve /api/auctions endpoint'leri JWT Authorization header ister.

/api/auctions/public/* endpoint’leri ise giriş yapılmadan erişilebilir.

Response formatı ResponseHandler yapısına göre standartlaştırılmıştır:

{
  "success": true,
  "message": "optional_message",
  "data": {}
}
---

### 5️⃣ Docker Compose ile Projeyi Başlat
```bash
docker-compose up -d
```
