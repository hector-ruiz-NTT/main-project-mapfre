---
name: qa-mapfre-agentic
on:
  workflow_dispatch:
    inputs:
      custom_instructions:
        description: "Instrucciones adicionales que el agente debe revisar antes de ejecutar el flujo"
        required: false
        default: ""
  command:
    name: qa-mapfre
permissions:
  contents: read
  actions: read
  checks: read
  issues: write
  pull-requests: write
engine:
  id: copilot
concurrency: qa-mapfre
timeout_minutes: 60
safe-outputs:
  create-pull-request:
    title-prefix: "[QA Mapfre] "
    labels: [qa, automation, selenium]
    draft: true
  add-comment:
    max: 1
env:
  MAPFRE_BASE_URL: "https://mapfrespain-test.appiancloud.com/suite/sites/sgo?signin=native"
  MAVEN_SUITE_CMD: "mvn clean verify \"-DsuiteXmlFile=testng.xml\""
  MAPFRE_USER: "${{ secrets.APPIAN_USER }}"        # Mapeado a secreto existente
  MAPFRE_PASSWORD: "${{ secrets.APPIAN_PASS }}"    # Mapeado a secreto existente
tools:
  edit:
  bash:
  playwright:
  github:
    allowed: [get_issue]
  agentic-workflows:
network:
  allowed:
    - defaults          # Infra básica
    - playwright        # Ecosistema Playwright
    - java              # Maven repos si fueran necesarios
    - "mapfrespain-test.appiancloud.com"  # Dominio AUT bajo prueba
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
      - "/tmp/gh-aw/playwright-init/disable-blockers.js"
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
      echo 'KGZ1bmN0aW9uKCkgewogIGNvbnNvbGUubG9nKCJJbml0IHNjcmlwdCBydW5uaW5nIik7CiAgaWYgKHdpbmRvdy5uYXZpZ2F0b3IgJiYgd2luZG93Lm5hdmlnYXRvci5zZXJ2aWNlV29ya2VyKSB7CiAgICB3aW5kb3cubmF2aWdhdG9yLnNlcnZpY2VXb3JrZXIuZ2V0UmVnaXN0cmF0aW9ucygpLnRoZW4oZnVuY3Rpb24ocmVnaXN0cmF0aW9ucykgewogICAgICByZWdpc3RyYXRpb25zLmZvckVhY2goZnVuY3Rpb24ocmVnKSB7IHJlZy51bnJlZ2lzdGVyKCk7IH0pOwogICAgfSk7CiAgfQogIE9iamVjdC5kZWZpbmVQcm9wZXJ0eShuYXZpZ2F0b3IsICJ3ZWJkcml2ZXIiLCB7IGdldDogKCkgPT4gZmFsc2UgfSk7CiAgY29uc29sZS5sb2coIkluaXQgc2NyaXB0IGNvbXBsZXRlIik7Cn0pKCk7Cg==' | base64 -d > /tmp/gh-aw/playwright-init/disable-blockers.js
      echo "Script created successfully"
      ls -la /tmp/gh-aw/playwright-init/
  - name: Configurar Chrome/ChromeDriver para CI (headless)
    run: |
      echo "=== Instalando Chrome y ChromeDriver para Selenium ==="
      
      # Verificar versión de Chrome pre-instalada en GitHub Actions
      google-chrome --version
      CHROME_VERSION=$(google-chrome --version | awk '{print $3}' | cut -d'.' -f1)
      echo "Chrome major version: $CHROME_VERSION"
      
      # Instalar ChromeDriver compatible usando el método oficial de Google
      CHROMEDRIVER_VERSION=$(curl -s "https://googlechromelabs.github.io/chrome-for-testing/LATEST_RELEASE_$CHROME_VERSION")
      echo "Installing ChromeDriver version: $CHROMEDRIVER_VERSION"
      
      wget -q "https://storage.googleapis.com/chrome-for-testing-public/$CHROMEDRIVER_VERSION/linux64/chromedriver-linux64.zip"
      unzip -q chromedriver-linux64.zip
      sudo mv chromedriver-linux64/chromedriver /usr/local/bin/
      sudo chmod +x /usr/local/bin/chromedriver
      rm -rf chromedriver-linux64.zip chromedriver-linux64
      
      # Verificar instalación
      chromedriver --version
      which chromedriver
      
      # Configurar variables de entorno para Selenium
      echo "CHROME_HEADLESS=true" >> $GITHUB_ENV
      echo "webdriver.chrome.driver=/usr/local/bin/chromedriver" >> $GITHUB_ENV
      
      echo "✅ Chrome y ChromeDriver configurados correctamente para CI"
