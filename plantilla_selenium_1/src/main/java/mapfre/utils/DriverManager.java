package mapfre.utils;

import mapfre.Browser;
import mapfre.FrameworkProperties;
import mapfre.Log;
import mapfre.ProjectProperties;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DriverManager {

    private static Thread continuousActivityThread;

    private static ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<>();

    public static RemoteWebDriver getDriver() {
        return driver.get();
    }

    private static void setDriver(RemoteWebDriver webDriver) {
        driver.set(webDriver);
    }


    public static void start() throws Exception {

        if(FrameworkProperties.getProperty("local").equalsIgnoreCase("SI")){
            initDriver();
        } else {
            Browser.start();
        }
    }

    public static void initDriver() {
        if (getDriver() == null) {
            String remoteUrl = System.getProperty("SELENIUM_REMOTE_URL");
            System.out.println("El remoto es :" + remoteUrl);
            RemoteWebDriver webDriver;
            ChromeOptions options = new ChromeOptions();

            // Configuración común de opciones para local y remoto
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--start-maximized");
            options.addArguments("--disable-infobars");                   // Quita la barra "Chrome está siendo controlado..."
            options.addArguments("--disable-popup-blocking");             // Evita bloqueos al abrir nuevas pestañas
            options.addArguments("--disable-notifications");              // Bloquea notificaciones del navegador
            options.addArguments("--disable-software-rasterizer");        // Mejora rendimiento en VMs
            
            // Detectar ambiente CI y activar headless 
            // NOTA: esta integración fue realizada por copilot para que pueda ser ejecutado en el entorno ci, no corrsponde al proyecto original. Pero es necesario su uso
            String isCI = System.getenv("CI");
            String chromeHeadless = System.getenv("CHROME_HEADLESS");
            if ("true".equalsIgnoreCase(isCI) || "true".equalsIgnoreCase(chromeHeadless)) {
                System.out.println("INFO: Detectado ambiente CI - Activando Chrome en modo headless");
                options.addArguments("--headless=new");
                options.addArguments("--disable-gpu");
                options.addArguments("--window-size=1920,1080");
            }

            if (remoteUrl != null && !remoteUrl.isEmpty()) {
                // --- MODO REMOTO (SELENIUM GRID) ---
                try {
                    URL gridUrl = new URL("http://"+remoteUrl);
                     webDriver = new RemoteWebDriver(gridUrl, options);
                    System.out.println("INFO: Conectando a Selenium Grid en: " + remoteUrl);
                } catch (Exception e) {
                    System.err.println("ERROR: Fallo al conectar con el WebDriver remoto en " + remoteUrl + ". " +
                            "Asegúrese de que el Grid esté activo y la URL sea correcta.");
                    e.printStackTrace();
                    throw new RuntimeException("No se pudo iniciar el WebDriver remoto.", e);
                }
            } else {
                // --- MODO LOCAL (FALLBACK) ---
                // Primero intentar usar ChromeDriver del PATH (instalado en CI o localmente)
                String driverPathFromEnv = System.getenv("webdriver.chrome.driver");
                String driverPathFromProperty = System.getProperty("webdriver.chrome.driver");
                
                if (driverPathFromEnv != null && !driverPathFromEnv.isEmpty()) {
                    System.setProperty("webdriver.chrome.driver", driverPathFromEnv);
                    System.out.println("INFO: Usando ChromeDriver de variable de entorno: " + driverPathFromEnv);
                } else if (driverPathFromProperty != null && !driverPathFromProperty.isEmpty()) {
                    System.out.println("INFO: Usando ChromeDriver de system property: " + driverPathFromProperty);
                } else {
                    // Intentar encontrar chromedriver en el PATH
                    try {
                        Process process = Runtime.getRuntime().exec(new String[]{"which", "chromedriver"});
                        java.io.BufferedReader reader = new java.io.BufferedReader(
                            new java.io.InputStreamReader(process.getInputStream()));
                        String chromedriverPath = reader.readLine();
                        if (chromedriverPath != null && !chromedriverPath.isEmpty()) {
                            System.setProperty("webdriver.chrome.driver", chromedriverPath);
                            System.out.println("INFO: ChromeDriver encontrado en PATH: " + chromedriverPath);
                        }
                    } catch (Exception e) {
                        System.out.println("INFO: No se pudo ejecutar 'which chromedriver', intentando descarga automática...");
                    }
                    
                    // Si aún no está configurado, intentar descarga automática (método anterior)
                    if (System.getProperty("webdriver.chrome.driver") == null) {
                        String chromeVersion = getInstalledChromeVersion();
                        String driverPath = downloadChromeDriver(chromeVersion);

                        if (driverPath != null) {
                            System.setProperty("webdriver.chrome.driver", driverPath);
                        } else {
                            System.err.println("WARN: No se pudo descargar el ChromeDriver compatible. Se intentará usar el driver por defecto.");
                        }
                    }
                }

                webDriver = new ChromeDriver(options);
                System.out.println("INFO: ChromeDriver iniciado correctamente en modo local.");
            }

            // Configuración de timeouts y cookies (común a ambos modos)
            webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(120));
            webDriver.manage().deleteAllCookies();
            setDriver(webDriver);

        }
    }



    public static void quitDriver() {
        if (getDriver() != null) {
            getDriver().quit();
            driver.remove();
        }
    }


    /**
     * Detecta la versión de Chrome instalada en macOS.
     *
     * @return versión principal de Chrome (por ejemplo, "141") o null si no se detecta.
     */
    private static String getInstalledChromeVersion() {
        try {
            // Nota: Este comando es específico para macOS. Necesitaría ajuste para Windows/Linux.
            String[] cmd = {"/Applications/Google Chrome.app/Contents/MacOS/Google Chrome", "--version"};
            Process process = Runtime.getRuntime().exec(cmd);
            java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream())
            );
            String line = reader.readLine();
            process.waitFor();
            if (line != null && line.contains("Google Chrome")) {
                // Ejemplo: "Google Chrome 141.0.7390.108"
                String[] parts = line.split(" ");
                if (parts.length >= 3) {
                    String version = parts[2];
                    // Solo la versión principal ("141")
                    return version.split("\\.")[0];
                }
            }
        } catch (Exception e) {
            // Si no se puede detectar, retorna null
            System.err.println("WARN: No se pudo detectar la versión de Chrome instalada. Intentando el último build compatible.");
        }
        return null;
    }

    /**
     * Descarga el ChromeDriver compatible con la versión de Chrome instalada.
     *
     * @param chromeVersion versión principal de Chrome (por ejemplo, "141")
     * @return ruta al ejecutable de ChromeDriver descargado
     */
    private static String downloadChromeDriver(String chromeVersion) {
        if (chromeVersion == null || chromeVersion.isEmpty()) return null;
        String arch = System.getProperty("os.arch");
        if(arch.equalsIgnoreCase("aarch64")) arch="arm64";
        String baseUrl = "https://storage.googleapis.com/chrome-for-testing-public/";
        String driverDir = "src/test/resources/";
        String driverPath = driverDir + "chromedriver";
        java.io.File driverFile = new java.io.File(driverPath);
        if (driverFile.exists()) return driverFile.getAbsolutePath();
        try {
            String latestBuild = getLatestChromeBuild(chromeVersion);
            if (latestBuild == null) return null;
            String zipUrl = baseUrl + latestBuild + "/" + arch + "/chromedriver-" + arch + ".zip";
            Files.createDirectories(Paths.get(driverDir));
            Path zipPath = Paths.get(driverDir, "chromedriver.zip");
            // Descarga el zip usando GET
            URL url = new java.net.URL(zipUrl);
            HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.connect();
            try (java.io.InputStream in = conn.getInputStream()) {
                Files.copy(in, zipPath, StandardCopyOption.REPLACE_EXISTING);
            }
            // Extrae solo el binario chromedriver del zip
            try (ZipInputStream zis = new java.util.zip.ZipInputStream(new FileInputStream(zipPath.toFile()))) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    // El binario suele estar en una carpeta, por ejemplo: "chromedriver-mac-arm64/chromedriver"
                    if (entry.getName().endsWith("/chromedriver") || entry.getName().equals("chromedriver")) {
                        Path outPath = Paths.get(driverPath);
                        Files.copy(zis, outPath, StandardCopyOption.REPLACE_EXISTING);
                        outPath.toFile().setExecutable(true);
                        break;
                    }
                }
            }
            zipPath.toFile().delete(); // Borra el zip
            return driverFile.getAbsolutePath();
        } catch (Exception e) {
            System.err.println("ERROR: Fallo durante la descarga del ChromeDriver: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene el último build disponible para la versión principal de Chrome.
     * NOTA: Esta implementación es un placeholder simple y debería usar la API oficial de Chrome for Testing.
     * @param chromeVersion versión principal (por ejemplo, "141")
     * @return versión completa (por ejemplo, "141.0.7390.78") o null si no se encuentra
     */
    private static String getLatestChromeBuild(String chromeVersion) {
        // En una implementación real, se consultaría a la API oficial.
        // Aquí mantenemos el valor de tu código original como un ejemplo.
        return chromeVersion + ".0.7390.78";
    }


    public static String createURL(String url,String user, String pass) {
        Log.info("Entramos en createURL");
        url= url.substring(8);
		/*PassDecode passDecode = new PassDecode(pass);
		pass = passDecode.getPassDecode();
		pass = new String(pass);*/
        String urlFinal = "https://"+user+":"+pass+"@"+url;
        return urlFinal;
    }


    public static boolean isWeekend() {
        LocalDate today = LocalDate.now();  // Obtener la fecha actual
        DayOfWeek dayOfWeek = today.getDayOfWeek();  // Obtener el día de la semana
        // Verificar si es sábado o domingo
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    /**
     * Metodo getUrlApp
     *
     * @author CEX
     * @return
     * @throws Exception
     */
    public static String getUrlApp() throws Exception {
        String url = "";

        Properties datosConfig = ProjectProperties.getConfigProperties();
        Properties datosLogin = ProjectProperties.getLoginProperties();
        String entorno = datosConfig.getProperty("entorno");
        //url = datosLogin.getProperty("URL_MF_" + entorno);
        //Comprobamos si estamos en fin de semana para apuntar a una url u otra
        if(isWeekend()==true){
            url = datosLogin.getProperty("URL_MF_MENSUAL_" + entorno);
        }
        else{
            url = datosLogin.getProperty("URL_MF_" + entorno);
        }

        Log.info("La url es:" +url);
        return url;
    }

// Inicia un hilo secundario para enviar actividad continua al nodo de Selenium Grid
    public static void startContinuousActivity(RemoteWebDriver driver, String testName) {
        // Crear el hilo que ejecutará la actividad continua
        continuousActivityThread = new Thread(() -> {
            try {
                while (true) {
                    // Verifica si el driver o la sesión es nula. Si es así, termina el hilo.
                    if (driver == null || driver.getSessionId() == null) {
                        Log.infoHiloSec("La sesión del WebDriver es nula. Este hilo se cierra.");
                        break;  // Sale del bucle infinito y termina el hilo
                    }

                    try {
                        // Envía un script simple al navegador para mantener activa la sesión en el nodo
                        driver.executeScript("return document.title;");
                        Log.infoHiloSec("Actividad continua enviada al nodo...");
                    } catch (NoSuchSessionException e) {
                        // Si la sesión del WebDriver ya no existe, termina el hilo
                        Log.infoHiloSec("No se pudo enviar actividad. La sesión del WebDriver ha finalizado.");
                        break;  // Termina el bucle
                    }

                    // Espera 30 segundos antes de enviar la próxima actividad
                    Thread.sleep(30000);
                }
            } catch (InterruptedException e) {
                // Si el hilo es interrumpido (por ejemplo, al final del test), se maneja la interrupción aquí
                Log.infoHiloSec("Hilo de actividad continua interrumpido.");
            }
        });

        // Asignar un nombre descriptivo al hilo para facilitar su identificación
        continuousActivityThread.setName("HiloActividad-" + testName);

        // Iniciar el hilo secundario
        continuousActivityThread.start();
    }

    // Detiene el hilo secundario de actividad continua
    public static void stopContinuousActivity() {
        // Verifica si el hilo está activo y en ejecución
        if (continuousActivityThread != null && continuousActivityThread.isAlive()) {
            // Interrumpe el hilo, lo que causará que se lance la excepción InterruptedException en el hilo
            continuousActivityThread.interrupt();
            Log.infoHiloSec("Hilo de actividad continua detenido.");
        } else {
            // Si el hilo ya no está activo o no existe, informa que ya está detenido
            Log.infoHiloSec("Hilo de actividad continua ya está detenido.");
        }
    }


}
