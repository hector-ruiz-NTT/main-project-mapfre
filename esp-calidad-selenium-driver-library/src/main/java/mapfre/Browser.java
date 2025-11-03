package mapfre;


import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static mapfre.FrameworkProperties.setProperty;


public class Browser {

    // Property vars.

    private static Integer tiempoDelay;
    private static Integer timeImplicitlyWait;
    public static Integer timeWebDriverWait;
    public static String urlGrid;

    ///////////////////////////////////////////
    private static Properties datosConfig = ProjectProperties.getConfigProperties();

    //public static String urlaws =datosConfig.getProperty("URL");
    public static String urlaws = ObtenerURLresponse();

    public static String urlawsLocal = datosConfig.getProperty("URL");
    // Path vars.
    private static String rootPath = ProjectPaths.getRootDirectory();


    // Driver ThreadLocal vars.
    protected static ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<>();

    public static WebDriverWait driverWait;

    private static String navSelect;
    private static String downloadFolder;

    private static volatile boolean stopActivity = false;

    // Private constructor is necessary (Sonar).
    private Browser() {
        throw new IllegalStateException("Utility class");
    }

    // Parametrizacion driver //////////////////////////////////////

    private static JsonObject driverConfig;

    public static void setDriverConfig(JsonObject webdriverConfig) {
        driverConfig = webdriverConfig;
        navSelect = driverConfig.get("navSelect").getAsString();

//		if(driverConfig.get("Local").getAsString().contains("OK")){}

        urlGrid = driverConfig.get("urlGrid").getAsString();

//        downloadFolder = driverConfig.get("downloadFolder").getAsString();

        // Acceder al objeto "waits"
        JsonObject waitsObject = driverConfig.getAsJsonObject("waits");
        System.out.println("waitsObject: " + waitsObject);

        // Acceder a la propiedad "timeImplicitlyWait" dentro de "waits"
        tiempoDelay = waitsObject.get("timeDelay").getAsInt();
        timeImplicitlyWait = waitsObject.get("timeImplicitlyWait").getAsInt();
        timeWebDriverWait = waitsObject.get("timeWebDriverWait").getAsInt();
    }
    public static void setDriverConfigProperties() {

        navSelect = FrameworkProperties.getProperty("navSelect");

        urlGrid = urlaws;

//        downloadFolder = FrameworkProperties.getProperty("downloadFolder");

//        tiempoDelay = Integer.valueOf(FrameworkProperties.getProperty("waits.timeDelay"));

//        timeImplicitlyWait = Integer.valueOf(FrameworkProperties.getProperty("waits.timeImplicitlyWait"));

        if (FrameworkProperties.getProperty("waits.timeImplicitlyWait") != null) {
            timeImplicitlyWait = Integer.valueOf(FrameworkProperties.getProperty("waits.timeImplicitlyWait"));
        } else {
            timeImplicitlyWait = 60; // Valor por defecto
        }

//        timeWebDriverWait = Integer.valueOf(FrameworkProperties.getProperty("waits.timeWebDriverWait"));


        if (FrameworkProperties.getProperty("waits.timeWebDriverWait") != null ) {
            timeWebDriverWait = Integer.valueOf(FrameworkProperties.getProperty("waits.timeWebDriverWait"));
        } else {

            timeWebDriverWait = 60; // Valor por defecto
        }

    }


