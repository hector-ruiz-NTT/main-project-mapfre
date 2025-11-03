---
name: qa-test-plan-doc-agentic
on:
  workflow_run:
    workflows: ["qa-test-plan-agentic"]
    types: [completed]
  workflow_dispatch:
permissions:
  contents: read
  actions: read
  checks: read
engine:
  id: copilot
concurrency: qa-test-plan-doc-agentic
timeout_minutes: 90
if: github.event_name != 'workflow_run' || github.event.workflow_run.conclusion == 'success'
safe-outputs:
  add-comment:
    max: 1
env:
  MAPFRE_BASE_URL: "https://mapfrespain-test.appiancloud.com/suite/sites/sgo?signin=native"
  MAPFRE_USER: "${{ secrets.APPIAN_USER }}"
  MAPFRE_PASSWORD: "${{ secrets.APPIAN_PASS }}"
  TEST_PLAN_ARTIFACT_NAME: "sgo-test-plan"
  TEST_PLAN_DIR: "test-plan-input"
  TEST_PLAN_FILENAME: "test-plan-output/SGO-Test-Plan.md"
  DOC_ROOT: "tests-documentation"
  PLAYWRIGHT_INIT_SCRIPT: "/tmp/gh-aw/playwright-init/disable-blockers.js"
tools:
  edit:
  bash:
  playwright:
  github:
    allowed: [get_issue, list_issues, get_pull_request, list_pull_requests]
  agentic-workflows:
network:
  allowed:
    - defaults
    - playwright
    - java
    - "mapfrespain-test.appiancloud.com"
mcp-servers:
  playwright-custom:
    command: "npx"
    args:
      - "@playwright/mcp@latest"
      - "--output-dir"
      - "/tmp/gh-aw/mcp-logs/playwright"
      - "--allowed-origins"
      - "*"
      - "--no-sandbox"
      - "--ignore-https-errors"
      - "--block-service-workers"
      - "--user-agent"
      - "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
      - "--init-script"
      - "${{ env.PLAYWRIGHT_INIT_SCRIPT }}"
steps:
  - name: Descargar artefacto del Test Plan base
    run: |
      mkdir -p "${TEST_PLAN_DIR}"
      gh run download "${{ github.event.workflow_run.id || github.run_id }}" \
        --repo "${{ github.repository }}" \
        --name "${TEST_PLAN_ARTIFACT_NAME}" \
        --dir "${TEST_PLAN_DIR}"
      ls -R "${TEST_PLAN_DIR}"
  - name: Preparar entorno Playwright (browser automation)
    run: |
      echo "Node version:" && node -v || true
      mkdir -p temp-agent && cd temp-agent
      npm init -y
      npm install playwright@latest --no-audit --no-fund
      npx playwright install chromium --with-deps
  - name: Crear script de inicialización para deshabilitar bloqueadores
    run: |
      mkdir -p /tmp/gh-aw/playwright-init
      echo 'KGZ1bmN0aW9uKCkgewogIGNvbnNvbGUubG9nKCJJbml0IHNjcmlwdCBydW5uaW5nIik7CiAgaWYgKHdpbmRvdy5uYXZpZ2F0b3IgJiYgd2luZG93Lm5hdmlnYXRvci5zZXJ2aWNlV29ya2VyKSB7CiAgICB3aW5kb3cubmF2aWdhdG9yLnNlcnZpY2VXb3JrZXIuZ2V0UmVnaXN0cmF0aW9ucygpLnRoZW4oZnVuY3Rpb24ocmVnaXN0cmF0aW9ucykgewogICAgICByZWdpc3RyYXRpb25zLmZvckVhY2goZnVuY3Rpb24ocmVnKSB7IHJlZy51bnJlZ2lzdGVyKCk7IH0pOwogICAgfSk7CiAgfQogIE9iamVjdC5kZWZpbmVQcm9wZXJ0eShuYXZpZ2F0b3IsICJ3ZWJkcml2ZXIiLCB7IGdldDogKCkgPT4gZmFsc2UgfSk7CiAgY29uc29sZS5sb2coIkluaXQgc2NyaXB0IGNvbXBsZXRlIik7Cn0pKCk7Cg==' | base64 -d > ${PLAYWRIGHT_INIT_SCRIPT}
      echo "Script created successfully"
      ls -la /tmp/gh-aw/playwright-init/
