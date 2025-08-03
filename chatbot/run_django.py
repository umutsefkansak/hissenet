import os
import subprocess

project_dir = r"/Users/zeynepguney/Downloads/chatbot/"
os.chdir(project_dir)
subprocess.call("source .venv/bin/activate && python manage.py runserver", shell=True)
run_django.py