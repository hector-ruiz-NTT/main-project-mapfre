# ALM Octane Test Reporter

GitHub Action para enviar resultados de pruebas automáticamente a ALM Octane. Soporta múltiples formatos de resultados de pruebas y proporciona integración completa con el pipeline de CI/CD.

## ✨ Características

- 🔄 **Múltiples formatos**: JUnit XML, TestNG XML, Jest JSON, Mocha JSON
- 🚀 **Alta performance**: Envío en lotes optimizado
- 🔒 **Seguro**: Autenticación OAuth2 con validación SSL
- 📊 **Métricas completas**: Tiempo de ejecución, estadísticas detalladas
- 🔧 **Configuración flexible**: Parámetros personalizables
- 🛡️ **Robusto**: Reintentos automáticos y manejo de errores
- 📝 **Trazabilidad**: Logs detallados y debugging

## 📋 Requisitos

- ALM Octane 15.1.20 o superior
- Credenciales OAuth2 configuradas en Octane
- GitHub Actions runner

### 🔧 Configuración para Servidores Internos

Si tu servidor ALM Octane está en una red interna y requiere resolución de DNS personalizada, agrega este paso **antes** de usar la acción:

```yaml
- name: Configure network access to internal Octane server
  run: echo "YOUR_SERVER_IP your-octane-domain.com" | sudo tee -a /etc/hosts
```

**Ejemplo:**
```yaml
- name: Configure network access to internal Octane server  
  run: echo "10.228.134.59 wportalinterno wportalinterno.es.mapfre.net" | sudo tee -a /etc/hosts

- name: Send Results to Octane
  uses: mapfre-tech/esp-calidad-octane-test-reporter@v1
  with:
    octane-url: 'https://wportalinterno.es.mapfre.net/octane/'
    # ... resto de configuración
```

## 🚀 Uso

### Uso Básico

```yaml
name: Tests with Octane Integration

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Run Tests
        run: |
          # Ejecutar tus pruebas aquí
          npm test
          # o pytest --junit-xml=results.xml
          # o mvn test
      
      - name: Send Results to Octane
        uses: ./octane-test-reporter
        with:
          octane-url: 'https://your-octane.domain.com'
          shared-space: '1001'
          workspace: '1002'
          client-id: ${{ secrets.OCTANE_CLIENT_ID }}
          client-secret: ${{ secrets.OCTANE_CLIENT_SECRET }}
          test-results-path: 'test-results/**/*.xml'
```

### Configuración Completa

```yaml
      - name: Send Results to Octane
        uses: ./octane-test-reporter
        with:
          # Configuración de Octane (Requerido)
          octane-url: 'https://your-octane.domain.com'
          shared-space: '1001'
          workspace: '1002'
          client-id: ${{ secrets.OCTANE_CLIENT_ID }}
          client-secret: ${{ secrets.OCTANE_CLIENT_SECRET }}
          
          # Configuración de resultados (Requerido)
          test-results-path: 'test-results/**/*.xml'
          
          # Configuración del pipeline (Opcional)
          pipeline-name: 'My Application Pipeline'
          job-name: 'Test Job'
          build-number: ${{ github.run_number }}
          
          # Configuración avanzada (Opcional)
          github-server-url: ${{ github.server_url }}
          timeout: 120
          skip-ssl-verify: false
```

## 📥 Inputs

### Requeridos

| Input | Descripción | Ejemplo |
|-------|-------------|---------|
| `octane-url` | URL base de ALM Octane | `https://octane.company.com` |
| `shared-space` | ID del Shared Space | `1001` |
| `workspace` | ID del Workspace | `1002` |
| `client-id` | Cliente ID OAuth2 | `my_client_id` |
| `client-secret` | Cliente Secret OAuth2 | `${{ secrets.OCTANE_SECRET }}` |
| `test-results-path` | Patrón de archivos de resultados | `test-results/**/*.xml` |

### Opcionales

