//package mapfre.base.actions;
//
//import mapfre.general.*;
//import mapfre.utils.Listeners.TestListener;
//import mapfre.utils.functional_utils.ServerUtils;
//import org.apache.commons.io.FileUtils;
//import org.openqa.selenium.*;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.Select;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import org.testng.Assert;
//import mapfre.utils.functional_utils.ExcelUtils;
//
//import java.awt.*;
//import java.awt.event.KeyEvent;
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.time.Duration;
//import java.util.*;
//
//public class BasePageOriginal {
//
//    protected WebDriver driver = Browser.getDriver();
//    private static String idCasoPrueba;
//    public static String FRAME_OPERACIONES = "ARQOS_MC";
//    private static String ID_BTN_DETALLES = "details-button";
//    private static String ID_URL_ACCEDER = "proceed-link";
//    private static final String XPATH_BTN_ACEPTAR = "//button[@id='onetrust-accept-btn-handler']";
//
//    protected WebElement findElementByXpath(String xpath) {
//        return driver.findElement(By.xpath(xpath));
//    }
//
//    protected WebElement findElementById(String id) {
//        return driver.findElement(By.id(id));
//    }
//
//    public void esperarCarga() {
//        sleep(1000);
//    }
//
//    public void esperarCargaAlta() {
//        sleep(10000);
//    }
//
//    public void esperarCargaMedia() {
//        sleep(3000);
//    }
//
//
//    public void sleep(long millis) {
//        try {
//            Thread.sleep(millis);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//    protected boolean existeElemento(String idElemento) {
//        try {
//            findElementById(idElemento);
//        } catch (Exception e) {
//            return false;
//        }
//        return true;
//    }
//
//    protected boolean existeElementoXpath(String xpath) {
//        try {
//            findElementByXpath(xpath);
//        } catch (Exception e) {
//            return false;
//        }
//        return true;
//    }
//
//    protected int numAleatorio(int numero1, int numero2) {
//        int min = numero1;
//        int max = numero2;
//        Random random = new Random();
//        int value = random.nextInt(max + min) + min;
//        return value;
//
//    }
//
//    protected WebElement seleccionarComboPorTextoVisible(String idCombo, String textoVisible) {
//        WebElement combo = findElementById(idCombo);
//        Select selectObject = new Select(combo);
//        selectObject.selectByVisibleText(textoVisible);
//        return combo;
//    }
//
//    protected void seleccionarElementoInput(String idInput, String texto) {
//        pulsarBoton(idInput);
//        esperarCarga();
//        mostrarElementoPorXPATH(texto);
//        pulsarBotonXpath(texto);
//
//    }
//
//    protected void pulsarBoton(String idBoton) {
//        WebElement boton = findElementById(idBoton);
//        boton.click();
//    }
//
//    protected void pulsarBotonXpath(String xpathBoton) {
//        WebElement boton = findElementByXpath(xpathBoton);
//        boton.click();
//    }
//
//    protected String fechaMasDias(int n) {
//        Calendar c = new GregorianCalendar();
//        c.add(Calendar.DAY_OF_YEAR, n);
//        c.add(Calendar.MONTH, 1);
//        String date = c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR);
//        return date;
//    }
//
//
//    protected String fechaActual() {
//        //Creación de objeto tipo Date
//        Date myDate = new Date();
//
//        //Selección de formato deseado
//        System.out.println(new SimpleDateFormat("dd/MM/yyyy").format(myDate));
//        String date = new SimpleDateFormat("dd/MM/yyyy").format(myDate);
//        return date;
//    }
//
//    public void init(WebDriver driver) {
//        this.driver = driver;
//    }
//
//    public WebElement rellenarCampo(String idCampo, String valor) {
//        WebElement campoNombre = findElementById(idCampo);
//        campoNombre.sendKeys(valor);
//        campoNombre.sendKeys(Keys.TAB);
//        return campoNombre;
//    }
//
//    public WebElement vaciarCampo(String idCampo) {
//        WebElement campoNombre = findElementById(idCampo);
//        campoNombre.clear();
//        return campoNombre;
//    }
//
//    public WebElement vaciarCampoXpath(String xpath) {
//        WebElement campoNombre = findElementByXpath(xpath);
//        campoNombre.clear();
//        return campoNombre;
//    }
//
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
//    protected void mostrarElementoPorXPATH(String xpathElemento) {
//        WebElement element = findElementByXpath(xpathElemento);
//        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: \"center\"});", element);
//        try {
//            Thread.sleep(4000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void pulsarEnter() {
//        try {
//            Robot r = new Robot();
//            Thread.sleep(2000);
//            r.keyPress(KeyEvent.VK_ENTER);
//            Thread.sleep(100);
//            r.keyRelease(KeyEvent.VK_ENTER);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void introducirLogin(String user, String passwd, String url) throws Exception {
//        Thread.sleep(1000);
//        //ServerUtils.RobotPulsarEnter(url);
//        //Thread.sleep(2000);
//        PassDecode objDec = new PassDecode(passwd);
//        String pass = objDec.getPassDecode();
//        ServerUtils.RobotEscribirLetraALetra(url, user);
//        TestListener.CapturaPantalla("Introducimos el user", Final.INFO);
//        ServerUtils.RobotPulsarTAB(url);
//        ServerUtils.RobotIntroducirTexto(url, pass);
//        TestListener.CapturaPantalla("Introducimos la pass", Final.INFO);
//        //Introducimos el user y pass desde la tabla excel. Recorremos todas las posiciones tanto de user como de pass para comrpbar si es mayuscula
//        // y en ese caso pulsamos SHIFT
//        ServerUtils.RobotPulsarEnter(url);
//
//    }
//
//    public void introducirLogin2(String user, String pass, String url, String nameCP) throws Exception {
//        Log.info("Escribimos el user");
//        Thread.sleep(1000);
//        ServerUtils.RobotEscribir(url, user);
//        Log.info("Escribimos la password");
//        Thread.sleep(1000);
//        ServerUtils.RobotPulsarTAB(url);
//        ServerUtils.RobotIntroducirTexto(url, pass);
//        TestListener.CapturaPantalla("Introducimos la pass para " + nameCP, Final.INFO);
//        Thread.sleep(1000);
//        //Introducimos el user y pass desde la tabla excel. Recorremos todas las posiciones tanto de user como de pass para comrpbar si es mayuscula
//        // y en ese caso pulsamos SHIFT
//        ServerUtils.RobotPulsarEnter(url);
//
//    }
//
//
//    public void introducirElementoRobot(String elemento) {
//        try {
//            esperarCarga();
//            Robot r = new Robot();
//            Log.info("Introducimos el valor o la palabra elegida");
//            Thread.sleep(4000);
//            for (int i = 0; i < elemento.length(); i++) {
//                char letra = elemento.charAt(i);
//                if (Character.isUpperCase(letra)) {
//                    r.keyPress(KeyEvent.VK_SHIFT);
//                    Thread.sleep(300);
//                }
//                r.keyPress(Character.toUpperCase(letra));
//                Thread.sleep(300);
//                r.keyRelease(Character.toUpperCase(letra));
//                if (Character.isUpperCase(letra)) {
//                    r.keyRelease(KeyEvent.VK_SHIFT);
//                    Thread.sleep(300);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public String getNifRandom() {
//        String[] wordDNI = {"T", "R", "W", "A", "G", "M", "Y", "F", "P", "D", "X", "B", "N", "J", "Z", "S", "Q", "V", "H", "L", "C", "K", "E"};
//        Random randomGenerator = new Random();
//        int max = 99999999;
//        int min = 80000000;
//        int numberNif = randomGenerator.nextInt(max - min) + min;
//        String nif = numberNif + wordDNI[numberNif % 23];
//        return nif;
//    }
//
//    public void cambiarFocoVentana() {
//        esperarCargaMedia();
//        Log.info("Cambiamos el foco a la nueva ventana del catálogo y maximizamos la ventana");
//        Browser.switchToNewWindow();
//        Browser.maximizeWindow();
//        esperarCarga();
//
//    }
//
//    public String formatearUrl(String url, String user, String passwd, String cp) throws Exception {
//        String PREFIJO_URL = "https://";
//        PassDecode objDec = new PassDecode(passwd);
//        String pass = objDec.getPassDecode();
//        String urlFinal = PREFIJO_URL + user + ':' + pass + '@' + url;
//        Log.info("La url del caso " + cp + "es: " + urlFinal);
//        return urlFinal;
//    }
//
//    public void aceptarErrorPrivacidad(String idioma) {
//        String nombrePestana = "";
//        switch (idioma) {
//            case "ESP":
//                nombrePestana = "Error de privacidad";
//                break;
//            case "ENG":
//                nombrePestana = "Privacy error";
//                break;
//            default:
//                Log.info("No se ha indicado ningún idioma correcto");
//                nombrePestana = "";
//                break;
//        }
//
//        try {
//            if (tabExists(nombrePestana)) {
//                waitAndSwitchTab(nombrePestana);
//                sleep(5000);
//                esperarExisteElemento(ID_BTN_DETALLES);
//                findElementById(ID_BTN_DETALLES).click();
//                esperarExisteElemento(ID_URL_ACCEDER);
//                findElementById(ID_URL_ACCEDER).click();
//                Log.info("Pasamos la página de error de privacidad");
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    public void aceptarCookies() throws Exception {
//        TestListener.CapturaPantalla("Aceptamos las cookies", Final.INFO);
//
//        if (esperaDinamicaElementoXPATH(XPATH_BTN_ACEPTAR, 30)) {
//            pulsarBotonXpath(XPATH_BTN_ACEPTAR);
//            Log.info("Se han acpetado las cookies");
//        } else Assert.fail("No se ha encontrado el boton aceptar cookies");
//    }
//
//    private static String XPATH_TITULO_PRIVACIDAD = "//h1[contains(text(), 'private')]";
//    private static String XPATH_BUTTON_ADVANCED = "//button[contains(text(), 'Advanced')]";
//    private static String XPATH_LINK_PRIVACIDAD = "//a[contains(text(), 'Continue to')]";
//
//    public void privacidad() throws InterruptedException {
//
//        if (existeElementoXpath(XPATH_TITULO_PRIVACIDAD)) {
//            pulsarBotonXpath(XPATH_TITULO_PRIVACIDAD);
//
//            esperarCarga();
//            if (existeElementoXpath(XPATH_BUTTON_ADVANCED)) {
//                pulsarBotonXpath(XPATH_BUTTON_ADVANCED);
//            } else Assert.fail("No aparece el boton Advanced");
//
//            esperarCarga();
//            if (existeElementoXpath(XPATH_LINK_PRIVACIDAD)) {
//                pulsarBotonXpath(XPATH_LINK_PRIVACIDAD);
//            } else Assert.fail("No se encuentra el link a la pagina");
//        } else Log.fail("No aparece la pagina de privacidad");
//
//    }
//
//    protected void esperarExisteElemento(String idElemento) {
//        int contador = 0;
//        while (!existeElemento(idElemento)) {
//            sleep(1000);
//            contador++;
//            if (contador == 20) Assert.fail("No se ha encontrado el elemento esperado");
//        }
//    }
//
//    public void waitAndSwitchTab(String titlePage) throws InterruptedException {
//        boolean flag = true;
//        int count = 0;
//        Set<String> windows;
//        while (flag) {
//            windows = Browser.getDriver().getWindowHandles();
//            String goodHandle = "";
//            for (String handle : windows) {
//                Browser.getDriver().switchTo().window(handle);
//                String title = Browser.getDriver().getTitle();
//                if (title.contains(titlePage)) {
//                    goodHandle = Browser.getDriver().getWindowHandle();
//                    flag = false;
//                }
//            }
//            if (count == 10) Assert.fail("Error al cambiar de ventana");
//            Browser.getDriver().switchTo().window(goodHandle);
//            count++;
//            Thread.sleep(500);
//        }
//    }
//
//    public boolean tabExists(String titlePage) throws Exception {
//        Set<String> windows = Browser.getDriver().getWindowHandles();
//        for (String handle : windows) {
//            Browser.getDriver().switchTo().window(handle);
//            String title = Browser.getDriver().getTitle();
//            if (title.contains(titlePage)) {
//                Log.info("Pestaña " + titlePage + " se encuentra abierta");
//                return true;
//            } else {
//                Log.info("Pestaña " + titlePage + " no se encuentra abierta");
//                return false;
//            }
//        }
//
//        Thread.sleep(500);
//        return false;
//    }
//
//    public static String idCasoPrueba(String nombre) {
//
//        String nombrecasoPrueba[];
//        nombrecasoPrueba = nombre.split("_");
//        idCasoPrueba = nombrecasoPrueba[0];
//        return nombrecasoPrueba[0];
//    }
//
//    public String gettakeScreenShot(String mensaje, String TestCaseName) throws IOException {
//        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy HH-mm-ss");
//        Date date = new Date();
//        String actualDate = format.format(date);
//
//        // Convierta el objeto del controlador web en Screenshot
//
//        TakesScreenshot ts = ((TakesScreenshot) driver);
//
//        File source = ts.getScreenshotAs(OutputType.FILE);
//
//        String destpath = System.getProperty("user.dir") + "/reports/Screenshots/" + TestCaseName + actualDate
//                + ".jpg";
//
//        Log.info(mensaje);
//        File file = new File(destpath);
//
//        FileUtils.copyFile(source, file);
//        return destpath;
//
//    }
//
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
//    public WebElement rellenarCampoXpath(String idCampo, String valor) {
//        WebElement campoNombre = findElementByXpath(idCampo);
//        mostrarElementoPorXpath(idCampo);
//        campoNombre.sendKeys(valor);
//        campoNombre.sendKeys(Keys.TAB);
//        return campoNombre;
//    }
//
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
//    public static String generarNif() {
//        // Array con las letras válidas del NIF
//        char[] letras = {'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D', 'X', 'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E'};
//        // Generar un número aleatorio de 8 dígitos
//        Random random = new Random();
//        int numero = random.nextInt(90000000) + 10000000;
//        // Genera un número entre 10000000 y 99999999
//        // Calcular el índice de la letra correspondiente
//        char letra = letras[numero % 23];
//        // Concatenar el número y la letra
//        return numero + String.valueOf(letra);
//
//    }
//
//    public static String generarNifCero() {
//        // Array con las letras válidas del NIF
//        char[] letras = {'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D', 'X', 'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E'};
//        // Generar un número aleatorio de 8 dígitos
//        Random random = new Random();
//        int numero = random.nextInt(9000000) + 1000000;
//        // Genera un número entre 10000000 y 99999999
//        // Calcular el índice de la letra correspondiente
//        char letra = letras[numero % 23];
//        // Concatenar el número y la letra
//        return "0" + numero + String.valueOf(letra);
//
//    }
//
//    protected void pulsarBotonXpathjs(String xpathBoton) {
//        WebElement element = driver.findElement(By.xpath(xpathBoton));
//        JavascriptExecutor js = (JavascriptExecutor) driver;
//        js.executeScript("arguments[0].scrollIntoView(true);", element);  // Desplazarse al elemento
//        js.executeScript("arguments[0].click();", element);  // Hacer clic en el elemento
//
//    }
//}
//

