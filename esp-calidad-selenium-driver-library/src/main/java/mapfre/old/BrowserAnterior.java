package mapfre.old;


import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.bonigarcia.wdm.WebDriverManager;
import mapfre.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
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


public class BrowserAnterior {

	// Property vars.
	private static Properties datosConfig = ProjectProperties.getConfigProperties();
//	private static Integer tiempoDelay = Integer.parseInt(datosConfig.getProperty("tiempoDelay"));
//	private static Integer timeImplicitlyWait = Integer.parseInt(datosConfig.getProperty("timeOut"));
//	public static Integer timeWebDriverWait = Integer.parseInt(datosConfig.getProperty("timeOut"));

	private static Integer tiempoDelay;
	private static Integer timeImplicitlyWait;
	public static Integer timeWebDriverWait;



	//public static String urlaws =datosConfig.getProperty("URL");
	public static String urlaws =ObtenerURLresponse();
	public static String urlGrid ;
	public static String urlawsLocal =datosConfig.getProperty("URL");
	// Path vars.
	private static String rootPath = ProjectPaths.getRootDirectory();


	// Driver ThreadLocal vars.
	protected static ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<>();

	public static WebDriverWait driverWait;

	private static String navSelect;
	private static String downloadFolder;
	private static Thread activityThread;
	private static Thread continuousActivityThread;
	private static volatile boolean stopActivity = false;

	// Private constructor is necessary (Sonar).
	private BrowserAnterior() {
		throw new IllegalStateException("Utility class");
	}

	// parametrizacion driver //////////////////////////////////////

	private static JsonObject driverConfig;

	public static void setChromeDriverConfig(JsonObject webdriverConfig) {
		driverConfig = webdriverConfig;
		navSelect = driverConfig.get("navSelect").getAsString();

//		if(driverConfig.get("Local").getAsString().contains("OK")){}

		urlGrid=driverConfig.get("urlGrid").getAsString();

		downloadFolder = driverConfig.get("downloadFolder").getAsString();;

		// Acceder al objeto "waits"
		JsonObject waitsObject = driverConfig.getAsJsonObject("waits");
		System.out.println("waitsObject: " + waitsObject);

		// Acceder a la propiedad "timeImplicitlyWait" dentro de "waits"
		tiempoDelay = waitsObject.get("timeDelay").getAsInt();
		timeImplicitlyWait = waitsObject.get("timeImplicitlyWait").getAsInt();
		timeWebDriverWait = waitsObject.get("timeWebDriverWait").getAsInt();
	}

