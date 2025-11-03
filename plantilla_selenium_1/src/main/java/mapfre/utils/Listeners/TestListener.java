package mapfre.utils.Listeners;

import java.io.IOException;
import com.aventstack.extentreports.MediaEntityBuilder;

import mapfre.Browser;
import mapfre.general.Log;
import mapfre.general.BaseLocal;
import mapfre.utils.DriverManager;
import mapfre.utils.ExtentManager.ExtentFactory;
import mapfre.utils.ExtentManager.ExtentManager;
import mapfre.utils.functional_utils.ScreenShots;
import org.testng.*;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.Status;

import static mapfre.utils.DriverManager.getDriver;

public class TestListener implements ITestListener, ISuiteListener {
    ExtentReports extent = ExtentManager.getReporter();

    private static ThreadLocal<Integer> screenshotCounter = ThreadLocal.withInitial(() -> 0);
    @Override
    public void onTestStart(ITestResult result) {

        ExtentManager.starTest(result.getMethod().getMethodName());
        //System.out.println("El test " + result.getMethod().getMethodName() + " ha empezado");
        screenshotCounter.set(0);
        }


    public static void CapturaPantalla(String mensaje,String status) throws Exception {

        ITestResult result = Reporter.getCurrentTestResult();
        ScreenShots.CapturaPantalla(mensaje,result,status);
    }

    @Override
    public void onTestSuccess(ITestResult result) {

        ExtentFactory.getInstance().removeExtentObject();

        try {

            Log.pass(BaseLocal.idCasoPrueba(result.getMethod().getMethodName()) +": ***** FIN EJECUCION ***** El estado de la ejecucion es "+TestResult(result));
            ExtentManager.getTest().log(Status.PASS, MediaEntityBuilder.createScreenCaptureFromPath(ScreenShots.gettakeScreenShot(getDriver(),result.getMethod().getMethodName())).build());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            ExtentFactory.getInstance().removeExtentObject();
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {

        try {
            if (Browser.isBrowserOpen(getDriver())) {
                String screenshotPath = ScreenShots.gettakeScreenShot(getDriver(), result.getMethod().getMethodName());
                ExtentManager.getTest().fail(result.getThrowable()).addScreenCaptureFromPath(screenshotPath);
                Log.fail(BaseLocal.idCasoPrueba(result.getMethod().getMethodName()) + ": ***** FIN EJECUCION ***** El estado de la ejecucion es " + TestResult(result));
            } else {
                ExtentManager.getTest().fail(result.getThrowable());
                Log.info("El navegador se ha cerrado antes de sacar la captura del fallo");
                Log.fail(BaseLocal.idCasoPrueba(result.getMethod().getMethodName()) + ": ***** FIN EJECUCION *****");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // Remueve el objeto de Extent para evitar conflictos en la siguiente prueba
            ExtentFactory.getInstance().removeExtentObject();
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {

        ExtentManager.getTest().log(Status.SKIP, "Test Case: " + result.getMethod().getMethodName() + " is skipped.");
        ExtentFactory.getInstance().removeExtentObject();
    }


    @Override
    public void onFinish(ITestContext context) {
        Log.infoHiloSec("Finalización del contexto de pruebas: " + context.getName());
        try {
            Log.infoHiloSec("Deteniendo el hilo de actividad continua.");
            DriverManager.stopContinuousActivity();  // Llamada al método que finaliza el hilo

            Log.infoHiloSec("Llamadas a métodos finalizadas correctamente.");
        } catch (Exception e) {
            Log.infoHiloSec("Error en onFinish: " + e.getMessage());
        }
        Log.infoHiloSec("El contexto de pruebas ha finalizado completamente.");
        extent.flush();
    }

    public static String TestResult(ITestResult result){

        String testResult="";
        try{
            if(result.getStatus()== ITestResult.SUCCESS){
                testResult="PASSED";

            }
            else if(result.getStatus()==ITestResult.FAILURE){
                testResult="FAILURE";
            }
            else if (result.getStatus()==ITestResult.SKIP){
                testResult="SKIP";

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  testResult;
    }

}