post-steps:
  - name: Publicar documentación generada
    uses: actions/upload-artifact@v4
    with:
      name: sgo-test-documentation
      path: |
        tests-documentation
        listado-casosdeprueba.md
      if-no-files-found: warn
---

# QA Mapfre – Documentación y Validación de Casos desde Test Plan

**Objetivo:** Consumir el Test Plan generado por `qa-test-plan-agentic`, validar cada caso usando Playwright MCP y producir toda la documentación exigida (carpetas, archivos, formatos, tablas y pasos) descrita en esta instrucción.

## Flujo de trabajo obligatorio

1. **Preparación**
   - Confirmar que el artefacto `${{ env.TEST_PLAN_ARTIFACT_NAME }}` se descargó en `${{ env.TEST_PLAN_DIR }}`.
   - Abrir `"${{ env.TEST_PLAN_DIR }}/${{ env.TEST_PLAN_FILENAME }}"` y listar todos los IDs de casos (formato `TP-###`).
   - Registrar si el Test Plan incluye referencias a historias de usuario; mantener esa trazabilidad en la documentación.

2. **Selección de casos para documentar**
  - Cada caso del Test Plan debe mapearse a un flujo concreto.
  - Crear subcarpeta en `${{ env.DOC_ROOT }}` con formato `<CP_ID>-<CLASE_FLUJO>` y dentro generar las carpetas hijas obligatorias `gherkin/`, `ui-elements/` y `steps/`.
  - Reutilizar IDs o generar nuevos (`CP###`) según convenga, manteniendo una tabla de equivalencias con los IDs `TP-###` del plan.

3. **Validación con Playwright MCP**
   - Para cada caso:
     - Iniciar sesión en `${{ env.MAPFRE_BASE_URL }}` usando `playwright_browser_type` y `playwright_browser_click` (`SGO_PRUEBAS1` / `Mapfre2023`).
     - Verificar URL contiene `/suite/sites/sgo`.
     - Navegar por todos los pasos descritos en el Test Plan, confirmando locators con `playwright_browser_evaluate`.
     - Documentar atributos faltantes (usar `null` explícito donde no existan).
     - Capturar estados alternos, mensajes y dependencias de datos (ej. IDs de operación, opciones de menú).
   - Si algún locator falla, ajustar y revalidar antes de documentar.

