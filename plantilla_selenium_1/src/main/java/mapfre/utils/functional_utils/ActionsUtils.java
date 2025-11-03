package mapfre.utils.functional_utils;



//import org.junit.Assert;
import mapfre.Browser;
import org.testng.Assert;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.RandomUtils.nextInt;


public class ActionsUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionsUtils.class);
    private JSUtils jsUtils;
    private WaitUtils waitUtils;


    public void waitAndSwitchTab(String notTitlePage) throws InterruptedException {
        boolean flag = true;
        int count = 0;
        Set<String> windows;
        while (flag) {
            windows = Browser.getDriver().getWindowHandles();
            String goodHandle = "";
            for (String handle : windows) {
                Browser.getDriver().switchTo().window(handle);
                String title = Browser.getDriver().getCurrentUrl();
                System.out.println(title);
                if (title.contains(notTitlePage)) {
                    goodHandle = Browser.getDriver().getWindowHandle();
                    flag = false;
                }
            }
            if (count == 10) Assert.fail("CHANGE_TAB_ERROR");
            Browser.getDriver().switchTo().window(goodHandle);
            count++;
            Thread.sleep(500);
        }
    }
    public void browserConfiguration() {
        Browser.getDriver().manage().window().maximize();
    }

    public List<String> getHandleWindow() {
        return convertSetToList(Browser.getDriver().getWindowHandles());
    }

    public void switchWindow(List<String> windowList, int windowId) {
       Browser.getDriver().switchTo().window(windowList.get(windowId));
    }

    public void actionClick(WebElement element){
        Actions actions = new Actions(Browser.getDriver());
        actions.click(element).perform();
    }
    public static boolean checkWeb() throws IOException {
        boolean flag;
        String file = System.getProperty("user.dir") +
                File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator
                + "app.properties";
        Path path = Paths.get(file);
        Charset charset = StandardCharsets.UTF_8;

        String content = new String(Files.readAllBytes(path), charset);
        if (content.contains("execution.type=WEB")) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }
    public void changeWindowAndClose(){
        Browser.getDriver().close();
        switchWindow(getHandleWindow(), 0);
    }

    public void changeWindow(){
        switchWindow(getHandleWindow(), 0);
    }


    public void accessToMainWindow(){
        int numVentanas = Browser.getDriver().getWindowHandles().size();
        LOGGER.debug("Numero de ventanas " +numVentanas);
        Set<String> windows = Browser.getDriver().getWindowHandles();
        Browser.getDriver().switchTo().window(windows.iterator().next());
    }
//
    public static void changeAPItoWEBandWEBtoAPI(String property1, String property2) throws IOException {

        String file = System.getProperty("user.dir") +
                File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator
                + "app.properties";
        Path path = Paths.get(file);
        Charset charset = StandardCharsets.UTF_8;

        String content = new String(Files.readAllBytes(path), charset);
        if (content.contains("execution.type=" + property2)) {
            LOGGER.debug("La propiedad ya está en modo:" + property2);
        } else {
            content = content.replaceAll("execution.type=" + property1, "execution.type=" + property2);
            Files.write(path, content.getBytes(charset));
            LOGGER.debug("La propiedad  está en modo:" + property1);
        }
    }
//    private static void attachScreenshotReport(Scenario scenario, File scrFile) throws IOException {
//        byte[] data = FileUtils.readFileToByteArray(scrFile);
//        scenario.embed(data, "image/png");
//    }

    public void navigateTo(String url) {
        LOGGER.info("Navigating to " + url);
        jsUtils.executeJavascriptCommand("this.document.location = " + "\'" + url + "'");
        waitUtils.waitForPageToLoad();
    }

    public void deleteLocalStorage() {
        jsUtils.executeJavascriptCommand("window.localStorage.clear();");
    }

    public void loadPage(String url) {
        Browser.getDriver().get(url);
    }

    public Set<Cookie> getCookies() {
        return Browser.getDriver().manage().getCookies();
    }

    public void deleteCookies() {
        Browser.getDriver().manage().deleteAllCookies();
    }

    public String getItemFromLocalStorage(String item) {
        return (String) jsUtils.executeJavascriptCommand("return window.localStorage.getItem(" + "\'" + item + "');");
    }


    public static List<String> convertSetToList(Set<String> setToConvert) {
        List<String> mainList = new ArrayList<>();
        mainList.addAll(setToConvert);
        return mainList;
    }

    public WebElement getElement(List<WebElement> list) {
        WebElement searched = null;
        for (WebElement element : list) {
            if (element.getAttribute("class").contains("form-control")) {
                searched = element;
            }
        }
        return searched;
    }

    public static WebElement getRandomWebElement(List<WebElement> list) {
        WebElement element = list.get(nextInt(1, list.size() - 1));
        LOGGER.info("Se selecciona " + element.getText());
        return element;
    }

    public static WebElement getElementFromString(List<WebElement> list, String s) {
        WebElement element = null;
        for (WebElement e :
                list) {
            if (e.getText().equals(s)) {
                element = e;
                break;
            }
        }
        LOGGER.info("Se selecciona " + element.getText());
        return element;
    }

    public static List<WebElement> getRowFromTable(List<WebElement> list, String s, int start, int rowSize) {
        List<WebElement> row= new ArrayList<>();
        int count = 0;
        boolean flag = false;
        for (int i= start; i < list.size() - 1; i++) {
            row.add(list.get(i));
            if (list.get(i).getText().equals(s)) {
                flag = true;
            }
            if (count == rowSize) {
                if (flag) {
                    break;
                } else {
                    count = 0;
                    row.clear();
                }
            }
        }
        return row;
    }
}