post-steps:
  - name: Consolidar reportes JUnit XML
    if: always()
    run: |
      echo "📋 Consolidando reportes JUnit XML..."
      
      # Verificar que existe el directorio de JUnit reports
      JUNIT_DIR="plantilla_selenium_1/target/surefire-reports/junitreports"
      
      if [ -d "$JUNIT_DIR" ]; then
        # Contar archivos XML antes de consolidar
        XML_COUNT=$(find "$JUNIT_DIR" -name "TEST-*.xml" -type f | wc -l)
        echo "📄 Archivos JUnit XML encontrados: $XML_COUNT"
        
        if [ $XML_COUNT -gt 0 ]; then
          # Ejecutar consolidador Python
          python3 python/junit_consolidator.py "$JUNIT_DIR" "consolidated-junit-report.xml"
          
          # Verificar resultado
          if [ -f "$JUNIT_DIR/consolidated-junit-report.xml" ]; then
            echo "✅ Reporte consolidado generado exitosamente"
            echo "📦 Ubicación: $JUNIT_DIR/consolidated-junit-report.xml"
            
            # Mostrar tamaño del archivo
            CONSOLIDATED_SIZE=$(stat -f%z "$JUNIT_DIR/consolidated-junit-report.xml" 2>/dev/null || stat -c%s "$JUNIT_DIR/consolidated-junit-report.xml" 2>/dev/null)
            echo "📊 Tamaño del archivo consolidado: $CONSOLIDATED_SIZE bytes"
          else
            echo "⚠️ No se pudo generar el reporte consolidado"
          fi
        else
          echo "⚠️ No se encontraron archivos TEST-*.xml en $JUNIT_DIR"
        fi
      else
        echo "⚠️ Directorio $JUNIT_DIR no existe"
        echo "   Los reportes JUnit pueden no haberse generado correctamente"
      fi
  - name: Upload Test Results
    if: always()
    uses: actions/upload-artifact@v4
    with:
      name: maven-test-results
      path: |
        template-models/target/surefire-reports/junitreports
        template-models/ExtentReport/Screenshots/
      retention-days: 7
  - name: Guardar resultados de tests como artifacts
    uses: actions/upload-artifact@v4
    if: always()
    with:
      name: test-results-${{ github.run_number }}
      path: plantilla_selenium_1/

      retention-days: 30
      if-no-files-found: warn
  - name: Configure network access to internal Octane server
    if: ${{ success() }}
    run: |
      echo "10.228.134.59 wportalinterno wportalinterno.es.mapfre.net" | sudo tee -a /etc/hosts
  - name: Send Test Results to Octane
    if: ${{ success() }}
    uses: ./esp-calidad-octane-test-reporter-feature-convertJunitToOctane
    with:
      # Configuración de Octane (Requerido)
      octane-url: 'https://wportalinterno.es.mapfre.net/octane/'
      shared-space: '4006'
      workspace: '1005'
      client-id: ${{ secrets.OCTANE_CLIENT_ID }}
      client-secret: ${{ secrets.OCTANE_CLIENT_SECRET }}
      # Configuración de resultados (Requerido)
      # Directorio con archivos JUnit XML individuales y consolidado
      test-results-path: 'plantilla_selenium_1/target/surefire-reports/junitreports/consolidated-junit-report.xml'
      # Configuración del pipeline (Opcional)
      pipeline-name: 'QA-Mapfre-Agentic-Pipeline'
      job-name: 'Selenium Tests'
      build-number: ${{ github.run_number }}
      # Configuración avanzada (Opcional)
      github-server-url: ${{ github.server_url }}/${{ github.repository }}
      timeout: 120
      skip-ssl-verify: true
 
