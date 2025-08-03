from django.contrib import admin
from django.urls import path, include
from chat.views import chat_page

urlpatterns = [
    path('admin/', admin.site.urls),
    path('api/', include('chat.urls')),
    path('', chat_page, name='chat'),
]