	public static final WebDriver start(JsonObject webdriverConfig) throws Exception {

		setChromeDriverConfig(webdriverConfig);

		//Obteneer las url del hub
		Log.info("Obtenemos la url del HUB");

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
//			case Final.EDGE:
//				driver = startEdge();
//				break;
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

	// fin parametrizacion driver ////////////////////////////////////////////////////////

	/**
	 * Method that receives a Browser parameter to start among the possible
	 * browsers.
	 * 
	 * @param navegador
	 * @return
	 * @throws Exception
	 */
	public static final WebDriver start(String navegador) throws Exception {
		// Get the nav to select.
		navSelect = navegador;

		//Obteneer las url del hub
		Log.info("Obtenemos la url del HUB");

		// Select the method to initialize driver
		switch (navSelect) {
		case Final.CHROMEREMOTEDRIVER:
			driver = startChromeRemoteDriver();
			break;
		case Final.CHROME:
			driver = startChrome();
			break;
		case Final.CHROME_HEADLESS:
			driver = startChromeHeadless();
			break;
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
			//driver.manage().window().setSize(new Dimension(1920, 1080));
			getDriver().manage().window().setSize(new Dimension(1920, 1080));
		} else {
			getDriver().manage().window().maximize();
		}
		// PREPARE DOWNLOAD FOLDER.
		ProjectFileControl.initializeDownloadFolder();
		Log.info("Se inicializa la carpeta de descargas");
		//return driver;
		return getDriver();
	}

	/**
	 * Method that starts the driver with Chrome.
	 * 
	 * @throws Exception
	 */
	//private static WebDriver startChromeRemoteDriver() throws Exception {
	private static ThreadLocal<RemoteWebDriver> startChromeRemoteDriverOriginal() throws MalformedURLException, InterruptedException {
		// Clear open Chrome.
		Log.info("Entramos en la función startChromeRemoteDriver");
		DesiredCapabilities capabilities= new DesiredCapabilities();
		capabilities.setBrowserName("chrome");
		//Capabilities
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--disable-first-run");
		options.merge(capabilities);
		Log.info("Se han definido los argumentos");

		// Initialize driver.
		//Se filtra para ejecución en local
		if(datosConfig.getProperty("Local").equals("OK")){
			driver.set(new RemoteWebDriver(new URL(urlawsLocal), options,false));
		}else{
			driver.set(new RemoteWebDriver(new URL(urlaws), options,false));
		}
		Log.info("Se ha realizado el driver.set");
		System.out.println("EL hilo de ejecución dectectado es: "+ Thread.currentThread().toString() + " - y la sesión es: "+ getDriver().getSessionId().toString());
		//getDriver().switchTo().window(getDriver().getWindowHandle());
		//getDriver().manage().window().maximize();
		// Return driver.
		return driver;
	}

	private static ThreadLocal<RemoteWebDriver> startChromeRemoteDriver() throws MalformedURLException, InterruptedException {
		// Clear open Chrome.
		Log.info("Entramos en la función startChromeRemoteDriver");
		DesiredCapabilities capabilities= new DesiredCapabilities();
		capabilities.setBrowserName("chrome");
		//Capabilities
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--disable-first-run");
		options.merge(capabilities);
		Log.info("Se han definido los argumentos");
		// Initialize driver.
		driver.set(new RemoteWebDriver(new URL(urlGrid), options,false));
		Log.info("Se ha realizado el driver.set");
		System.out.println("EL hilo de ejecución dectectado es: "+ Thread.currentThread().toString() + " - y la sesión es: "+ getDriver().getSessionId().toString());
		return driver;
	}









	public static String IPNodo(String testname) {
		String nodeIP = "";
		int error = -1;
		String sessionid = getDriver().getSessionId().toString();
		System.out.println("La sesión del nodo donde se va a lanzar es: " + sessionid);
		//Construye la URL para consultar los detalles de la sesión
		System.out.println("La url que introduce para obtener datos es la siguiente: " + urlaws + "/status");
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpGet request = new HttpGet(urlaws + "/status");
			startContinuousActivity(getDriver(),testname);  // Pasar el WebDriver inicializado al hilo de actividad
			try (CloseableHttpResponse httpResponse = httpClient.execute(request)) {
				String jsonResponse = EntityUtils.toString(httpResponse.getEntity());
				BufferedReader reader = new BufferedReader(new StringReader(jsonResponse));
				String inputLine;
				boolean foundSessionId = false;

				while ((inputLine = reader.readLine()) != null) {
					// Verificar si la línea contiene el sessionid y "sessionId"
					if (inputLine.contains(sessionid) && inputLine.contains("sessionId")) {
						error = 0;
						foundSessionId = true;
						Log.info("PP7");
					}

					// Si se ha encontrado el sessionId, buscar la URI en las líneas siguientes
					if (foundSessionId && inputLine.contains("uri")) {
						String[] uriParts = inputLine.split("u002f");
						if (uriParts.length > 2) {
							String[] uri2 = uriParts[2].split(":");
							if (uri2.length > 1) {
								Log.info("URI1: " + uri2[0]);
								Log.info("URI2: " + uri2[1]);
								nodeIP = uri2[0];
								break; // Salir del bucle si ya se ha encontrado la URI
							}
						}
					}
				}
				if (error == -1) {
					Assert.fail("SessionId no encontrado.");
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nodeIP;
	}

	private static ThreadLocal<RemoteWebDriver> startEdge() throws Exception {

		EdgeOptions options = new EdgeOptions();
		//options.addArguments("--incognito");
		options.addArguments("start-maximized");
		options.addArguments("--disable-first-run");

		DesiredCapabilities capabilities = new DesiredCapabilities();
		//capabilities.setBrowserName(navegador);


		options.merge(capabilities);


		// Initialize driver.
		//driver = new RemoteWebDriver(new URL(urlaws),options);
		driver.set(new RemoteWebDriver(new URL(BrowserAnterior.ObtenerURLresponse()),options));
		return driver;

	}



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
	 * @throws Exception
	 * @return driver
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
		BrowserAnterior.waitExt(1);

		Log.info("Modo headless activado");

		// Return driver.
		return driver;
	}

	/**
	 * This method takes care of navigating to the provided URL.
	 * 
	 * @author CEX
	 * @param url
	 */
	public static void navigateDirect(String url) throws InterruptedException {
		//driver.navigate().to(url);

		getDriver().navigate().to(url);
		Log.info("Se introduce la url");
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


	/**
	 * This method takes care of waiting in real time.
	 * 
	 * @author CEX
	 * @throws Exception
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
	public static void stopDriver()  {
		// Stop driver.
		Log.info("Se accede a la función stopDriver");
		if (driver.get() != null) {
				if (BrowserAnterior.isBrowserOpen(BrowserAnterior.getDriver())) {
				Log.info("Se realiza el driver.remove");
				getDriver().quit();
				driver.remove();
			}
				else{
					Log.info("No se realiza el stopdriver ya que el navegador se ha cerrado por sí mismo");
				}
		}else{
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

	/**
	 * Return the text of an element
	 * 
	 * @param element {By} the element to click
	 * @return
	 * @throws Exception
	 * @author CEX
	 */
	public static String getText(By element) {
		driverWait.until(ExpectedConditions.visibilityOfElementLocated(element));
		//return driver.findElement(element).getText();
		return getDriver().findElement(element).getText();
	}

	public static RemoteWebDriver getDriver() {
		return driver.get();
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

	public static String ObtenerURLresponse() {
		Gson gson = new Gson();
		String filepath;
		Log.info("Entra en funcion");
		if(datosConfig.getProperty("Jenkins").equals("OK")){
			Log.info("Entra en jenkins ok en obtenerurl");
			 filepath = System.getenv("WORKSPACE")+"/response_create.json";
		}else{
			Log.info("Entra en jenkins no en obtenerurl");
			 filepath = System.getProperty("user.dir") + "/response_create.json";
		}

		Log.info("La ruta del create_json es: "+filepath);
		String elbDNS = null;

		try (FileReader reader = new FileReader(filepath)) {
			Log.info("Hemos entrador el try catch");
			Type type = new TypeToken<Map<String, Object>>() {
			}.getType();
			Map<String, Object> mapDatos = gson.fromJson(reader, type);
				Log.info("El mapdtaos de obtenerurl es: "+mapDatos);
			if (mapDatos.containsKey("body")) {
				Log.info("Hemos encontrado la key body");
				Map<String, Object> body = (Map<String, Object>) mapDatos.get("body");
				Log.info("El valor de body es: "+body);
				if (body.containsKey("elbDNS")) {
					Log.info("Hemos encontrado la key elbDNS");
					elbDNS = body.get("elbDNS").toString();
					Log.info("El valor de elbDNS es: "+elbDNS);
				} else {
					System.out.println("No se ha encontrado el campo elbDNS en el body del JSON.");
				}
			} else {
				System.out.println("No se ha encontrado el body en el JSON.");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return "http://"+elbDNS;
	}

	// Iniciar el hilo que envía actividad de manera continua mientras el test se ejecuta
	/*public static void startContinuousActivity(RemoteWebDriver driver) {
		activityThread = new Thread(() -> {
			try {
				while (!Thread.currentThread().isInterrupted() && !stopActivity) {
					if (driver != null|| driver.getSessionId() == null) {
						JavascriptExecutor js = (JavascriptExecutor) driver;  // Usamos el WebDriver pasado como parámetro
						js.executeScript("return document.title;");
						Log.infoHiloSec("La sesión del WebDriver es nula. Deteniendo el hilo de actividad continua.");
						break; // Salir del bucle y detener el hilo si no hay sesión activa
					} else {
						Log.infoHiloSec("WebDriver no está inicializado en el hilo de actividad.");
					}
					Thread.sleep(30000);  // Esperar 30 segundos entre actividades
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		});

		activityThread.start();
	}

	// Detener el hilo de actividad continua cuando el test termina
	public static void stopContinuousActivity() {
		stopActivity = true; // Detener la actividad continua
		if (activityThread != null && activityThread.isAlive()) {
			activityThread.interrupt();  // Interrumpir el hilo de actividad
		}
	}*/
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


