
package mapfre.base.actions;

//import mapfre.base.exceptions.ElementoNoDesaparecidoException;

import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class BaseActionsSelenium {
//    protected static WebDriver webDriver;
protected static ThreadLocal<RemoteWebDriver> webDriver = new ThreadLocal<>();

    private static String XPATH_ID_PARCIAL = "//*[contains(@id, '%s')]";


//    public BaseActionsSelenium(WebDriver driver) {
//        this.webDriver = driver;
//    }
    public BaseActionsSelenium(RemoteWebDriver driver) {
//        this.webDriver = driver;
        webDriver.set(driver);
    }

    public static RemoteWebDriver getWebDriver() {
        return webDriver.get();
    }


    private static String idCasoPrueba;

    // -------------------- FIND ELEMENTS --------------------

    /**
     * Busca un elemento en la página usando XPath.
     *
     * @param xpath Expresión XPath del elemento a buscar.
     * @return WebElement encontrado.
     */
    public static WebElement findElementByXpath(String xpath) {
        return getWebDriver().findElement(By.xpath(xpath));
    }

    /**
     * Busca un elemento en la página usando su ID.
     *
     * @param id ID del elemento a buscar.
     * @return WebElement encontrado.
     */
    public static WebElement findElementById(String id) {
//        return webDriver.findElement(By.id(id));
        (new WebDriverWait(getWebDriver(), Duration.ofSeconds(60))).until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
        return getWebDriver().findElement(By.id(id));
    }

    /**
     * Busca los elemento en la página usando su XPath.
     *
     * @param xpath Expresion XPath del los elementos a buscar.
     * @return Listado de WebElement encontrados.
     */
    public List<WebElement> findElementsByXpath(String xpath) {
        return getWebDriver().findElements(By.xpath(xpath));
    }

    /**
     * Busca los elemento en la página usando su ID.
     *
     * @param id ID del los elementos a buscar.
     * @return Listado de WebElement encontrados.
     */
    public List<WebElement> findElementsID(String id) {
        return getWebDriver().findElements(By.id(id));
    }

    /**
     * Busca un elemento en la página usando su nombre.
     *
     * @param name Nombre del elemento a buscar.
     * @return WebElement encontrado.
     */

    public WebElement findElementByName(String name) {
        return getWebDriver().findElement(By.name(name));
    }

    /**
     * Busca un elemento en la página usando su CSS selector.
     *
     * @param cssSelector Selector CSS del elemento a buscar.
     * @return WebElement encontrado.
     */
    public WebElement findElementByCssSelector(String cssSelector) {
        return getWebDriver().findElement(By.cssSelector(cssSelector));
    }

    /**
     * Busca un elemento en la página usando su class name.
     *
     * @param className Nombre de la clase del elemento a buscar.
     * @return WebElement encontrado.
     */
    public WebElement findElementByClassName(String className) {
        return getWebDriver().findElement(By.className(className));
    }

    public boolean existeElementoTexto(String texto) {
        try {
            findElementByText(texto);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public WebElement findElementByText(String text) {
        return getWebDriver().findElement(By.xpath("//*[contains (text(),\"" + text + "\")]"));
    }

    public void pulsarBotonTexto(String texto) {
        WebElement boton = findElementByText(texto);
        boton.click();
    }

    protected boolean esperaDinamicaElementoTexto(String texto, int tiempo) {
        getWebDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        for (int i = 0; i < tiempo; i++) {
            if (existeElementoTexto(texto)) {
                getWebDriver().manage().timeouts().implicitlyWait(90, TimeUnit.SECONDS);
                return true;
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        getWebDriver().manage().timeouts().implicitlyWait(90, TimeUnit.SECONDS);
        return false;
    }

    protected WebElement findElementByIdParcial(String id) {
        return findElementByXpath(String.format(XPATH_ID_PARCIAL, id));
    }

    public boolean seMuestraElementoIdParcial(String elemento) {
        return findElementByIdParcial(elemento).isDisplayed();
    }

    public boolean seMuestraElemento(String elemento) {
        return findElementById(elemento).isDisplayed();
    }

    public boolean seMuestraElementoXpath(String elemento) {
        return findElementByXpath(elemento).isDisplayed();
    }

    protected boolean esperarElementoXpathHabilitado(String xpath, int tiempo) {
        try {
            WebElement elemento = findElementByXpath(xpath);
            for (int i = 0; i < tiempo; i++) {
                if (elemento.isEnabled()) {
                    return true;
                } else {
                    Thread.sleep(1000);
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public String recogerValorCampoXpath(String xpathInput) {
        String texto = "";
        if (elementExistsByXpath(xpathInput)) {
            WebElement element = findElementByXpath(xpathInput);
            texto = element.getText();
        } else {
            Assert.fail("No se ha encontrado el elemento");
        }
        return texto;
    }

    public boolean existeElementoIdParcialXpath(String idElemento) {
        try {
            findElementByXpath(String.format(XPATH_ID_PARCIAL, idElemento));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    protected List<WebElement> findElementsByTagName(String tagName) {
        return getWebDriver().findElements(By.tagName(tagName));
    }

    protected WebElement findElementByLabel(String label) {
        return getWebDriver().findElement(By.linkText(label));
    }

    /**
     * Verifica si un elemento con un ID específico existe en la página.
     *
     * @param id ID del elemento a verificar.
     * @return true si el elemento existe, false en caso contrario.
     */
    public static boolean elementExistsById(String id) {
        try {
            findElementById(id);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Verifica si un elemento con un XPath específico existe en la página.
     *
     * @param xpath Expresión XPath del elemento a verificar.
     * @return true si el elemento existe, false en caso contrario.
     */
    public static boolean elementExistsByXpath(String xpath) {
        try {
            findElementByXpath(xpath);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    // -------------------- WAIT METHODS --------------------

    /**
     * Espera un número determinado de segundos.
     *
     * @param seconds Número de segundos a esperar.
     * @throws InterruptedException si ocurre una interrupción durante la espera.
     */
    public static void waitFor(int seconds) throws InterruptedException {
        Thread.sleep(seconds * 1000);
    }

    /**
     * Espera hasta que un elemento identificado por XPath sea visible.
     *
     * @param xpath   XPath del elemento a esperar.
     * @param seconds Tiempo máximo de espera en segundos.
     */
    public static void waitForElementToBeVisibleByXpath(String xpath, int seconds) {
        WebDriverWait wait = new WebDriverWait(getWebDriver(), Duration.ofSeconds(seconds));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
    }

    public WebElement waitUntilElementIsVisible(WebElement webElement) {
        return new WebDriverWait(getWebDriver(), Duration.ofSeconds(30))
                .until(ExpectedConditions.visibilityOf(webElement));
    }

    /**
     * Espera hasta que un elemento identificado por XPath sea clickeable.
     *
     * @param xpath   XPath del elemento a esperar.
     * @param seconds Tiempo máximo de espera en segundos.
     */
    public static void waitForElementToBeClickableByXpath(String xpath, int seconds) {
        WebDriverWait wait = new WebDriverWait(getWebDriver(), Duration.ofSeconds(seconds));
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
    }

    /**
     * Espera hasta que un elemento identificado por XPath desaparezca de la página.
     *
     * @param xpath   XPath del elemento a esperar.
     * @param timeout Tiempo máximo de espera en segundos.
     */
    public static void waitForElementToDisappearByXpath(String xpath, int timeout) {
        WebDriverWait wait = new WebDriverWait(getWebDriver(), Duration.ofSeconds(timeout));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)));
    }

    protected boolean esperaInvisibilidadElementoXPATH(String xpath, int tiempo) {
        try {
            WebDriverWait wait = new WebDriverWait(getWebDriver(), Duration.ofSeconds(tiempo));
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)));
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected boolean esperaDinamicaElementoXPATH(String xpath, int tiempo) {
        try {
            WebDriverWait wait = new WebDriverWait(getWebDriver(), Duration.ofSeconds(tiempo));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected boolean esperaDinamicaElementoID(String id, int tiempo) {
        try {
            WebDriverWait wait = new WebDriverWait(getWebDriver(), Duration.ofSeconds(tiempo));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    // -------------------- CLICK METHODS --------------------

    /**
     * Hace clic en un elemento identificado por su ID.
     *
     * @param id ID del elemento a hacer clic.
     */
    public static void clickElementById(String id) {
        WebElement webElement = findElementById(id);
        webElement.click();
    }

    /**
     * Hace clic en un elemento identificado por XPath.
     *
     * @param xpath XPath del elemento a hacer clic.
     */
    public static void clickElementByXpath(String xpath) {
        WebElement webElement = findElementByXpath(xpath);
        webElement.click();
    }


    public static void clickElement(WebElement webElement) {
        webElement.click();
    }

    public void pulsarBotonIdParcial(String idParcial) {
        WebElement boton = findElementByIdParcial(idParcial);

        if (!boton.isEnabled())
            waitForCondition(10000, () -> !boton.isEnabled());

        boton.click();
    }

    // -------------------- INPUT METHODS --------------------

    /**
     * Limpia un campo de entrada identificado por ID.
     *
     * @param idCampo ID del campo a limpiar.
     * @return WebElement del campo limpiado.
     */
    public static WebElement clearElementById(String idCampo) {
        WebElement campoNombre = findElementById(idCampo);
        campoNombre.clear();
        return campoNombre;
    }

    /**
     * Limpia un campo de entrada identificado por XPath.
     *
     * @param xpath XPath del campo a limpiar.
     * @return WebElement del campo limpiado.
     */
    public static WebElement clearElementByXpath(String xpath) {
        WebElement campoNombre = findElementByXpath(xpath);
        campoNombre.clear();
        return campoNombre;
    }

    /**
     * Rellena un campo de entrada identificado por ID con un valor específico.
     *
     * @param idCampo ID del campo a rellenar.
     * @param valor   Texto a ingresar en el campo.
     * @return WebElement del campo rellenado.
     */
    public static WebElement fillFieldById(String idCampo, String valor) {
        WebElement campoNombre = findElementById(idCampo);
        campoNombre.sendKeys(valor);
        campoNombre.sendKeys(Keys.TAB);
        return campoNombre;
    }

    /**
     * Rellena un campo de entrada identificado por XPath con un valor específico.
     *
     * @param xpath XPath del campo a rellenar.
     * @param valor Texto a ingresar en el campo.
     * @return WebElement del campo rellenado.
     */
    public static WebElement fillFieldByXpath(String xpath, String valor) {
        WebElement campoNombre = findElementByXpath(xpath);
        campoNombre.sendKeys(valor);
        return campoNombre;
    }


    public static WebElement fillField(WebElement element, String valor) {
        element.sendKeys(valor);
        return element;
    }

    // -------------------- GET TEXT METHODS --------------------

    /**
     * Obtiene el texto de un elemento identificado por ID.
     *
     * @param id ID del elemento.
     * @return Texto del elemento.
     */
    public static String getElementTextById(String id) {
        return findElementById(id).getText();
    }

    /**
     * Obtiene el texto de un elemento identificado por XPath.
     *
     * @param xpath XPath del elemento.
     * @return Texto del elemento.
     */
    public static String getElementTextByXpath(String xpath) {
        return findElementByXpath(xpath).getText();
    }

    /**
     * Obtiene un atributo específico de un elemento identificado por XPath.
     *
     * @param xpath     XPath del elemento.
     * @param attribute Nombre del atributo a obtener.
     * @return Valor del atributo.
     */
    public static String getElementAttributeByXpath(String xpath, String attribute) {
        return findElementByXpath(xpath).getAttribute(attribute);
    }

    public boolean isAttributePresent(WebElement element, String attribute) {
        Boolean result = false;
        try {
            String value = element.getAttribute(attribute);
            if (value != null) {
                result = true;
            }
        } catch (Exception e) {
        }

        return result;
    }

    /**
     * Obtiene el valor de una propiedad CSS de un elemento identificado por XPath.
     *
     * @param xpath    XPath del elemento.
     * @param property Nombre de la propiedad CSS a obtener.
     * @return Valor de la propiedad CSS.
     */
    public static String getElementCssPropertyByXpath(String xpath, String property) {
        return findElementByXpath(xpath).getCssValue(property);
    }

    // -------------------- SCREENSHOT METHOD --------------------

    /**
     * Captura una captura de pantalla y la guarda en la ruta especificada.
     *
     * @param filePath Ruta donde se guardará la captura de pantalla.
     */
    public static void takeScreenshot(String filePath) {
        File srcFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
        try {
            Files.copy(srcFile.toPath(), Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -------------------- ALERT HANDLING --------------------

    /**
     * Acepta una alerta emergente en la página.
     */
    public static void acceptAlert() {
        WebDriverWait wait = new WebDriverWait(getWebDriver(), Duration.ofSeconds(5));
        wait.until(ExpectedConditions.alertIsPresent());
        getWebDriver().switchTo().alert().accept();
    }

    /**
     * Rechaza una alerta emergente en la página.
     */
    public static void dismissAlert() {
        WebDriverWait wait = new WebDriverWait(getWebDriver(), Duration.ofSeconds(5));
        wait.until(ExpectedConditions.alertIsPresent());
        getWebDriver().switchTo().alert().dismiss();
    }

    // -------------------- WINDOW HANDLING --------------------

    /**
     * Cambia el control de Selenium a una ventana específica por su título.
     *
     * @param title Título de la ventana a la que se desea cambiar.
     */
    public static void switchToWindowByTitle(String title) {
        for (String handle : getWebDriver().getWindowHandles()) {
            getWebDriver().switchTo().window(handle);
            if (getWebDriver().getTitle().equals(title)) {
                return;
            }
        }
        Assert.fail("No se encontró la ventana con el título: " + title);
    }

    // -------------------- SELECT COMBO --------------------

    /**
     * Selecciona un valor en un combo box identificado por ID usando el texto visible.
     *
     * @param id   ID del combo box.
     * @param text Texto visible a seleccionar.
     */
    public static void selectComboByVisibleText(String id, String text) {
        Select select = new Select(findElementById(id));
        select.selectByVisibleText(text);
    }

    /**
     * Selecciona un valor en un combo box identificado por ID usando su valor.
     *
     * @param id    ID del combo box.
     * @param value Valor del option a seleccionar.
     */
    public static void selectComboByValue(String id, String value) {
        Select select = new Select(findElementById(id));
        select.selectByValue(value);
    }

    /**
     * Selecciona un valor en un combo box identificado por XPath usando el texto visible.
     *
     * @param xpath XPath del combo box.
     * @param text  Texto visible a seleccionar.
     */
    public static void selectComboByVisibleTextByXPATH(String xpath, String text) {
        Select select = new Select(findElementByXpath(xpath));
        select.selectByVisibleText(text);
    }

    /**
     * Selecciona un valor en un combo box identificado por XPath usando su valor.
     *
     * @param xpath XPath del combo box.
     * @param value Valor del option a seleccionar.
     */
    public static void selectComboByValueByXPATH(String xpath, String value) {
        Select select = new Select(findElementByXpath(xpath));
        select.selectByValue(value);
    }

    protected WebElement seleccionarComboPorTextoVisible(String idCombo, String textoVisible) {
        WebElement combo = findElementById(idCombo);
        Select selectObject = new Select(combo);
        selectObject.selectByVisibleText(textoVisible);
        return combo;
    }

    public void seleccionarIndiceComboXpath(String xpathCombo, int indice) {
        WebElement combo = findElementByXpath(xpathCombo);
        for (int i = 0; i < indice; i++)
            combo.sendKeys(Keys.DOWN);
        combo.sendKeys(Keys.TAB);
    }

    // -------------------- FILE UPLOAD --------------------

    /**
     * Sube un archivo a un input file identificado por ID.
     *
     * @param id       ID del input file.
     * @param filePath Ruta del archivo a subir.
     */
    public static void uploadFileById(String id, String filePath) {
        WebElement fileInput = findElementById(id);
        fileInput.sendKeys(filePath);
    }

    /**
     * Sube un archivo a un input file identificado por XPath.
     *
     * @param xpath    XPath del input file.
     * @param filePath Ruta del archivo a subir.
     */
    public static void uploadFileByXpath(String xpath, String filePath) {
        WebElement fileInput = findElementByXpath(xpath);
        fileInput.sendKeys(filePath);
    }

    // -------------------- CHECKBOX HANDLING --------------------

    /**
     * Verifica si un checkbox identificado por XPath está seleccionado.
     *
     * @param xpath XPath del checkbox.
     * @return true si está seleccionado, false en caso contrario.
     */
    public static boolean isCheckboxCheckedByXpath(String xpath) {
        return findElementByXpath(xpath).isSelected();
    }

    /**
     * Verifica si un checkbox identificado por ID está seleccionado.
     *
     * @param id ID del checkbox.
     * @return true si está seleccionado, false en caso contrario.
     */
    public static boolean isCheckboxCheckedById(String id) {
        return findElementById(id).isSelected();
    }

    // -------------------- NAVIGATION METHODS --------------------

    /**
     * Obtiene la URL actual de la página.
     *
     * @return URL actual.
     */
    public static String getCurrentUrl() {
        return getWebDriver().getCurrentUrl();
    }

    /**
     * Refresca la página actual.
     */
    public static void refreshPage() {
        getWebDriver().navigate().refresh();
    }

    /**
     * Navega hacia la página anterior en el historial.
     */
    public static void goBack() {
        getWebDriver().navigate().back();
    }

    /**
     * Navega hacia la página siguiente en el historial.
     */
    public static void goForward() {
        getWebDriver().navigate().forward();
    }

    // -------------------- COUNT ELEMENTS --------------------

    /**
     * Cuenta la cantidad de elementos que coinciden con un XPath dado.
     *
     * @param xpath XPath de los elementos a contar.
     * @return Número de elementos encontrados.
     */
    public static int countElementsByXpath(String xpath) {
        return getWebDriver().findElements(By.xpath(xpath)).size();
    }

    /**
     * Cuenta la cantidad de elementos que coinciden con un id dado.
     *
     * @param id id de los elementos a contar.
     * @return Número de elementos encontrados.
     */
    public static int countElementsById(String id) {
        return getWebDriver().findElements(By.id(id)).size();
    }

    // -------------------- WAIT FOR ELEMENT EXISTENCE --------------------

    /**
     * Espera hasta que un elemento identificado por su ID exista en la página o se alcance el tiempo de espera.
     *
     * @param id      ID del elemento a esperar.
     * @param timeout Tiempo máximo de espera en segundos.
     */
    public static void waitForElementToExistById(String id, int timeout) {
        int contador = 0;
        while (!elementExistsById(id)) {
            try {
                waitFor(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            contador++;
            if (contador == timeout) Assert.fail("Elemento no encontrado después de " + timeout + " segundos.");
        }
    }

    /**
     * Espera hasta que un elemento identificado por su XPath exista en la página o se alcance el tiempo de espera.
     *
     * @param xpath   XPath del elemento a esperar.
     * @param timeout Tiempo máximo de espera en segundos.
     */
    public static void waitForElementToExistByXpath(String xpath, int timeout) {
        int contador = 0;
        while (!elementExistsByXpath(xpath)) {
            try {
                waitFor(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            contador++;
            if (contador == timeout) Assert.fail("Elemento no encontrado después de " + timeout + " segundos.");
        }
    }

    public void waitForCondition(int tiempoEspera, Supplier<Boolean> funcCondition) {
        ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return funcCondition.get();
            }
        };
        WebDriverWait wait = new WebDriverWait(getWebDriver(), Duration.ofSeconds(tiempoEspera));
        wait.until(pageLoadCondition);
    }

    protected void pulsarBotonConEspera(String idBoton) {
        WebElement boton = findElementById(idBoton);
        if (!boton.isDisplayed())
            mostrarElementoPorId(idBoton);

        if (!boton.isEnabled())
            waitForCondition(20, () -> !boton.isEnabled());

        boton.click();
        try {
            waitFor(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void mostrarElementoPorId(String idElemento) {
        WebElement element = findElementById(idElemento);
        ((JavascriptExecutor) getWebDriver()).executeScript("arguments[0].scrollIntoView({block: \"center\"});", element);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void mostrarElementoPorXpath(String xpathElemento) {
        WebElement element = findElementByXpath(xpathElemento);
        ((JavascriptExecutor) getWebDriver()).executeScript("arguments[0].scrollIntoView({block: \"center\"});", element);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Genera un ID de caso de prueba a partir de un nombre, separando por el guion bajo y tomando la primera parte.
     *
     * @param name Nombre del caso de prueba.
     * @return ID del caso de prueba generado.
     */
    public static String generateTestCaseId(String name) {
        return name.split("_")[0];
    }

}