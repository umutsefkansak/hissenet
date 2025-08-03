from pathlib import Path

# GÜVENLİK
SECRET_KEY = 'gizli_anahtar'
BASE_DIR = Path(__file__).resolve().parent.parent

DEBUG = True

ALLOWED_HOSTS = []

# UYGULAMALAR
INSTALLED_APPS = [
    'django.contrib.admin',            
    'django.contrib.auth',             
    'django.contrib.contenttypes',     
    'django.contrib.sessions',
    'django.contrib.messages',
    'django.contrib.staticfiles',
    'chat',  # Senin uygulaman burada olmalı
    'chatbot_api',
]

MIDDLEWARE = [
    'django.middleware.security.SecurityMiddleware',
    'django.contrib.sessions.middleware.SessionMiddleware', 
    'django.middleware.common.CommonMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware', 
    'django.contrib.messages.middleware.MessageMiddleware', 
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
]

ROOT_URLCONF = 'chatbot_api.urls'

# HTML DOSYALARI (şablonlar)
TEMPLATES = [
    {
        'BACKEND': 'django.template.backends.django.DjangoTemplates',
        'DIRS': [BASE_DIR / 'chat' / 'templates'],
        'APP_DIRS': True,
        'OPTIONS': {
            'context_processors': [
                'django.template.context_processors.debug',
                'django.template.context_processors.request',  
                'django.contrib.auth.context_processors.auth',  
                'django.contrib.messages.context_processors.messages',  
            ],}
    }
]

# ZAMAN, DİL
LANGUAGE_CODE = 'tr-tr'
TIME_ZONE = 'Europe/Istanbul'

# STATİK DOSYALAR (opsiyonel)
STATIC_URL = '/static/'
