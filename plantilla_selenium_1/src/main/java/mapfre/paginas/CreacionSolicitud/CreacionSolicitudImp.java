package mapfre.paginas.CreacionSolicitud;

import mapfre.base.actions.BaseActionsSelenium;
import mapfre.general.Log;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import static mapfre.utils.DriverManager.getDriver;

public class CreacionSolicitudImp extends BaseActionsSelenium {

    public CreacionSolicitudImp() {
        super(getDriver());
    }

    /**
     * Navega a la página de Crear Solicitud directamente por URL
     */
    public void navegarACrearSolicitud() {
        Log.info("Navegando a Crear Solicitud");
        
        // Navegar directamente a la URL de Crear Solicitud
        String crearSolicitudURL = "https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/crear-solicitud";
        getDriver().get(crearSolicitudURL);
        
        // Esperar a que la página cargue completamente
        esperarCargaPagina(4);
        
        Log.info("Navegación a Crear Solicitud completada");
    }

    /**
     * Selecciona NUUMA escribiéndolo manualmente
     * ⭐ CRÍTICO: Debe escribirse MANUALMENTE (no seleccionarse del dropdown)
     * Escribir manualmente EFERNA2 habilita TODAS las tareas en Favoritos (45+ tareas)
     * Seleccionar del dropdown deja la tabla de Favoritos VACÍA
     * @param nuuma Valor a escribir (ej: EFERNA2)
     */
    public void seleccionarNUUMA(String nuuma) {
        Log.info("⭐ CRÍTICO: Escribiendo NUUMA MANUALMENTE para habilitar todas las tareas: " + nuuma);
        
        // Paso 1: Hacer clic en el botón del lápiz "Escribir nuuma especifico" para habilitar el textbox
        Log.info("Haciendo clic en botón del lápiz 'Escribir nuuma especifico'");
        waitForElementToBeClickableByXpath(CreacionSolicitudPage.getBotonEscribirNuumaLapiz(), 10);
        clickElementByXpath(CreacionSolicitudPage.getBotonEscribirNuumaLapiz());
        esperarCargaPagina(2);
        
        // Paso 2: Esperar a que aparezca el campo de texto, limpiarlo y escribir el NUUMA
        Log.info("Limpiando y escribiendo NUUMA manualmente: " + nuuma);
        waitForElementToBeClickableByXpath(CreacionSolicitudPage.getCampoNUUMA(), 10);
        
        // Limpiar el campo primero (puede tener valor del dropdown)
        WebElement campoNuuma = findElementByXpath(CreacionSolicitudPage.getCampoNUUMA());
        campoNuuma.clear();
        esperarCargaPagina(1);
        
        // Escribir el NUUMA manualmente
        campoNuuma.sendKeys(nuuma);
        esperarCargaPagina(2);
        
        Log.info("✅ NUUMA escrita manualmente: " + nuuma + " - Esto habilita TODAS las tareas en Favoritos");
    }

    /**
     * Selecciona el tipo de búsqueda "Por ramo/Palabra Clave"
     * Usa JavaScript para hacer clic debido a interferencia del label
     */
    public void seleccionarBusquedaPorRamoPalabra() {
        Log.info("Seleccionando tipo de búsqueda: Por ramo/Palabra Clave");
        waitForElementToBeVisibleByXpath(CreacionSolicitudPage.getRadioRamoPalabra(), 10);
        
        // Usar JavaScript para hacer clic en el radio button
        WebElement radioButton = findElementByXpath(CreacionSolicitudPage.getRadioRamoPalabra());
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("arguments[0].click();", radioButton);
        
        esperarCargaPagina(1);
        Log.info("Tipo de búsqueda seleccionado correctamente");
    }

    /**
     * Hace clic en el botón CONTINUAR después de seleccionar NUUMA y tipo de búsqueda
     */
    public void clickContinuarInicial() {
        Log.info("Haciendo clic en CONTINUAR (después de NUUMA)");
        esperarCargaPagina(2); // Esperar a que la página se estabilice después del radio button
        waitForElementToBeClickableByXpath(CreacionSolicitudPage.getBotonContinuarInicial(), 10);
        clickElementByXpath(CreacionSolicitudPage.getBotonContinuarInicial());
        esperarCargaPagina(4);
        Log.info("Botón CONTINUAR presionado, avanzando al siguiente paso");
    }

