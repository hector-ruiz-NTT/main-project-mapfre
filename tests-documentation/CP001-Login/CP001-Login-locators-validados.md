# CP001 - Login - Locators Validados con Playwright

## Validación realizada: 2025-11-01

### Página de Login

| Elemento | Selector | ID | Locator XPath | Validación Playwright | Estado |
|----------|----------|----|---------|-----------| --------|
| Banner Entorno | div | N/A | `//div[@role='banner']` | ✅ Role attribute confirmado | ✅ VALIDADO |
| Logo Appian | img | N/A | `//img[@alt='Appian']` | ✅ Alt attribute confirmado | ✅ VALIDADO |
| Campo Usuario | input[type="text"] | `un` | `//input[@id='un']` | ✅ ID estable confirmado | ✅ VALIDADO |
| Campo Contraseña | input[type="password"] | `pw` | `//input[@id='pw']` | ✅ ID estable confirmado | ✅ VALIDADO |
| Botón Entrar | button[type="submit"] | `jsLoginButton` | `//button[@id='jsLoginButton']` | ✅ ID estable confirmado | ✅ VALIDADO |
| Checkbox Recordarme | input[type="checkbox"] | N/A | `//input[@type='checkbox']` | ✅ Type attribute confirmado | ✅ VALIDADO |
| Link Olvidó Contraseña | a | N/A | `//a[contains(text(),'Olvidó su contraseña')]` | ✅ Text content confirmado | ✅ VALIDADO |

### Página Principal (Post-Login)

| Elemento | Selector | Locator XPath | Validación Playwright | Estado |
|----------|----------|---------------|-----------| --------|
| Banner Entorno | div | `//div[@role='banner']` | ✅ Role attribute confirmado | ✅ VALIDADO |
| Botón Abrir Menú | button | `//button[contains(.,'Abrir menú')]` | ✅ Text content confirmado | ✅ VALIDADO |
| Botón Navegación | button | `//button[contains(.,'Navegación')]` | ✅ Text content confirmado | ✅ VALIDADO |
| Botón Opciones Usuario | button | `//button[contains(.,'Opciones de usuario')]` | ✅ Text content confirmado | ✅ VALIDADO |
| Main Content | main | `//main` | ✅ Tag confirmado | ✅ VALIDADO |
| URL Pattern | String | contains('/suite/sites/sgo') | ✅ Pattern confirmado | ✅ VALIDADO |

### Menú Principal

| Elemento | URL | Locator XPath | Validación Playwright | Estado |
|----------|-----|---------------|-----------| --------|
| Opción Bandejas | .../page/bandejas | `//a[@href='https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/bandejas']` | ✅ HREF confirmado | ✅ VALIDADO |
| Opción Crear Solicitud | .../page/crear-solicitud | `//a[@href='https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/crear-solicitud']` | ✅ HREF confirmado | ✅ VALIDADO |
| Opción Cuadro Resumen | .../page/cuadro-resumen | `//a[@href='https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/cuadro-resumen']` | ✅ HREF confirmado | ✅ VALIDADO |
| Opción Informe Supervisor | .../page/informe-supervisor | `//a[@href='https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/informe-supervisor']` | ✅ HREF confirmado | ✅ VALIDADO |
| Opción Coordinador | .../page/coordinador | `//a[@href='https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/coordinador']` | ✅ HREF confirmado | ✅ VALIDADO |
| Opción Módulo de Búsqueda | .../page/m-dulo-de-b-squeda | `//a[@href='https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/m-dulo-de-b-squeda']` | ✅ HREF confirmado | ✅ VALIDADO |

## Resultados de la Validación

### ✅ Todos los locators validados exitosamente

**Total de elementos validados:** 19  
**Elementos con ID estable:** 3 (un, pw, jsLoginButton)  
**Elementos con atributos estables:** 16  

### Notas de Validación

1. **IDs Estables:** Los campos de usuario, contraseña y botón entrar tienen IDs estables (`un`, `pw`, `jsLoginButton`) que pueden usarse como locators primarios.

2. **Atributos Role:** El banner utiliza el atributo `role='banner'` que es estable y accesible.

3. **URLs del Menú:** Todas las opciones del menú principal tienen URLs completas y estables en su atributo `href`.

4. **Text Content:** Los botones y links utilizan text content estable para su identificación.

5. **URL Verification:** La URL post-login contiene `/suite/sites/sgo` como patrón estable.

### Pruebas Realizadas con Playwright

✅ Navegación a página de login  
✅ Verificación de elementos del formulario  
✅ Introducción de credenciales (SGO_PRUEBAS1 / Mapfre2023)  
✅ Click en botón "Entrar"  
✅ Verificación de redirección exitosa  
✅ Verificación de URL post-login  
✅ Apertura de menú principal  
✅ Verificación de todas las opciones del menú  

### Tiempo de Ejecución

- Navegación inicial: ~1 segundo
- Login: ~2 segundos
- Carga página principal: ~3 segundos
- Apertura menú: ~1 segundo
- **Total: ~7 segundos**

## Conclusión

✅ **Todos los locators son válidos y estables**  
✅ **El flujo de login funciona correctamente**  
✅ **Los elementos son identificables de manera única**  
✅ **Listos para implementación en Selenium**  

**Estado:** APROBADO PARA EJECUCIÓN MAVEN
