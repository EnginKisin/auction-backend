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

### 5️⃣ Docker Compose ile Projeyi Başlat
```bash
docker-compose up -d
```

---

### 6️⃣ Frontend Bilgisi

Bu proje yalnızca backend (REST API) kısmını içerir.
Uygulamanın frontend kısmı, ayrı bir GitHub reposunda yer almaktadır.

🔗 Frontend Repository:
👉 https://github.com/EnginKisin/auction-frontend

Frontend uygulaması React + Vite kullanılarak geliştirilmiştir ve bu backend API’si ile entegre çalışır.
Varsayılan olarak http://localhost:5173 adresinde çalışır.
Frontend’in nasıl başlatılacağına dair kurulum ve çalıştırma adımları, ilgili frontend reposunun README dosyasında açıklanmıştır.
