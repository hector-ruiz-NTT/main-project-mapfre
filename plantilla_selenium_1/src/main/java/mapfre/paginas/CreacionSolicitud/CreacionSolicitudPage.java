package mapfre.paginas.CreacionSolicitud;

public class CreacionSolicitudPage {

    // Elementos de la página Crear Solicitud
    // ⭐ CRÍTICO: Hacer clic en el botón del lápiz primero para habilitar el textbox
    private static final String botonEscribirNuumaLapiz = "//button[contains(.,'Escribir nuuma especifico')]";
    // ⭐ CRÍTICO: Escribir el NUUMA manualmente (EFERNA2) en lugar de seleccionarlo del dropdown
    // Esto habilita todas las tareas en la tabla de Favoritos
    private static final String campoNUUMA = "//input[@placeholder='Escribir NUUMA...']";
    // El botón CONTINUAR es un StampWidget (div con role=link) que está al lado del texto CONTINUAR
    private static final String botonContinuarInicial = "//strong[text()='CONTINUAR']/ancestor::div[contains(@class,'SideBySideItem')]//following-sibling::div//div[@role='link' and contains(@class,'StampWidget')]";
    
    // Radio buttons: usar el label genérico directamente, sin aria-label
    private static final String radioPoliNombre = "//input[@role='radio' and @name='Tipo de búsqueda']";
    private static final String radioRamoPalabra = "//input[@value='Por ramo/Palabra clave']";
    private static final String campoNIFCIF = "(//input[@placeholder='Escribir...'])[1]"; // Primer input con este placeholder después de CONTINUAR
    // Campo de matrícula - aparece en lugar de NIF/CIF cuando se selecciona búsqueda por matrícula
    private static final String campoMatricula = "//input[@placeholder='Escribir matrícula...']";
    // Combobox de Catálogo - aparece para seleccionar catálogos predefinidos
    private static final String comboCatalogo = "//div[contains(text(),'Catálogo')]/following-sibling::div//div[@role='combobox']";
    private static final String opcionCatalogo = "//div[@role='option'][normalize-space(.)='%s']"; // parametrizable
    // VER RESULTADOS es un botón HTML estándar (<button>), no un StampWidget
    private static final String botonVerResultados = "//button[contains(@class,'Button---btn') and .//span[text()='VER RESULTADOS']]";
    // El botón CONTINUAR (después de pólizas) - el último CONTINUAR de la página
    // El texto cambia dinámicamente: "CONTINUAR" o "CONTINUAR SIN POLIZA"
    private static final String botonContinuarPoliza = "(//strong[contains(text(),'CONTINUAR')])[last()]/ancestor::div[contains(@class,'SideBySideGroup')]//div[@role='link' and contains(@class,'StampWidget')]";
    
    // Botón CONTINUAR (después de resultados): usa contains para funcionar con ambos textos
    private static final String botonContinuarSinPoliza = "(//strong[contains(text(),'CONTINUAR')])[last()]/ancestor::div[contains(@class,'SideBySideGroup')]//div[@role='link' and contains(@class,'StampWidget')]";
    
    // Tabla de Favoritos: seleccionar primera tarea disponible
    private static final String primeraTareaFavoritos = "//table//tbody//tr[1]//td[1]//p//a[contains(@href,'#')]";
    
    // Elementos de la tabla de resultados de pólizas
    // Los resultados NO son un grid, sino una lista de elementos genéricos con información de pólizas
    private static final String contadorResultados = "//strong[contains(text(),'Elementos')]"; // Ej: "1 Elementos"
    private static final String primerResultado = "(//a[contains(@href,'#') and .//span[text()='Seleccionar']])[1]"; // Primera póliza
    private static final String segundoResultado = "(//a[contains(@href,'#') and .//span[text()='Seleccionar']])[2]"; // Segunda póliza
    private static final String tercerResultado = "(//a[contains(@href,'#') and .//span[text()='Seleccionar']])[3]"; // Tercera póliza
    private static final String numeroPoliza = "//p[contains(text(),'Póliza')]/following-sibling::div//p"; // Número de póliza del resultado seleccionado
    private static final String ramoPoliza = "//div[text()='Ramo']/following-sibling::div//p"; // Ramo del resultado seleccionado
    