---

# QA Mapfre SGO – Workflow Agentic para Exploración y Generación de Test Cases Selenium

Este workflow ejecuta de forma automatizada el proceso de: Exploración del respositorio → Exploración con Playwright (MCP o BROWSER TOOL)→ Extracción de locators → Generación de código (Page/Imp/Test) → Validación Playwright → Ejecución Maven → Reporte y Pull Request. 
En caso no se pueda acceder a las credenciales son estas:
SGO_PRUEBAS1
Mapfre2023
Y una vez logeado detectar que la url sea: /suite/sites/sgo

# Instrucciones previas

{{#if ${{ github.event.inputs.custom_instructions }} }}
> **Revisar primero:** ${{ github.event.inputs.custom_instructions }}
{{/if}}

---

## 📋 INFORMACIÓN EXCLUSIVA: FLUJO CREACIÓN DE SOLICITUD
**⚠️ LEER ESTA SECCIÓN SOLO AL TRABAJAR CON CASOS DE PRUEBA DE CREACIÓN DE SOLICITUDES**

### 🔴 HALLAZGO CRÍTICO: NUUMA Manual Entry (OBLIGATORIO)
**⚠️ COMPORTAMIENTO CLAVE DEL SISTEMA:**
- **Seleccionar NUUMA del dropdown** → Tabla Favoritos VACÍA (0 tareas) ❌
- **Escribir NUUMA manualmente** → Tabla Favoritos POBLADA (45+ tareas) ✅
- De igual manera **TODO VALIDADO CON EL MCP O BROWSER TOOL DE PLAYWRIGHT** siempre

**Secuencia correcta obligatoria:**
1. Hacer clic en botón lápiz: `//button[contains(.,'Escribir nuuma especifico')]`
2. Limpiar campo: `campoNuuma.clear()`
3. Escribir valor: `campoNuuma.sendKeys("EFERNA2")`
4. **NO usar dropdown** - el comportamiento backend es diferente

### 🎯 Locators Críticos Validados con Playwright MCP O BROWSER TOOL:

#### **Página Crear Solicitud (crear-solicitud):**

**1. Campo NUUMA y Botón Lápiz:**
```java
// Botón para habilitar entrada manual (CRÍTICO - clic primero)
private static final String botonEscribirNuumaLapiz = "//button[contains(.,'Escribir nuuma especifico')]";

// Campo NUUMA (usar después del clic en lápiz)
private static final String campoNuuma = "//input[@placeholder='Escribir NUUMA...']";
// Playwright selector: getByRole('textbox', { name: 'NUUMA TEST:' })
```

**2. Selección de Tipo de Búsqueda:**
```java
// Radio button "Por ramo/Palabra clave"
// ⚠️ El label intercepta el clic - hacer clic en el label, NO en el input
private static final String radioPorRamo = "//input[@value='Por ramo/Palabra clave']";
private static final String labelPorRamo = "//label[contains(@for,'radioSelect')]//generic[text()='Por ramo/Palabra clave']";
// Playwright selector: getByText('Por ramo/Palabra clave').click()
```

**3. Botón CONTINUAR (Primera Pantalla):**
```java
// Botón CONTINUAR después de seleccionar tipo de búsqueda
// Playwright: Usa strong con filter hasText porque el link no tiene texto
private static final String botonContinuarBusqueda = "//strong[text()='CONTINUAR']/ancestor::div[contains(@class,'SideBySideGroup')]//a[@role='link']";
// Alternativa más robusta: buscar por paragraph con strong
```

**4. Búsqueda por NIF/CIF:**
```java
// Campo de entrada NIF/CIF
private static final String campoNIF = "//input[@placeholder='Escribir...']";
// Playwright selector: getByPlaceholder('Escribir...')

// Botón VER RESULTADOS
private static final String botonVerResultados = "//button[contains(.,'VER RESULTADOS')]";
// Playwright selector: getByRole('button', { name: 'VER RESULTADOS' })
```

**5. Selección de Póliza:**
```java
// Link "Seleccionar" en primera póliza (índice específico según necesidad)
private static final String linkSeleccionarPrimeraPoliza = "(//a[contains(text(),'Seleccionar')])[1]";

// Botón CONTINUAR SIN POLIZA
private static final String botonContinuarSinPoliza = "//strong[contains(text(),'CONTINUAR SIN POLIZA')]/ancestor::div[contains(@class,'SideBySideGroup')]//a[@role='link']";
// Playwright selector: getByRole('link').filter({ hasText: /^$/ }).nth(2)
```

**6. Selección de Tarea (Favoritos):**
```java
// Verificación título página
private static final String tituloSeleccionarTarea = "//strong[text()='Seleccionar Tarea']";

// Primera tarea de tabla Favoritos
private static final String primeraFavoritoLink = "//table//a[contains(@class,'LinkWidget') or contains(text(),'Comercialización')]";
// Playwright selector: getByRole('link', { name: 'Comercialización de Productos/Consultas/BK...', exact: true })
```

**7. Formulario Final y Botón ENVIAR:**
```java
// Campo Observaciones (OBLIGATORIO)
private static final String campoObservaciones = "//textarea[@placeholder='Introduzca una observación...']";
// Playwright selector: getByRole('textbox', { name: 'Observaciones' })

// ⚠️ BOTÓN ENVIAR SOLICITUD - SELECTOR CORREGIDO CON PLAYWRIGHT
// El botón NO es un <a> ni tiene role='link' en el texto
// Es un StampWidget (icono de flecha) que está en el mismo contenedor que el texto
private static final String botonEnviarSolicitud = "//strong[text()='ENVIAR SOLICITUD']/ancestor::div[contains(@class,'SideBySideGroup')]//div[contains(@class,'StampWidget') and @role='link']";
```

### 🔍 **Estructura DOM Real del Botón ENVIAR SOLICITUD (Validado con Playwright):**
```html
<!-- Contenedor padre SideBySideGroup -->
<div class="SideBySideGroup---side_by_side">
  
  <!-- Hermano 1: Solo texto (NO clickeable) -->
  <div class="SideBySideItem---flex_item">
    <p><strong>ENVIAR SOLICITUD</strong></p>
  </div>
  
  <!-- Hermano 2: Botón real clickeable (StampWidget) -->
  <div class="SideBySideItem---flex_item SideBySideItem---minimize">
    <div class="StampWidget---stamp" role="link" tabindex="0">
      <!-- Icono SVG de flecha derecha -->
      <svg>...</svg>
    </div>
  </div>
  
</div>
```

**Explicación del selector correcto:**
1. `//strong[text()='ENVIAR SOLICITUD']` - Encuentra el texto
2. `/ancestor::div[contains(@class,'SideBySideGroup')]` - Sube al contenedor padre compartido
3. `//div[contains(@class,'StampWidget') and @role='link']` - Busca el botón clickeable dentro

**JavaScript para validar con Playwright evaluate:**
```javascript
// Buscar el contenedor que tiene ambos elementos
const container = document.querySelector('.SideBySideGroup');
const hasText = container.textContent.includes('ENVIAR SOLICITUD');
const clickableStamp = container.querySelector('.StampWidget---stamp[role="link"]');

return {
  containerFound: !!container,
  hasText: hasText,
  stampClickable: !!clickableStamp,
  stampClasses: clickableStamp?.className,
  stampRole: clickableStamp?.getAttribute('role'),
  stampTabindex: clickableStamp?.getAttribute('tabindex')
};
```

### ⚠️ Errores Comunes y Soluciones:

1. **❌ Tabla Favoritos vacía después de seleccionar NUUMA**
   - **Causa**: Uso de dropdown en lugar de entrada manual
   - **Solución**: Implementar secuencia lápiz → clear → sendKeys
   - **Validación Playwright o browser tool**: `await page.getByRole('textbox', { name: 'NUUMA TEST:' }).fill('EFERNA2')`

2. **❌ TimeoutException en botón VER RESULTADOS**
   - **Causa**: Botón se deshabilita durante carga de pólizas
   - **Solución**: Esperar hasta 12 segundos con `waitForClickable`
   - **Validación Playwright o browser tool**: Verificar que botón tiene estado `enabled` antes de click

3. **❌ Radio button "Por ramo/Palabra clave" no seleccionable**
   - **Causa**: El `<input>` está oculto, el `<label>` intercepta el clic
   - **Solución**: Hacer clic en el label con `getByText('Por ramo/Palabra clave').click()`
   - **Validación Playwright o browser tool**: Verificar atributo `checked` después del clic

4. **❌ Múltiples botones CONTINUAR causan ambigüedad**
   - **Causa**: Appian reutiliza texto en diferentes contextos
   - **Solución**: Usar `ancestor::div[contains(@class,'SideBySideGroup')]` para contexto específico
   - **Validación Playwright o browser tool**: `getByRole('link').filter({ hasText: /^$/ }).nth(2)` con índice específico

5. **❌ Botón ENVIAR SOLICITUD no responde a clics**
   - **Causa**: Selector busca `<strong>` dentro de `div[@role='link']` pero están en hermanos separados
   - **Solución**: Usar ancestor al SideBySideGroup y buscar el StampWidget clickeable
   - **Validación Playwright o browser tool**: `await page.locator('.StampWidget---stamp[role="link"]').click()`

### 📊 JavaScript de Validación Playwright para Elementos Críticos:

**Validar visibilidad y estado de botón:**
```javascript
const button = document.querySelector('button:has-text("VER RESULTADOS")');
return {
  exists: !!button,
  disabled: button?.disabled,
  visible: button?.offsetParent !== null,
  className: button?.className
};
```

**Verificar tabla Favoritos poblada:**
```javascript
const table = document.querySelector('table');
const rows = table?.querySelectorAll('tbody tr');
return {
  tableExists: !!table,
  rowCount: rows?.length || 0,
  hasFavoritos: rows?.length > 0
};
```

**Validar campo NUUMA después de cambio a manual:**
```javascript
const input = document.querySelector('input[placeholder="Escribir NUUMA..."]');
return {
  inputExists: !!input,
  inputType: input?.type,
  placeholder: input?.placeholder,
  disabled: input?.disabled,
  value: input?.value
};
```

### 📊 SUB-CASOS DE PRUEBA IDENTIFICADOS (CP101):

---

## PASOS A SEGUIR SIEMPRE (OBLIGATORIO)

1. Realizar la exploración inicial de la aplicación (Mapfre SGO) usando Playwright MCP con el objetivo de encontrar todos los casos de prueba existentes dentro de la aplicación y armar un listado.

### Para cada caso encontrado realizar lo siguiente:
1. Hacer una exploración más detallada del caso de prueba
2. Generar los scripts de Page / Imp / Test
3. Documentación específica del flujo en la carpeta del caso (`tests-documentation/<CP_ID>-<CLASE_FLUJO>/`)
4. Validar los script nuevamente con la exploración de playwright
5. Documentar los locators validados
6. **Instalar librerías locales antes de ejecutar Maven:**
  - `cd esp-calidad-library-commons && mvn clean install`
  - `cd esp-calidad-selenium-driver-library && mvn clean install`
  - Estos comandos publican los artefactos `lib-selenium-commons` y `lib-selenium-driver` en el repositorio Maven local (`~/.m2`).
7. **Ejecución Maven OBLIGATORIA** con el comando: `mvn clean verify "-DsuiteXmlFile=testng.xml"` (NO SALTAR ESTE PASO)
8. Validar que el resultado haya sido con todos los tests, en caso haya un fallo, corregir.
9. Repetir con el siguiente flujo.

## ⚠️ ENTORNO CI COMPLETAMENTE CONFIGURADO
**EL NAVEGADOR Y CHROMEDRIVER ESTÁN DISPONIBLES:**
- ✅ Chrome está pre-instalado en GitHub Actions runners (ubuntu-latest)
- ✅ ChromeDriver se instala automáticamente en el step "Configurar Chrome/ChromeDriver para CI"
- ✅ Variables de entorno configuradas: `CHROME_HEADLESS=true` y `webdriver.chrome.driver=/usr/local/bin/chromedriver`
- ✅ DriverManager.java detecta automáticamente el ambiente CI y activa modo headless
- ✅ **NO HAY LIMITACIONES PARA EJECUTAR SELENIUM - El entorno está 100% funcional**

## ⚠️ COMANDO MAVEN OBLIGATORIO
**SIEMPRE ejecutar después de generar/actualizar código:**
```bash
cd plantilla_selenium_1
mvn clean verify "-DsuiteXmlFile=testng.xml"
```
- Este comando es CRÍTICO y NO debe omitirse
- NO usar alternativas como `mvn test` o `mvn clean test`
- El parámetro `-DsuiteXmlFile=testng.xml` es OBLIGATORIO
- **EL ENTORNO ESTÁ PREPARADO**: Chrome y ChromeDriver están instalados y configurados
- **DEBES EJECUTAR ESTE COMANDO**: No asumas que fallará, el entorno CI está completamente funcional
- En caso de falla es IMPORTANTE volver a utilizar el mcp de playwright o browser tool para validar los locators y corregir cualquier error antes de reintentar la ejecución de Maven

RESTRICCIONES ESTRICTAS:
- PROHIBIDO crear archivos globales (ej: guías, README, documento general) fuera del árbol específico del flujo, solo el inicial de recolección de casos de prueb.
- NO auditar ni compilar otros workflows en esta ejecución.
- NO crear Pull Request si no hubo cambios en Page/Imp/Test o documentación del flujo.
- SI el flujo ya estaba completo y los locators siguen válidos → emitir comentario "Sin cambios – flujo ya validado" y NO crear PR.
- PROHIBIDO tomar los casos de prueba del proyecto, SIEMRPE obtene los casos de prueba mediante la exploración inicial del playwright
- **PROHIBIDO OMITIR LA EJECUCIÓN DE MAVEN**: El entorno CI tiene Chrome y ChromeDriver instalados. NO asumas que Maven fallará por falta de navegador.
- **SI MAVEN FALLA**: SOLUCIONAR con validación de playwright y corrección de locators antes de reintentar

VALIDACIÓN PLAYWRIGHT (usar herramientas concretas):
1. Navegación: `playwright_browser_navigate` a `MAPFRE_BASE_URL`
2. Snapshot inicial: `playwright_browser_snapshot`
3. Para cada elemento: `playwright_browser_evaluate` (extraer id, name, aria-label, className, textContent, dataset)
4. Interacciones: `playwright_browser_click`, `playwright_browser_type`, `playwright_browser_wait_for` según necesidad
5. Verificación post-login: comprobar URL contiene `/suite/sites/sgo` y existencia del enlace Bandejas

SELECCIÓN DE LOCATORS:
Prioridad estricta: id > name > data-* > texto estable > XPath sin índices numéricos. Documentar explícitamente atributos null.

SALIDA ESTRUCTURADA FINAL (obligatoria dentro de cada caso de prueba):
```
## RESULTADO FLUJO <CP_ID>_<FLOW_NAME>
Estado: ✅ COMPLETO | ❌ FALLIDO | ⚠️ SIN CAMBIOS
Tests run: X | Failures: F | Errors: E | Skipped: S
Locators validados: N (detalle de cambios si los hubo)
Notas: <breve justificación>
```

NO GENERAR otras secciones fuera de esta estructura básica.

### Con respecto a la: Generación de Código

Crear/actualizar:
- Page: `plantilla_selenium_1/src/main/java/mapfre/paginas/${FLUJO_NORMALIZADO_DIR}/${CLASE_FLUJO}Page.java`
  - Locators privados `private static final String ...` + getters.
  - Archivo de ejemplo a usar como guia: `plantilla_selenium_1/src/main/java/mapfre/paginas/Ejemplo/EjemploPage.java`
- Implementation: `.../${CLASE_FLUJO}Imp.java` extiende `BaseActionsSelenium`.
  - Métodos de acción y verificación (esperas explícitas + asserts).
  - Archivo de ejemplo a usar como guia: `plantilla_selenium_1/src/main/java/mapfre/paginas/Ejemplo/EjemploImp.java`
- Test: `plantilla_selenium_1/src/test/java/mapfre/Tests/${TEST_CLASS}.java`
  - `@BeforeMethod`: iniciar Driver, instanciar `Imp`.
  - `@Test`: logs internos (inicio ejecución + pasos). Navegación con `getDriver().get(MAPFRE_BASE_URL)`.
  - `@AfterMethod`: `DriverManager.quitDriver()` sin logs.
  - Archivo de ejemplo a usar como guia: `plantilla_selenium_1/src/test/java/mapfre/Tests/CP00XX_Ejemplo.java`
- Actualizar `plantilla_selenium_1/testng.xml` agregando `<class name="mapfre.Tests.${TEST_CLASS}"/>` si no existe.

### Con respecto a la: Documentación

Crear carpeta: `tests-documentation/<CP_ID>-<CLASE_FLUJO>/` con:
- `<CP_ID>-<CLASE_FLUJO>.md`: Descripción, objetivo, precondiciones y locators validados del flujo.
- `gherkin/<CP_ID>_<CLASE_FLUJO>.feature`: Feature + Scenario principal (Given/When/Then funcional).
- `ui-elements/<CP_ID>_<CLASE_FLUJO>-elements.md`: Tabla de ui elements, según ejemplo
- `steps/<CP_ID>_<CLASE_FLUJO>-steps.md`: Pasos detallados del flujo, según ejemplo.

Dentro de la carpeta `tests-documentation`, pero fuera de todas las carpetas de los flujos:
- Crear un `listado-casosdeprueba.md`: Listado de todos los casos de prueba descubiertos en la exploración inicial

#### Con respecto a Como redactar feature files

##### Ejemplo Gherkin Autocontenido (con login explícito):
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

#### Con respecto a Como crear ui-elements

##### Tabla de Referencia UI (usar para clasificar elementos)
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

#### Con respecto a Como escribir los steps en imperativo

##### Ejemplo de pasos imperativos (ESTO ES SOLO UN EJEMPLO DE GUIA A REPLICAR EN TODOS LOS FLUJOS)

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

##### NOTA:
Crear más casos de prueba imperativos según escenarios creados en el Gherkin. Respetar el formato

##### Mapeo de Acciones a Elementos DOM
Al generar pasos imperativos, usar el siguiente mapeo para identificar elementos objetivo:

**Grupos Base:**
- CLICKABLES: button, a, span, div, @aria-label
- SELECTABLES: select, option, input[type="radio"], input[type="checkbox"], button, a, @aria-label
- INPUTS: input, textarea, div[contenteditable="true"], @placeholder, @value
- TEXTUALS: div, span, p, label, input, @title, @alt
- FILE_INPUTS: input[type="file"], button, label, @aria-label
- LINKABLES: a, button, li, div, @aria-label
- CONTROL_BUTTONS: button, a, svg, div, span, @aria-label
- FORM_ACTIONS: form, button, input[type="submit"], @aria-label
- TOGGLEABLES: input[type="checkbox"], button, a, toggle, @aria-label
- PROGRESSIVE: div, span, progress, @aria-label, @title
- INFORMATIVE: a, button, i, svg, div, @title, @aria-label, @alt

**Acciones Específicas:**
- **Interacción básica**: pulsar, hacer clic, clickar, presionar → CLICKABLES
- **Selección**: seleccionar, elegir, marcar → SELECTABLES
- **Entrada de texto**: escribir, introducir, rellenar, completar, ingresar, teclear → INPUTS
- **Navegación**: navegar, ir a, abrir → LINKABLES
- **Formularios**: buscar, filtrar, ordenar, confirmar, aceptar, enviar, cancelar, volver → FORM_ACTIONS/CONTROL_BUTTONS
- **Archivos**: subir, cargar, adjuntar → FILE_INPUTS
- **Control interfaz**: cerrar, salir, ocultar, minimizar, mostrar, desplegar, expandir, contraer → CONTROL_BUTTONS
- **Estado**: activar, desactivar, habilitar, deshabilitar → TOGGLEABLES
- **Validación**: validar, verificar, comprobar, revisar → TEXTUALS
- **Proceso**: esperar, procesar, cargar estado → PROGRESSIVE
- **Gestión**: eliminar, borrar, quitar, limpiar, guardar, descargar, exportar → CONTROL_BUTTONS
- **Sesión**: registrarse, iniciar sesión, loguearse → FORM_ACTIONS
- **Información**: ver detalles, ver información, buscar ayuda → INFORMATIVE

### Con respecto a la: Validación Playwright (Checkpoint)
- Simular acciones del test en el navegador usando los locators generados.
- Registrar resultado de cada acción (✅ / ❌). Si algún locator falla: regresar al de refinar locator antes de continuar
- Generar documentación de los locators validados

#### Ejemplo de Documentación de Locators
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

### Con respecto a la: Ejecución Maven (OBLIGATORIO - NO SALTAR)
**⚠️ PASO CRÍTICO:** Este paso es OBLIGATORIO y NO debe ser omitido bajo ninguna circunstancia.

**🚨 EL ENTORNO CI ESTÁ COMPLETAMENTE FUNCIONAL:**
- Chrome está instalado y configurado en modo headless
- ChromeDriver está instalado en `/usr/local/bin/chromedriver`
- DriverManager.java detecta automáticamente el ambiente CI
- **NO HAY RAZÓN PARA OMITIR ESTE PASO - TODO ESTÁ PREPARADO PARA EJECUTAR SELENIUM**

**Preparación previa (ejecutar una vez por corrida):**
```bash
cd esp-calidad-library-commons
mvn clean install

cd ../esp-calidad-selenium-driver-library
mvn clean install
```
- Estos `clean install` garantizan que los SNAPSHOT locales (`mapfre:lib-selenium-commons` y `mapfre:lib-selenium-driver`) estén presentes en `~/.m2/repository` antes de lanzar la suite.
- Si los módulos ya fueron instalados en la misma ejecución no es necesario repetirlos.

**Comando a ejecutar:**
```bash
cd plantilla_selenium_1
mvn clean verify "-DsuiteXmlFile=testng.xml"
```

**Requisitos:**
- SIEMPRE ejecutar desde el directorio `plantilla_selenium_1/`
- SIEMPRE usar el comando exacto: `mvn clean verify "-DsuiteXmlFile=testng.xml"`
- NO usar comandos alternativos como `mvn test` o `mvn clean test` (no ejecutan la suite completa correctamente)
- El parámetro `-DsuiteXmlFile=testng.xml` es OBLIGATORIO para que Maven ejecute la suite definida
- **EJECUTAR ESTE COMANDO SIN EXCEPCIONES** - El entorno está preparado, Chrome y ChromeDriver funcionan

**Acciones post-ejecución:**
- Parsear `target/surefire-reports/testng-results.xml` para obtener: Tests run, Failures, Errors, Skipped.
- Condición de éxito: `Failures == 0 && Errors == 0`.
- Si hay fallos, analizar logs en `target/surefire-reports/` y volver al Paso 2 si son problemas de locators.
- **SI EL TEST FALLA**: Reportar el error específico del log, NO asumir que es por falta de navegador


### Paso 8: Cierre Seguro
- No exponer credenciales en archivos generados.
- Asegurar que ningún locator se base en suposiciones sin evidencia.
- Todos los Casos de Prueba ejecutados y validados.


## Ejemplo de Evaluación de Elemento (Playwright)
JavaScript a ejecutar en `browser_evaluate`:
```javascript
const elem = document.querySelector('input[type="text"]');
({ id: elem.id || null, name: elem.name || null, ariaLabel: elem.getAttribute('aria-label') || null,
  className: elem.className || null, textContent: (elem.textContent || '').trim(), dataset: { ...elem.dataset } });
```

## Criterios de Éxito del Workflow
- Archivos Page/Imp/Test y documentación generados sin credenciales embebidas.
- Locators todos verificados (Playwright o browser tool) antes de ejecución Selenium.
- Maven BUILD SUCCESS sin Failures ni Errors.
- Todos los casos de pruebas cubiertos al 100%
- PR creado (draft) con cambios y comentario resumen.
- Artifacts correctamente generados

## Si el Flujo Falla
- Analizar el primer elemento fallido y ajustar locator.
- Repetir pasos

---

