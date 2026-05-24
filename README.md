# 🧩 Auction Backend

## 📝 Proje Hakkında
**Auction Backend**, açık artırma (auction) mantığıyla çalışan bir **RESTful API** uygulamasıdır.  
Kullanıcılar kayıt olabilir, ürün ekleyebilir, açık arttırma oluşturabilir, gerçek zamanlı teklif verebilir ve JWT ile güvenli şekilde API istekleri yapabilirler.  

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
- **WebSocket (STOMP)** (gerçek zamanlı açık artırma güncellemeleri için)
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
spring.datasource.url=jdbc:sqlserver://sqlserver:1433;databaseName=auctionDB;encrypt=true;trustServerCertificate=true
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
      ACCEPT_EULA: Y
      SA_PASSWORD: YOUR_DB_PASSWORD
    ports:
      - "1434:1433"

  auction-app:
    build: .
    container_name: auction-app
    ports:
      - "8080:8080"
    depends_on:
      - sqlserver
    environment:
      SPRING_DATASOURCE_URL: jdbc:sqlserver://sqlserver:1433;databaseName=auctionDB;encrypt=true;trustServerCertificate=true
      SPRING_DATASOURCE_USERNAME: sa
      SPRING_DATASOURCE_PASSWORD: YOUR_DB_PASSWORD
```

---

### 5️⃣ SQL Server Container’ını Başlat ve Veritabanını Manuel Oluştur
```bash
docker-compose up -d sqlserver
```
SQL Server ayağa kalktıktan sonra, Microsoft SQL Server Management Studio (SSMS) veya herhangi bir SQL istemcisi ile aşağıdaki bilgilerle bağlantı kurun:

- Server: localhost,1434
- Username: sa
- Password: YOUR_DB_PASSWORD

Bağlandıktan sonra aşağıdaki SQL komutunu çalıştırarak veritabanını manuel olarak oluşturun:
```bash
CREATE DATABASE auctionDB;
```
---

### 6️⃣ Auction Backend Container’ını Başlat
Veritabanı oluşturulduktan sonra backend uygulamasını başlatın:
```bash
docker-compose up -d auction-app
```
Uygulama başarıyla ayağa kalktıktan sonra API aşağıdaki adresten frontend tarafından erişilebilir olacaktır:
👉 http://localhost:8080

---

### 7️⃣ Frontend Bilgisi
Bu proje yalnızca backend (REST API) kısmını içerir.
Uygulamanın frontend kısmı, ayrı bir GitHub reposunda yer almaktadır.

🔗 Frontend Repository:
👉 https://github.com/EnginKisin/auction-frontend

Frontend uygulaması React + Vite kullanılarak geliştirilmiştir ve bu backend API’si ile entegre çalışır.
Varsayılan olarak http://localhost:5173 adresinde çalışır.
Frontend’in nasıl başlatılacağına dair kurulum ve çalıştırma adımları, ilgili frontend reposunun README dosyasında açıklanmıştır.
