import os
import json
import requests
import urllib3
from dotenv import load_dotenv

# Cargar variables de entorno
load_dotenv()

# Configuración desde .env
OCTANE_URL = os.environ["OCTANE_URL"]
OCTANE_CLIENT_ID = os.environ["OCTANE_CLIENT_ID"]
OCTANE_CLIENT_SECRET = os.environ["OCTANE_CLIENT_SECRET"]
OCTANE_SHARED_SPACE = os.environ["OCTANE_SHARED_SPACE"]
OCTANE_WORKSPACE = os.environ["OCTANE_WORKSPACE"]

# Desactivar advertencias SSL
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

# Headers
HEADERS = {
    'Content-Type': 'application/json',
    'ALM_OCTANE_TECH_PREVIEW': 'true',
    'ALM-OCTANE-PRIVATE': 'true'
}

def sign_in(url, client_id, client_secret):
    resource = 'authentication/sign_in'
    payload = {"client_id": client_id, "client_secret": client_secret}

    resp = requests.post(url + '/' + resource,
                         data=json.dumps(payload),
                         headers=HEADERS,
                         verify=False)  # Ignorar verificación SSL

    cookie = resp.cookies
    print('Login to ALM Octane response code: ' + str(resp.status_code))
    return resp.status_code, cookie

def sign_out(url, cookie):
    resource = 'authentication/sign_out'

    resp = requests.post(url + '/' + resource,
                         headers=HEADERS,
                         cookies=cookie,
                         verify=False)  # Ignorar verificación SSL

    print('Logout to ALM Octane response code: ' + str(resp.status_code))
    return resp.status_code


