package mapfre.utils.ExtentManager;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import mapfre.general.ProjectProperties;
import mapfre.general.BaseLocal;


import java.util.Properties;


public class ExtentManager {

    private static ExtentReports extent;
	private static Properties datosConfig = ProjectProperties.getConfigProperties();
	private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();


	public static ExtentReports getReporter() {
		ExtentSparkReporter sparkReport;
		if (extent ==null) {

			//Introducimos la ruta donde queremos guardar el reporte
			//String reportPath = "/mnt/efs/Evidencias/"+System.getenv("JOB_NAME")+"/"+System.getenv("BUILD_NUMBER")+"/"+datosConfig.getProperty("report")+"Reports.html";
			String reportPath = "/home/jenkins/workspace/"+System.getenv("JOB_NAME")+"/"+System.getenv("BUILD_NUMBER")+"/"+datosConfig.getProperty("report")+"Reports.html";
			String reportPathLocal = System.getProperty("user.home")+"\\Documents\\"+datosConfig.getProperty("report")+"Reports.html"+"\\"+ BaseLocal.ProjectNameExtractor();


			//Configuramos el ExtentSparkReporter
			//Se filtra para la ejecución el local
			if(datosConfig.getProperty("Local").equals("OK")){
				BaseLocal.CrearDirectorio(reportPathLocal);
				sparkReport = new ExtentSparkReporter(reportPathLocal).viewConfigurer().viewOrder().as(new ViewName[]{ViewName.DASHBOARD, ViewName.TEST, ViewName.CATEGORY}).apply();
			}else{
				sparkReport = new ExtentSparkReporter(reportPath).viewConfigurer().viewOrder().as(new ViewName[]{ViewName.DASHBOARD, ViewName.TEST, ViewName.CATEGORY}).apply();
			}
		//Configura el nombre del reporte
		sparkReport.config().setReportName("BUILD: #" + System.getenv("BUILD_NUMBER"));

		//Configura el nombre del reporte
		sparkReport.config().setDocumentTitle(System.getenv("JOB_NAME") + " - Resultados Regresión Automatizada");

		//Configura el modo offline
		sparkReport.config().setOfflineMode(true);

		//Habilita la línea de tiempo
		sparkReport.config().setTimelineEnabled(true);

		//Configura el formato de fecha y hora
		sparkReport.config().setTimeStampFormat("dd/MM/yyyy HH:mm:ss");

		//Configura el tema del reporte
		sparkReport.config().setTheme(Theme.DARK);

		extent = new ExtentReports();
		extent.attachReporter(sparkReport);

	}
		return extent;
	}

	//El synchornized en el nombre se utiliza para garantizar que solo un hilo pueda acceder a un método a la vez ya que se lanzan varios hilos a la vez
	public static synchronized ExtentTest getTest() {
		return test.get();
	}
	public static synchronized void setTest(ExtentTest tst) {
		test.set(tst);
	}
	public static synchronized ExtentTest starTest(String testName){
		ExtentTest extentTest = ExtentManager.getReporter().createTest(testName).assignCategory("REGRESION");
		setTest(extentTest);
		return extentTest;
	}


}