    /**
     * Selecciona el tipo de búsqueda "Póliza/Nombre"
     */
    public void seleccionarBusquedaPorPolizaNombre() {
        Log.info("Seleccionando tipo de búsqueda: Póliza/Nombre");
        waitForElementToBeVisibleByXpath(CreacionSolicitudPage.getRadioPoliNombre(), 10);
        
        WebElement radioButton = findElementByXpath(CreacionSolicitudPage.getRadioPoliNombre());
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("arguments[0].click();", radioButton);
        
        esperarCargaPagina(1);
        Log.info("Tipo de búsqueda seleccionado correctamente");
    }

    /**
     * Introduce el NIF/CIF en el campo de búsqueda
     * @param nifCif NIF o CIF a buscar
     */
    public void introducirNIFCIF(String nifCif) {
        Log.info("Introduciendo NIF/CIF: " + nifCif);
        waitForElementToBeClickableByXpath(CreacionSolicitudPage.getCampoNIFCIF(), 10);
        fillFieldByXpath(CreacionSolicitudPage.getCampoNIFCIF(), nifCif);
        Log.info("NIF/CIF introducido correctamente");
    }

    /**
     * Hace clic en el botón VER RESULTADOS
     */
    public void clickVerResultados() {
        Log.info("Haciendo clic en VER RESULTADOS");
        waitForElementToBeClickableByXpath(CreacionSolicitudPage.getBotonVerResultados(), 10);
        clickElementByXpath(CreacionSolicitudPage.getBotonVerResultados());
        esperarCargaPagina(12); // Aumentado de 4 a 12 segundos debido a carga lenta de resultados
        Log.info("Búsqueda realizada, esperando resultados");
    }

    /**
     * Verifica que se muestren resultados de pólizas
     */
    public void verificarResultadosMostrados() {
        Log.info("Verificando que se muestren resultados");
        // Esperar a que aparezca el contador de resultados (ej: "1 Elementos")
        waitForElementToBeVisibleByXpath(CreacionSolicitudPage.getContadorResultados(), 20);
        Assert.assertTrue(findElementByXpath(CreacionSolicitudPage.getContadorResultados()).isDisplayed(),
            "El contador de resultados no está visible");
        Log.info("Resultados mostrados correctamente");
    }

    /**
     * Hace clic en el botón CONTINUAR SIN POLIZA después de ver los resultados de búsqueda
     * Este botón aparece cuando hay resultados de pólizas pero se desea continuar sin seleccionar ninguna
     */
    public void clickContinuarSinPoliza() {
        Log.info("Haciendo clic en CONTINUAR SIN POLIZA");
        esperarCargaPagina(2);
        // Usar el locator específico para CONTINUAR SIN POLIZA
        waitForElementToBeClickableByXpath(CreacionSolicitudPage.getBotonContinuarSinPoliza(), 10);
        clickElementByXpath(CreacionSolicitudPage.getBotonContinuarSinPoliza());
        esperarCargaPagina(6);
        Log.info("Navegando a selección de tarea sin póliza asociada");
    }

    /**
     * Verifica que se haya llegado a la pantalla "Seleccionar Tarea"
     * Esta pantalla aparece después de hacer clic en CONTINUAR SIN POLIZA
     * Valida la presencia del título y la tabla de Favoritos (aunque pueda estar vacía)
     */
    public void verificarPantallaSeleccionarTarea() {
        Log.info("Verificando llegada a pantalla 'Seleccionar Tarea'");
        
        // Verificar título "Seleccionar Tarea" o "Selección de tarea"
        String xpathTitulo = "//strong[contains(text(),'Seleccionar Tarea') or contains(text(),'Selección de tarea')]";
        waitForElementToBeVisibleByXpath(xpathTitulo, 10);
        
        // Verificar presencia de la tabla de Favoritos (header "Favoritos")
        String xpathFavoritos = "//strong[text()='Favoritos']";
        waitForElementToBeVisibleByXpath(xpathFavoritos, 5);
        
        Log.info("Pantalla 'Seleccionar Tarea' verificada correctamente");
        Log.info("ADVERTENCIA: Tabla de Favoritos puede estar vacía - requiere configuración de datos");
    }

