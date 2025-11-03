import os
import requests
from auth_octane import HEADERS
import xml.etree.ElementTree as ET
import os
import glob
from datetime import datetime, timedelta


from datetime import datetime


def transform_datetime_to_dd_mm_yyyy_hh_mm_ss(datetime_str):
    """
    Transforma una fecha ISO (2025-10-29T10:13:04Z) al formato DD_MM_YYYY_HH_MM_SS
    
    Args:
        datetime_str (str): Fecha en formato ISO (ej: '2025-10-29T10:13:04Z')
    
    Returns:
        str: Fecha en formato DD_MM_YYYY_HH_MM_SS (ej: '29_10_2025_10_13_04')
    """
    try:
        # Parsear la fecha ISO
        if datetime_str.endswith('Z'):
            datetime_str = datetime_str[:-1] + '+00:00'
        
        dt = datetime.fromisoformat(datetime_str.replace('Z', '+00:00'))
        
        # Sumar una hora
        dt = dt + timedelta(hours=1)
        
        # Formatear al formato deseado DD_MM_YYYY_HH_MM_SS
        return dt.strftime("%d_%m_%Y_%H_%M_%S")
    
    except Exception as e:
        print(f"Error al transformar la fecha {datetime_str}: {e}")
        # Fallback al método anterior si hay error
        return datetime_str.replace("-", "_").replace("T", "_").replace(":", "_").replace("Z", "")


def build_url(base, shared_space, workspace, endpoint, params=""):
    return f"{base}api/shared_spaces/{shared_space}/workspaces/{workspace}/{endpoint}?{params}"

def get_test_run_by_id(base_url, shared_space, workspace, run_id, cookie):
    fields = "started,error_details,error_type,status,error_message,test_name,name,creation_time"
    query = f"fetch_single_entity=true&fields={fields}&query=\"(id='{run_id}')\""
    url = build_url(base_url, shared_space, workspace, "runs", query)
    response = requests.get(url, headers=HEADERS, cookies=cookie, verify=False)
    return response.json() if response.status_code == 200 else None

def get_automation_test(base_url, shared_space, workspace, run_id, cookie):
    query = f"(subtype='test_automated';run={{id={run_id}}})"
    params = f"fields=id,name&query=\"{query}\""
    url = build_url(base_url, shared_space, workspace, "tests", params)
    response = requests.get(url, headers=HEADERS, cookies=cookie, verify=False)
    return response.json() if response.status_code == 200 else None

def get_attachments(base_url, shared_space, workspace, test_id, cookie):
    query = f"(owner_test={{id={test_id}}})"
    params = f"fields=id,name,description&limit=1000&order_by=-id&query=\"{query}\""
    url = build_url(base_url, shared_space, workspace, "attachments", params)
    response = requests.get(url, headers=HEADERS, cookies=cookie, verify=False)
    return response.json() if response.status_code == 200 else None

def download_attachments_by_date(base_url, shared_space, workspace, test_id, cookie, date_str):
    formatted_date = transform_datetime_to_dd_mm_yyyy_hh_mm_ss(date_str)
    
    folder = str(test_id)
    os.makedirs(folder, exist_ok=True)

    attachments = get_attachments(base_url, shared_space, workspace, test_id, cookie)
    if not attachments or not attachments.get("data"):
        return None

    for item in attachments["data"]:
        filename = item.get("name", "")
        if formatted_date in filename:
            print(f"Imagen asociada al test {test_id} con nombre: {filename}")
            attachment_id = item["id"]
            url = f"{base_url}api/shared_spaces/{shared_space}/workspaces/{workspace}/attachments/{attachment_id}/{filename}"
            path = os.path.join(folder, filename)

            response = requests.get(url, headers=HEADERS, cookies=cookie, verify=False, stream=True)
            if response.status_code == 200:
                with open(path, "wb") as f:
                    for chunk in response.iter_content(chunk_size=8192):
                        f.write(chunk)
                return path

    return None

