package mapfre.paginas.Ejemplo;

import mapfre.base.actions.BaseActionsSelenium;
import mapfre.general.Log;
import mapfre.utils.functional_utils.WaitUtils;

import static mapfre.utils.DriverManager.getDriver;

public class EjemploImp extends BaseActionsSelenium {

    private EjemploPage ejemploPage = new EjemploPage();
    private WaitUtils waitUtils;

    public EjemploImp() {
        super(getDriver());
        waitUtils = new WaitUtils();
    }

    public void rechazarCookies() {
        Log.info("Rechazando cookies");
        // Intentar botón "Rechazar"; si no existe, intentar "Cerrar" dentro del dialog
        try {
            waitUtils.fluentWaitForClickable(findElementByXpath(ejemploPage.getPopUpCoockies()),30);
            clickElementByXpath(ejemploPage.getPopUpCoockies());
        } catch (Exception e) {
            Log.info("Botón 'Rechazar' no encontrado, intentando 'Cerrar' de cookies");
            try {
                waitUtils.fluentWaitForClickable(findElementByXpath(ejemploPage.getPopUpCookiesCerrar()),15);
                clickElementByXpath(ejemploPage.getPopUpCookiesCerrar());
            } catch (Exception ignored) {
                Log.info("No se mostró popup de cookies relevante");
            }
        }
        // Esperar caja de búsqueda como señal de que la home está interactuable
        waitForElementToBeClickableByXpath(ejemploPage.getBoxSearch(),30);
    }

    public void buscarPantalones() {
        Log.info("Buscando pantalones en Zara");
        clickElementByXpath(ejemploPage.getBoxSearch());
        fillFieldByXpath(ejemploPage.getBoxSearch(),"pantalones");
        findElementByXpath(ejemploPage.getPopUpCoockies()).submit();
    }

    public void manejarModalRegion() {
        Log.info("Verificando modal de región");
        try {
            // Si aparece el modal, presionar continuar en España; si no, ignorar
            waitUtils.fluentWaitForClickable(findElementByXpath(ejemploPage.getModalContinuarEspana()),5);
            clickElementByXpath(ejemploPage.getModalContinuarEspana());
            Log.info("Modal región cerrado con 'Continuar en España'");
        } catch (Exception e) {
            Log.info("No apareció modal de región inicial");
        }
    }

}