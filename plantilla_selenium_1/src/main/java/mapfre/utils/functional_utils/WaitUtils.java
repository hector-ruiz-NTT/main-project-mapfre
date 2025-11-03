package mapfre.utils.functional_utils;

//import org.junit.Assert;
import mapfre.Browser;
import org.testng.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Set;


import static mapfre.utils.DriverManager.getDriver;
import static org.openqa.selenium.support.ui.ExpectedConditions.not;

public class WaitUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(WaitUtils.class);


    private ActionsUtils actionsUtils;

    public WebDriverWait wait(int seconds) {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(seconds));
    }

    public void waitUntilVisible(WebElement element, int seconds) {
        wait(seconds).until(ExpectedConditions.visibilityOf(element));
    }

    public void fluentWaitUntilVisible(WebElement element, int seconds) {
        wait(seconds).ignoring(NoSuchElementException.class).until(ExpectedConditions.visibilityOf(element));
    }

    public void waitUntilClickable(WebElement element, int seconds) {
        wait(seconds).until(ExpectedConditions.elementToBeClickable(element));
    }

    public void waitUntilWrite(WebElement element, String text, int seconds) throws InterruptedException {
        boolean flag = false;
        int count = 0;
        while (!flag && count < seconds)
            try {
                count++;
                element.sendKeys(text);
                flag = true;
            } catch (Exception e) {
                Thread.sleep(1000);
            }
    }

    public void waitAndSwitchTab(String titlePage) throws InterruptedException {
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

    public void waitAndBackToOldTab() throws InterruptedException {
        Set<String> windows = Browser.getDriver().getWindowHandles();
        while (windows.size() > 1) {
            windows = Browser.getDriver().getWindowHandles();
            Thread.sleep(500);
        }
        String goodHandle = "";
        for (String handle : windows) {
            Browser.getDriver().switchTo().window(handle);
            goodHandle = Browser.getDriver().getWindowHandle();
        }
        Browser.getDriver().switchTo().window(goodHandle);
    }

    public void switchTabAndClose(String titlePage) {
        Set<String> windows = Browser.getDriver().getWindowHandles();
        String goodHandle = "";
        for (String handle : windows) {
            Browser.getDriver().switchTo().window(handle);
            String title = Browser.getDriver().getTitle();
            if (!title.contains(titlePage)) {
                Browser.getDriver().close();
                System.out.println("Closed the " + title + " tab now");
            } else {
                goodHandle = Browser.getDriver().getWindowHandle();
            }
        }
        Browser.getDriver().switchTo().window(goodHandle);
    }

    public void waitAndClick(WebElement element) {
        wait(30).until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }

    public void fluentWaitForClickable(WebElement element, int seconds) {
        wait(seconds).ignoring(NoSuchElementException.class).until(ExpectedConditions.elementToBeClickable(element));
    }

    public void waitUntilInvisible(WebElement element, int seconds) {
        wait(seconds).until(not(ExpectedConditions.visibilityOf(element)));
    }

    public void waitUntilContainsAttribute(WebElement element, String attribute, String value, int seconds) {
        wait(seconds).until(ExpectedConditions.attributeContains(element, attribute, value));
    }

    public void waitUntilTextDisappear(String element, String value, int seconds) {
        wait(seconds).until(not(ExpectedConditions.textToBe(By.cssSelector(element), value)));
    }

    public void waitUntilTextAppear(String element, String value, int seconds) {
        wait(seconds).until(not(ExpectedConditions.textToBe(By.cssSelector(element), value)));
    }

    public Alert fluentWaitUntilAlert(int seconds) {
        return wait(seconds).ignoring(NoAlertPresentException.class).until(ExpectedConditions.alertIsPresent());
    }

    public Alert waitUntilAlert(int seconds) {
        return wait(seconds).until(ExpectedConditions.alertIsPresent());
    }

    public void fluentWaitUntilUrlContains(String url, int seconds) throws InterruptedException {
        wait(seconds).ignoring(NoSuchElementException.class).until(ExpectedConditions.urlContains(url));
    }

    public void fluentWaitUntilUrlMatches(String regex, int seconds) {
        wait(seconds).ignoring(NoSuchElementException.class).until(ExpectedConditions.urlMatches(regex));
    }

    public void waitUntilWindowHandle(int windowId) throws InterruptedException {
        int i;
        for (i = 0; i <= 10; i++) {
            Thread.sleep(1000);
            if (actionsUtils.getHandleWindow().size() > 1) {
                actionsUtils.switchWindow(actionsUtils.getHandleWindow(), windowId);
                i = 10;
            }
        }
    }


    public void waitForPageToLoad() {
        try {
            Thread.sleep(200);
            int default_wait_time = 90;
            (new WebDriverWait(Browser.getDriver(), Duration.ofSeconds(default_wait_time))).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver d) {
                    return (((JavascriptExecutor) Browser.getDriver())
                            .executeScript("return document.readyState").toString().equals("complete"));

                }
            });
        } catch (Exception ex) {
        }
    }

    public WebElement giveMeTheParent(WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) Browser.getDriver();
        return (WebElement) (executor.executeScript("return arguments[0].parentNode;", element));
    }
}
