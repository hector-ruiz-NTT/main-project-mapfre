package mapfre.utils.functional_utils;



import mapfre.Browser;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

public class JSUtils {

    public void clickJS(WebElement element) {
        JavascriptExecutor executor = ((JavascriptExecutor) Browser.getDriver());
        executor.executeScript("arguments[0].click();", element);
    }

    public void scrollToElement(WebElement element) {
        JavascriptExecutor js = (((JavascriptExecutor) Browser.getDriver()));
        js.executeScript("arguments[0].scrollIntoView(true);", element);
    }
    public void nuevaVentana() {
        JavascriptExecutor js = (((JavascriptExecutor) Browser.getDriver()));
        js.executeScript("window.open('');");
    }
    public void scrollToPixel() {
        JavascriptExecutor js = (((JavascriptExecutor) Browser.getDriver()));
        js.executeScript("window.scrollBy(0,1000)");
    }

    public Object executeJavascriptCommand(String command) {
        return (((JavascriptExecutor) Browser.getDriver())).executeScript(command);
    }

    public void centerElement(WebElement webElement) {
        (((JavascriptExecutor) Browser.getDriver())).executeScript("arguments[0].scrollIntoView({block: \"center\"});", webElement);
    }

    public void zoomOut() {
        (((JavascriptExecutor) Browser.getDriver())).executeScript("document.body.style.zoom = '0.75'");
    }

    public void restarurarZoom() {
        (((JavascriptExecutor) Browser.getDriver())).executeScript("document.body.style.zoom = '1'");
    }


    public void scrollToBottom() {
        (((JavascriptExecutor) Browser.getDriver())).executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }


    public void informarValor(String s, WebElement elemento) {
        (((JavascriptExecutor) Browser.getDriver())).executeScript("arguments[0].value=" + s + "; arguments[0].blur(); return true", elemento);
    }
}
