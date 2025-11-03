# CP001 - Login Exitoso - Pasos Imperativos

## CASO DE PRUEBA CP001: LOGIN_EXITOSO

### LOGIN: 'https://mapfrespain-test.appiancloud.com/suite/sites/sgo?signin=native'

**Paso 1:** Navegar a la página de login
- Abrir navegador
- Navegar a la URL 'https://mapfrespain-test.appiancloud.com/suite/sites/sgo?signin=native'
- Esperar a que cargue el DOM de la pantalla
- Verificar que se muestra el banner "Entorno Appian TEST"
- Verificar que se muestra el logo de Appian
- Verificar que se muestra el formulario de login

**Paso 2:** Verificar elementos del formulario
- Verificar que el campo "Nombre de usuario" está visible
- Verificar que el campo "Contraseña" está visible
- Verificar que el checkbox "Recordarme" está visible
- Verificar que el link "¿Olvidó su contraseña?" está visible
- Verificar que el botón "Entrar" está visible

**Paso 3:** Introducir credenciales
- Hacer clic en el campo "Nombre de usuario"
- Introducir el usuario 'SGO_PRUEBAS1'
- Hacer clic en el campo "Contraseña"
- Introducir la contraseña 'Mapfre2023'

**Paso 4:** Iniciar sesión
- Hacer clic en el botón 'ENTRAR'
- Esperar a que cargue el DOM de la pantalla (máximo 5 segundos)

### HOME: 'https://mapfrespain-test.appiancloud.com/suite/sites/sgo'

**Paso 5:** Verificar redirección exitosa
- Verificar que la URL actual es 'https://mapfrespain-test.appiancloud.com/suite/sites/sgo' o contiene '/suite/sites/sgo'
- Verificar que se muestra el banner "Entorno Appian TEST"
- Verificar que el título de la página contiene "Sistema Gestión Operativa" o "Bandejas"

**Paso 6:** Verificar elementos de la página principal
- Verificar que se muestra el botón "Abrir menú"
- Verificar que se muestra el logo principal
- Verificar que se muestra el botón "Navegación"
- Verificar que se muestra el botón "Opciones de usuario" con el texto "SP"
- Verificar que se muestra el logo pequeño de Appian
- Verificar que existe el elemento <main> en el DOM

**Paso 7:** Verificar menú de navegación
- Hacer clic en el botón "Abrir menú"
- Esperar a que cargue el DOM de la pantalla
- Verificar que se muestra la opción "Bandejas"
- Verificar que se muestra la opción "Crear Solicitud"
- Verificar que se muestra la opción "Cuadro Resumen"
- Verificar que se muestra la opción "Informe Supervisor"
- Verificar que se muestra la opción "Coordinador"
- Verificar que se muestra la opción "Módulo de Búsqueda"

**Paso 8:** Verificar usuario logueado
- Hacer clic en el botón "Opciones de usuario"
- Esperar a que cargue el menú desplegable
- Verificar que se muestra el nombre del usuario "SGO PRUEBAS PRUEBAS"
- Verificar que se muestra el username "SGO_PRUEBAS1"
- Verificar que se muestran las opciones:
  - Perfil
  - Configuraciones
  - Cerrar sesión

**Paso 9:** Cerrar sesión (opcional - cleanup)
- Hacer clic en el botón "Cerrar"
- Esperar a que se cierre la sesión
- Verificar redirección a la página de login

## Mapeo de Acciones a Elementos

### Acciones de Interacción
- **Navegar:** Se utiliza el navegador para acceder a URLs
- **Hacer clic:** Se aplica a botones (Entrar), links (opciones menú), campos de texto (para focus)
- **Introducir/Escribir:** Se aplica a campos input (usuario, contraseña)
- **Esperar:** Se utiliza para dar tiempo a que cargue el DOM después de acciones
- **Verificar:** Se aplica a todos los elementos para validar su presencia/estado

### Elementos Objetivo por Acción
- **Hacer clic en botones:** Entrar, Abrir menú, Navegación, Opciones de usuario, Cerrar
- **Introducir texto en inputs:** Campo usuario (id='un'), Campo contraseña (id='pw')
- **Hacer clic en links:** Bandejas, Crear Solicitud, Cuadro Resumen, etc.
- **Verificar elementos visuales:** Banner, logos, labels, opciones de menú

## Criterios de Éxito
✅ Navegación a página de login exitosa  
✅ Formulario de login visible y completo  
✅ Credenciales introducidas correctamente  
✅ Login ejecutado sin errores  
✅ Redirección a página principal exitosa  
✅ URL correcta después del login  
✅ Elementos de navegación visibles  
✅ Menú principal accesible y completo  
✅ Usuario logueado verificable  
✅ Funcionalidad de logout disponible  

## Tiempo Estimado de Ejecución
- Navegación inicial: ~2 segundos
- Introducción de credenciales: ~3 segundos
- Login y redirección: ~3 segundos
- Verificaciones: ~2 segundos
- **Total: ~10 segundos**

## Datos de Prueba Utilizados
```
Usuario: SGO_PRUEBAS1
Contraseña: Mapfre2023
URL Login: https://mapfrespain-test.appiancloud.com/suite/sites/sgo?signin=native
URL Esperada: https://mapfrespain-test.appiancloud.com/suite/sites/sgo
```
