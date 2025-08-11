# 🚀 Sardis - HisseNet B2B Alım Satım Platformu

<div align="center">
  <img src="https://res.cloudinary.com/dtmebvljq/image/upload/v1754924876/kexgebnphuvu2xsaflhp.png" alt="Sardis Logo" width="200"/>
  <br/>
  <em>Gerçek zamanlı veri akışı ile modern, ölçeklenebilir B2B hisse alım satım platformu</em>
</div>

---

## 📋 İçindekiler

- [Genel Bakış](#genel-bakış)
- [Sistem Mimarisi](#sistem-mimarisi)
- [Özellikler](#özellikler)
- [Teknoloji Altyapısı](#teknoloji-altyapısı)
- [Proje Yapısı](#proje-yapısı)
- [API Dokümantasyonu](#api-dokümantasyonu)
- [Kurulum ve Yapılandırma](#kurulum-ve-yapılandırma)
- [Ortam Değişkenleri](#ortam-değişkenleri)
- [Uygulamayı Çalıştırma](#uygulamayı-çalıştırma)
- [Docker Dağıtımı](#docker-dağıtımı)
- [Geliştirme Kuralları](#geliştirme-kuralları)
- [Katkıda Bulunma](#katkıda-bulunma)

---

## 🎯 Genel Bakış

Sardis, gerçek zamanlı hisse fiyat takibi, portföy yönetimi, risk değerlendirmesi ve yapay zeka destekli müşteri hizmetleri sunan kapsamlı bir B2B hisse alım satım platformudur. Sistem üç ana bileşenden oluşur:

- **Backend**: İş mantığı ve gerçek zamanlı veri işleyen Spring Boot mikroservisi
- **Frontend**: Canlı fiyat akışı ile React tabanlı web uygulaması
- **Chatbot**: Google Gemini ve vektör arama kullanan yapay zeka destekli destek sistemi

---

## 🏗️ Sistem Mimarisi

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │    Backend      │    │    Chatbot      │
│   (React)       │◄──►│  (Spring Boot)  │◄──►│   (Django)      │
│   Port: 3000    │    │   Port: 8080    │    │   Port: 8000    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│     Nginx       │    │   SQL Server    │    │ MongoDB Atlas   │
│   (Reverse      │    │   (Veritabanı)  │    │ (Vektör Arama)  │
│    Proxy)       │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │
         │                       │
         ▼                       ▼
┌─────────────────┐    ┌─────────────────┐
│     Redis       │    │  Harici API'lar │
│   (Önbellek)    │    │ (CollectAPI,    │
│                 │    │  Infina)        │
└─────────────────┘    └─────────────────┘
```

---

## ✨ Özellikler

### 🔄 Gerçek Zamanlı Veri Akışı
- WebSocket/STOMP üzerinden canlı hisse fiyat güncellemeleri
- BIST100 endeks takibi
- 3-10 saniyede bir otomatik veri yenileme

### 💼 Portföy Yönetimi
- Çoklu portföy desteği
- Risk profili değerlendirmesi
- Kar/zarar takibi
- Hisse miktarı yönetimi

### 🔐 Güvenlik ve Kimlik Doğrulama
- JWT tabanlı kimlik doğrulama
- Rol tabanlı erişim kontrolü (ADMIN, EMPLOYEE)
- Hız sınırlama koruması
- Redis ile oturum yönetimi

### 🤖 Yapay Zeka Destekli Hizmet
- Google Gemini kullanan akıllı chatbot
- Bağlamsal yanıtlar için vektör arama
- Sohbet geçmişi takibi
- Türkçe dil desteği

### 📊 Alım Satım İşlemleri
- Emir yönetimi (alım/satım)
- Cüzdan işlemleri
- Müşteri yönetimi (bireysel/kurumsal)
- Adres ve belge yönetimi

---

## 🛠️ Teknoloji Altyapısı

### Backend
- **Java 21** ile **Spring Boot 3.5.4**
- JWT kimlik doğrulama ile **Spring Security**
- SQL Server ile **Spring Data JPA**
- Önbellekleme ve oturum yönetimi için **Redis**
- Gerçek zamanlı iletişim için **STOMP WebSocket**
- Nesne eşleme için **MapStruct**
- API dokümantasyonu için **OpenAPI/Swagger**

### Frontend
- Create React App ile **React 19**
- WebSocket iletişimi için **STOMP.js**
- Navigasyon için **React Router**
- HTTP istekleri için **Axios**
- Statik dosya servisi ve reverse proxy için **Nginx**

### Chatbot
- **Django 5.2.4** web framework
- LLM orkestrasyonu için **LangChain**
- AI yanıtları için **Google Gemini** (2.5-flash)
- Vektör arama için **MongoDB Atlas**
- Gömme işlemleri için **Sentence Transformers**

### Altyapı
- **Docker** konteynerizasyonu
- Java bağımlılık yönetimi için **Maven**
- Node.js paket yönetimi için **npm**
- **Python sanal ortamları**

---

## 📁 Proje Yapısı

```
Sardis/
├── backend/                 # Spring Boot uygulaması
│   ├── hissenet/          # Ana uygulama modülü
│   │   ├── src/main/java/com/infina/hissenet/
│   │   │   ├── controller/    # REST API endpoint'leri
│   │   │   ├── service/       # İş mantığı
│   │   │   ├── entity/        # JPA varlıkları
│   │   │   ├── config/        # Yapılandırma sınıfları
│   │   │   ├── security/      # Güvenlik yapılandırması
│   │   │   └── scheduler/     # Zamanlanmış görevler
│   │   ├── pom.xml
│   │   └── Dockerfile
│   └── pom.xml
├── frontend/               # React uygulaması
│   ├── src/
│   │   ├── components/     # React bileşenleri
│   │   ├── pages/         # Sayfa bileşenleri
│   │   ├── hooks/         # Özel hook'lar
│   │   ├── server/        # API ve WebSocket servisleri
│   │   └── utils/         # Yardımcı fonksiyonlar
│   ├── package.json
│   ├── Dockerfile
│   └── default.conf       # Nginx yapılandırması
├── chatbot/               # Django chatbot uygulaması
│   ├── chatbot_api/       # Django proje ayarları
│   ├── chat/             # Ana uygulama
│   ├── LLM/              # Dil modeli entegrasyonu
│   ├── Embed/            # Gömme işleme
│   ├── Database/         # Veritabanı işlemleri
│   ├── Preprocessing/    # Veri ön işleme
│   ├── requirements.txt
│   └── Dockerfile
└── README.md
```

---

## 📚 API Dokümantasyonu

### Kimlik Doğrulama ve Güvenlik
- **POST** `/api/v1/auth/login` - Kullanıcı kimlik doğrulama
- **POST** `/api/v1/mail/send-verification` - Doğrulama kodu gönderme (sadece ADMIN)
- **POST** `/api/v1/mail/verify` - E-posta kodu doğrulama
- **POST** `/api/v1/mail/send-password-reset` - Şifre sıfırlama talebi
- **PATCH** `/api/v1/employees/changePassword` - Çalışan şifre değiştirme

### Müşteri Yönetimi
- **POST** `/api/v1/customers/individual` - Bireysel müşteri oluşturma
- **POST** `/api/v1/customers/corporate` - Kurumsal müşteri oluşturma
- **PUT** `/api/v1/customers/individual/{id}` - Bireysel müşteri güncelleme
- **GET** `/api/v1/customers/{id}` - ID ile müşteri getirme
- **GET** `/api/v1/customers` - Tüm müşterileri getirme (sayfalanmış)

### Portföy ve Alım Satım
- **POST** `/api/v1/portfolio` - Portföy oluşturma
- **GET** `/api/v1/portfolio` - Tüm portföyleri getirme
- **GET** `/api/v1/portfolio/summary` - Portföy özeti getirme
- **POST** `/api/v1/orders` - Alım satım emri oluşturma
- **GET** `/api/v1/orders` - Tüm emirleri getirme
- **GET** `/api/v1/orders/portfolio` - Portföy emirlerini getirme
- **GET** `/api/v1/orders/owned-quantity` - Sahip olunan hisse miktarını getirme

### Risk Değerlendirmesi
- **GET** `/api/v1/risk-assessment/questions` - Risk değerlendirme sorularını getirme
- **POST** `/api/v1/risk-assessment/calculate` - Risk profili hesaplama

### Cüzdan Yönetimi
- **POST** `/api/v1/wallet` - Cüzdan oluşturma
- **GET** `/api/v1/wallet` - Cüzdan bilgilerini getirme
- **POST** `/api/v1/wallet-transactions` - Cüzdan işlemi oluşturma
- **GET** `/api/v1/wallet-transactions` - İşlem geçmişini getirme

### Çalışan Yönetimi
- **POST** `/api/v1/employees` - Çalışan oluşturma (sadece ADMIN)
- **PUT** `/api/v1/employees` - Çalışan güncelleme
- **GET** `/api/v1/employees/{id}` - ID ile çalışan getirme
- **GET** `/api/v1/employees` - Tüm çalışanları getirme
- **DELETE** `/api/v1/employees/{id}` - Çalışan silme (sadece ADMIN)

### Gerçek Zamanlı Veri
- **WebSocket** `/ws-stock` - Gerçek zamanlı veri için STOMP endpoint'i
- **Topic** `/topic/prices` - Canlı hisse fiyat güncellemeleri
- **Topic** `/topic/bist100` - BIST100 endeks güncellemeleri
- **Topic** `/topic/infina-prices` - Infina API fiyat güncellemeleri

### Chatbot API
- **POST** `/api/chat/` - AI chatbot'a mesaj gönderme
- **GET** `/` - Chatbot web arayüzü

### Önbellek Yönetimi
- **GET** `/api/cache/combined` - Tüm önbelleklenmiş hisse verilerini getirme
- **GET** `/api/cache/combined/{code}` - Belirli hisse için önbelleklenmiş veriyi getirme

---

## 🚀 Kurulum ve Yapılandırma

### Ön Gereksinimler
- Java 21 (OpenJDK veya Oracle JDK)
- Node.js 18+ ve npm
- Python 3.11+
- Docker ve Docker Compose
- SQL Server örneği
- Redis örneği
- MongoDB Atlas hesabı
- Google Cloud API anahtarı (Gemini için)

### Backend Kurulumu
```bash
cd backend/hissenet
mvn clean install
mvn spring-boot:run
```

### Frontend Kurulumu
```bash
cd frontend
npm install
npm start
```

### Chatbot Kurulumu
```bash
cd chatbot
python -m venv .venv
source .venv/bin/activate  # Windows'ta: .venv\Scripts\activate
pip install -r requirements.txt
python manage.py runserver
```

---

## 🔧 Ortam Değişkenleri

### Backend (.env)
```bash
# Veritabanı
DB_URL=jdbc:sqlserver://localhost:1433;databaseName=hissenet
DB_USERNAME=kullanici_adi
DB_PASSWORD=sifre

# Redis
REDIS_HOST=localhost
REDIS_PASSWORD=redis_sifresi

# E-posta (Gmail)
MAIL_USERNAME=eposta@gmail.com
MAIL_PASSWORD=uygulama_sifresi
MAIL_FROM=noreply@hissenet.com
MAIL_FROM_NAME=HisseNet

# Harici API'lar
COLLECTAPI_APIKEY=collectapi_anahtari
INFINA_API_KEY=infina_api_anahtari
```

### Chatbot (.SECRETS)
```bash
googleAPIKEY=google_api_anahtari
mongoDBURI=mongodb_atlas_uri
pathPREPROCESS=./on_isleme_dosyalari/yolu/
projectDIR=./chatbot/
```

---

## 🏃‍♂️ Uygulamayı Çalıştırma

### Geliştirme Modu
1. SQL Server ve Redis'i başlatın
2. Backend'i başlatın: `mvn spring-boot:run` (port 8080)
3. Frontend'i başlatın: `npm start` (port 3000)
4. Chatbot'u başlatın: `python manage.py runserver` (port 8000)

### Üretim Modu
```bash
# Docker ile build edip çalıştırın
docker-compose up --build
```

---

## 🐳 Docker Dağıtımı

### Backend Konteyneri
```dockerfile
FROM maven:3.9.11-eclipse-temurin-21 AS builder
# Optimize edilmiş Java runtime için çok aşamalı build
```

### Frontend Konteyneri
```dockerfile
FROM node:18-alpine AS builder
# React uygulamasını build edip Nginx ile servis etme
```

### Chatbot Konteyneri
```dockerfile
FROM python:3.11-slim
# Django ile Python runtime
```

### Docker Compose
```yaml
version: '3.8'
services:
  hissenet-backend:
    build: ./backend/hissenet
    ports:
      - "8080:8080"
    environment:
      - DB_URL=${DB_URL}
      - REDIS_HOST=${REDIS_HOST}
    depends_on:
      - redis
      - sqlserver

  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - hissenet-backend

  chatbot:
    build: ./chatbot
    ports:
      - "8000:8000"
    environment:
      - GOOGLE_API_KEY=${GOOGLE_API_KEY}
```

---

## 💻 Geliştirme Kuralları

### Kod Stili
- **Backend**: Spring Boot konvansiyonlarını takip edin, DTO'lar için MapStruct kullanın
- **Frontend**: Hook'larla fonksiyonel bileşenler kullanın, React en iyi uygulamalarını takip edin
- **Chatbot**: Django konvansiyonlarını takip edin, mümkün olduğunda type hint'ler kullanın

### Test
- **Backend**: Spring Boot Test ile JUnit 5
- **Frontend**: React Testing Library
- **Chatbot**: Django test framework

### Güvenlik
- Tüm hassas veriler ortam değişkenleri üzerinden
- Redis oturum yönetimi ile JWT token'ları
- Tüm endpoint'lerde hız sınırlama
- Geliştirme için CORS yapılandırması

---

## 🤝 Katkıda Bulunma

1. Repository'yi fork edin
2. Özellik dalı oluşturun (`git checkout -b feature/harika-ozellik`)
3. Değişikliklerinizi commit edin (`git commit -m 'Harika özellik ekle'`)
4. Dalı push edin (`git push origin feature/harika-ozellik`)
5. Pull Request açın

### Geliştirme İş Akışı
- Conventional commit mesajları kullanın
- Tüm testlerin geçtiğinden emin olun
- Yeni özellikler için dokümantasyonu güncelleyin
- Belirlenmiş kod stilini takip edin

---

## 📄 Lisans

Bu proje Infina için geliştirilmiş tescilli yazılımdır. Tüm hakları saklıdır.

---

## 📞 Destek

Teknik destek veya sorular için:
- **Backend Sorunları**: Spring Boot loglarını ve application.yml yapılandırmasını kontrol edin
- **Frontend Sorunları**: Tarayıcı konsolunu ve React DevTools'u kontrol edin
- **Chatbot Sorunları**: Django loglarını ve .SECRETS yapılandırmasını kontrol edin
- **Veritabanı Sorunları**: Bağlantı dizelerini ve kimlik bilgilerini doğrulayın

---

<div align="center">
  <strong>Derin Hissedenler Geliştirme Ekibi tarafından ❤️ ile geliştirildi</strong>
</div> 