def analizar_xml_tests_fallidos(directorio_input="input"):
    """
    Analiza archivos XML en el directorio especificado y extrae los nombres 
    de los tests que han fallado (con errores o failures).
    
    Args:
        directorio_input (str): Directorio donde buscar archivos XML
    
    Returns:
        list: Array con los nombres de los tests fallidos
    """
    tests_fallidos = []
    
    # Buscar todos los archivos XML en el directorio input
    patron_xml = os.path.join(directorio_input, "*.xml")
    archivos_xml = glob.glob(patron_xml)
    
    if not archivos_xml:
        print(f"No se encontraron archivos XML en el directorio '{directorio_input}'")
        return tests_fallidos
    
    for archivo_xml in archivos_xml:
        print(f"Analizando archivo: {archivo_xml}")
        
        try:
            # Parsear el archivo XML
            tree = ET.parse(archivo_xml)
            root = tree.getroot()
            
            # Buscar todos los testcase que tengan elementos error o failure
            for testcase in root.findall('.//testcase'):
                nombre_test = testcase.get('name')
                
                # Verificar si el testcase tiene errores o failures
                error = testcase.find('error')
                failure = testcase.find('failure')
                
                if error is not None or failure is not None:
                    if nombre_test:
                        tests_fallidos.append(nombre_test)
                        print(f"  - Test fallido encontrado: {nombre_test}")
                        
                        # Mostrar información adicional del error
                        if error is not None:
                            error_message = error.get('message', 'Sin mensaje de error')
                            #print(f"    Error: {error_message[:100]}...")
                        
                        if failure is not None:
                            failure_message = failure.get('message', 'Sin mensaje de failure')
                            #print(f"    Failure: {failure_message[:100]}...")
        
        except ET.ParseError as e:
            print(f"Error al parsear el archivo {archivo_xml}: {e}")
        except Exception as e:
            print(f"Error inesperado al procesar {archivo_xml}: {e}")
    
    return tests_fallidos


def obtener_detalles_tests_fallidos(directorio_input="input"):
    """
    Obtiene información detallada de los tests fallidos incluyendo 
    nombre, clase, tiempo de ejecución y mensaje de error.
    
    Args:
        directorio_input (str): Directorio donde buscar archivos XML
    
    Returns:
        list: Array con diccionarios conteniendo detalles de tests fallidos
    """
    tests_fallidos_detallados = []
    
    # Buscar todos los archivos XML en el directorio input
    patron_xml = os.path.join(directorio_input, "*.xml")
    archivos_xml = glob.glob(patron_xml)
    
    for archivo_xml in archivos_xml:
        try:
            tree = ET.parse(archivo_xml)
            root = tree.getroot()
            
            for testcase in root.findall('.//testcase'):
                error = testcase.find('error')
                failure = testcase.find('failure')
                
                if error is not None or failure is not None:
                    test_info = {
                        'name': testcase.get('name'),
                        'classname': testcase.get('classname'),
                        'time': testcase.get('time'),
                        'archivo_origen': os.path.basename(archivo_xml)
                    }
                    
                    if error is not None:
                        test_info['tipo_fallo'] = 'error'
                        test_info['mensaje'] = error.get('message', '')
                        test_info['tipo_error'] = error.get('type', '')
                    
                    if failure is not None:
                        test_info['tipo_fallo'] = 'failure'
                        test_info['mensaje'] = failure.get('message', '')
                        test_info['tipo_error'] = failure.get('type', '')
                    
                    tests_fallidos_detallados.append(test_info)
        
        except Exception as e:
            print(f"Error al procesar {archivo_xml}: {e}")
    
    return tests_fallidos_detallados


def get_test_case_by_test_name(base_url, shared_space, workspace, test_name, cookie):
    # api/shared_spaces/4006/workspaces/7001/runs?query="status EQ {id EQ ^list_node.run_status.passed^};test EQ {name EQ ^simple test^}"

    query = f"status EQ {{id EQ ^list_node.run_status.failed^}};test EQ {{name EQ ^{test_name}^}}"
    params = f"query=\"{query}\""
    url = build_url(base_url, shared_space, workspace, "runs", params)
    response = requests.get(url, headers=HEADERS, cookies=cookie, verify=False)
    return response.json() if response.status_code == 200 else None