4. **Documentación requerida por caso**
   - En cada carpeta `<CP_ID>-<CLASE_FLUJO>` crear los archivos:
     - `/<CP_ID>-<CLASE_FLUJO>.md`: Descripción, objetivo, precondiciones, tabla de locators validados (columnas sugeridas: Elemento | Selector | Atributos | Validación | Estado) y notas relevantes.
     - Ejemplo de tabla de locators:
        ´´´
        Elemento | Selector | ID | Locator | Validación | Estado |
        |----------|----------|----|---------|-----------| --------|
        | Usuario  | input[type="text"] | `un` | `//input[@id='un']` | ID estable confirmado | ✅ |
        | Contraseña | input[type="password"] | `pw` | `//input[@id='pw']` | ID estable confirmado | ✅ |
        | Botón Login | input[type="submit"] | `jsLoginButton` | `//input[@id='jsLoginButton']` | ID estable confirmado | ✅ |
        | Banner Post-Login | div | N/A | `//div[@role='banner']` | Role attribute confirmado | ✅ |
        | Main Content | main | N/A | `//main` | Tag confirmado | ✅ |
        | URL Pattern | String | N/A | contains('/suite/sites') | Pattern confirmado | ✅ |
        ´´´
     - `/gherkin/<CP_ID>_<CLASE_FLUJO>.feature`: Incluir todos los escenarios posibles por cada caso de prueba, sin caso de prueba se necesita de un logeo, no se debe de dar por hecho que se conoce el proceso de login (se tiene que ser expplicito en cada proceso del gherkin). Utiliza el siguiente patrón general:
        ```gherkin
        Feature: Creación de solicitud en Mapfre SGO
        Como usuario de SGO
        Quiero crear una nueva solicitud por ramo/palabra clave
        Para gestionar duplicados de pólizas de auto

        Scenario: Crear solicitud de duplicados para póliza de auto mediante NIF
            Given navego a la página de login 'https://mapfrespain-test.appiancloud.com/suite/sites/sgo?signin=native'
            When introduzco el usuario 'SGO_PRUEBAS1' en el campo de usuario
            And introduzco la contraseña 'Mapfre2023' en el campo de contraseña
            And pulso el botón 'ENTRAR'
            Then soy redirigido a la página principal de SGO
            And espero a que el DOM de la pantalla cargue completamente
            
            When pulso sobre el botón 'CREAR SOLICITUD'
            Then navego a la página 'https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/crear-solicitud'
            And espero a que el DOM de la pantalla cargue completamente
            
            When pulso en el icono del lápiz junto al campo 'NUUMA TEST'
            And escribo 'EFERNA2' en el campo NUUMA TEST
            And selecciono la opción 'Por ramo/Palabra Clave'
            And pulso sobre el botón 'CONTINUAR'
            And espero a que el DOM de la pantalla cargue completamente
            
            When escribo el DNI '50098501Q' en el campo de búsqueda
            And pulso el botón 'VER RESULTADOS'
            And espero a que el DOM de la pantalla cargue completamente
            Then se muestran las pólizas asociadas al DNI
            
            When selecciono una póliza de tipo 'AUTO'
            And pulso el botón 'CONTINUAR'
            And espero a que el DOM de la pantalla cargue completamente
            
            When selecciono 'Solicitud de duplicados' en el desplegable de Tarea
            And pulso el botón 'BUSCAR'
            And espero a que el DOM de la pantalla cargue completamente
            Then se muestra un resultado coincidente para la tarea seleccionada
            
            When pulso sobre el botón 'CONTINUAR'
            And espero a que el DOM de la pantalla cargue completamente
            And escribo 'CP101_CREACIÓN DE SOLICITUDES_Acceso por Ramo / Palabra Clave. NIF/ CIF' en el campo 'Información adicional'
            And pulso el botón 'ENVIAR SOLICITUD'
            And espero a que el DOM de la pantalla cargue completamente
            Then la solicitud es creada exitosamente
            
            When pulso sobre la opción 'BANDEJAS' en el menú de navegación
            And espero a que el DOM de la pantalla cargue completamente
            Then navego a 'https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/bandejas'
            
            When pulso el icono del lápiz en el campo 'NUUMA TEST'
            And escribo 'EFERNA2' en el campo NUUMA TEST
            And pulso el botón 'BUSCAR SOLICITUD'
            And espero a que los resultados se carguen
            Then se muestra el ID de operación 'SGO11202510270000024' correctamente
        ```
     - `/ui-elements/<CP_ID>_<CLASE_FLUJO>-elements.md`: Tabla de elementos UI siguiendo la referencia:
       ```
                +----------------------+----------------------+-----------------------------------------------+
            | Nombre común         | Etiqueta HTML        | Descripción breve                             |
            +----------------------+----------------------+-----------------------------------------------+
            | botón                | button               | Botones interactivos.                         |
            | enlace               | a                    | Hipervínculos.                                |
            | imagen               | img                  | Mostrar imágenes.                             |
            | entrada de texto     | input                | Campo de texto (text, password, email...).    |
            | área de texto        | textarea             | Campo multilínea.                             |
            | contenedor genérico  | div                  | Agrupa elementos sin semántica específica.    |
            | línea de separación  | hr                   | Línea horizontal.                             |
            | lista sin orden      | ul                   | Lista con viñetas.                            |
            | lista con orden      | ol                   | Lista numerada.                               |
            | elemento de lista    | li                   | Ítem dentro de ul o ol.                       |
            | párrafo              | p                    | Texto en párrafo.                             |
            | cabecera             | h1-h6                | Encabezados.                                  |
            | contenedor en línea  | span                 | Contenido en línea agrupado.                  |
            | imagen vectorial     | svg                  | Gráfico vectorial escalable.                  |
            | formulario           | form                 | Contiene campos para enviar datos.            |
            | etiqueta formulario  | label                | Asociada a inputs para accesibilidad.         |
            | sección principal    | main                 | Contenido principal.                          |
            | sección artículo     | article              | Contenido autocontenible.                     |
            | sección navegación   | nav                  | Menú de navegación.                           |
            | pie de página        | footer               | Información al final.                         |
            | encabezado           | header               | Encabezado de página/sección.                 |
            | botón de envío       | input type="submit"  | Envía un formulario.                          |
            +----------------------+----------------------+-----------------------------------------------+
       ```
       Amplía la tabla con los elementos detectados en el flujo.
     - `/steps/<CP_ID>_<CLASE_FLUJO>-steps.md`: Pasos en imperativo, agrupados por pantalla. Ejemplo de formato:
       ```
        CASO DE PRUEBA_2: CREACION_SOLICITUD
        -> LOGIN: 'https://mapfrespain-test.appiancloud.com/suite/sites/sgo?signin=native'
        - Navegar a la pagina 'https://mapfrespain-test.appiancloud.com/suite/sites/sgo?signin=native'
        - Introducir usuario y contraseña (SGO_PRUEBAS1 / Mapfre2023)
        - Pulsar en el boton 'ENTRAR'
        - Esperar a que cargue el DOM de la pantalla
        -> Home: https://mapfrespain-test.appiancloud.com/suite/sites/sgo
        - Pulsar sobre el botón 'CREAR SOLICITUD'
        - Esperar a que cargue el DOM de la pantalla
        -> CrearSolicitud: 'https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/crear-solicitud'
        - Pulsa en el icono del lapiz junto al campo NUUMA TEST: y escribe 'EFERNA2'
        - Selecciona el botón 'Por ramo/Palabra Clave'
        - Pulsa sobre el botón 'CONTINUAR'
        - Esperar a que cargue el DOM de la pantalla
        - Escribir el DNI '50098501Q' y pulsa en 'VER RSULTADOS'
        - Esperar a que cargue el DOM de la pantalla
        - Seleccionar una póliza de AUTO y pulsa 'CONTINUAR'
        - Esperar a que cargue el DOM de la pantalla
        - Sobre el desplegable de Tarea informa 'Solicitud de duplicados' y pulsa el botón 'BUSCAR'
        - Esperar a que cargue el DOM de la pantalla
        - Se muestra un resultado coincidente
        - Pulsamos sobre el boton 'CONTINUAR'
        - Esperar a que cargue el DOM de la pantalla
        - En el campo de texto 'Informacion adicional' escribimos lo siguiente 'CP101_CREACIÓN DE SOLICITUDES_Acceso por Ramo / Palabra Clave. NIF/ CIF'
        - Pulsamos en enviar solicitud
        - Esperar a que cargue el DOM de la pantalla
        - Pulsar sobre la opcion 'BANDEJAS'
        - Esperar a que cargue el DOM de la pantalla
        -> Bandejas: 'https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/bandejas'
        - Sobre el campo NUUMA TEST, pulsa el lapiz y escribe 'EFERNA2'
        - Pulsar sobre buscar Solicitud
        - Comprobar que se muestra el ID Operación correctamente (SGO11202510270000024)
       ```
       IMPORTANTE crear otro caso de prueba según escenarios se detecten en el gherkin
   - Actualizar/crear `listado-casosdeprueba.md` en la raíz de `tests-documentation/` con el inventario completo, indicando origen (`TP-###`) y estado de documentación.