    public static final WebDriver start() throws Exception {

        setDriverConfigProperties();
        Thread.sleep(2500);
        //NMJORG3 - Añadimos la condicion para comprobar que si local es OK, deje la URL como localhost
        try{
            if("OK".equals(FrameworkProperties.getProperty("local"))){
                urlGrid = urlawsLocal;
            }
        }catch (NullPointerException e){
            Log.info("No se ha podido leer la propiedad que indica ejecucion local, se deja la URL del response_create");
        }

        setProperty("urlGrid", urlGrid);

        //Obteneer las url del hub
        Log.info("Obtenemos la url del HUB");

        //Comprobar que se ha cargado la variable navSelect y si no es así cargarla del properties
        if (navSelect == null){
           navSelect = FrameworkProperties.getProperty("navSelect");
        }
        Log.info("El navegador seleccionado es: " + navSelect);
        // Select the method to initialize driver
        switch (navSelect) {
            case Final.CHROMEREMOTEDRIVER:
                driver = startChromeRemoteDriver();
                break;
//			case Final.CHROME:
//				driver = startChrome();
//				break;
//			case Final.CHROME_HEADLESS:
//				driver = startChromeHeadless();
//				break;
            case Final.EDGE:
                driver = startEdge();
                break;

            default:
                Log.info("No se ha indicado un navegador valido");
                break;
        }

        Log.info("Sale de la selección de navegadores");
        // Initialize WebDdriverWait. Se cambia para la versión 4.18.1
        driverWait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeWebDriverWait));
        getDriver().manage().timeouts().pageLoadTimeout(timeImplicitlyWait, TimeUnit.SECONDS);
        getDriver().manage().deleteAllCookies();
        getDriver().manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        Log.info("Inicializa el driverwait");

        // If we are working in headless we need to set windows size.
        if (navSelect.equalsIgnoreCase(Final.CHROME_HEADLESS)) {
            getDriver().manage().window().setSize(new Dimension(1920, 1080));
        } else {
            getDriver().manage().window().maximize();
        }

        //return driver;
        return getDriver();
    }

    /**
     * Method that starts the driver with Chrome.
     *
     * @throws Exception
     */
    private static ThreadLocal<RemoteWebDriver> startChromeRemoteDriver() throws MalformedURLException {
//        int maxRetries = 3; // Número máximo de reintentos
//        int retryCount = 0;
//        while (retryCount < maxRetries) {
//            try {
                //Log.info("Intento de inicializar el driver: " + (retryCount + 1));
                DesiredCapabilities capabilities = new DesiredCapabilities();
                capabilities.setBrowserName("chrome");

                ChromeOptions options = new ChromeOptions();
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-first-run");
                options.addArguments("--lang=es");
                options.addArguments("--disable-search-engine-choice-screen");
                options.merge(capabilities);

                Map<String, Object> prefs = new HashMap<>();
                prefs.put("profile.default_content_setting_values.clipboard", 1); // 1 para permitir
                prefs.put("plugins.always_open_pdf_externally", false); // false para abrir PDFs en Chrome
                prefs.put("plugins.plugins_enabled", new String[]{"Chrome PDF Viewer"});
                options.setExperimentalOption("prefs", prefs);
                URL grid = new URL(urlGrid);
                RemoteWebDriver url = new RemoteWebDriver(grid, options, false);
                driver.set(url);
//                Log.info("Driver inicializado correctamente en el intento: " + (retryCount + 1));
                getDriver().manage().window().maximize();
                
//            } catch (Exception e) {
//                Log.fail("Error al inicializar el driver en el intento: " + (retryCount + 1));
//                retryCount++;
//                if (retryCount >= maxRetries) {
//                    Log.fail("Se alcanzó el número máximo de reintentos para inicializar el driver.");
//                    throw e; // Propagar la excepción después de agotar los reintentos
//                }
//                Thread.sleep(2000); // Esperar antes de reintentar
//            }
//        }
        return driver;
    }


    // PARAMETRIZACION EDGE COMPATIBILIDAD IEXPLORER