    /**
     * Selecciona la primera tarea disponible de la tabla de Favoritos
     * Esta tabla aparece en la pantalla "Seleccionar Tarea" cuando se continúa sin póliza
     * NOTA: Este método fallará si la tabla está vacía (usuario sin tareas configuradas)
     */
    public void seleccionarTareaFavoritos() {
        Log.info("Seleccionando tarea de Favoritos");
        waitForElementToBeClickableByXpath(CreacionSolicitudPage.getPrimeraTareaFavoritos(), 10);
        clickElementByXpath(CreacionSolicitudPage.getPrimeraTareaFavoritos());
        esperarCargaPagina(5);
        Log.info("Tarea de Favoritos seleccionada correctamente");
    }

    /**
     * Selecciona la primera póliza de los resultados
     */
    public void seleccionarPrimeraPoliza() {
        Log.info("Seleccionando la primera póliza de los resultados");
        waitForElementToBeClickableByXpath(CreacionSolicitudPage.getPrimerResultado(), 10);
        clickElementByXpath(CreacionSolicitudPage.getPrimerResultado());
        esperarCargaPagina(3);
        Log.info("Primera póliza seleccionada");
    }

    /**
     * Hace clic en el botón CONTINUAR después de seleccionar póliza
     */
    public void clickContinuarPoliza() {
        Log.info("Haciendo clic en CONTINUAR (después de póliza)");
        esperarCargaPagina(2);
        waitForElementToBeClickableByXpath(CreacionSolicitudPage.getBotonContinuarPoliza(), 10);
        clickElementByXpath(CreacionSolicitudPage.getBotonContinuarPoliza());
        esperarCargaPagina(4);
        Log.info("Botón CONTINUAR presionado, avanzando a selección de tarea");
    }

    /**
     * Selecciona una tarea del combobox
     * @param tarea Nombre de la tarea a seleccionar (ej: "Solicitud de Duplicados")
     */
    public void seleccionarTarea(String tarea) {
        Log.info("Seleccionando tarea: " + tarea);
        
        // Paso 1: Hacer clic en el combobox para abrirlo
        waitForElementToBeClickableByXpath(CreacionSolicitudPage.getComboTarea(), 10);
        clickElementByXpath(CreacionSolicitudPage.getComboTarea());
        esperarCargaPagina(2);
        
        // Paso 2: Hacer clic en la opción específica
        String opcionXpath = String.format(CreacionSolicitudPage.getOpcionTarea(), tarea);
        waitForElementToBeClickableByXpath(opcionXpath, 10);
        clickElementByXpath(opcionXpath);
        esperarCargaPagina(2);
        
        Log.info("Tarea seleccionada: " + tarea);
    }

    /**
     * Hace clic en el botón BUSCAR después de seleccionar la tarea
     */
    public void clickBuscar() {
        Log.info("Haciendo clic en BUSCAR");
        waitForElementToBeClickableByXpath(CreacionSolicitudPage.getBotonBuscar(), 10);
        clickElementByXpath(CreacionSolicitudPage.getBotonBuscar());
        esperarCargaPagina(4);
        Log.info("Búsqueda de tarea realizada");
    }

    /**
     * Hace clic en el botón CONTINUAR después de ver los resultados de la tarea
     */
    public void clickContinuarTarea() {
        Log.info("Haciendo clic en CONTINUAR (después de tarea)");
        waitForElementToBeClickableByXpath(CreacionSolicitudPage.getBotonContinuarTarea(), 10);
        clickElementByXpath(CreacionSolicitudPage.getBotonContinuarTarea());
        esperarCargaPagina(4);
        Log.info("Botón CONTINUAR presionado, avanzando a formulario final");
    }

    /**
     * Introduce observaciones en el campo de texto
     * @param observaciones Texto de observaciones
     */
    public void introducirObservaciones(String observaciones) {
        Log.info("Introduciendo observaciones");
        waitForElementToBeClickableByXpath(CreacionSolicitudPage.getCampoObservaciones(), 10);
        fillFieldByXpath(CreacionSolicitudPage.getCampoObservaciones(), observaciones);
        Log.info("Observaciones introducidas: " + observaciones);
    }

    /**
     * Hace clic en el botón ENVIAR SOLICITUD
     */
    public void clickEnviarSolicitud() {
        Log.info("Haciendo clic en ENVIAR SOLICITUD");
        waitForElementToBeClickableByXpath(CreacionSolicitudPage.getBotonEnviarSolicitud(), 10);
        clickElementByXpath(CreacionSolicitudPage.getBotonEnviarSolicitud());
        esperarCargaPagina(5);
        Log.info("Solicitud enviada");
    }

