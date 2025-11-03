# CP001 - Login Exitoso

## Descripción
Validar el acceso exitoso al sistema SGO (Sistema de Gestión Operativa) de Mapfre con credenciales válidas.

## Objetivo
Verificar que un usuario puede autenticarse correctamente en el sistema y es redirigido a la página principal (Bandejas).

## Precondiciones
- Usuario debe tener credenciales válidas en el sistema
- Usuario: SGO_PRUEBAS1
- Contraseña: Mapfre2023
- URL de acceso disponible

## Flujo de Prueba

### Paso 1: Navegar a la página de login
- URL: https://mapfrespain-test.appiancloud.com/suite/sites/sgo?signin=native
- Verificar que se muestra el formulario de login

### Paso 2: Ingresar credenciales
- Ingresar usuario: SGO_PRUEBAS1
- Ingresar contraseña: Mapfre2023

### Paso 3: Hacer clic en botón "ENTRAR"
- Click en botón "Entrar"
- Esperar redirección

### Paso 4: Verificar acceso exitoso
- Verificar URL contiene: /suite/sites/sgo
- Verificar que se muestra la página principal (Bandejas)
- Verificar que existe el menú de navegación
- Verificar que se muestra el usuario logueado

## Resultado Esperado
- Usuario autenticado exitosamente
- Redirección a página principal: https://mapfrespain-test.appiancloud.com/suite/sites/sgo
- Se muestra el menú de navegación con las opciones:
  - Bandejas
  - Crear Solicitud
  - Cuadro Resumen
  - Informe Supervisor
  - Coordinador
  - Módulo de Búsqueda

## Locators Validados

| Elemento | Selector | ID | Name | Type | Locator | Validación | Estado |
|----------|----------|----|---------|-----------| --------|-----------|--------|
| Campo Usuario | input[type="text"] | `un` | `un` | text | `//input[@id='un']` | ID estable confirmado | ✅ |
| Campo Contraseña | input[type="password"] | `pw` | `pw` | password | `//input[@id='pw']` | ID estable confirmado | ✅ |
| Botón Entrar | button[type="submit"] | `jsLoginButton` | null | submit | `//button[@id='jsLoginButton']` | ID estable confirmado | ✅ |
| Banner POST-Login | div | N/A | N/A | div | `//div[@role='banner']` | Role attribute confirmado | ✅ |
| Main Content | main | N/A | N/A | main | `//main` | Tag confirmado | ✅ |
| URL Pattern | String | N/A | N/A | N/A | contains('/suite/sites/sgo') | Pattern confirmado | ✅ |

## Datos de Prueba
- **Usuario válido:** SGO_PRUEBAS1
- **Contraseña válida:** Mapfre2023
- **URL esperada:** https://mapfrespain-test.appiancloud.com/suite/sites/sgo

## Criterios de Aceptación
✅ Usuario puede iniciar sesión con credenciales válidas  
✅ Sistema redirige a la página principal después del login  
✅ Se muestra correctamente el menú de navegación  
✅ El usuario logueado es visible en la interfaz  
✅ No se muestran mensajes de error  

## Notas Técnicas
- La aplicación utiliza Appian como plataforma
- El login utiliza autenticación nativa (signin=native)
- Los IDs de los campos son estables y pueden usarse como locators primarios
- El sistema verifica la sesión mediante cookie
- Tiempo de carga esperado: < 5 segundos
