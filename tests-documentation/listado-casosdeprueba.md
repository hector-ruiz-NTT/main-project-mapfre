# Listado de Casos de Prueba - Mapfre SGO

## Exploración Inicial - Sistema Gestión Operativa (SGO)

### Fecha de exploración: 2025-11-01
### URL Base: https://mapfrespain-test.appiancloud.com/suite/sites/sgo
### Credenciales de prueba: SGO_PRUEBAS1 / Mapfre2023

---

## Módulos Principales Identificados

### 1. **Login**
   - URL: https://mapfrespain-test.appiancloud.com/suite/sites/sgo?signin=native
   - Funcionalidad: Autenticación de usuarios en el sistema SGO

### 2. **Bandejas**
   - URL: https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/bandejas
   - Funcionalidad: Gestión de tareas y solicitudes
   - Opciones disponibles:
     - Mis Solicitudes
     - Mis tareas pendientes
     - Tareas cursadas
     - Asignación de tareas
     - Tareas en seguimiento

### 3. **Crear Solicitud**
   - URL: https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/crear-solicitud
   - Funcionalidad: Creación de nuevas solicitudes
   - Tipos de búsqueda:
     - Por ramo/Palabra clave
     - Por catálogo de procesos

### 4. **Cuadro Resumen**
   - URL: https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/cuadro-resumen
   - Funcionalidad: Panel de resumen de operaciones

### 5. **Informe Supervisor**
   - URL: https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/informe-supervisor
   - Funcionalidad: Informes para supervisores

### 6. **Coordinador**
   - URL: https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/coordinador
   - Funcionalidad: Funciones de coordinación

### 7. **Módulo de Búsqueda**
   - URL: https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/m-dulo-de-b-squeda
   - Funcionalidad: Búsqueda avanzada en el sistema

---

## Casos de Prueba Identificados

### CP001: Login Exitoso
**Descripción:** Validar el acceso exitoso al sistema SGO con credenciales válidas
**Precondiciones:** Usuario debe tener credenciales válidas
**Flujo:**
1. Navegar a la página de login
2. Ingresar usuario y contraseña
3. Hacer clic en botón "ENTRAR"
4. Verificar redirección a página principal (Bandejas)
**Resultado esperado:** Usuario autenticado y redirigido a /suite/sites/sgo

---

### CP002: Creación de Solicitud - Por Ramo/Palabra Clave con NIF
**Descripción:** Crear una solicitud de duplicados para póliza de auto mediante búsqueda por NIF
**Precondiciones:** Usuario autenticado en el sistema
**Flujo principal:**
1. Login en el sistema
2. Navegar a "CREAR SOLICITUD"
3. Seleccionar "Por ramo/Palabra Clave"
4. Continuar
5. Ingresar NIF/CIF (Ej: 50098501Q)
6. Ver resultados
7. Seleccionar una póliza de tipo AUTO
8. Continuar
9. Seleccionar tarea (Ej: "Solicitud de duplicados")
10. Buscar
11. Completar información adicional
12. Enviar solicitud
13. Verificar en Bandejas
**Resultado esperado:** Solicitud creada exitosamente con ID de operación

---

### CP003: Consulta de Solicitudes en Bandejas
**Descripción:** Buscar y visualizar solicitudes existentes en el módulo de Bandejas
**Precondiciones:** Usuario autenticado, solicitudes existentes en el sistema
**Flujo:**
1. Login en el sistema
2. Navegar a "Bandejas"
3. Filtrar por NUUMA TEST
4. Ingresar ID de solicitud o criterios de búsqueda
5. Buscar solicitud
6. Verificar resultados mostrados
**Resultado esperado:** Solicitudes encontradas se muestran correctamente en la tabla

---