6. **Informe final**
   - Incluir al final de cada archivo principal `<CP_ID>-<CLASE_FLUJO>.md` el bloque de resultados:
     ```
     ## RESULTADO FLUJO <CP_ID>_<FLOW_NAME>
     Estado: ✅ COMPLETO | ⚠️ CON OBSERVACIONES | ❌ INCOMPLETO
     Casos documentados: N (referencias TP-###)
     Validaciones Playwright: ✅/❌ (detalle breve)
     Notas: <resumen conciso>
     ```
   - Documentar en `listado-casosdeprueba.md` el conteo global de casos y porcentaje cubierto.

7. **Artefactos**
   - Verificar que `tests-documentation/` y `listado-casosdeprueba.md` contienen todo lo requerido.
   - El paso de post-procesamiento subirá el contenido como artefacto `sgo-test-documentation`.
   - Incluir en la salida principal del workflow referencias a cada carpeta generada.

## Consideraciones de seguridad y calidad

- Si un flujo requiere datos adicionales, documentar los valores utilizados sin subir evidencia externa.
- En caso de encontrar errores en la aplicación, añadir sección "Riesgos detectados" en el archivo del caso.
- Mantener sesiones Playwright activas solo durante la validación; cerrar cuando finalice cada flujo.
- Evitar duplicar contenido en múltiples archivos; centralizar referencias en `listado-casosdeprueba.md` y enlazar desde cada caso.

## Entregables

- Documentación completa en `tests-documentation/` para todos los casos derivados del Test Plan.
- Archivo `listado-casosdeprueba.md` actualizado con estado y trazabilidad.
- Reporte de validaciones Playwright por caso.
- Artefacto `sgo-test-documentation` adjuntado automáticamente.
