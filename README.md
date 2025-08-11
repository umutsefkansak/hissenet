# HisseNet - B2B Alım Satım Platformu

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

HisseNet, gerçek zamanlı hisse fiyat takibi, portföy yönetimi, risk değerlendirmesi ve yapay zeka destekli müşteri hizmetleri sunan kapsamlı bir B2B hisse alım satım platformudur. Sistem üç ana bileşenden oluşur:

- **Backend**: İş mantığı ve gerçek zamanlı veri işleyen Spring Boot mikroservisi
- **Frontend**: Canlı fiyat akışı ile React tabanlı web uygulaması
- **Chatbot**: Google Gemini ve vektör arama kullanan yapay zeka destekli destek sistemi

---

## Sistem Mimarisi

<div align="center">
  <img src="https://res.cloudinary.com/dtmebvljq/image/upload/v1754927102/dxaazjevy0l20etqwygk.png" alt="HisseNet Sistem Mimarisi" style="max-width: 100%; height: auto; cursor: pointer;" onclick="window.open(this.src, '_blank')" title="Resmi yakınlaştırmak için tıklayın"/>
  <br/>
  <em>Resmi yakınlaştırmak için tıklayın</em>
</div>

### Mimari Bileşenleri:

**Ana Servisler:**
- **Frontend (React)**: Port 3000 - Kullanıcı arayüzü ve gerçek zamanlı veri görüntüleme
- **Backend (Spring Boot)**: Port 8080 - İş mantığı, API'lar ve veri işleme
- **Chatbot (Django)**: Port 8000 - Yapay zeka destekli müşteri hizmetleri

**Veritabanları:**
- **SQL Server**: Ana iş veritabanı (müşteriler, portföyler, emirler)
- **Redis**: Önbellekleme ve oturum yönetimi
- **MongoDB Atlas**: Chatbot için vektör arama veritabanı

**Altyapı Servisleri:**
- **Nginx**: Web sunucu ve reverse proxy
- **CollectAPI**: Hisse fiyat verileri
- **Infina API**: Finansal veri entegrasyonu

**Veri Akışları:**
- REST API iletişimi (Frontend ↔ Backend)
- WebSocket gerçek zamanlı veri akışı (/topic/prices, /topic/bist100)
- Harici API entegrasyonları (Backend → CollectAPI, Infina)
- Veritabanı bağlantıları (Backend ↔ SQL Server, Redis)

---

## Özellikler

### Gerçek Zamanlı Veri Akışı
- WebSocket/STOMP üzerinden canlı hisse fiyat güncellemeleri
- BIST100 endeks takibi
- 3-10 saniyede bir otomatik veri yenileme

### Portföy Yönetimi
- Çoklu portföy desteği
- Risk profili değerlendirmesi
- Kar/zarar takibi
- Hisse miktarı yönetimi

### Güvenlik ve Kimlik Doğrulama
- JWT tabanlı kimlik doğrulama
- Rol tabanlı erişim kontrolü (ADMIN, EMPLOYEE)
- Hız sınırlama koruması
- Redis ile oturum yönetimi
- Otomatik session uzatma
- RFC 7807 Problem Details hata formatı
- CSRF koruması devre dışı (stateless API)
- HTTP-only cookie güvenliği

### Yapay Zeka Destekli Hizmet
- Google Gemini kullanan akıllı chatbot
- Bağlamsal yanıtlar için vektör arama
- Sohbet geçmişi takibi
- Türkçe dil desteği

### Alım Satım İşlemleri
- Emir yönetimi (alım/satım)
- Cüzdan işlemleri
- Müşteri yönetimi (bireysel/kurumsal)
- Adres ve belge yönetimi

---

## Ekran Görüntüleri

### Ana Sayfa
<div align="center">
  <img src="https://res.cloudinary.com/dtmebvljq/image/upload/v1754935867/sqtlosc2xnjkhy1zzojj.png" alt="HisseNet Ana Sayfa" style="max-width: 100%; height: auto; cursor: pointer;" onclick="window.open(this.src, '_blank')" title="Resmi yakınlaştırmak için tıklayın"/>
  <br/>
  <em>Ana sayfa - Genel bakış ve navigasyon</em>
</div>