| Input | Descripción | Default | Ejemplo |
|-------|-------------|---------|---------|
| `pipeline-name` | Nombre del pipeline en Octane | Nombre del repositorio | `My App Pipeline` |
| `job-name` | Nombre del job en Octane | `${{ github.job }}` | `Integration Tests` |
| `build-number` | Número de build | `${{ github.run_number }}` | `123` |
| `github-server-url` | URL del servidor GitHub | `${{ github.server_url }}` | `https://github.com` |
| `timeout` | Timeout en segundos | `60` | `120` |
| `skip-ssl-verify` | Omitir verificación SSL | `false` | `true` |

## 📤 Outputs

| Output | Descripción | Ejemplo |
|--------|-------------|---------|
| `octane-server-id` | ID del servidor CI en Octane | `github-server-123` |
| `octane-pipeline-id` | ID del pipeline en Octane | `pipeline-456` |
| `tests-sent` | Número de pruebas enviadas | `42` |
| `tests-passed` | Número de pruebas exitosas | `38` |
| `tests-failed` | Número de pruebas fallidas | `4` |

## 🎯 Formatos de Resultados Soportados

### JUnit XML
```xml
<?xml version="1.0" encoding="UTF-8"?>
<testsuites>
  <testsuite name="TestSuite" tests="2" failures="0" time="0.123">
    <testcase classname="com.example.Test" name="testMethod" time="0.045"/>
    <testcase classname="com.example.Test" name="testMethod2" time="0.078"/>
  </testsuite>
</testsuites>
```

### Jest JSON
```json
{
  "testResults": [
    {
      "name": "test.spec.js",
      "assertionResults": [
        {
          "title": "should pass",
          "status": "passed",
          "duration": 45
        }
      ]
    }
  ]
}
```

### TestNG XML
```xml
<?xml version="1.0" encoding="UTF-8"?>
<testng-results>
  <suite name="TestSuite">
    <test name="Test">
      <class name="com.example.Test">
        <test-method name="testMethod" status="PASS" duration-ms="45"/>
      </class>
    </test>
  </suite>
</testng-results>
```

### Mocha JSON
```json
{
  "tests": [
    {
      "title": "should pass",
      "state": "passed",
      "duration": 45
    }
  ]
}
```

## 🔧 Configuración de Octane

### 1. Crear Cliente OAuth2

1. En Octane, ve a **Settings → Spaces → API Access**
2. Click en **Create API Access**
3. Configura:
   - **Name**: `github-actions-client`
   - **Roles**: `CI/CD Integration`
   - **Scopes**: Todos los scopes necesarios
4. Guarda el **Client ID** y **Client Secret**

### 2. Configurar Secrets en GitHub

```bash
# En tu repositorio GitHub
Settings → Secrets and variables → Actions → Repository secrets

OCTANE_CLIENT_ID: tu_client_id
OCTANE_CLIENT_SECRET: tu_client_secret
```

## 📊 Ejemplos por Framework

### Maven/Java con JUnit

```yaml
- name: Run Maven Tests
  run: mvn clean test

- name: Send to Octane
  uses: ./octane-test-reporter
  with:
    octane-url: ${{ vars.OCTANE_URL }}
    shared-space: ${{ vars.OCTANE_SHARED_SPACE }}
    workspace: ${{ vars.OCTANE_WORKSPACE }}
    client-id: ${{ secrets.OCTANE_CLIENT_ID }}
    client-secret: ${{ secrets.OCTANE_CLIENT_SECRET }}
    test-results-path: 'target/surefire-reports/*.xml'
    pipeline-name: 'Java Application'
```

### Node.js con Jest

```yaml
- name: Run Jest Tests
  run: npm test -- --ci --json --outputFile=test-results.json

- name: Send to Octane
  uses: ./octane-test-reporter
  with:
    octane-url: ${{ vars.OCTANE_URL }}
    shared-space: ${{ vars.OCTANE_SHARED_SPACE }}
    workspace: ${{ vars.OCTANE_WORKSPACE }}
    client-id: ${{ secrets.OCTANE_CLIENT_ID }}
    client-secret: ${{ secrets.OCTANE_CLIENT_SECRET }}
    test-results-path: 'test-results.json'
    pipeline-name: 'Node.js Application'
```

