---
mode: qa-mapre
description: 'Explorar Mapfre SGO y generar test cases (ruta, UI tabla, Gherkin, Selenium) separados'
tools: ['edit', 'runNotebooks', 'search', 'new', 'runCommands', 'runTasks', 'sequential-thinking/*', 'playwright/*', 'atlassian/*', 'usages', 'vscodeAPI', 'problems', 'changes', 'testFailure', 'openSimpleBrowser', 'fetch', 'githubRepo', 'extensions', 'todos']
model: Claude Sonnet 4.5 (copilot)
---

# QA Mapfre SGO – Exploración y Generación de Casos

Objetivo: Usar Playwright MCP para explorar la página y generar test cases individuales (uno por flujo) con: ruta, tabla de elementos UI encontrados, referencia a archivo Gherkin externo y script Selenium en archivo separado. No consolidar todos los casos en un solo bloque grande; cada caso sale como sección separada.

## Pasos
1. Navegar a la URL proporcionada y autenticar (si credenciales disponibles en contexto).
2. Capturar todos los flujos: navegación, formularios, acciones, validaciones.
3. Para cada flujo identificado, producir un test case independiente con el formato indicado y crear:
	- Carpeta del test case identificado
    - Archivo del test case identificado (Seguir el standar de plantillas de test case en QA, agregar la url de la vista correspondiente del test case)
    - Archivo Gherkin: `tests/TC-001-{feature}/gherkin/TC-###.feature`
	- Archivo Selenium: `tests/TC-001-{feature}/selenium/TC-###.feature`
	- Mantener nombres sincronizados con el Test ID.
4. Ordenar los elementos detectados del flujo usando la tabla estándar.
5. Generar resumen breve final.

## Tabla de Referencia UI (usar para clasificar elementos)
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

## Estructura de Carpetas
```
tests/
    TC-01-{feature}
        TC-01-{feature}.md (Archivo generarl del test identificado)
	    gherkin/      (escenarios .feature)
	    selenium/     (scripts en selenium)
	    ui-elements/  (opcional: tablas de elementos por flujo en .md)
```

## Formato de Cada Test Case (en listado de salida principal)
```
Ruta: <URL o path específico de la vista del test case analizado>
Título: <Nombre descriptivo>
Flujo / Descripción breve: <Resumen del objetivo>
Elementos UI:
| Nombre común | Etiqueta | Locator | Rol/Notas |
| ...          | ...      | ...     | ...       |
Archivo Gherkin: tests/TC-01-{feature}/gherkin/TC-###.feature
Archivo Selenium: tests/TC-01-{feature}/selenium/TC-###
Tabla UI: tests/TC-01-{feature}/ui-elements/FLW-###.md (opcional si se requiere archivo separado)
Notas: <Riesgos, datos, pendientes>
```

## Reglas
- Un bloque por test case (no agrupar todos juntos).
- Locators deben ser lo más resilientes posible (data-* / role / semantic).
- Generar Gherkin claro y mínimo (Given/When/Then, opcional And) dentro del archivo `.feature`.
- Generar Selenium con waits explícitos
- No duplicar lógica entre casos; considerar funciones auxiliares sólo si se repiten >3 veces.
- Mantener concisión, evitar texto redundante.

## Resumen Final
Incluir: generación de documentación que contenga número de casos generados, lista de rutas cubiertas, elementos UI más frecuentes, rutas de archivos creados y recomendaciones inmediatas.
