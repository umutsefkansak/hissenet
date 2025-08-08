@echo off
cd ./
call .venv\Scripts\activate
python manage.py runserver
pause