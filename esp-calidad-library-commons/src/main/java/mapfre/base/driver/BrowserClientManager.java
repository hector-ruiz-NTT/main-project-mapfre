package mapfre.base.driver;

import org.openqa.selenium.remote.RemoteWebDriver;

public class BrowserClientManager {

    protected static ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<>();

    public static RemoteWebDriver getDriver() {
        return driver.get();
    }
}