### CP004: Creación de Solicitud - Por Catálogo de Procesos
**Descripción:** Crear una solicitud utilizando el catálogo de procesos
**Precondiciones:** Usuario autenticado en el sistema
**Flujo:**
1. Login en el sistema
2. Navegar a "CREAR SOLICITUD"
3. Seleccionar "Por catálogo de procesos"
4. Continuar
5. Seleccionar proceso del catálogo
6. Completar formulario de solicitud
7. Enviar solicitud
**Resultado esperado:** Solicitud creada exitosamente

---

### CP005: Búsqueda de Pólizas por Diferentes Filtros
**Descripción:** Validar búsqueda de pólizas utilizando diferentes tipos de filtro (NIF/CIF, Matrícula, etc.)
**Precondiciones:** Usuario autenticado, en pantalla de crear solicitud
**Flujo:**
1. Navegar a "CREAR SOLICITUD"
2. Seleccionar "Por ramo/Palabra Clave"
3. Probar búsqueda con diferentes filtros:
   - NIF/CIF
   - Matrícula
   - Número de póliza
4. Verificar resultados para cada filtro
**Resultado esperado:** Búsquedas retornan resultados correctos según filtro aplicado

---

### CP006: Gestión de Tareas Pendientes
**Descripción:** Visualizar y gestionar tareas pendientes del usuario
**Precondiciones:** Usuario autenticado, tareas asignadas
**Flujo:**
1. Login en el sistema
2. Navegar a "Bandejas"
3. Seleccionar "Mis tareas pendientes"
4. Visualizar listado de tareas
5. Seleccionar una tarea
6. Procesar tarea
**Resultado esperado:** Tareas pendientes se muestran y pueden gestionarse

---

### CP007: Consulta de Cuadro Resumen
**Descripción:** Acceder y visualizar el cuadro resumen de operaciones
**Precondiciones:** Usuario autenticado
**Flujo:**
1. Login en el sistema
2. Navegar a "Cuadro Resumen"
3. Visualizar información del dashboard
4. Aplicar filtros si están disponibles
**Resultado esperado:** Dashboard muestra información resumida correctamente

---

### CP008: Informe Supervisor
**Descripción:** Acceder a informes de supervisor y visualizar métricas
**Precondiciones:** Usuario con rol de supervisor, autenticado
**Flujo:**
1. Login en el sistema
2. Navegar a "Informe Supervisor"
3. Visualizar informes disponibles
4. Aplicar filtros de fecha/usuario si disponibles
**Resultado esperado:** Informes se muestran correctamente

---

### CP009: Búsqueda Avanzada en Módulo de Búsqueda
**Descripción:** Realizar búsquedas avanzadas en el sistema
**Precondiciones:** Usuario autenticado
**Flujo:**
1. Login en el sistema
2. Navegar a "Módulo de Búsqueda"
3. Configurar criterios de búsqueda avanzada
4. Ejecutar búsqueda
5. Visualizar resultados
**Resultado esperado:** Búsqueda avanzada retorna resultados según criterios

---

### CP010: Coordinador - Funciones de Coordinación
**Descripción:** Acceder y utilizar funciones del módulo Coordinador
**Precondiciones:** Usuario con permisos de coordinador
**Flujo:**
1. Login en el sistema
2. Navegar a "Coordinador"
3. Visualizar opciones disponibles
4. Realizar acciones de coordinación
**Resultado esperado:** Funciones de coordinación disponibles y operativas

---

## Notas de Exploración

- La aplicación utiliza Appian como plataforma
- Sistema multi-ramo (AUTOS, HOGAR, etc.)
- Gestión de solicitudes con ID de operación único
- Búsquedas por múltiples criterios (NIF/CIF, Matrícula, Póliza)
- Sistema de bandejas para gestión de tareas
- Roles diferenciados (usuario, supervisor, coordinador)

---

## Próximos Pasos

Para cada caso de prueba identificado:
1. Realizar exploración detallada del flujo
2. Identificar y documentar todos los elementos UI
3. Generar scripts Page/Imp/Test
4. Validar con Playwright
5. Ejecutar con Maven
6. Documentar resultados

---

**Estado del documento:** Versión inicial - Pendiente de refinamiento con exploraciones detalladas
