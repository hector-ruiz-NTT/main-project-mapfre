# CP001 - Login - Elementos UI

## Elementos de la Página de Login

| Nombre Elemento | Etiqueta HTML | ID | Name | Type | Placeholder | Class | Descripción | Locator Principal |
|-----------------|---------------|----|----- |------|-------------|-------|-------------|-------------------|
| Banner Entorno | div | N/A | N/A | div | N/A | N/A | Banner informativo "Entorno Appian TEST" | `//div[@role='banner']` |
| Logo Appian | img | N/A | N/A | img | N/A | N/A | Imagen del logo de Appian | `//img[@alt='Appian']` |
| Label Usuario | generic (div) | N/A | N/A | div | N/A | N/A | Etiqueta "Nombre de usuario" | `//div[text()='Nombre de usuario']` |
| Campo Usuario | input | `un` | `un` | text | "Nombre de usuario" | N/A | Campo de texto para usuario | `//input[@id='un']` |
| Label Contraseña | generic (div) | N/A | N/A | div | N/A | N/A | Etiqueta "Contraseña" | `//div[text()='Contraseña']` |
| Campo Contraseña | input | `pw` | `pw` | password | "Contraseña" | N/A | Campo de contraseña | `//input[@id='pw']` |
| Checkbox Recordar | input | N/A | N/A | checkbox | N/A | N/A | Checkbox "Recordarme" | `//input[@type='checkbox']` |
| Label Recordar | generic (div) | N/A | N/A | div | N/A | N/A | Texto "Recordarme" | `//div[text()='Recordarme']` |
| Link Olvidó Contraseña | a | N/A | N/A | link | N/A | N/A | Link "¿Olvidó su contraseña?" | `//a[contains(text(),'Olvidó su contraseña')]` |
| Botón Entrar | button | `jsLoginButton` | N/A | submit | N/A | "btn primary" | Botón para iniciar sesión | `//button[@id='jsLoginButton']` |
| Footer Copyright | generic (div) | N/A | N/A | div | N/A | N/A | Texto "©2003-2025 Appian Corporation" | `//div[contains(text(),'Appian Corporation')]` |

## Elementos de la Página Principal (Post-Login)

| Nombre Elemento | Etiqueta HTML | ID | Name | Type | Descripción | Locator Principal |
|-----------------|---------------|----|----- |------|-------------|-------------------|
| Banner Entorno | div | N/A | N/A | div | Banner "Entorno Appian TEST" | `//div[@role='banner']` |
| Botón Abrir Menú | button | N/A | N/A | button | Botón hamburguesa para menú | `//button[contains(.,'Abrir menú')]` |
| Logo Principal | img | N/A | N/A | img | Logo de la aplicación | `//a[@href='https://mapfrespain-test.appiancloud.com/suite/sites/sgo']/img` |
| Botón Navegación | button | N/A | N/A | button | Botón de navegación principal | `//button[contains(.,'Navegación')]` |
| Botón Opciones Usuario | button | N/A | N/A | button | Botón de opciones de usuario (SP) | `//button[contains(.,'Opciones de usuario')]` |
| Logo Appian Pequeño | img | N/A | N/A | img | Logo pequeño de Appian | `//img[@alt='Logotipo de Appian']` |
| Main Content | main | N/A | N/A | main | Contenedor principal de contenido | `//main` |

## Elementos del Menú Principal

| Nombre Elemento | Etiqueta HTML | URL | Descripción | Locator Principal |
|-----------------|---------------|-----|-------------|-------------------|
| Opción Bandejas | link | .../page/bandejas | Link a Bandejas | `//a[@href='https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/bandejas']` |
| Opción Crear Solicitud | link | .../page/crear-solicitud | Link a Crear Solicitud | `//a[@href='https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/crear-solicitud']` |
| Opción Cuadro Resumen | link | .../page/cuadro-resumen | Link a Cuadro Resumen | `//a[@href='https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/cuadro-resumen']` |
| Opción Informe Supervisor | link | .../page/informe-supervisor | Link a Informe Supervisor | `//a[@href='https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/informe-supervisor']` |
| Opción Coordinador | link | .../page/coordinador | Link a Coordinador | `//a[@href='https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/coordinador']` |
| Opción Módulo de Búsqueda | link | .../page/m-dulo-de-b-squeda | Link a Módulo de Búsqueda | `//a[@href='https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/m-dulo-de-b-squeda']` |

## Notas
- Los IDs `un`, `pw` y `jsLoginButton` son estables y recomendados para locators
- El sistema usa role attributes para accesibilidad (ej: banner, main)
- Los campos tienen placeholders que coinciden con sus labels
- La navegación post-login requiere interacción con botón "Abrir menú"
