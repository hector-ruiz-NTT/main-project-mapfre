package mapfre.paginas.Login;

public class LoginPage {

    // Login Page Elements
    private static final String campoUsuario = "//input[@id='un']";
    private static final String campoContrasena = "//input[@id='pw']";
    private static final String botonEntrar = "//button[contains(.,'Entrar')] | //input[@id='jsLoginButton']";
    private static final String checkboxRecordarme = "//input[@type='checkbox']";
    private static final String linkOlvidoContrasena = "//a[contains(text(),'Olvidó su contraseña')]";
    private static final String bannerEntornoTest = "//div[@role='banner']";
    private static final String logoAppian = "//img[@alt='Appian']";
    
    // Home Page Elements (Post-Login)
    private static final String botonAbrirMenu = "//button[contains(.,'Abrir menú')]";
    private static final String botonNavegacion = "//button[contains(.,'Navegación')]";
    private static final String botonOpcionesUsuario = "//button[contains(.,'Opciones de usuario')]";
    private static final String mainContent = "//main";
    
    // Menu Options
    private static final String opcionBandejas = "//a[@href='https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/bandejas']";
    private static final String opcionCrearSolicitud = "//a[@href='https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/crear-solicitud']";
    private static final String opcionCuadroResumen = "//a[@href='https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/cuadro-resumen']";
    private static final String opcionInformeSupervisor = "//a[@href='https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/informe-supervisor']";
    private static final String opcionCoordinador = "//a[@href='https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/coordinador']";
    private static final String opcionModuloBusqueda = "//a[@href='https://mapfrespain-test.appiancloud.com/suite/sites/sgo/page/m-dulo-de-b-squeda']";
    
    // User Menu Elements
    private static final String nombreUsuarioLogueado = "//div[contains(text(),'SGO_PRUEBAS1')]";
    private static final String botonCerrarSesion = "//button[contains(.,'Cerrar')]";
    
    // Getters
    public static String getCampoUsuario() {
        return campoUsuario;
    }

    public static String getCampoContrasena() {
        return campoContrasena;
    }

    public static String getBotonEntrar() {
        return botonEntrar;
    }

    public static String getCheckboxRecordarme() {
        return checkboxRecordarme;
    }

    public static String getLinkOlvidoContrasena() {
        return linkOlvidoContrasena;
    }

    public static String getBannerEntornoTest() {
        return bannerEntornoTest;
    }

    public static String getLogoAppian() {
        return logoAppian;
    }

    public static String getBotonAbrirMenu() {
        return botonAbrirMenu;
    }

    public static String getBotonNavegacion() {
        return botonNavegacion;
    }

    public static String getBotonOpcionesUsuario() {
        return botonOpcionesUsuario;
    }

    public static String getMainContent() {
        return mainContent;
    }

    public static String getOpcionBandejas() {
        return opcionBandejas;
    }

    public static String getOpcionCrearSolicitud() {
        return opcionCrearSolicitud;
    }

    public static String getOpcionCuadroResumen() {
        return opcionCuadroResumen;
    }

    public static String getOpcionInformeSupervisor() {
        return opcionInformeSupervisor;
    }

    public static String getOpcionCoordinador() {
        return opcionCoordinador;
    }

    public static String getOpcionModuloBusqueda() {
        return opcionModuloBusqueda;
    }

    public static String getNombreUsuarioLogueado() {
        return nombreUsuarioLogueado;
    }

    public static String getBotonCerrarSesion() {
        return botonCerrarSesion;
    }
}