### Müşteri Anasayfa
<div align="center">
  <img src="https://res.cloudinary.com/dtmebvljq/image/upload/v1754935919/ursjcal6saktikrloflk.png" alt="Müşteri Anasayfa" style="max-width: 100%; height: auto; cursor: pointer;" onclick="window.open(this.src, '_blank')" title="Resmi yakınlaştırmak için tıklayın"/>
  <br/>
  <em>Müşteri anasayfa - Kişisel bilgiler ve portföy özeti</em>
</div>

### Müşteri Portföyü
<div align="center">
  <img src="https://res.cloudinary.com/dtmebvljq/image/upload/v1754935920/ldspererqpivvdrmuo43.png" alt="Müşteri Portföyü" style="max-width: 100%; height: auto; cursor: pointer;" onclick="window.open(this.src, '_blank')" title="Resmi yakınlaştırmak için tıklayın"/>
  <br/>
  <em>Müşteri portföyü - Hisse detayları ve performans takibi</em>
</div>

### Personel Giriş
<div align="center">
  <img src="https://res.cloudinary.com/dtmebvljq/image/upload/v1754935915/trr9repvr6x4de2gmojt.png" alt="Personel Giriş" style="max-width: 100%; height: auto; cursor: pointer;" onclick="window.open(this.src, '_blank')" title="Resmi yakınlaştırmak için tıklayın"/>
  <br/>
  <em>Personel giriş ekranı - Çalışan kimlik doğrulama</em>
</div>

### Kullanıcı Yönetimi
<div align="center">
  <img src="https://res.cloudinary.com/dtmebvljq/image/upload/v1754935918/rwjfdnucv8i9twlqapml.png" alt="Kullanıcı Yönetimi" style="max-width: 100%; height: auto; cursor: pointer;" onclick="window.open(this.src, '_blank')" title="Resmi yakınlaştırmak için tıklayın"/>
  <br/>
  <em>Kullanıcı yönetimi - Müşteri ve çalışan yönetimi paneli</em>
</div>

### Hisseler
<div align="center">
  <img src="https://res.cloudinary.com/dtmebvljq/image/upload/v1754935916/m8nos1g3eseue5zarive.png" alt="Hisseler" style="max-width: 100%; height: auto; cursor: pointer;" onclick="window.open(this.src, '_blank')" title="Resmi yakınlaştırmak için tıklayın"/>
  <br/>
  <em>Hisseler - Canlı fiyat takibi ve hisse listesi</em>
</div>

### Şifremi Unuttum
<div align="center">
  <img src="https://res.cloudinary.com/dtmebvljq/image/upload/v1754935865/bbyozy6vp40wyjfuxnfe.png" alt="Şifremi Unuttum" style="max-width: 100%; height: auto; cursor: pointer;" onclick="window.open(this.src, '_blank')" title="Resmi yakınlaştırmak için tıklayın"/>
  <br/>
  <em>Şifremi unuttum - Şifre sıfırlama ekranı</em>
</div>

### Alış Satış Ekranı
<div align="center">
  <img src="https://res.cloudinary.com/dtmebvljq/image/upload/v1754935866/xdod5e8xin3kk8xlcdtk.png" alt="Alış Satış Ekranı" style="max-width: 100%; height: auto; cursor: pointer;" onclick="window.open(this.src, '_blank')" title="Resmi yakınlaştırmak için tıklayın"/>
  <br/>
  <em>Alış satış ekranı - Hisse alım satım işlemleri</em>
</div>

### Cüzdan
<div align="center">
  <img src="https://res.cloudinary.com/dtmebvljq/image/upload/v1754935868/yv5obtgsjpe5jhkaqxh3.png" alt="Cüzdan" style="max-width: 100%; height: auto; cursor: pointer;" onclick="window.open(this.src, '_blank')" title="Resmi yakınlaştırmak için tıklayın"/>
  <br/>
  <em>Cüzdan - Bakiye yönetimi ve işlem geçmişi</em>
</div>

### Chat AI
<div align="center">
  <img src="https://res.cloudinary.com/dtmebvljq/image/upload/v1754935869/kvgzcdzqwkplcgqks3dv.png" alt="Chat AI" style="max-width: 100%; height: auto; cursor: pointer;" onclick="window.open(this.src, '_blank')" title="Resmi yakınlaştırmak için tıklayın"/>
  <br/>
  <em>Chat AI - Yapay zeka destekli müşteri hizmetleri</em>