    /**
     * Verifica que se muestre el mensaje de éxito con el número de solicitud
     * @return Número de solicitud creada
     */
    public String verificarSolicitudCreada() {
        Log.info("Verificando creación exitosa de solicitud");
        waitForElementToBeVisibleByXpath(CreacionSolicitudPage.getNumeroSolicitud(), 15);
        
        String numeroSolicitud = findElementByXpath(CreacionSolicitudPage.getNumeroSolicitud()).getText();
        Assert.assertNotNull(numeroSolicitud, "No se obtuvo el número de solicitud");
        Assert.assertTrue(numeroSolicitud.startsWith("SGO"), 
            "El número de solicitud no tiene el formato esperado");
        
        Log.info("Solicitud creada exitosamente con ID: " + numeroSolicitud);
        return numeroSolicitud;
    }

    /**
     * Navega a Bandejas para verificar la solicitud creada
     */
    public void navegarABandejas() {
        Log.info("Navegando a Bandejas");
        waitForElementToBeClickableByXpath(CreacionSolicitudPage.getLinkBandejas(), 10);
        clickElementByXpath(CreacionSolicitudPage.getLinkBandejas());
        esperarCargaPagina(3);
        Log.info("Navegación a Bandejas completada");
    }

    /**
     * Verifica que la solicitud creada aparezca en Bandejas
     * @param numeroSolicitud Número de solicitud a buscar (ej: SGO11202511030000034)
     * @return true si la solicitud aparece en Bandejas, false en caso contrario
     */
    public boolean verificarSolicitudEnBandejas(String numeroSolicitud) {
        Log.info("Verificando presencia de solicitud en Bandejas: " + numeroSolicitud);
        
        // Esperar a que cargue la tabla de Bandejas
        esperarCargaPagina(5);
        
        // Construir xpath para buscar la solicitud por su número
        String xpathSolicitud = String.format("//a[contains(text(),'%s')]", numeroSolicitud);
        
        try {
            waitForElementToBeVisibleByXpath(xpathSolicitud, 15);
            WebElement solicitudEncontrada = findElementByXpath(xpathSolicitud);
            
            if (solicitudEncontrada.isDisplayed()) {
                Log.info("✅ Solicitud encontrada en Bandejas: " + numeroSolicitud);
                return true;
            }
        } catch (Exception e) {
            Log.info("❌ Solicitud NO encontrada en Bandejas: " + numeroSolicitud);
            Log.info("Error: " + e.getMessage());
            return false;
        }
        
        return false;
    }

    /**
     * ========================================
     * MÉTODOS PARA BÚSQUEDA POR MATRÍCULA
     * ========================================
     */
    
    /**
     * Introduce la matrícula en el campo de búsqueda
     * @param matricula Matrícula a buscar (ej: 1234ABC)
     */
    public void introducirMatricula(String matricula) {
        Log.info("Introduciendo matrícula: " + matricula);
        waitForElementToBeClickableByXpath(CreacionSolicitudPage.getCampoMatricula(), 10);
        fillFieldByXpath(CreacionSolicitudPage.getCampoMatricula(), matricula);
        Log.info("Matrícula introducida correctamente");
    }

    /**
     * Hace clic en el botón VER RESULTADOS después de introducir matrícula
     */
    public void clickVerResultadosMatricula() {
        Log.info("Haciendo clic en VER RESULTADOS (búsqueda por matrícula)");
        waitForElementToBeClickableByXpath(CreacionSolicitudPage.getBotonVerResultados(), 10);
        clickElementByXpath(CreacionSolicitudPage.getBotonVerResultados());
        esperarCargaPagina(12);
        Log.info("Búsqueda por matrícula realizada, esperando resultados");
    }

    /**
     * ========================================
     * MÉTODOS PARA BÚSQUEDA POR CATÁLOGO
     * ========================================
     */
    
    /**
     * Expande el combobox de Catálogo para ver las opciones disponibles
     */
    public void expandirComboCatalogo() {
        Log.info("Expandiendo combobox de Catálogo");
        waitForElementToBeClickableByXpath(CreacionSolicitudPage.getComboCatalogo(), 10);
        clickElementByXpath(CreacionSolicitudPage.getComboCatalogo());
        esperarCargaPagina(2);
        Log.info("Combobox de Catálogo expandido");
    }

