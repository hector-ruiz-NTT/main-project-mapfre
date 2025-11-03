package mapfre.base.actions;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

public class BaseActionsJS {
    protected static WebDriver webDriver;

    public BaseActionsJS() {
        this.webDriver = BaseActionsSelenium.getWebDriver();
    }

    // -------------------- MÉTODO GENÉRICO PARA EJECUTAR JAVASCRIPT --------------------

    /**
     * Ejecuta un script de JavaScript con manejo de excepciones.
     * @param script Código JavaScript a ejecutar.
     * @param args Argumentos opcionales que el script pueda necesitar.
     */
    private static void executeWithJS(String script, Object... args) {
        try {
            JavascriptExecutor jsExecutor = (JavascriptExecutor) webDriver;
            jsExecutor.executeScript(script, args);
        } catch (Exception e) {
            System.err.println("Error ejecutando JavaScript: " + e.getMessage());
        }
    }

    // -------------------- MÉTODOS DE SCROLL --------------------

    /**
     * Desplaza la vista hasta que el elemento identificado por su XPath quede centrado en la pantalla.
     * @param xpath XPath del elemento a visualizar.
     */
    public static void scrollIntoViewByXpath(String xpath) {
        try {
            WebElement element = BaseActionsSelenium.findElementByXpath(xpath);
            executeWithJS("arguments[0].scrollIntoView({block: \"center\"});", element);
        } catch (Exception e) {
            System.err.println("Error en scrollIntoViewByXpath: " + e.getMessage());
        }
    }

    /**
     * Desplaza la página hacia abajo 500 píxeles.
     */
    public static void scrollDown() {
        executeWithJS("window.scrollBy(0,500)");
    }

    /**
     * Desplaza la página hacia arriba 500 píxeles.
     */
    public static void scrollUp() {
        executeWithJS("window.scrollBy(0,-500)");
    }

    /**
     * Desplaza la página hasta la parte inferior del documento.
     */
    public static void scrollToBottom() {
        executeWithJS("window.scrollTo(0, document.body.scrollHeight)");
    }

    /**
     * Desplaza la página hasta la parte superior del documento.
     */
    public static void scrollToTop() {
        executeWithJS("window.scrollTo(0, 0)");
    }

    // -------------------- MÉTODOS DE CLICK --------------------

    /**
     * Hace clic en un elemento usando JavaScript, identificándolo por su XPath.
     * Útil cuando el elemento no es interactuable mediante WebDriver.click().
     * @param xpath XPath del elemento a hacer clic.
     */
    public static void clickElementWithJS(String xpath) {
        try {
            WebElement element = BaseActionsSelenium.findElementByXpath(xpath);
            executeWithJS("arguments[0].click();", element);
        } catch (Exception e) {
            System.err.println("Error en clickElementWithJS: " + e.getMessage());
        }
    }

}