//    private static ThreadLocal<RemoteWebDriver> startEdgeCompatibilidad() throws MalformedURLException, InterruptedException {
//        // Acceder al objeto "iexplorerCapabilities"
//
////        JsonObject iexplorerCapabilitiesObject = driverConfig.getAsJsonObject("iexplorerCapabilities");
////        System.out.println("iexplorerCapabilitiesObject: " + iexplorerCapabilitiesObject);
//
//        InternetExplorerOptions ieOptions = new InternetExplorerOptions();
//        ieOptions.setCapability("se:ieOptions", new HashMap<String, Object>() {{
//            put("ie.edgechromium", true);
////            put("ignoreProtectedModeSettings", iexplorerCapabilitiesObject.get("ignoreProtectedModeSettings").getAsBoolean());
//            put("ignoreProtectedModeSettings", Boolean.valueOf(FrameworkProperties.getProperty("iexplorer.ignoreProtectedModeSettings")));
//
////            put("requireWindowFocus", iexplorerCapabilitiesObject.get("requireWindowFocus").getAsBoolean());
//            put("requireWindowFocus", Boolean.valueOf(FrameworkProperties.getProperty("iexplorer.requireWindowFocus")));
//
////            put("browserAttachTimeout", iexplorerCapabilitiesObject.get("browserAttachTimeout").getAsInt());
//            put("browserAttachTimeout", Integer.parseInt(FrameworkProperties.getProperty("iexplorer.browserAttachTimeout")));
//
//            put("unexpectedAlertBehaviour", "accept");
//            put("elementScrollBehavior", 0);
////            put("ie.ensureCleanSession", iexplorerCapabilitiesObject.get("ensureCleanSession").getAsBoolean());
//            put("ie.ensureCleanSession", Boolean.valueOf(FrameworkProperties.getProperty("iexplorer.ensureCleanSession")));
//
//        }});
////        ieOptions.setCapability("ignoreProtectedModeSettings", iexplorerCapabilitiesObject.get("ignoreProtectedModeSettings").getAsBoolean());
//        ieOptions.setCapability("ignoreProtectedModeSettings", Boolean.valueOf(FrameworkProperties.getProperty("iexplorer.ignoreProtectedModeSettings")));
//
//
////        ieOptions.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, iexplorerCapabilitiesObject.get("requireWindowFocus").getAsBoolean());
//        ieOptions.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, Boolean.valueOf(FrameworkProperties.getProperty("iexplorer.requireWindowFocus")));
//
//        ieOptions.setCapability("ms:edgeOptions", new HashMap<String, Object>() {{
//            put("ieMode", true);
//        }});
////        ieOptions.setCapability("requireWindowFocus", iexplorerCapabilitiesObject.get("requireWindowFocus").getAsBoolean());
//        ieOptions.setCapability("requireWindowFocus", Boolean.valueOf(FrameworkProperties.getProperty("iexplorer.requireWindowFocus")));
//
//
//        ieOptions.setCapability("timeouts", new HashMap<String, Object>() {{
////            put("implicit", iexplorerCapabilitiesObject.get("implicitTimeout").getAsInt());
//            put("implicit", Integer.parseInt(FrameworkProperties.getProperty("iexplorer.implicitTimeout")));
//
////            put("pageLoad", iexplorerCapabilitiesObject.get("pageLoadTimeout").getAsInt());
//            put("pageLoad", Integer.parseInt(FrameworkProperties.getProperty("iexplorer.pageLoadTimeout")));
//
////            put("script", iexplorerCapabilitiesObject.get("scriptTimeout").getAsInt());
//            put("script", Integer.parseInt(FrameworkProperties.getProperty("iexplorer.scriptTimeout")));
//
//        }});
//        Log.info("Realiza las capabilities");
//        Log.info("La url de conexión es: " + urlGrid);
//        driver.set(new RemoteWebDriver(new URL(urlGrid), ieOptions));
//        Log.info("Realiza el driver.set");
//        return driver;
//    }


    /**
     * Method that starts the driver with Edge.
     *
     * @throws Exception
     */
    private static ThreadLocal<RemoteWebDriver> startEdge() throws Exception {
//        int maxRetries = 3; // Número máximo de reintentos
//        int retryCount = 0;
//        while (retryCount < maxRetries) {
//            try {
//                Log.info("Intento de inicializar el driver Edge: " + (retryCount + 1));
                EdgeOptions options = new EdgeOptions();
                HashMap<String, Object> edgePrefs = new HashMap<>();
                edgePrefs.put("profile.default_content_setting_values.clipboard", 1);
                edgePrefs.put("plugins.always_open_pdf_externally", false);
                options.setExperimentalOption("prefs", edgePrefs);
                options.addArguments("start-maximized");
                options.addArguments("--lang=es");
                options.addArguments("--disable-first-run");
                options.addArguments("disable-popup-blocking"); // Asegura que no bloquee ventanas emergentes
//                options.setExperimentalOption("prefs", new HashMap<String, Object>() {{
//                    put("plugins.always_open_pdf_externally", true); // Configura para abrir PDFs en una pestaña
//                }});

                DesiredCapabilities capabilities = new DesiredCapabilities();
                capabilities.setBrowserName("edge");
                options.merge(capabilities);

                driver.set(new RemoteWebDriver(new URL(urlGrid), options, false));
//                Log.info("Driver Edge inicializado correctamente en el intento: " + (retryCount + 1));
                getDriver().manage().window().maximize();
//            } catch (Exception e) {
//                Log.fail("Error al inicializar el driver Edge en el intento: " + (retryCount + 1));
//                retryCount++;
//                if (retryCount >= maxRetries) {
//                    Log.fail("Se alcanzó el número máximo de reintentos para inicializar el driver Edge.");
//                    throw e; // Propagar la excepción después de agotar los reintentos
//                }
//                Thread.sleep(2000); // Esperar antes de reintentar
//            }
//        }
        return driver;
    }