    // Elementos de selección de tarea
    // NOTA: En Appian, el combobox "Tarea" tiene un label anterior. Buscamos el contenedor que tiene el texto "Tarea" y luego el combobox
    private static final String comboTarea = "//div[contains(text(),'Tarea')]/following-sibling::div//div[@role='combobox']";
    // Las opciones aparecen en un listbox. El texto puede estar en un elemento hijo como span o directamente en el div[@role='option']
    private static final String opcionTarea = "//div[@role='option'][normalize-space(.)='%s']"; // parametrizable
    private static final String botonBuscar = "//strong[text()='BUSCAR']";
    private static final String botonContinuarTarea = "//a[contains(@class,'StampWidget')]//strong[text()='CONTINUAR']";
    
    // Elementos del formulario final
    private static final String campoObservaciones = "//textarea[@placeholder='Introduzca una observación...']";
    // El botón ENVIAR SOLICITUD es un StampWidget con role='link' que está en el mismo contenedor que el texto
    // Buscamos el StampWidget que está después del strong con el texto 'ENVIAR SOLICITUD'
    private static final String botonEnviarSolicitud = "//strong[text()='ENVIAR SOLICITUD']/ancestor::div[contains(@class,'SideBySideGroup')]//div[contains(@class,'StampWidget') and @role='link']";
    
    // Elementos de confirmación
    private static final String mensajeExito = "//div[contains(@class,'MessageLayout')]";
    private static final String numeroSolicitud = "//strong[contains(text(),'SGO')]";
    
    // Elementos de navegación (del menú principal)
    private static final String botonNavegacion = "//button[contains(.,'Navegación')]";
    private static final String linkBandejas = "//a[@href='https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/bandejas']";
    private static final String opcionCrearSolicitud = "//a[@href='https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/crear-solicitud']";
    
    // Getters
    public static String getBotonEscribirNuumaLapiz() {
        return botonEscribirNuumaLapiz;
    }

    public static String getCampoNUUMA() {
        return campoNUUMA;
    }

    public static String getBotonContinuarInicial() {
        return botonContinuarInicial;
    }

    public static String getRadioPoliNombre() {
        return radioPoliNombre;
    }

    public static String getRadioRamoPalabra() {
        return radioRamoPalabra;
    }

    public static String getCampoNIFCIF() {
        return campoNIFCIF;
    }

    public static String getBotonVerResultados() {
        return botonVerResultados;
    }

    public static String getBotonContinuarPoliza() {
        return botonContinuarPoliza;
    }

    public static String getBotonContinuarSinPoliza() {
        return botonContinuarSinPoliza;
    }

    public static String getPrimeraTareaFavoritos() {
        return primeraTareaFavoritos;
    }

    public static String getContadorResultados() {
        return contadorResultados;
    }

    public static String getPrimerResultado() {
        return primerResultado;
    }

    public static String getSegundoResultado() {
        return segundoResultado;
    }

    public static String getTercerResultado() {
        return tercerResultado;
    }

    public static String getNumeroPoliza() {
        return numeroPoliza;
    }

    public static String getRamoPoliza() {
        return ramoPoliza;
    }

    public static String getComboTarea() {
        return comboTarea;
    }

    public static String getOpcionTarea() {
        return opcionTarea;
    }

    public static String getBotonBuscar() {
        return botonBuscar;
    }

    public static String getBotonContinuarTarea() {
        return botonContinuarTarea;
    }

    public static String getCampoObservaciones() {
        return campoObservaciones;
    }

    public static String getBotonEnviarSolicitud() {
        return botonEnviarSolicitud;
    }

    public static String getMensajeExito() {
        return mensajeExito;
    }

    public static String getNumeroSolicitud() {
        return numeroSolicitud;
    }

    public static String getLinkBandejas() {
        return linkBandejas;
    }

    public static String getBotonNavegacion() {
        return botonNavegacion;
    }

    public static String getOpcionCrearSolicitud() {
        return opcionCrearSolicitud;
    }

    public static String getCampoMatricula() {
        return campoMatricula;
    }

    public static String getComboCatalogo() {
        return comboCatalogo;
    }

    public static String getOpcionCatalogo() {
        return opcionCatalogo;
    }
}
