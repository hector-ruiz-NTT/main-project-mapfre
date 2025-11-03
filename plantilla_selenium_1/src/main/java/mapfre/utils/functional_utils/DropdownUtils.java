package mapfre.utils.functional_utils;

//import org.junit.Assert;
import org.testng.Assert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DropdownUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DropdownUtils.class);


    public static boolean setSelect(WebElement element, int index) {
        boolean flag = false;
        int i = 0;
        LOGGER.debug("Indice para el select='" + index + "'");
        Select s = new Select(element);
        do {
            try {
                Thread.sleep(1000);
                s.selectByIndex(index);
                LOGGER.info(s.getFirstSelectedOption().getText());
                flag = true;
            } catch (Exception e) {
                i++;
                    if (i == 40) {
                    break;
                }
            }
        } while (!flag);
        return flag;
    }

    public static boolean setSelect(WebElement element, String value) {
        boolean flag = false;
        int i = 0;
        LOGGER.debug("Valor para el select='" + value + "'");
        Select s;
        do {

            try {
                s = new Select(element);
                Thread.sleep(1000);
                s.selectByVisibleText(value);
                flag = true;
            } catch (Exception e) {
                i++;
                if (i == 40) {
                    break;
                }
            }
        } while (!flag);
        return flag;
    }

    public static void checkSelectedAnd(WebElement element, String selected, boolean enable) {
        Select s = new Select(element);
        String error;
        if (enable)
            error = "Se esperaba que el elemento estuviera habilitado";
        else
            error = "Se esperaba que el elemento no estuviera habilitado";
        ValidationUtils.elementEquals(s.getFirstSelectedOption(), selected);
        boolean selectStatus = true;
        try {
            if (element.getAttribute("disabled").equals("disabled"))
                selectStatus = false;
        } catch (Exception e) {
            System.out.println("El select está activo");
        }
        if (selectStatus != enable) {
            Assert.fail(error);
        }

    }

    public static String valorSeleccionadoCombo(WebElement webElement) {
        Select s = new Select(webElement);
        return s.getFirstSelectedOption().getText();
    }

    public static boolean comprobarSelected(WebElement elemento, String valor) {
        Select s = new Select(elemento);
        return s.getFirstSelectedOption().getText().equals(valor);


    }


    public static int comprobarNumeroDeResultados(WebElement elemento) {
        Select s = new Select(elemento);
        return s.getAllSelectedOptions().size();
    }
}
