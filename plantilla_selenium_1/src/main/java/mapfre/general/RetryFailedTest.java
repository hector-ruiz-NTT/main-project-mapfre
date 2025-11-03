package mapfre.general;


import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.util.Properties;

public class RetryFailedTest implements IRetryAnalyzer {
    // Property vars.
    private static final Properties retryConfig = ProjectProperties.getConfigProperties();
    private static Integer counter = 0;
    private static Integer retryLimit = null;

    public Integer getRetryConfig() {
        if (retryConfig.containsKey("RetryLimit")) {
            retryLimit = Integer.parseInt(retryConfig.getProperty("RetryLimit"));
        } else {
            retryLimit = 2;
        }
        return retryLimit;
    }

    @Override
    public boolean retry(ITestResult result) {
        if(counter < getRetryConfig()) {
            counter++;
            return true;
        }
        return false;
    }
}
