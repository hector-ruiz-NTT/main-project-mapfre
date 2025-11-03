package mapfre.utils.functional_utils;


import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;

import mapfre.Browser;
import mapfre.general.ProjectProperties;
import mapfre.general.Log;
import mapfre.general.BaseLocal;
import mapfre.utils.ExtentManager.ExtentFactory;
import mapfre.utils.ExtentManager.ExtentManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.Properties;

public class ScreenShots {
    private static Properties datosConfig = ProjectProperties.getConfigProperties();
    //public static ExtentTest test;

    private static ThreadLocal<Integer> screenshotCounter = ThreadLocal.withInitial(() -> 0);
    //private static String ruta = "/mnt/efs/EvidenciasAuto/FormularioBoiUnbounce/Report/Screenshots/"+fechaActual()+"/";
    //private static String ruta =  "/mnt/efs/Evidencias/"+System.getenv("JOB_NAME")+"/"+System.getenv("BUILD_NUMBER")+"/"+datosConfig.getProperty("screenshots");
    private static String rutaLocal = System.getProperty("user.home")+"\\"+datosConfig.getProperty("screenshotsLocal")+ BaseLocal.ProjectNameExtractor()+"\\";
    private static String rutaRLocal = System.getProperty("user.home")+"\\"+datosConfig.getProperty("screenshotsLocal")+BaseLocal.ProjectNameExtractor()+"\\";

    private static String ruta =  "/home/jenkins/workspace/"+System.getenv("JOB_NAME")+"/"+System.getenv("BUILD_NUMBER")+"/"+datosConfig.getProperty("screenshots");
    private static String rutaR = System.getenv("BUILD_URL")+datosConfig.getProperty("screenshots");

    public static void CapturaPantalla(String mensaje, ITestResult result,String status) throws Exception {
        if(datosConfig.getProperty("Reporting").equals("ACTIVE"))
        {
            Status finalStatus;
            int count = screenshotCounter.get()+1;
            screenshotCounter.set(count);

            //comprobamos el status que le pasamos
            String testname = BaseLocal.idCasoPrueba(result.getMethod().getMethodName()) + "_Captura-"+count;

            // Determinar el estado final basado en el parámetro `status`
            switch (status.toUpperCase()) {
                case "PASS":
                    finalStatus = Status.PASS;
                    break;
                case "INFO":
                    finalStatus = Status.INFO;
                    break;
                default:
                    finalStatus = Status.FAIL;
                    break;
            }
            // Capturar la pantalla y agregar al log con mensaje
            try {
                String screenshotPath = gettakeScreenShot(Browser.getDriver(), testname);
                ExtentManager.getTest().log(finalStatus, mensaje, MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                ExtentFactory.getInstance().removeExtentObject();
            }

        }else{
            System.out.println("El reporte está desactivado");
        }


    }


    public static String gettakeScreenShot(WebDriver driver,String TestCaseName) throws IOException, InterruptedException {

        String destpath;
        String rutaFinal;
        // Convertir el objeto del controlador web en Screenshot
        Log.info("Entramos en la función de captura de gettakeScreenShot");
        TakesScreenshot ts = ((TakesScreenshot) driver);
        Log.info("Hace el TakesScreenshot");
        File source = ts.getScreenshotAs(OutputType.FILE);
        Log.info("Hace el s.getScreenshotAs");

        // Reemplazar caracteres no validos en el nombre del archivo
        String sanitizedTestCaseName = TestCaseName.replace(":", ".");
        //Se filtra para ejecución en local
        if(datosConfig.getProperty("Local").equals("OK")){
            BaseLocal.CrearDirectorio(rutaLocal);
            BaseLocal.CrearDirectorio(rutaRLocal);
            System.out.println("Se quiere guardar primero en la ruta " + rutaLocal + sanitizedTestCaseName +".png");
            destpath = rutaLocal + sanitizedTestCaseName + ".png";
            System.out.println("Se quiere guardar la captura en ruta " + rutaRLocal + sanitizedTestCaseName +".png");
            rutaFinal = rutaRLocal + sanitizedTestCaseName + ".png";
        }else{
            System.out.println("Se quiere guardar primero en la ruta " + ruta + sanitizedTestCaseName +".png");
            destpath = ruta + sanitizedTestCaseName + ".png";
            System.out.println("Se quiere guardar la captura en ruta " + rutaR + sanitizedTestCaseName +".png");
            rutaFinal = rutaR + sanitizedTestCaseName + ".png";
        }

        // Crear las carpetas si no existen
        Path rutaCompleta = Paths.get(destpath);
        Files.createDirectories(rutaCompleta.getParent());

        // Guardar la captura de pantalla en la ruta especificada
        File file = new File(destpath);
        FileUtils.copyFile(source, file);

        if (file.exists()){
            Log.info("Se ha guardado la captura del " + sanitizedTestCaseName + " correctamente");
        }
        else{
            Log.info("No se ha guardado la captura del " + sanitizedTestCaseName + " correctamente");
        }

        return rutaFinal;
    }


        public static String fechaActual() {
            // Establecer la zona horaria de Madrid
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Madrid"));


            // Extraer el dia, mes y año
            int day = now.getDayOfMonth();
            int month = now.getMonthValue();
            int year = now.getYear();


            // Extraer horas, minutos y segundos
            int hour = now.getHour();
            int minute = now.getMinute();
            int second = now.getSecond();


            // Formatear la fecha y hora
            String formattedDateTime = day+"."+month+"."+year+"."+hour+":"+minute;

            return formattedDateTime;
        }

}
