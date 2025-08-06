# -*- coding: utf-8 -*-
import os
import subprocess
from dotenv import dotenv_values

secrets = dotenv_values(".SECRETS")
project_dir = secrets["projectDIR"]

os.chdir(project_dir)
subprocess.call(r".venv\Scripts\activate && python manage.py runserver", shell=True)
