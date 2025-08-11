# HisseNet Asistan

<div align="center">
  <img src="https://res.cloudinary.com/dtmebvljq/image/upload/v1754924876/kexgebnphuvu2xsaflhp.png" alt="HisseNet Logo" width="200"/>
  <br/>
  <em>Gerçek zamanlı veri akışı ile modern, ölçeklenebilir B2B hisse alım satım platformu</em>
</div>

---

## İçindekiler

- [Genel Bakış](#genel-bakış)
- [Sistem Mimarisi](#sistem-mimarisi)
- [Özellikler](#özellikler)
- [Ekran Görüntüleri](#ekran-görüntüleri)
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

## Genel Bakış

Hissenet Asistan projesi, Hissenet ürününü kullanan banka ve aracı kurum personelinin adaptasyon süresini kısaltmak, teknik destek birimlerinin üzerindeki yoğunluğu azaltmak ve işlem süreçlerini daha verimli hale getirmek amacıyla başlatılmıştır. Proje, müşteri memnuniyetini artırmak ve operasyonel verimliliği yükseltmek açısından önem taşımaktadır. Chatbot, semantik arama mantığına dayanan, RAG mimarisi ile geliştirilmiş ve Gemini 2.5 Flash ile kullanıcıya yanıt dönen bir yapıda kurulmuştur. HisseNet uygulamasının kolay kullanılması ve personlin adaptasyonunu hızlandırmak amacıyla geliştirilmiştir.

- **Chatbot**: Google Gemini ve vektör arama kullanan yapay zeka destekli destek sistemi
- **Backend**: İş mantığı ve gerçek zamanlı veri işleyen Spring Boot projesi
- **Frontend**: Canlı fiyat akışı ile React tabanlı web uygulaması

---
### Mimari Bileşenleri:

**Ana Servisler:**
- **Chatbot (Django)**: Port 8000 - Yapay zeka destekli müşteri hizmetleri
- **Frontend (React)**: Port 3000 - Kullanıcı arayüzü ve gerçek zamanlı veri görüntüleme
- **Backend (Spring Boot)**: Port 8080 - İş mantığı, API'lar ve veri işleme

**Veritabanları:**
- **MongoDB Atlas**: Chatbot için vektör arama veritabanı
- **SQL Server**: Ana iş veritabanı (müşteriler, portföyler, emirler)
- **Redis**: Önbellekleme ve oturum yönetimi

**Veri Akışları:**
- Veritabanı bağlantıları (Backend ↔ MongoDB Atlas)
- REST API iletişimi (Frontend ↔ Backend)
- WebSocket gerçek zamanlı veri akışı (/topic/prices, /topic/bist100)
- Harici API entegrasyonları (Backend → CollectAPI, Infina)

---

## Özellikler

### Yapay Zeka Destekli Hizmet
- Google Gemini kullanan akıllı chatbot
- Bağlamsal yanıtlar için vektör arama
- Sohbet geçmişi takibi
- Türkçe dil desteği

---

## Ekran Görüntüleri

### Ana Sayfa
<div align="center">
  <img src="https://res.cloudinary.com/dtmebvljq/image/upload/v1754935867/sqtlosc2xnjkhy1zzojj.png" alt="HisseNet Ana Sayfa" style="max-width: 100%; height: auto; cursor: pointer;" onclick="window.open(this.src, '_blank')" title="Resmi yakınlaştırmak için tıklayın"/>
  <br/>
  <em>Ana sayfa - Genel bakış ve navigasyon</em>
</div>

### Chat AI
<div align="center">
  <img src="https://res.cloudinary.com/dtmebvljq/image/upload/v1754951801/cc5vz6ciei2thy9o3jpv.png" alt="Chat AI" style="max-width: 100%; height: auto; cursor: pointer;" onclick="window.open(this.src, '_blank')" title="Resmi yakınlaştırmak için tıklayın"/>
  <br/>
  <em>Chat AI - Yapay zeka destekli müşteri hizmetleri</em>
</div>

---

## Teknoloji Altyapısı

### Chatbot
- **Django 5.2.4** web framework
- LLM orkestrasyonu için **LangChain**
- AI yanıtları için **Google Gemini** (2.5-flash)
- Vektör arama için **MongoDB Atlas**
- Embedding işlemleri için **Google Embedding**

### Altyapı
- **Docker** konteynerizasyonu
- Java bağımlılık yönetimi için **Maven**
- Node.js paket yönetimi için **npm**
- **Python sanal ortamları**

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

---

## Proje Yapısı

```
HisseNet/
├── .git/                                   # Git repository
├── .gitignore                              # Proje git ignore
├── docker-compose.yml                      # Docker Compose yapılandırması
├── python-backend/                         # Python backend (boş)
├── backend/                                # Java backend ana klasörü
│   ├── pom.xml/                            # Parent Maven POM klasörü
│   ├── src/                                # Backend src klasörü (boş)
│   └── hissenet/                           # Ana Spring Boot uygulaması
│       ├── src/main/java/com/infina/hissenet/
│       │   ├── controller/                 # REST API endpoint'leri
│       │   ├── service/                    # İş mantığı katmanı
│       │   ├── entity/                     # JPA varlıkları
│       │   ├── repository/                 # Veri erişim katmanı
│       │   ├── dto/                        # Veri transfer nesneleri
│       │   ├── mapper/                     # Nesne eşleme (MapStruct)
│       │   ├── config/                     # Yapılandırma sınıfları
│       │   ├── security/                   # Güvenlik katmanı
│       │   ├── scheduler/                  # Zamanlanmış görevler
│       │   ├── websocket/                  # WebSocket işlemleri
│       │   ├── interceptor/                # Interceptor'lar
│       │   ├── exception/                  # Özel exception sınıfları
│       │   ├── validation/                 # Doğrulama sınıfları
│       │   ├── utils/                      # Yardımcı sınıflar
│       │   ├── constants/                  # Sabit değerler
│       │   ├── listener/                   # Event listener'lar
│       │   ├── event/                      # Event sınıfları
│       │   ├── client/                     # Harici API client'ları
│       │   ├── properties/                 # Konfigürasyon özellikleri
│       │   ├── common/                     # Ortak sınıflar
│       │   └── logging/                    # Logging yapılandırması
│       ├── src/main/resources/
│       ├── src/test/java/                  # Test sınıfları
│       ├── pom.xml                         # Maven bağımlılıkları
│       ├── Dockerfile                      # Docker yapılandırması
│       ├── mvnw                            # Maven wrapper
│       └── .gitignore                      # Git ignore dosyası
├── frontend/                               # React uygulaması
│   ├── node_modules/                       # Node.js bağımlılıkları
│   ├── public/                             # Statik dosyalar
│   │   ├── fonts/                          # Font dosyaları
│   ├── src/                                # React kaynak kodları
│   │   ├── components/                     # React bileşenleri
│   │   ├── pages/                          # Sayfa bileşenleri
│   │   ├── hooks/                          # Özel React hook'ları
│   │   ├── server/                         # API ve WebSocket servisleri
│   │   │   └── websocket/                  # WebSocket servisleri
│   │   ├── utils/                          # Yardımcı fonksiyonlar
│   │   ├── constants/                      # Sabit değerler
│   │   ├── images/                         # Görsel dosyalar
│   │   ├── App.jsx                         # Ana uygulama bileşeni
│   │   ├── App.css                         # Ana uygulama stilleri
│   │   ├── index.js                        # Giriş noktası
│   │   ├── index.css                       # Ana stiller
│   │   ├── logo.svg                        # Logo SVG
│   │   ├── reportWebVitals.js              # Web vitals raporlama
│   │   └── App.test.js                     # Ana uygulama testi
│   ├── .vscode/                            # VS Code ayarları (boş)
│   ├── package.json                        # Node.js bağımlılıkları
│   ├── package-lock.json                   # Bağımlılık kilidi
│   ├── Dockerfile                          # Docker yapılandırması
│   ├── default.conf                        # Nginx yapılandırması
│   ├── .gitignore                          # Git ignore dosyası
│   └── README.md                           # Frontend README
├── chatbot/                                # Django chatbot uygulaması
│   ├── chatbot_api/                        # Django proje ayarları
│   ├── chat/                               # Ana Django uygulaması
│   ├── LLM/                                # Dil modeli entegrasyonu
│   ├── Embed/                              # Gömme işleme
│   ├── Database/                           # Veritabanı işlemleri
│   ├── Preprocessing/                      # Veri ön işleme
│   ├── __init__.py                         # Python paket tanımı
│   ├── requirements.txt                    # Python bağımlılıkları
│   ├── Dockerfile                          # Docker yapılandırması
│   ├── .dockerignore                       # Docker ignore dosyası
│   ├── run_django.py                       # Django çalıştırma betiği
│   ├── run_chatbot.bat                     # Windows çalıştırma betiği
│   ├── run_chatbot.sh                      # Linux çalıştırma betiği
│   └── manage.py                           # Django yönetim betiği
├── README.md                               # Ana proje README
```


### Proje Yapısı Açıklaması:

**Chatbot (Django):**
- **chatbot_api/**: Django proje ayarları ve konfigürasyon
- **chat/**: Ana uygulama ve view'lar
- **LLM/**: Google Gemini entegrasyonu ve AI servisleri
- **Embed/**: Vektör embedding işlemleri
- **Database/**: MongoDB Atlas bağlantısı
- **Preprocessing/**: Veri ön işleme ve hazırlama

---

## API Dokümantasyonu

### Chatbot API
- **POST** `/api/chat/` - AI chatbot'a mesaj gönderme
- **GET** `/` - Chatbot web arayüzü

---

## Kurulum ve Yapılandırma

### Ön Gereksinimler
- Python 3.11+
- MongoDB Atlas hesabı
- Google Cloud API anahtarı (Gemini için)
- Java 21 (OpenJDK veya Oracle JDK)
- Node.js 18+ ve npm
- Docker ve Docker Compose
- SQL Server örneği
- Redis örneği

### Chatbot Kurulumu
```bash
cd chatbot
python -m venv .venv
source .venv/bin/activate  # Windows'ta: .venv\Scripts\activate
pip install -r requirements.txt
python manage.py runserver
```

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
---

## Ortam Değişkenleri

### Chatbot (.SECRETS)
```bash
googleAPIKEY=google_api_anahtari
mongoDBURI=mongodb_atlas_uri
pathPREPROCESS=./on_isleme_dosyalari/yolu/
projectDIR=./chatbot/
```

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
---

## Uygulamayı Çalıştırma

### Sadece Chatbot Çalıştırma
Chatbot'un çalışması için .SECRETS dosyası içinde googleAPIKEY ve mongoDBURI keyleri verilmelidir.
#### Chatbot (.SECRETS)
```bash
googleAPIKEY=google_api_anahtari
mongoDBURI=mongodb_atlas_uri
pathPREPROCESS=./on_isleme_dosyalari/yolu/
projectDIR=./chatbot/
```

#### Lokalde Çalıştırmak İçin
```dockerfile
# Sanal ortam kur
python venv .venv

# Sanal ortam çalıştır
.venv\Scripts\activate

# Gereksinimleri yükle
pip install --upgrade pip && pip install -r requirements.txt

# Django backend çalıştır
python manage.py runserver
```

### Docker İle Çalıştırma
#### Docker Compose
```yaml
services:
  web:
    build:
      context: ./chatbot
      dockerfile: Dockerfile
    image: hissenetchatbot
    container_name: hissenetchatbot
    ports:
      - "8000:8000"
```
```bash
# Docker ile build edip çalıştırın
docker-compose up --build
```

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

## Docker Dağıtımı

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

## Katkıda Bulunma

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

## Lisans

Bu proje Infina Akademi staj için geliştirilmiş yazılımdır.

---

## Destek

Teknik destek veya sorular için:
- **Backend Sorunları**: Spring Boot loglarını ve application.yml yapılandırmasını kontrol edin
- **Frontend Sorunları**: Tarayıcı konsolunu ve React DevTools'u kontrol edin
- **Chatbot Sorunları**: Django loglarını ve .SECRETS yapılandırmasını kontrol edin
- **Veritabanı Sorunları**: Bağlantı dizelerini ve kimlik bilgilerini doğrulayın
- **Güvenlik Sorunları**: JWT token'ları, Redis bağlantısını ve rate limiting loglarını kontrol edin

---

<div align="center">
  <strong>Derin Hissedenler Ar-Ge Ekibi tarafından geliştirildi</strong>
</div> 