</div>

---

## Teknoloji Altyapısı

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

**Backend (Spring Boot):**
- **controller/**: REST API endpoint'leri ve HTTP isteklerinin işlenmesi
- **service/**: İş mantığı ve iş kurallarının uygulanması
- **entity/**: Veritabanı tablolarının Java sınıf karşılıkları
- **repository/**: Veri erişim katmanı ve veritabanı sorguları
- **dto/**: Veri transfer nesneleri (request/response)
- **mapper/**: MapStruct ile nesne eşleme işlemleri
- **config/**: Uygulama yapılandırmaları ve bean tanımları
- **security/**: JWT, authentication ve authorization
- **scheduler/**: Zamanlanmış görevler ve cron job'lar
- **websocket/**: Gerçek zamanlı veri iletişimi
- **interceptor/**: HTTP isteklerinin ön işlenmesi
- **exception/**: Özel hata sınıfları ve yönetimi
- **properties/**: Konfigürasyon özellikleri ve ayarlar

**Frontend (React):**
- **components/**: Yeniden kullanılabilir UI bileşenleri
- **pages/**: Sayfa bileşenleri ve routing
- **hooks/**: Özel React hook'ları ve state yönetimi
- **server/**: API çağrıları ve WebSocket bağlantıları
- **utils/**: Yardımcı fonksiyonlar ve utility'ler
- **constants/**: Sabit değerler ve konfigürasyon
- **images/**: Görsel dosyalar ve asset'ler

**Chatbot (Django):**
- **chatbot_api/**: Django proje ayarları ve konfigürasyon
- **chat/**: Ana uygulama ve view'lar
- **LLM/**: Google Gemini entegrasyonu ve AI servisleri
- **Embed/**: Vektör gömme işlemleri
- **Database/**: MongoDB Atlas bağlantısı
- **Preprocessing/**: Veri ön işleme ve hazırlama

---

## API Dokümantasyonu

### Kimlik Doğrulama ve Güvenlik
- **POST** `/api/v1/auth/login` - Kullanıcı kimlik doğrulama
- **DELETE** `/api/v1/auth/logout` - Kullanıcı çıkış işlemi
- **POST** `/api/v1/mail/send` - Genel e-posta gönderme
- **POST** `/api/v1/mail/send-verification` - Doğrulama kodu gönderme (sadece ADMIN)
- **POST** `/api/v1/mail/verify` - E-posta kodu doğrulama
- **POST** `/api/v1/mail/send-password-reset` - Şifre sıfırlama talebi
- **PATCH** `/api/v1/employees/changePassword` - Çalışan şifre değiştirme

### Müşteri Yönetimi
- **POST** `/api/v1/customers/individual` - Bireysel müşteri oluşturma
- **POST** `/api/v1/customers/corporate` - Kurumsal müşteri oluşturma
- **PUT** `/api/v1/customers/individual/{id}` - Bireysel müşteri güncelleme
- **PUT** `/api/v1/customers/corporate/{id}` - Kurumsal müşteri güncelleme
- **GET** `/api/v1/customers/{id}` - ID ile müşteri getirme
- **GET** `/api/v1/customers` - Tüm müşterileri getirme (sayfalanmış)
- **GET** `/api/v1/customers/page` - Sayfalanmış müşteri listesi

### Portföy ve Alım Satım
- **POST** `/api/v1/portfolio/{customerId}` - Müşteri için portföy oluşturma
- **PUT** `/api/v1/portfolio/{id}` - Portföy güncelleme
- **GET** `/api/v1/portfolio/customer/{customerId}` - Müşteri portföylerini getirme
- **DELETE** `/api/v1/portfolio/{id}` - Portföy silme
- **PATCH** `/api/v1/portfolio/{id}/values` - Portföy değerlerini güncelleme
- **POST** `/api/v1/orders` - Alım satım emri oluşturma
- **PATCH** `/api/v1/orders/{id}` - Emir güncelleme
- **GET** `/api/v1/orders/{id}` - ID ile emir getirme
- **GET** `/api/v1/orders` - Tüm emirleri getirme
- **GET** `/api/v1/orders/by-customer` - Müşteri emirlerini getirme
- **GET** `/api/v1/orders/owned-quantity` - Sahip olunan hisse miktarını getirme

### Risk Değerlendirmesi
- **GET** `/api/v1/risk-assessment/questions` - Risk değerlendirme sorularını getirme
- **POST** `/api/v1/risk-assessment/calculate` - Risk profili hesaplama

### Cüzdan Yönetimi
- **POST** `/api/v1/wallet` - Cüzdan oluşturma
- **GET** `/api/v1/wallet/customer/{customerId}` - Müşteri cüzdanını getirme
- **GET** `/api/v1/wallet/customer/{customerId}/balance` - Cüzdan bakiyesini getirme
- **PUT** `/api/v1/wallet/customer/{customerId}/limits` - Cüzdan limitlerini güncelleme
- **POST** `/api/v1/wallet/customer/{customerId}/add-balance` - Cüzdana bakiye ekleme
- **POST** `/api/v1/wallet-transactions` - Cüzdan işlemi oluşturma
- **GET** `/api/v1/wallet-transactions` - Tüm işlemleri getirme
- **GET** `/api/v1/wallet-transactions/page` - Sayfalanmış işlem listesi
- **PUT** `/api/v1/wallet-transactions/{transactionId}` - İşlem güncelleme
- **POST** `/api/v1/wallet-transactions/{transactionId}/complete` - İşlemi tamamlama
- **POST** `/api/v1/wallet-transactions/{transactionId}/cancel` - İşlemi iptal etme
- **DELETE** `/api/v1/wallet-transactions/{transactionId}` - İşlem silme

### Çalışan Yönetimi
- **POST** `/api/v1/employees` - Çalışan oluşturma (sadece ADMIN)
- **PUT** `/api/v1/employees` - Çalışan güncelleme
- **GET** `/api/v1/employees/{id}` - ID ile çalışan getirme
- **GET** `/api/v1/employees` - Tüm çalışanları getirme
- **DELETE** `/api/v1/employees/{id}` - Çalışan silme (sadece ADMIN)

### Rol Yönetimi
- **POST** `/api/v1/roles` - Rol oluşturma
- **GET** `/api/v1/roles/{id}` - ID ile rol getirme
- **GET** `/api/v1/roles/name/{name}` - İsim ile rol getirme
- **GET** `/api/v1/roles` - Tüm rolleri getirme
- **GET** `/api/v1/roles/page` - Sayfalanmış rol listesi
- **GET** `/api/v1/roles/active` - Aktif rolleri getirme
- **GET** `/api/v1/roles/inactive` - Pasif rolleri getirme

### Adres Yönetimi
- **POST** `/api/v1/addresses` - Adres oluşturma
- **GET** `/api/v1/addresses/{id}` - ID ile adres getirme
- **GET** `/api/v1/addresses` - Tüm adresleri getirme
- **GET** `/api/v1/addresses/page` - Sayfalanmış adres listesi
- **GET** `/api/v1/addresses/customer/{customerId}` - Müşteri adreslerini getirme
- **GET** `/api/v1/addresses/customer/{customerId}/primary` - Müşteri ana adresini getirme
- **PUT** `/api/v1/addresses/{id}` - Adres güncelleme
- **DELETE** `/api/v1/addresses/{id}` - Adres silme
- **DELETE** `/api/v1/addresses/customer/{customerId}` - Müşteri tüm adreslerini silme

### Hisse İşlemleri
- **GET** `/api/v1/stock-transactions/buylist/{portfolioId}` - Portföy alım işlemlerini getirme
- **PATCH** `/api/v1/stock-transactions/{transactionId}/{portfolioId}` - Hisse işlemini portföye taşıma
- **GET** `/api/v1/stock-transactions/quantity/{customerId}/{stockCode}` - Müşteri hisse miktarını getirme

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

## Kurulum ve Yapılandırma

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

## Ortam Değişkenleri

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

## Uygulamayı Çalıştırma

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

## Geliştirme Kuralları

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
- IP tabanlı rate limiting (Bucket4j)
- Session hijacking koruması
- XSS ve CSRF koruması
- Güvenli header yapılandırması

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

## Güvenlik Mimarisi ve Senaryoları

### Kimlik Doğrulama Akışı
1. **Login İşlemi**:
   - Kullanıcı email/şifre ile giriş yapar
   - Backend şifreyi BCrypt ile doğrular
   - JWT token oluşturulur (HS256 algoritması)
   - Session ID (UUID) oluşturulur
   - Token Redis'e kaydedilir (7 gün TTL)
   - HTTP-only cookie olarak sessionId döner

2. **Token Doğrulama**:
   - Her istekte TokenFilter çalışır
   - Cookie'den sessionId alınır
   - Redis'ten JWT token çekilir
   - Token geçerliliği kontrol edilir
   - Spring Security context'e kullanıcı bilgileri set edilir

3. **Session Uzatma**:
   - SessionExtensionFilter her istekte çalışır
   - Memory cache ile 1 saatte bir Redis'e gider
   - Session ve token TTL'i 1 saat uzatılır
   - Async thread pool ile performans optimizasyonu

### Yetkilendirme Sistemi
- **Public Endpoint'ler**:
  - `/api/v1/auth/**` - Kimlik doğrulama
  - `/api/v1/mail/verify` - E-posta doğrulama
  - `/api/v1/mail/send-password-reset` - Şifre sıfırlama
  - `/swagger-ui/**` - API dokümantasyonu
  - `/ws-stock/**` - WebSocket bağlantıları

- **ADMIN Only Endpoint'ler**:
  - `/api/v1/employees/**` - Çalışan yönetimi
  - `/api/v1/mail/send-verification` - Doğrulama kodu gönderme

- **Authenticated Endpoint'ler**:
  - Tüm diğer API'lar (müşteri, portföy, emir vb.)

### Rate Limiting Mekanizması
- **Bucket4j Token Bucket Algoritması**:
  - Varsayılan: 100 istek/dakika
  - IP adresi bazında ayrı bucket'lar
  - X-Forwarded-For header desteği
  - ConcurrentHashMap ile thread-safe
  - Response header'da kalan token sayısı

- **İstisnalar**:
  - `/error` endpoint'leri
  - Swagger dokümantasyonu
  - WebSocket bağlantıları

### Güvenlik Filtreleri
1. **TokenFilter**: JWT token doğrulama
2. **SessionExtensionFilter**: Otomatik session uzatma
3. **RateLimitInterceptor**: Hız sınırlama
4. **CorsConfig**: Cross-origin resource sharing

### Hata Yönetimi
- **RFC 7807 Problem Details Format**:
  - 401 Unauthorized: Kimlik doğrulama hatası
  - 403 Forbidden: Yetkilendirme hatası
  - 429 Too Many Requests: Rate limit aşıldı
  - 500 Internal Server Error: Sunucu hatası

### Güvenlik Özellikleri
- **JWT Güvenliği**: HS256 imzalama, expiration kontrolü
- **Session Güvenliği**: UUID session ID, Redis TTL
- **Cookie Güvenliği**: HTTP-only, secure flag
- **Header Güvenliği**: XSS koruması, content type validation
- **IP Güvenliği**: Rate limiting, IP spoofing koruması

### Güvenlik Test Senaryoları
- **Authentication Test**:
  - Geçersiz JWT token ile istek
  - Expired token ile istek
  - Session ID olmadan istek

- **Authorization Test**:
  - EMPLOYEE rolü ile ADMIN endpoint'e erişim
  - Unauthenticated kullanıcı ile protected endpoint'e erişim

- **Rate Limiting Test**:
  - Dakikada 100+ istek gönderme
  - Farklı IP'lerden eşzamanlı istekler

- **Session Security Test**:
  - Session hijacking denemesi
  - Cookie manipulation testi

## Destek

Teknik destek veya sorular için:
- **Backend Sorunları**: Spring Boot loglarını ve application.yml yapılandırmasını kontrol edin
- **Frontend Sorunları**: Tarayıcı konsolunu ve React DevTools'u kontrol edin
- **Chatbot Sorunları**: Django loglarını ve .SECRETS yapılandırmasını kontrol edin
- **Veritabanı Sorunları**: Bağlantı dizelerini ve kimlik bilgilerini doğrulayın
- **Güvenlik Sorunları**: JWT token'ları, Redis bağlantısını ve rate limiting loglarını kontrol edin

---

<div align="center">
  <strong>Derin Hissedenler Geliştirme Ekibi tarafından geliştirildi</strong>
</div> 