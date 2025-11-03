---
name: qa-test-plan-agentic
on:
  workflow_dispatch:
  command:
    name: qa-testplan
permissions:
  contents: read
  actions: read
  checks: read
engine:
  id: copilot
concurrency: qa-test-plan-agentic
timeout_minutes: 90
safe-outputs:
  add-comment:
    max: 1
env:
  MAPFRE_BASE_URL: "https://mapfrespain-test.appiancloud.com/suite/sites/sgo?signin=native"
  MAPFRE_USER: "${{ secrets.APPIAN_USER }}"
  MAPFRE_PASSWORD: "${{ secrets.APPIAN_PASS }}"
  TEST_PLAN_OUTPUT_DIR: "test-plan-output"
  TEST_PLAN_FILENAME: "SGO-Test-Plan.md"
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
  - name: Inicializar directorio de salida del Test Plan
    run: |
      mkdir -p "${TEST_PLAN_OUTPUT_DIR}"
      if [ ! -f "${TEST_PLAN_OUTPUT_DIR}/${TEST_PLAN_FILENAME}" ]; then
        touch "${TEST_PLAN_OUTPUT_DIR}/${TEST_PLAN_FILENAME}"
      fi
      ls -la "${TEST_PLAN_OUTPUT_DIR}"
post-steps:
  - name: Publicar artefacto Test Plan
    uses: actions/upload-artifact@v4
    with:
      name: sgo-test-plan
      path: ${{ env.TEST_PLAN_OUTPUT_DIR }}
      if-no-files-found: error
---

# QA Mapfre – Generador Agentic de Test Plan Exhaustivo

**Objetivo general:** Elaborar y adjuntar como artefacto un Test Plan completo que cubra exhaustivamente la plataforma SGO, desde el login hasta los últimos flujos accesibles.

## Indicaciones principales

1. **Revisión de historias de usuario en el repositorio**
   - Buscar en carpetas comunes (`docs/`, `stories/`, `requirements/`, `jira/`, `user-stories/`, `*.md`, `*.story`, etc.).
   - Si existen historias, extraer títulos, criterios de aceptación y campos relevantes; usarlos como eje del Test Plan.
   - Si no existen, basar la planificación íntegramente en la exploración de la aplicación.

2. **Exploración exhaustiva con Playwright MCP**
   - Autenticarse usando las credenciales proporcionadas (`SGO_PRUEBAS1` / `Mapfre2023`).
   - Validar que tras el login la URL contiene `/suite/sites/sgo`.
   - Navegar metódicamente por todas las secciones y subflujos disponibles.
   - Para cada pantalla, capturar locators estables en orden de prioridad: `id` > `name` > `data-*` > texto > XPath sin índices numéricos.
   - Registrar cada acción con `playwright_browser_click`, `playwright_browser_type`, `playwright_browser_wait_for`, y verificar estados con `playwright_browser_evaluate`.
   - Documentar cualquier estado alternativo, mensajes de error, reglas de negocio y dependencias de datos.

3. **Construcción del Test Plan**
   - Estructura recomendada:
     - Resumen ejecutivo.
     - Alcance y fuera de alcance.
     - Referencias de historias de usuario o requisitos detectados.
     - Matriz de casuística (login, navegación, bandejas, creación de solicitud, reportes, administración, etc.).
     - Casos de prueba detallados (ID, objetivo, precondiciones, pasos, datos, resultados esperados, severidad, prioridad, automatización sugerida).
     - Cobertura de riesgos, pruebas negativas, no funcionales e integraciones externas.
     - Trazabilidad entre funcionalidades descubiertas y casos.
   - Generar IDs consecutivos (`TP-###`). Separar entre pruebas manuales, automatizadas y candidatos a automatización.
   - Incorporar tablas o listas donde agreguen valor; evitar contenido redundante.

4. **Generación del artefacto**
   - Crear la carpeta `${{ env.TEST_PLAN_OUTPUT_DIR }}` en la raíz del repositorio si no existe.
   - Escribir el Test Plan completo en `${{ env.TEST_PLAN_OUTPUT_DIR }}/${{ env.TEST_PLAN_FILENAME }}`.
   - Incluir al final del documento un resumen de cobertura.
   - Verificar que el archivo esté en formato Markdown legible.

5. **Reporte final dentro del Test Plan**
   - Incluir una sección "Resumen de Ejecución" indicando: cantidad de flujos explorados, casos identificados, riesgos detectados y dependencias.
   - Registrar si se encontraron limitaciones de acceso o datos ficticios.

## Reglas y recordatorios

- No exponer credenciales en archivos finales (solo mencionarlas en la sección dedicada si es imprescindible documentar accesos).
- Respetar el orden cronológico de las exploraciones y documentar evidencia suficiente para reproducir cada camino.
- Si se detectan bloqueos (performance, errores), documentarlos con detalle.
- Conservar sesiones de Playwright activas hasta completar la verificación de cada flujo.
- No crear Pull Requests ni modificaciones permanentes fuera de la carpeta designada.

## Entrega obligatoria

- Archivo Markdown: `${{ env.TEST_PLAN_OUTPUT_DIR }}/${{ env.TEST_PLAN_FILENAME }}`.
- El paso "Publicar artefacto Test Plan" subirá automáticamente la carpeta generada; asegúrate de que el archivo exista y esté completo.
- En la salida principal del workflow, indicar la ruta del archivo generado y el número total de casos documentados.
