@echo off
cd /d D:\Downloaded\Infina\HisseNetChatbot
call .venv\Scripts\activate
python manage.py runserver
pause