//package mapfre.base.actions.old;
//
//
//
//import mapfre.base.exceptions.ElementoNoDesaparecidoException;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//
//import org.openqa.selenium.*;
//import org.openqa.selenium.support.ui.ExpectedCondition;
//
//import org.openqa.selenium.support.ui.Select;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//
//import java.time.Duration;
//
//import java.util.function.Supplier;
//
//public class LibPaginaBase{
//    private static final Logger log = LogManager.getLogger();
//
//
//    protected WebDriver driver;
//
//    public LibPaginaBase(WebDriver driver) {
//        this.driver = driver;
//    }
//
//    public WebDriver getDriver() {
//        return driver;
//    }
//
//
//    // METODOS GENERICOS /////////////////////////////////////////////////////////////
//
//
//
//
//
//
//
//
//
//
//    //REPETIDOS DE BASEACTIONSELENIUM ///////////////////////////////////////////////////////////////////////////////////
//
//    protected WebElement findElementByXpath(String xpath) {
//        //return driver.findElement(By.xpath(xpath));
//        //return Browser.driverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
//        return getDriver().findElement(By.xpath(xpath));
//    }
//
//    //waitFor
//    public static void esperarCarga(int i) {
//        sleep(i*1000);
//    }
//
//    //No se usa
//    public void click(By elementLocation) {
//        driver.findElement(elementLocation).click();
//    }
//
//    //findElementById
//    protected WebElement findElementById(String id) {
//        // Espera hasta que el elemento esté presente en el DOM
//        (new WebDriverWait(getDriver(), Duration.ofSeconds(60))).until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
//        return getDriver().findElement(By.id(id));
//        //return Browser.driverWait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
//    }
//
//    //elementExistsById
//    protected boolean existeElemento(String idElemento) {
//        try {
//            findElementById(idElemento);
//        } catch (Exception e) {
//            return false;
//        }
//        return true;
//    }
//
//    //elementExistsByXpath
//    protected boolean existeElementoXpath(String xpath) {
//        try {
//            findElementByXpath(xpath);
//        } catch (Exception e) {
//            return false;
//        }
//        return true;
//    }
//
//    //waitFor
//    public static void sleep(long millis) {
//        try {
//            Thread.sleep(millis);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//    //clickElementById
//    protected void pulsarBoton(String idBoton) {
//        WebElement boton = findElementById(idBoton);
//        boton.click();
//    }
//
//    //clickElementByXpath
//    protected void pulsarBotonXpath(String xpathBoton) {
//        WebElement boton = findElementByXpath(xpathBoton);
//        boton.click();
//    }
//
//    //waitForCondition
//    public void waitForCondition(int tiempoEspera, Supplier<Boolean> funcCondition) {
//        ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() {
//            public Boolean apply(WebDriver driver) {
//                return funcCondition.get();
//            }
//        };
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(tiempoEspera));
//        wait.until(pageLoadCondition);
//    }
//
//    //pulsarBotonConEspera
//    protected void pulsarBotonConEspera(String idBoton) {
//        WebElement boton = findElementById(idBoton);
//        if (!boton.isDisplayed())
//            mostrarElementoPorId(idBoton);
//
//        if (!boton.isEnabled())
//            waitForCondition(20, () -> !boton.isEnabled());
//
//        boton.click();
//        esperarCarga(10);
//    }
//
//    //mostrarElementoPorId
//    protected void mostrarElementoPorId(String idElemento) {
//        WebElement element = findElementById(idElemento);
//        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: \"center\"});", element);
//        try {
//            Thread.sleep(4000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    //seleccionarComboPorTextoVisible
//    protected WebElement seleccionarComboPorTextoVisible(String idCombo, String textoVisible) {
//        WebElement combo = findElementById(idCombo);
//        Select selectObject = new Select(combo);
//        selectObject.selectByVisibleText(textoVisible);
//        return combo;
//    }
//
//    //mostrarElementoPorXpath
//    protected void mostrarElementoPorXpath(String xpathElemento) {
//        WebElement element = findElementByXpath(xpathElemento);
//        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: \"center\"});", element);
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    //esperaInvisibilidadElementoXPATH
//    protected boolean esperaInvisibilidadElementoXPATH(String xpath, int tiempo) {
//        try {
//            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(tiempo));
//            return wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)));
//        } catch (TimeoutException e) {
//            return false;
//        }
//    }
//
//    //esperarCargaEspera
//    public void esperarCargaEspera(String xpath) throws ElementoNoDesaparecidoException {
//        log.info("Esperamos la carga");
//
//        if (esperaDinamicaElementoXPATH(xpath, 10)) {
//            if (esperaInvisibilidadElementoXPATH(xpath, 60)) {
//                log.info("Ha desaparecido la ventana de carga");
//            } else {
//                log.info("El elemento de carga no ha desaparecido después de 60 segundos. Lanzando excepción específica...");
//                throw new ElementoNoDesaparecidoException("El elemento de carga no ha desaparecido tras 90 segundos.");
//            }
//        } else {
//            log.info("Elemento buscado no encontrado");
//        }
//        sleep(1000);
//    }
//
//    //fillFieldById
//    public WebElement rellenarCampo(String idCampo, String valor) {
//        WebElement campoNombre = findElementById(idCampo);
//        //mostrarElementoPorId(idCampo);
//        campoNombre.sendKeys(valor);
//        campoNombre.sendKeys(Keys.TAB);
//        return campoNombre;
//    }
//
//    //fillFieldByXpath
//    public WebElement rellenarCampoXpath(String idCampo, String valor) {
//        WebElement campoNombre = findElementByXpath(idCampo);
//        mostrarElementoPorXpath(idCampo);
//        campoNombre.sendKeys(valor);
//        campoNombre.sendKeys(Keys.TAB);
//        return campoNombre;
//    }
//
//    //esperaDinamicaElementoXPATH
//    protected boolean esperaDinamicaElementoXPATH(String xpath, int tiempo) {
//        try {
//            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(tiempo));
//            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
//            return true;
//        } catch (TimeoutException e) {
//            return false;
//        }
//    }
//
//}

