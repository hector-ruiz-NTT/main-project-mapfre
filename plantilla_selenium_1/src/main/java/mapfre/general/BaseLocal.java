package mapfre.general;

import mapfre.Browser;
import mapfre.base.actions.BaseActionsSelenium;
import mapfre.utils.functional_utils.ServerUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.testng.Assert.fail;

public class BaseLocal extends BaseActionsSelenium {

    public BaseLocal() {
        super(Browser.getDriver());
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static final Logger log = LogManager.getLogger();

    public static String FRAME_OPERACIONES = "ARQOS_MC";
    private static String ID_WAIT_LAYER = "waitMessageLayer2";
    private static String XPATH_POLIZA_PRIMAS = "//tbody//tr[@id='1']//td";
    private static String XPATH_IMPORTE_PRIMA_TOTAL = "//div[contains(text(),'Importe prima total')]";
    private static String XPATH_IMPORTE_TOTAL_DE_LA_PRIMA = "//tr[@id='1']//td[5]";
    private static final String XPATH_NOMBRE = "//div[@id = 'vida-ui-view']//span[text() = 'Beneficiario:']/following-sibling::span";
    private static final String XPATH_POLIZA = "//div[@id = 'vida-ui-view']//span[text() = 'P�liza:']/following-sibling::span";
    private static final String XPATH_IMPORTE = "//div[@id = 'vida-ui-view']//span[text() = 'Imp. Prestaci�n:']/following-sibling::span";
    public static String idCasoPrueba;
    private String nombreBeneficiario = null;
    private String DNIBeneficiario = null;
    private String poliza = null;
    private String importe = null;


    protected WebDriver driver;


    public void init(WebDriver driver) {
        this.driver = driver;

    }

    public WebDriver getDriver() {
        return driver;
    }

    public static void esperarCarga(int mil) throws InterruptedException {
        Thread.sleep(mil);
    }

    public static void esperarCarga() throws InterruptedException {
        Thread.sleep(1000);
    }




    public static boolean estadoCargandoActivado() {
        Browser.getDriver().switchTo().parentFrame();
        WebElement waitLayer = findElementById(ID_WAIT_LAYER);
        boolean salida = !waitLayer.getCssValue("display").equals("none");
        Browser.getDriver().switchTo().frame(FRAME_OPERACIONES);
        return salida;
    }
        public boolean cambiarPestanaSiguiente(){
        String[] handles = Browser.getDriver().getWindowHandles().toArray(new String[0]);
        if (handles.length > 1) {
            Browser.getDriver().switchTo().window(handles[1]);
            log.info("Se cambia a la pestana siguiente");
            return true;
        } else {
            log.info("No hay pestana");
            return false;
        }
    }


    public static void esperarCargaMedia() throws InterruptedException {
        Thread.sleep(3000);
    }


    public boolean waitCondition() {
        return !estadoCargandoActivado();
    }

    public static String idCasoPrueba(String nombre) {

        String nombrecasoPrueba[];
        nombrecasoPrueba = nombre.split("_");
        idCasoPrueba = nombrecasoPrueba[0];
        return nombrecasoPrueba[0];
    }

    public void introducirElementoRobot(String url, String elemento) {
        try {
            esperarCargaMedia();
            ServerUtils.RobotEscribir(url, elemento);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String ProjectNameExtractor() {

        // Obtener la fecha y hora actual
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String fechaHora = ahora.format(formato);  // Ejemplo: "2025-02-11_14-30-45"
        // Obtener el directorio de trabajo actual
        String projectPath = System.getProperty("user.dir");
        // Extraer el nombre del proyecto
        File projectFolder = new File(projectPath);
        String projectName = projectFolder.getName();
        System.out.println("Nombre del proyecto: " + projectName);
        return projectName + "_" + fechaHora;
    }

    public static void waitAndSwitchTab(String titlePage) throws InterruptedException {
        boolean flag = true;
        int count = 0;
        Set<String> windows;
        while (flag) {
            windows = Browser.getDriver().getWindowHandles();
            String goodHandle = "";
            for (String handle : windows) {
                Browser.getDriver().switchTo().window(handle);
                String title = Browser.getDriver().getTitle();
                if (title.contains(titlePage)) {
                    goodHandle = Browser.getDriver().getWindowHandle();
                    flag = false;
                }
            }
            if (count == 10) Assert.fail("Error al cambiar de ventana");
            Browser.getDriver().switchTo().window(goodHandle);
            count++;
            Thread.sleep(500);
        }
    }

    public static void CrearDirectorio(String ruta) {
        File directorio = new File(ruta);
        if (!directorio.exists()) {
            if (directorio.mkdirs()) {
                System.out.println("Directorio creado: " + directorio.getAbsolutePath());
            } else {
                System.out.println("No se pudo crear el directorio.");
            }
        } else {
            System.out.println("El directorio ya existe.");
        }
    }


}
