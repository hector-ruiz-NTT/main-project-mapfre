package mapfre.paginas.Login;

import mapfre.base.actions.BaseActionsSelenium;
import mapfre.general.Log;
import mapfre.utils.functional_utils.WaitUtils;
import org.testng.Assert;

import static mapfre.utils.DriverManager.getDriver;

public class LoginImp extends BaseActionsSelenium {

    private WaitUtils waitUtils;

    public LoginImp() {
        super(getDriver());
        waitUtils = new WaitUtils();
    }

    /**
     * Verifica que los elementos del formulario de login estén presentes
     */
    public void verificarElementosLogin() {
        Log.info("Verificando elementos del formulario de login");
        
        waitForElementToBeVisibleByXpath(LoginPage.getBannerEntornoTest(), 10);
        Assert.assertTrue(findElementByXpath(LoginPage.getBannerEntornoTest()).isDisplayed(), 
            "Banner 'Entorno Appian TEST' no está visible");
        
        Assert.assertTrue(findElementByXpath(LoginPage.getLogoAppian()).isDisplayed(), 
            "Logo de Appian no está visible");
        
        Assert.assertTrue(findElementByXpath(LoginPage.getCampoUsuario()).isDisplayed(), 
            "Campo Usuario no está visible");
        
        Assert.assertTrue(findElementByXpath(LoginPage.getCampoContrasena()).isDisplayed(), 
            "Campo Contraseña no está visible");
        
        Assert.assertTrue(findElementByXpath(LoginPage.getBotonEntrar()).isDisplayed(), 
            "Botón Entrar no está visible");
        
        Log.info("Todos los elementos del formulario de login están presentes");
    }

    /**
     * Introduce las credenciales de usuario
     * @param usuario Nombre de usuario
     * @param contrasena Contraseña del usuario
     */
    public void introducirCredenciales(String usuario, String contrasena) {
        Log.info("Introduciendo credenciales de usuario");
        
        waitForElementToBeClickableByXpath(LoginPage.getCampoUsuario(), 10);
        fillFieldByXpath(LoginPage.getCampoUsuario(), usuario);
        Log.info("Usuario introducido: " + usuario);
        
        fillFieldByXpath(LoginPage.getCampoContrasena(), contrasena);
        Log.info("Contraseña introducida");
    }

    /**
     * Hace clic en el botón Entrar para iniciar sesión
     */
    public void clickBotonEntrar() {
        Log.info("Haciendo clic en botón ENTRAR");
        clickElementByXpath(LoginPage.getBotonEntrar());
        try {
            Thread.sleep(5000); // Esperar que desaparezca el botón de login
        } catch (InterruptedException e) {
            Log.info("Espera interrumpida");
        }
    }

    /**
     * Verifica la redirección exitosa a la página principal
     */
    public void verificarRedireccionExitosa() {
        Log.info("Verificando redirección exitosa a página principal");
        
        // Esperar a que aparezca el contenido principal
        waitForElementToBeVisibleByXpath(LoginPage.getMainContent(), 15);
        
        // Verificar URL
        String currentUrl = getDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/suite/sites/sgo"), 
            "URL no contiene '/suite/sites/sgo'. URL actual: " + currentUrl);
        Log.info("URL verificada correctamente: " + currentUrl);
        
        // Verificar elementos de la página principal
        Assert.assertTrue(findElementByXpath(LoginPage.getBannerEntornoTest()).isDisplayed(), 
            "Banner no está visible después del login");
        
        Log.info("Redirección exitosa verificada");
    }

    /**
     * Abre el menú principal de navegación
     */
    public void abrirMenuPrincipal() {
        Log.info("Abriendo menú principal de navegación");
        waitForElementToBeClickableByXpath(LoginPage.getBotonAbrirMenu(), 10);
        clickElementByXpath(LoginPage.getBotonAbrirMenu());
        try {
            Thread.sleep(2000); // Esperar animación del menú
        } catch (InterruptedException e) {
            Log.info("Espera interrumpida");
        }
        Log.info("Menú principal abierto");
    }

    /**
     * Verifica que todas las opciones del menú principal estén presentes
     */
    public void verificarOpcionesMenuPrincipal() {
        Log.info("Verificando opciones del menú principal");
        
        waitForElementToBeVisibleByXpath(LoginPage.getOpcionBandejas(), 10);
        
        Assert.assertTrue(findElementByXpath(LoginPage.getOpcionBandejas()).isDisplayed(), 
            "Opción 'Bandejas' no está visible");
        
        Assert.assertTrue(findElementByXpath(LoginPage.getOpcionCrearSolicitud()).isDisplayed(), 
            "Opción 'Crear Solicitud' no está visible");
        
        Assert.assertTrue(findElementByXpath(LoginPage.getOpcionCuadroResumen()).isDisplayed(), 
            "Opción 'Cuadro Resumen' no está visible");
        
        Assert.assertTrue(findElementByXpath(LoginPage.getOpcionInformeSupervisor()).isDisplayed(), 
            "Opción 'Informe Supervisor' no está visible");
        
        Assert.assertTrue(findElementByXpath(LoginPage.getOpcionCoordinador()).isDisplayed(), 
            "Opción 'Coordinador' no está visible");
        
        Assert.assertTrue(findElementByXpath(LoginPage.getOpcionModuloBusqueda()).isDisplayed(), 
            "Opción 'Módulo de Búsqueda' no está visible");
        
        Log.info("Todas las opciones del menú principal están presentes");
    }

    /**
     * Verifica que el usuario esté correctamente logueado
     */
    public void verificarUsuarioLogueado() {
        Log.info("Verificando usuario logueado");
        
        waitForElementToBeClickableByXpath(LoginPage.getBotonOpcionesUsuario(), 10);
        clickElementByXpath(LoginPage.getBotonOpcionesUsuario());
        
        waitForElementToBeVisibleByXpath(LoginPage.getNombreUsuarioLogueado(), 10);
        Assert.assertTrue(findElementByXpath(LoginPage.getNombreUsuarioLogueado()).isDisplayed(), 
            "Nombre de usuario 'SGO_PRUEBAS1' no está visible en el menú de opciones");
        
        Log.info("Usuario verificado correctamente como logueado");
    }

    /**
     * Realiza el proceso completo de login
     * @param usuario Nombre de usuario
     * @param contrasena Contraseña del usuario
     */
    public void realizarLogin(String usuario, String contrasena) {
        Log.info("Iniciando proceso de login completo");
        verificarElementosLogin();
        introducirCredenciales(usuario, contrasena);
        clickBotonEntrar();
        verificarRedireccionExitosa();
        Log.info("Proceso de login completado exitosamente");
    }
}
