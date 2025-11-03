package mapfre.ExtentManager;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

public class ExtentFactory {
    // Singleton design Pattern
    // private constructor so that no one else can create object of this class
    private ExtentFactory() {

    }

//    static Map extentTestMap = new HashMap();
    static ExtentFactory instance = new ExtentFactory();


    public static ExtentFactory getInstance() {
	return instance;
    }

    // factory design pattern --> define separate factory methods for creating
    // objects and create objects by calling that methods
    ThreadLocal<ExtentTest> extent = new ThreadLocal<ExtentTest>();

//    public static synchronized ExtentTest getTest() {
//	return (ExtentTest) extentTestMap.get((int) (long) (Thread.currentThread().getId()));
//    }

    public void setExtent(ExtentTest extentTestObject) {
	extent.set(extentTestObject);
    }

    public void removeExtentObject() {
	extent.remove();
    }

   
}

