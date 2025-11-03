import os
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

def format_description_to_html(description):
    """
    Convierte texto plano a HTML con formato apropiado
    """
    if not description:
        return ""
    
    # Convertir saltos de línea a <br>
    html_desc = str(description).replace('\n', '<br>')
    # Reemplazar **texto** con <strong>texto</strong> para negritas
    import re
    html_desc = re.sub(r'\*\*(.*?)\*\*', r'<strong>\1</strong>', html_desc)
    
    return f"<html><body><p>{html_desc}</p></body></html>"

# Headers
HEADERS = {
    'Content-Type': 'application/json',
    'ALM_OCTANE_TECH_PREVIEW': 'true'
}

def create_defect(cookie, defect_data):
    """
    Crea un nuevo defect en Octane
    
    Args:
        cookie: Cookie de autenticación
        defect_data: Diccionario con los datos del defect
    
    Returns:
        Response JSON si es exitoso, None si hay error
    """
    fields = (
        "creation_time,parent,defect_root_level,version_stamp,program,waste_category,"
        "team_phase,workspace_id,path,num_comments,is_deleted_entity,rank,last_modified,"
        "fixed_on,covering_tests,has_children,pull_requests_count,reopens,priority,"
        "status_change_time_udf,user_tags,subject_udf,taxonomies,estimated_hours,"
        "access_granted,limit_line,ordering,search_result_in_fields,activity_level,"
        "followed_by_me,blocked,invested_hours,dependence,supplier_udf,logical_path,"
        "has_attachments,linked_items2,story_points,linked_items1,global_text_search_result,"
        "owners_count,team,trend_drill_down_field_new_values,progress,cycle_time_expiration,"
        "product_udf,original_id,acceptedrejection_udf,sprint,detection_date_udf,"
        "cycle_time_value,fixed_in_build,trend_drill_down_field_old_values,"
        "expected_resol_date_udf,item_origin,committers,time_in_current_team_phase,"
        "reason_for_rejection_udf,phase_age,ancestors,defect_type,waste,client_lock_stamp,"
        "author,product_areas,remaining_hours,resolution_descript_udf,last_runs,"
        "wrong_rejection_udf,commit_count,commit_files,id_net_udf,referenced_count,"
        "linked_processes,tasks_number,has_comments,dependency_problem_type,name,"
        "defect_category_udf,metaphase,detected_in_build,depends_on_count,logical_name,"
        "assigned_to_me,origin,description,flag_rules,phase_to_time_in_phase,has_coverage,"
        "detected_by,qa_owner,is_draft,closed_on,is_in_filter,repeating_phase,new_tasks,"
        "severity,owner,requirements,depends_on,application_udf,status_history_udf,"
        "branches_count,processes_count,blocked_reason,dependence_count,vulnerabilities,"
        "resolution_date_udf,category,time_in_current_phase,detected_in_environments,"
        "subtype,author{full_name},detected_by{full_name},qa_owner{full_name},"
        "owner{full_name},release{end_date},phase{master_phase,metaphase},"
        "run{sprint,subtype},detected_in_release{end_date},"
        "milestone{release_specific,date,release},parent{subtype},team_phase{subtype},"
        "taxonomies{subtype},dependence{subtype},linked_items2{subtype},"
        "linked_items1{subtype},linked_processes{subtype},requirements{subtype},"
        "depends_on{subtype}"
    )
    
    url = (
        f"{OCTANE_URL}api/shared_spaces/{OCTANE_SHARED_SPACE}/workspaces/{OCTANE_WORKSPACE}/"
        f"work_items?fetch_single_entity=true&fields={fields}"
    )
    
    response = requests.post(url, headers=HEADERS, cookies=cookie, 
                           json=defect_data, verify=False)
    
    if response.status_code == 201:
        return response.json()
    else:
        print(f"Error al crear defect: {response.status_code}")
        print(f"Response: {response.text}")
        return None


def create_example_defect_body(run_id=None, parent_id="2002", author_id="24007", 
                              owner_id="24007", detected_by_id="24007", group="9qe0r0j1ojw9dcvk60gdg31jm",description="Error de ejemplo generado por IA"):
    """
    Crea el cuerpo del defect con valores de ejemplo
    
    Args:
        run_id: ID del run (opcional, usa AUTOMATION_RUN_ID por defecto)
        parent_id: ID del elemento padre
        author_id: ID del autor
        owner_id: ID del propietario
        detected_by_id: ID de quien detectó el defect
        group: ID de la categoría del defect
        description: Descripción del defect
    
    Returns:
        Diccionario con la estructura del defect
    """
    
    return {
        "data": [
            {
                "name": "Test automation defect",
                "description": format_description_to_html(description),
                "subtype": "defect",
                "product_areas": {
                    "data": []
                },
                "run": {
                    "data": [
                        {
                            "type": "run",
                            "id": str(run_id)
                        }
                    ]
                },
                "detected_in_release": None,
                "parent": {
                    "type": "work_item",
                    "id": str(parent_id)
                },
                "author": {
                    "type": "workspace_user",
                    "id": str(author_id)
                },
                "phase": {
                    "type": "phase",
                    "id": "phase.defect.new"
                },
                "detected_by": {
                    "type": "workspace_user",
                    "id": str(detected_by_id)
                },
                "priority": {
                    "type": "list_node",
                    "id": "list_node.priority.medium"
                },
                "severity": {
                    "type": "list_node",
                    "id": "list_node.severity.medium"
                },
                "owner": {
                    "type": "workspace_user",
                    "id": str(owner_id)
                },
                "program": {
                    "type": "program",
                    "id": "63007"
                },
                "taxonomies": {
                    "data": [
                        {
                            "type": "taxonomy_node",
                            "id": "7046"
                        }
                    ]
                },
                "application_udf": {
                    "type": "list_node",
                    "id": "zeg1odek1195rfk8g7z5plwj7"
                },
                "defect_category_udf": {
                    "type": "list_node",
                    "id": str(group)
                }
            }
        ]
    }