### Python with pytest

```yaml
- name: Run Python Tests
  run: pytest --junit-xml=test-results.xml

- name: Send to Octane
  uses: ./octane-test-reporter
  with:
    octane-url: ${{ vars.OCTANE_URL }}
    shared-space: ${{ vars.OCTANE_SHARED_SPACE }}
    workspace: ${{ vars.OCTANE_WORKSPACE }}
    client-id: ${{ secrets.OCTANE_CLIENT_ID }}
    client-secret: ${{ secrets.OCTANE_CLIENT_SECRET }}
    test-results-path: 'test-results.xml'
    pipeline-name: 'Python Application'
```

### .NET con NUnit

```yaml
- name: Run .NET Tests
  run: dotnet test --logger "junit;LogFilePath=TestResults.xml"

- name: Send to Octane
  uses: ./octane-test-reporter
  with:
    octane-url: ${{ vars.OCTANE_URL }}
    shared-space: ${{ vars.OCTANE_SHARED_SPACE }}
    workspace: ${{ vars.OCTANE_WORKSPACE }}
    client-id: ${{ secrets.OCTANE_CLIENT_ID }}
    client-secret: ${{ secrets.OCTANE_CLIENT_SECRET }}
    test-results-path: 'TestResults.xml'
    pipeline-name: '.NET Application'
```

## 🔍 Troubleshooting

### Error de Autenticación
```
Error: Authentication failed with Octane
```
**Solución**: Verifica que las credenciales OAuth2 sean correctas y tengan los permisos necesarios.

### No se encuentran archivos de resultados
```
Warning: No test result files found matching pattern
```
**Solución**: Verifica que el patrón `test-results-path` sea correcto y que los tests generen archivos en esa ubicación.

### Timeout de conexión
```
Error: Request timeout after 60s
```
**Solución**: Aumenta el valor de `timeout` o verifica la conectividad de red.

### SSL Certificate issues
```
Error: SSL certificate verification failed
```
**Solución**: Si es un entorno de desarrollo, usa `skip-ssl-verify: true` (no recomendado para producción).

## 📈 Métricas y Logs

El Action proporciona logs detallados:

```
✅ Connected to Octane successfully
🔍 Found 15 test result files
📊 Parsed 142 test cases (138 passed, 4 failed)
🚀 Sent 142 tests to Octane in 2 batches
✅ Pipeline registered: pipeline-456
📋 Summary: 142 tests sent (138 passed, 4 failed)
```

## 🤝 Contribuir

1. Fork del repositorio
2. Crea una rama para tu feature (`git checkout -b feature/amazing-feature`)
3. Commit de tus cambios (`git commit -m 'Add amazing feature'`)
4. Push a la rama (`git push origin feature/amazing-feature`)
5. Abre un Pull Request

## 📄 Licencia

MIT License. Ver [LICENSE](LICENSE) para más detalles.

## 🛠️ Desarrollo

### Setup Local

```bash
# Clonar el repositorio
git clone <repository-url>
cd octane-test-reporter

# Instalar dependencias
npm install

# Compilar TypeScript
npm run build

# Ejecutar tests
npm test

# Package para producción
npm run package
```

### Scripts Disponibles

- `npm run build`: Compilar TypeScript
- `npm run test`: Ejecutar tests unitarios
- `npm run lint`: Ejecutar ESLint
- `npm run package`: Crear bundle de producción
- `npm run format`: Formatear código con Prettier

## 📞 Soporte

- 📧 Email: support@company.com
- 📖 Documentación: [Wiki del proyecto]
- 🐛 Issues: [GitHub Issues]
- 💬 Discusiones: [GitHub Discussions]