//    private static ThreadLocal<RemoteWebDriver> startEdge() throws Exception {
//
//        EdgeOptions options = new EdgeOptions();
//        HashMap<String, Object> edgePrefs = new HashMap<String, Object>();
//        edgePrefs.put("profile.default_content_setting_values.clipboard", 1);
//        edgePrefs.put("plugins.always_open_pdf_externally", false);
//        options.setExperimentalOption("prefs", edgePrefs);
//        //options.addArguments("--incognito");
//        options.addArguments("start-maximized");
//        options.addArguments("--lang=es");
//        options.addArguments("--disable-first-run");
//        DesiredCapabilities capabilities = new DesiredCapabilities();
//        capabilities.setBrowserName("edge");
//
//        options.addArguments("disable-popup-blocking"); // Asegura que no bloquee ventanas emergentes
//        options.setExperimentalOption("prefs", new java.util.HashMap<String, Object>() {{
//            put("plugins.always_open_pdf_externally", true); // Configura para abrir PDFs en una pestaña
//        }});
//
//        options.merge(capabilities);
//        // Initialize driver.
//        driver.set(new RemoteWebDriver(new URL(urlGrid), options, false));
//        return driver;
//    }


    //private static WebDriver startChrome() throws Exception {
    private static ThreadLocal<RemoteWebDriver> startChrome() {
        // Clear open Chrome.

        // Property.
        System.setProperty("webdriver.chrome.driver", rootPath + "\\lib\\chromedriver.exe");
        System.setProperty("webdriver.chrome.silentOutput", "true");
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);

        /** Chrome Options */
        ChromeOptions option = new ChromeOptions();
        HashMap<String, Object> datosPref = new HashMap<>();

        // Default directory for downloads.
        datosPref.put("download.default_directory", ProjectPaths.getDownloadPath());
        datosPref.put("profile.default_content_settings.popups", 0);
        datosPref.put("safebrowsing.enabled", "true");
        datosPref.put("plugins.always_open_pdf_externally", true);
        option.setExperimentalOption("prefs", datosPref);
        option.addArguments("--allow-running-insecure-content");
        option.addArguments("--ignore-certificate-errors");
        option.addArguments("--safebrowsing-disable-download-protection");
        option.setAcceptInsecureCerts(true);
        option.addArguments("--disable-logging");

        // Initialize driver.
        //driver = new ChromeDriver(option);
        driver.set(new ChromeDriver(option));
        // Return driver.
        return driver;
    }


    /**
     * Method that starts the driver with Chrome - headless
     *
     * @return driver
     * @throws Exception
     */
    //private static WebDriver startChromeHeadless() throws Exception {
    private static ThreadLocal<RemoteWebDriver> startChromeHeadless() throws Exception {
        // Clear open Chrome.
        Runtime.getRuntime().exec("Taskkill /F /IM chromedriver.exe");
        Runtime.getRuntime().exec("Taskkill /F /IM chrome.exe");

        // Property.
        System.setProperty("webdriver.chrome.driver", rootPath + "\\lib\\chromedriver.exe");
        System.setProperty("webdriver.chrome.silentOutput", "true");
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName("chrome");
        WebDriverManager.chromedriver().setup();

        // Chrome Options.
        ChromeOptions option = new ChromeOptions();
        HashMap<String, Object> datos = new HashMap<>();
        // Default directory for downloads.
        datos.put("download.default_directory", ProjectPaths.getDownloadPath());

        // Set options.
        option.setExperimentalOption("prefs", datos);

        // Options to be HEADLESS.
        List<String> argumentos = new ArrayList<>();
        argumentos.add("--no-sandbox");
        argumentos.add("--headless=false");
        argumentos.add("--disable-dev-shm-usage");
        argumentos.add("--ignore-certificate-errors");

        // Add arguments.
        option.addArguments(argumentos);

        // Initialize driver.
        //driver = new ChromeDriver(option);
        driver.set(new ChromeDriver(option));
        Browser.waitExt(1);

        Log.info("Modo headless activado");

        // Return driver.
        return driver;
    }

    /**
     * This method takes care of navigating to the provided URL.
     *
     * @param url
     * @author CEX
     */
    public static void navigateDirect(String url) throws InterruptedException {
        //driver.navigate().to(url);

        getDriver().navigate().to(url);
        Log.info("Se introduce la url");
    }


    /**
     * This method takes care of waiting in real time.
     *
     * @throws Exception
     * @author CEX
     */
    public static void waitExt(Integer tiempo) throws Exception {
        Integer calculo = tiempo * tiempoDelay;
        try {
            Thread.sleep(calculo);
        } catch (InterruptedException e) {
            Log.fail("Se ha producido un error en la espera");
            //Log.info("Se ha producido un error en la espera");
            Log.info(e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Method to logout in the application.
     *
     * @throws IOException
     */
    public static void stopDriver() {
        // Stop driver.
        Log.info("Se accede a la función stopDriver");
        if (driver.get() != null) {
            if (Browser.isBrowserOpen(Browser.getDriver())) {
                Log.info("Se realiza el driver.remove");
                getDriver().quit();
                driver.remove();
            } else {
                Log.info("No se realiza el stopdriver ya que el navegador se ha cerrado por sí mismo");
            }
        } else {
            Log.info("El driver no estaba inicializado");
        }
    }

    public static boolean isBrowserOpen(WebDriver driver) {
        try {
            driver.getTitle();  // Intentar realizar una operación simple
            return true;
        } catch (WebDriverException e) {
            return false;
        }
    }

    public static RemoteWebDriver getDriver() {
        return driver.get();
    }


    public static String ObtenerURLresponse() {
        Gson gson = new Gson();
        String filepath;
        Log.info("Entra en funcion");
        if (datosConfig.getProperty("Jenkins").equals("OK")) {
            Log.info("Entra en jenkins ok en obtenerurl");
            filepath = System.getenv("WORKSPACE") + "/response_create.json";
        } else {
            Log.info("Entra en jenkins no en obtenerurl");
            filepath = System.getProperty("user.dir") + "/response_create.json";
        }

        Log.info("La ruta del create_json es: " + filepath);
        String elbDNS = null;

        try (FileReader reader = new FileReader(filepath)) {
            Log.info("Hemos entrador el try catch");
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> mapDatos = gson.fromJson(reader, type);
            Log.info("El mapdtaos de obtenerurl es: " + mapDatos);
            if (mapDatos.containsKey("body")) {
                Log.info("Hemos encontrado la key body");
                Map<String, Object> body = (Map<String, Object>) mapDatos.get("body");
                Log.info("El valor de body es: " + body);
                if (body.containsKey("elbDNS")) {
                    Log.info("Hemos encontrado la key elbDNS");
                    elbDNS = body.get("elbDNS").toString();
                    Log.info("El valor de elbDNS es: " + elbDNS);
                } else {
                    System.out.println("No se ha encontrado el campo elbDNS en el body del JSON.");
                }
            } else {
                System.out.println("No se ha encontrado el body en el JSON.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "http://" + elbDNS;
    }

}