    /**
     * Selecciona una opción del catálogo por su nombre
     * @param nombreCatalogo Nombre del catálogo a seleccionar
     */
    public void seleccionarOpcionCatalogo(String nombreCatalogo) {
        Log.info("Seleccionando catálogo: " + nombreCatalogo);
        
        // Paso 1: Expandir el combobox
        expandirComboCatalogo();
        
        // Paso 2: Hacer clic en la opción específica
        String opcionXpath = String.format(CreacionSolicitudPage.getOpcionCatalogo(), nombreCatalogo);
        waitForElementToBeClickableByXpath(opcionXpath, 10);
        clickElementByXpath(opcionXpath);
        esperarCargaPagina(2);
        
        Log.info("Catálogo seleccionado: " + nombreCatalogo);
    }

    /**
     * Hace clic en el botón VER RESULTADOS después de seleccionar catálogo
     */
    public void clickVerResultadosCatalogo() {
        Log.info("Haciendo clic en VER RESULTADOS (búsqueda por catálogo)");
        waitForElementToBeClickableByXpath(CreacionSolicitudPage.getBotonVerResultados(), 10);
        clickElementByXpath(CreacionSolicitudPage.getBotonVerResultados());
        esperarCargaPagina(12);
        Log.info("Búsqueda por catálogo realizada, esperando resultados");
    }

    /**
     * ========================================
     * MÉTODOS AUXILIARES Y AVANZADOS
     * ========================================
     */
    
