import base64
from azure_client import client, DEPLOYMENT_NAME

import os

def encode_image_to_base64(image_path):
    if not os.path.exists(image_path):
        raise FileNotFoundError(f"Imagen no encontrada: {image_path}")
    
    ext = os.path.splitext(image_path)[1].lower()
    if ext not in [".jpg", ".jpeg", ".png"]:
        raise ValueError(f"Formato de imagen no soportado: {ext}")
    
    with open(image_path, "rb") as f:
        return base64.b64encode(f.read()).decode("utf-8")
    
def clasificar_error(image_path, descripcion):
    user_content = [
        {
            "type": "text",
            "text": f"Descripción del caso de prueba:\n{descripcion}"
        }
    ]

    if image_path and os.path.exists(image_path):
        image_base64 = encode_image_to_base64(image_path)
        user_content.append({
            "type": "image_url",
            "image_url": {
                "url": f"data:image/jpeg;base64,{image_base64}"
            }
        })

    prompt = """
        ERES UN EXPERTO EN QA Y DEBES CLASIFICAR EL TIPO DE ERROR BASADO EN LA IMAGEN Y DESCRIPCIÓN PROPORCIONADAS.
        LOS TIPOS SON:
            "Eres un experto en QA. Clasifica el error mostrado en la imagen y descrito a continuación como:\n"
            "| 9wmg38gnen2nwc0expz47rdpo:**Change Request (Solicitud de Cambio)** | Petición formal para modificar o agregar una funcionalidad. No es un defecto, ya que el sistema funciona según el diseño original. | El usuario solicita agregar un nuevo campo al formulario. |\n"
            "| eq50yl2pddz8kuxzvgkw6386x:**Configuration (Configuración)** | Error relacionado con la configuración del entorno, parámetros, archivos o permisos. | Una URL de base de datos incorrecta impide la conexión. |\n"
            "| 9qe0r0j1ojw9dcvk60gdg31jm:**Data Error (Error de Datos)** | Fallo causado por datos erróneos o inconsistentes, no por el código. | Un reporte muestra información incorrecta por datos mal cargados. |\n"
            "| lqk6x34814qjmvbkoz7myzo4v:**Deployment Defect (Defecto de Despliegue)** | Error que surge durante o después del despliegue debido a una instalación o versión incorrecta. | Una API deja de responder tras una actualización. |\n"
            "| 824d3v6p7d760ux6v1d9rwpev:**Duplicate Defect (Defecto Duplicado)** | Defecto que ya fue reportado previamente. Se marca como duplicado para evitar redundancia. | Un tester reporta un bug que ya existe en el sistema de seguimiento. |\n"
            "| o8dvynlwkzk6oc2pj7e60rm92:**Functional (Funcional)** | El sistema no cumple con los requisitos o se comporta incorrectamente. | Un botón no ejecuta la acción esperada. |\n"
            "| 9qmgr29jjqd0zh1gnjloz35x8:**Known Defect (Defecto Conocido)** | Error ya identificado y documentado, pendiente de corrección o aceptado temporalmente. | Se sabe que cierta función falla en casos específicos. |\n"
            "| o2le3p7vlzxvlt5n257m83w8g:**Quality Assurance Review (Revisión de QA)** | Observaciones del equipo de QA que no necesariamente representan un fallo funcional. | Una sugerencia de mejora en la interfaz de usuario. |\n"
            "| k4onydx0w209kh1wv24dz36qj:**Requirement/Design Defect (Defecto de Requerimiento o Diseño)** | Error derivado de un requerimiento mal definido o un diseño incorrecto. | Un flujo no contempla un caso de uso porque no fue especificado. |\n"
            "| 7olxnwoz50w2xhdxwgzv68wvr:**Not a Defect (No es un Defecto)** | Comportamiento esperado según las especificaciones; no es un error real. | El sistema bloquea una acción de acuerdo con una regla definida. |\n"
        DEVUELVE SOLO UN OBJETO JSON CON EL ID Y NOMBRE DEL TIPO DE ERROR EN EL SIGUIENTE
        FORMATO DE RESPUESTA:
        - Responde ÚNICAMENTE con JSON válido
        - NO incluyas texto explicativo antes o después del JSON  
        - NO uses etiquetas de código tipo ``````
        - NO agregues comentarios en el JSON
        - El JSON debe comenzar directamente con la llave de apertura

        DEVUELVE SOLO JSON VÁLIDO, EJEMPLO:
        {
            "id": "XXX", 
            "name": "XXX"
        }
    """

    response = client.chat.completions.create(
        model=DEPLOYMENT_NAME,
        messages=[
            {
                "role": "system",
                "content": prompt
            },
            {
                "role": "user",
                "content": user_content
            }
        ],
        max_tokens=100
    )

    return response.choices[0].message.content.strip()

def generar_descripcion_defecto(image_path, name=None, test_name=None, status=None,
                                 error_message=None, error_type=None, error_details=None):
    resumen = "Resumen del Test Run:\n"
    
    if name:
        resumen += f"name: {name}\n"
    if test_name:
        resumen += f"test_name: {test_name}\n"
    if status:
        resumen += f"status: {status}\n"
    if error_message:
        resumen += f"error_message: {error_message}\n"
    if error_type:
        resumen += f"error_type: {error_type}\n"
    if error_details:
        resumen += f"error_details: {error_details}\n"

    image_exists = image_path and os.path.exists(image_path)
    image_base64 = encode_image_to_base64(image_path) if image_exists else None

    user_content = [{"type": "text", "text": resumen}]
    if image_base64:
        user_content.append({
            "type": "image_url",
            "image_url": {
                "url": f"data:image/jpeg;base64,{image_base64}"
            }
        })

    prompt = """
        Eres un experto en QA. Genera una descripción clara y profesional del defecto mostrado basado en los datos del automation run. 
        Si se proporciona una imagen, úsala como referencia visual.
        FORMATO DE RESPUESTA:
        - NO uses etiquetas como # o -
        - Se claro y conciso
        - Usa lenguaje profesional
        - Responde en español
    """
    response = client.chat.completions.create(
        model=DEPLOYMENT_NAME,
        messages=[
            {
                "role": "system",
                "content": prompt
            },
            {
                "role": "user",
                "content": user_content
            }
        ],
        max_tokens=300
    )

    return response.choices[0].message.content.strip()