import os
import json
import sys
import requests
from dotenv import load_dotenv
from auth_octane import sign_in, sign_out
from get_data_octane import (

    get_attachments,
    download_attachments_by_date,
    analizar_xml_tests_fallidos,
    get_test_case_by_test_name
)
from clasificador_defects import clasificar_error, generar_descripcion_defecto
from create_defect import create_example_defect_body, create_defect

# Cargar variables de entorno
load_dotenv()

# Configuración Octane desde .env
OCTANE_URL = os.environ["OCTANE_URL"]
OCTANE_CLIENT_ID = os.environ["OCTANE_CLIENT_ID"]
OCTANE_CLIENT_SECRET = os.environ["OCTANE_CLIENT_SECRET"]
OCTANE_SHARED_SPACE = os.environ["OCTANE_SHARED_SPACE"]
OCTANE_WORKSPACE = os.environ["OCTANE_WORKSPACE"]

HEADERS = {
    'Content-Type': 'application/json',
    'ALM-OCTANE-TECH-PREVIEW': 'true',
}

def crear_defecto(nombre_test):
    print(f"Iniciando proceso para el test: {nombre_test}")
    
    print("Autenticando en Octane...")
    code, cookie = sign_in(OCTANE_URL, OCTANE_CLIENT_ID, OCTANE_CLIENT_SECRET)
    if code != 200:
        print(f"Error en autenticación con Octane. Código: {code}")
        return
    print("Autenticación exitosa")

    print(f"Obteniendo info del test: {nombre_test}")
    test_info = get_test_case_by_test_name(OCTANE_URL, OCTANE_SHARED_SPACE, OCTANE_WORKSPACE, nombre_test, cookie)

    run_id = test_info['data'][0].get("id")
    started = test_info['data'][0].get("started")
    error_details = test_info['data'][0].get("error_details")
    error_type = test_info['data'][0].get("error_type")
    status = test_info['data'][0]['status'].get("name")
    error_message = test_info['data'][0].get("error_message")
    name = test_info['data'][0].get("name")
    creation_time = test_info['data'][0].get("creation_time")
    test_id = test_info['data'][0]['test'].get("id")

    # Descargar el attachment con la fecha en el nombre
    image_path = download_attachments_by_date(
        OCTANE_URL,
        OCTANE_SHARED_SPACE,
        OCTANE_WORKSPACE,
        test_id,
        cookie,
        started
    )

    # Generar descripción con o sin imagen
    descripcion = generar_descripcion_defecto(image_path, run_id, name, status, error_message, error_type, error_details)
    print(f"Descripción generada por IA:\n{descripcion}")

    tipo_defecto = clasificar_error(image_path, descripcion)
    tipo_defecto_dict = json.loads(tipo_defecto)

    print(f"\nDefect Category asignada por IA: {tipo_defecto_dict['name']}")

    defect_body = create_example_defect_body(
        run_id=run_id,
        group=tipo_defecto_dict["id"],
        description=descripcion
    )
    
    print("Creando defect en Octane...\n")
    created_defect = create_defect(cookie, defect_body)
    if created_defect:
        print(f"Defecto creado exitosamente con ID: {created_defect['data'][0]['id']}")
    else:
        print("Error al crear el defecto")

    sign_out(OCTANE_URL, cookie)

if __name__ == "__main__":


    if len(sys.argv) != 2:
        print("Uso: python create_defect_from_xml.py <archivo_xml>")
        sys.exit(1)

    xml_path = sys.argv[1]

    # Obtener solo los nombres de los tests fallidos
    nombres_tests_fallidos = analizar_xml_tests_fallidos(directorio_input=str(xml_path))

    print(f"Total de tests fallidos encontrados: {len(nombres_tests_fallidos)}")
    print(f"\n=== Nombres de tests fallidos ===")
    print(nombres_tests_fallidos)

    # Crear defectos para cada test fallido
    for nombre_test in nombres_tests_fallidos:
        crear_defecto(nombre_test)
           