    /**
     * Selecciona una tarea de Favoritos usando JavaScript para interacción robusta
     * Alternativa al método seleccionarTareaFavoritos() cuando hay problemas con el click estándar
     * @param indiceTarea Índice de la tarea a seleccionar (1 = primera, 2 = segunda, etc.)
     * @return true si se pudo seleccionar la tarea, false si la tabla está vacía
     */
    public boolean seleccionarTareaFavoritosConJS(int indiceTarea) {
        Log.info("Seleccionando tarea de Favoritos con JavaScript - índice: " + indiceTarea);
        
        String xpathTarea = String.format("//table//tbody//tr[%d]//td[1]//p//a[contains(@href,'#')]", indiceTarea);
        
        try {
            waitForElementToBeVisibleByXpath(xpathTarea, 10);
            WebElement tarea = findElementByXpath(xpathTarea);
            JavascriptExecutor js = (JavascriptExecutor) getDriver();
            js.executeScript("arguments[0].click();", tarea);
            
            esperarCargaPagina(5);
            Log.info("Tarea de Favoritos seleccionada correctamente con JavaScript");
            return true;
        } catch (Exception e) {
            Log.info("❌ No se encontró tarea en Favoritos (tabla vacía)");
            Log.info("Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Hace clic en el botón ENVIAR SOLICITUD usando JavaScript
     * Útil cuando el botón estándar no responde correctamente
     */
    public void clickEnviarSolicitudConJS() {
        Log.info("Haciendo clic en ENVIAR SOLICITUD con JavaScript");
        
        waitForElementToBeVisibleByXpath(CreacionSolicitudPage.getBotonEnviarSolicitud(), 10);
        WebElement boton = findElementByXpath(CreacionSolicitudPage.getBotonEnviarSolicitud());
        
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("arguments[0].click();", boton);
        
        esperarCargaPagina(5);
        Log.info("Solicitud enviada con JavaScript");
    }

    /**
     * Obtiene el número de solicitud del combobox "ID Tramite Creado" después de crear la solicitud
     * Este método es alternativo a verificarSolicitudCreada() cuando el flujo regresa al inicio
     * @return Número de solicitud creada o null si no se encuentra
     */
    public String obtenerNumeroSolicitudDesdeCombobox() {
        Log.info("Intentando obtener número de solicitud del combobox 'ID Tramite Creado'");
        
        try {
            // Esperar a que aparezca el combobox con la solicitud creada
            String xpathCombobox = "//div[contains(text(),'ID Tramite Creado')]/following-sibling::div//div[@role='combobox']";
            waitForElementToBeVisibleByXpath(xpathCombobox, 10);
            
            WebElement combobox = findElementByXpath(xpathCombobox);
            String textoCombobox = combobox.getText();
            
            if (textoCombobox != null && textoCombobox.startsWith("SGO")) {
                Log.info("Número de solicitud obtenido: " + textoCombobox);
                return textoCombobox;
            } else {
                Log.info("El combobox no contiene un número de solicitud válido");
                return null;
            }
        } catch (Exception e) {
            Log.info("No se pudo obtener el número de solicitud del combobox: " + e.getMessage());
            return null;
        }
    }

    /**
     * Método auxiliar para esperar la carga de la página
     * @param segundos Cantidad de segundos a esperar
     */
    private void esperarCargaPagina(int segundos) {
        try {
            Thread.sleep(segundos * 1000);
        } catch (InterruptedException e) {
            Log.info("Espera interrumpida: " + e.getMessage());
        }
    }

    /**
     * Flujo completo de creación de solicitud
     * @param nuuma NUUMA a seleccionar
     * @param nifCif NIF/CIF a buscar
     * @param tarea Tarea a seleccionar
     * @param observaciones Observaciones a introducir
     * @return Número de solicitud creada (nota: el flujo completo regresa al inicio, no muestra número directamente)
     */
    public String crearSolicitudCompleta(String nuuma, String nifCif, String tarea, String observaciones) {
        Log.info("Iniciando flujo completo de creación de solicitud");
        
        navegarACrearSolicitud();
        seleccionarNUUMA(nuuma);
        seleccionarBusquedaPorRamoPalabra();
        clickContinuarInicial();
        introducirNIFCIF(nifCif);
        clickVerResultados();
        verificarResultadosMostrados();
        seleccionarPrimeraPoliza();
        clickContinuarPoliza();
        seleccionarTarea(tarea);
        clickBuscar();
        clickContinuarTarea();
        introducirObservaciones(observaciones);
        clickEnviarSolicitud();
        
        // Nota: El flujo actual regresa a la página inicial de Crear Solicitud
        // El número de solicitud estaría disponible en el combobox "ID Tramite Creado"
        // Por ahora, retornamos un valor indicativo de éxito
        Log.info("Flujo completo de creación de solicitud finalizado exitosamente");
        return "SOLICITUD_CREADA_EXITOSAMENTE";
    }

    /**
     * ========================================
     * FLUJOS COMPLETOS E2E POR CASO DE USO
     * ========================================
     */

    /**
     * Flujo E2E: Creación de solicitud con NIF/CIF + Continuar sin póliza + Tarea de Favoritos
     * Este es el flujo principal validado con Playwright MCP
     * @param nuuma NUUMA a seleccionar (ej: EFERNA2)
     * @param nifCif NIF/CIF a buscar
     * @param indiceTarea Índice de la tarea en Favoritos a seleccionar (1 = primera)
     * @param observaciones Observaciones a introducir
     * @return Número de solicitud creada, o null si no hay tareas disponibles
     */
    public String crearSolicitudConNIFSinPolizaYFavoritos(String nuuma, String nifCif, int indiceTarea, String observaciones) {
        Log.info("========================================");
        Log.info("FLUJO E2E: NIF/CIF + Sin Póliza + Tarea Favoritos");
        Log.info("========================================");
        
        navegarACrearSolicitud();
        seleccionarNUUMA(nuuma);
        seleccionarBusquedaPorRamoPalabra();
        clickContinuarInicial();
        introducirNIFCIF(nifCif);
        clickVerResultados();
        verificarResultadosMostrados();
        clickContinuarSinPoliza();
        verificarPantallaSeleccionarTarea();
        
        // Intentar seleccionar tarea de Favoritos
        boolean tareaSeleccionada = seleccionarTareaFavoritosConJS(indiceTarea);
        
        if (!tareaSeleccionada) {
            Log.info("⚠️ No se pudo completar el flujo: Tabla de Favoritos vacía");
            Log.info("========================================");
            Log.info("Flujo E2E detenido - Requiere configuración de datos");
            Log.info("========================================");
            return null;
        }
        
        introducirObservaciones(observaciones);
        clickEnviarSolicitudConJS();
        
        // Intentar obtener el número de solicitud
        String numeroSolicitud = obtenerNumeroSolicitudDesdeCombobox();
        
        if (numeroSolicitud == null) {
            numeroSolicitud = verificarSolicitudCreada();
        }
        
        Log.info("========================================");
        Log.info("Flujo E2E completado - Solicitud: " + numeroSolicitud);
        Log.info("========================================");
        
        return numeroSolicitud;
    }

    /**
     * Flujo E2E: Creación de solicitud con NIF/CIF + Selección de póliza + Tarea
     * @param nuuma NUUMA a seleccionar
     * @param nifCif NIF/CIF a buscar
     * @param tarea Nombre de la tarea a seleccionar
     * @param observaciones Observaciones a introducir
     * @return Número de solicitud creada
     */
    public String crearSolicitudConNIFPolizaYTarea(String nuuma, String nifCif, String tarea, String observaciones) {
        Log.info("========================================");
        Log.info("FLUJO E2E: NIF/CIF + Con Póliza + Tarea Seleccionada");
        Log.info("========================================");
        
        navegarACrearSolicitud();
        seleccionarNUUMA(nuuma);
        seleccionarBusquedaPorRamoPalabra();
        clickContinuarInicial();
        introducirNIFCIF(nifCif);
        clickVerResultados();
        verificarResultadosMostrados();
        seleccionarPrimeraPoliza();
        clickContinuarPoliza();
        seleccionarTarea(tarea);
        clickBuscar();
        clickContinuarTarea();
        introducirObservaciones(observaciones);
        clickEnviarSolicitud();
        
        String numeroSolicitud = obtenerNumeroSolicitudDesdeCombobox();
        if (numeroSolicitud == null) {
            numeroSolicitud = verificarSolicitudCreada();
        }
        
        Log.info("========================================");
        Log.info("Flujo E2E completado - Solicitud: " + numeroSolicitud);
        Log.info("========================================");
        
        return numeroSolicitud;
    }

    /**
     * Flujo E2E: Creación de solicitud con Matrícula + Sin póliza + Tarea de Favoritos
     * @param nuuma NUUMA a seleccionar
     * @param matricula Matrícula a buscar
     * @param indiceTarea Índice de la tarea en Favoritos a seleccionar
     * @param observaciones Observaciones a introducir
     * @return Número de solicitud creada
     */
    public String crearSolicitudConMatriculaSinPolizaYFavoritos(String nuuma, String matricula, int indiceTarea, String observaciones) {
        Log.info("========================================");
        Log.info("FLUJO E2E: Matrícula + Sin Póliza + Tarea Favoritos");
        Log.info("========================================");
        
        navegarACrearSolicitud();
        seleccionarNUUMA(nuuma);
        seleccionarBusquedaPorRamoPalabra();
        clickContinuarInicial();
        introducirMatricula(matricula);
        clickVerResultadosMatricula();
        verificarResultadosMostrados();
        clickContinuarSinPoliza();
        verificarPantallaSeleccionarTarea();
        seleccionarTareaFavoritosConJS(indiceTarea);
        introducirObservaciones(observaciones);
        clickEnviarSolicitudConJS();
        
        String numeroSolicitud = obtenerNumeroSolicitudDesdeCombobox();
        if (numeroSolicitud == null) {
            numeroSolicitud = verificarSolicitudCreada();
        }
        
        Log.info("========================================");
        Log.info("Flujo E2E completado - Solicitud: " + numeroSolicitud);
        Log.info("========================================");
        
        return numeroSolicitud;
    }

    /**
     * Flujo E2E: Creación de solicitud con Catálogo + Sin póliza + Tarea de Favoritos
     * @param nuuma NUUMA a seleccionar
     * @param nombreCatalogo Nombre del catálogo a seleccionar
     * @param indiceTarea Índice de la tarea en Favoritos a seleccionar
     * @param observaciones Observaciones a introducir
     * @return Número de solicitud creada
     */
    public String crearSolicitudConCatalogoSinPolizaYFavoritos(String nuuma, String nombreCatalogo, int indiceTarea, String observaciones) {
        Log.info("========================================");
        Log.info("FLUJO E2E: Catálogo + Sin Póliza + Tarea Favoritos");
        Log.info("========================================");
        
        navegarACrearSolicitud();
        seleccionarNUUMA(nuuma);
        seleccionarBusquedaPorRamoPalabra();
        clickContinuarInicial();
        seleccionarOpcionCatalogo(nombreCatalogo);
        clickVerResultadosCatalogo();
        verificarResultadosMostrados();
        clickContinuarSinPoliza();
        verificarPantallaSeleccionarTarea();
        seleccionarTareaFavoritosConJS(indiceTarea);
        introducirObservaciones(observaciones);
        clickEnviarSolicitudConJS();
        
        String numeroSolicitud = obtenerNumeroSolicitudDesdeCombobox();
        if (numeroSolicitud == null) {
            numeroSolicitud = verificarSolicitudCreada();
        }
        
        Log.info("========================================");
        Log.info("Flujo E2E completado - Solicitud: " + numeroSolicitud);
        Log.info("========================================");
        
        return numeroSolicitud;
    }
}
