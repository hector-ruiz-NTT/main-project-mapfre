package mapfre.utils.functional_utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.junit.Assert;
import org.testng.Assert;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Locale;

public class ValidationUtils {

    private static Logger log = LogManager.getLogger();


    public static void showErrors(List<String> list) {
        String errors = "";
        if (list.size() > 0) {
            for (String s : list) {
                errors = errors + s + "\n";
            }
            Assert.fail(errors);
        }
    }

    public static List<String> elementContains(List<String> list, WebElement element, String text) {
        log.debug("Se esta comparando '" + element.getText().trim().toUpperCase(Locale.ROOT) + "' con '" + text.trim().toUpperCase(Locale.ROOT) + "'");
        if (!element.getText().trim().toUpperCase(Locale.ROOT).contains(text.trim().toUpperCase(Locale.ROOT))) {
            list.add("El elemento '" + element.getText() + "' no contiene el texto '" + text + "'");
        }
        return list;
    }

    public static void elementContains(WebElement element, String text) {
        log.debug("¿Contiene " + element.getText().toUpperCase(Locale.ROOT) + " el texto: " + text.toUpperCase(Locale.ROOT) + "?");
        if (!element.getText().trim().toUpperCase(Locale.ROOT).contains(text.trim().toUpperCase(Locale.ROOT))) {
            Assert.fail(element.getText() + " no contiene '" + text + "'");
        } else {
            log.debug(element.getText() + " contiene '" + text + "'");
        }
    }


    public static void elementEquals(WebElement element, String text) {
        if (!element.getText().trim().toUpperCase(Locale.ROOT).equals(text.trim().toUpperCase(Locale.ROOT))) {
            Assert.fail("Se esperaba '" + text + "' y se ha encontrado '" + element.getText() + "'");
        } else {
            log.debug("Se ha encontrado el valor '" + text + "'");
        }
    }

    public static void elementEqualsValue(WebElement element, String text) {
        if (!element.getAttribute("value").trim().toUpperCase(Locale.ROOT).equals(text.trim().toUpperCase(Locale.ROOT))) {
            Assert.fail("Se esperaba '" + text + "' y se ha encontrado '" + element.getAttribute("value") + "'");
        } else {
            log.debug("Se ha encontrado el valor '" + text + "'");
        }
    }

    public static void compararString(String element, String text) {
        if (!element.trim().toUpperCase(Locale.ROOT).equals(text.trim().toUpperCase(Locale.ROOT))) {
            Assert.fail(element + " no contiene '" + text + "'");
        } else {
            log.debug("Se ha encontrado el valor '" + text + "'");
        }
    }

    public static void contieneString(String element, String text) {
        if (!element.trim().toUpperCase(Locale.ROOT).contains(text.trim().toUpperCase(Locale.ROOT))) {
            Assert.fail(element + " no contiene '" + text + "'");
        } else {
            log.debug("Se ha encontrado el valor '" + text + "'");
        }
    }

    public static void niVacioNiCero(WebElement element) {
        if (element.getText().equals("") || element.getText().equals("0,00")) {
            Assert.fail("El elemento no tiene valor. Valor actual del elemento = " + element.getText());
        } else {
            log.debug("Se valor actual del elemento es '" + element.getText() + "'");
        }
    }

    public static void elementEqualsValueSelected(WebElement element, String text) {
        if (!DropdownUtils.valorSeleccionadoCombo(element).trim().toUpperCase(Locale.ROOT).equals(text.trim().toUpperCase(Locale.ROOT))) {
            Assert.fail(DropdownUtils.valorSeleccionadoCombo(element) + " no contiene '" + text + "'");
        } else {
            log.debug("Seleccionado el valor: " + DropdownUtils.valorSeleccionadoCombo(element));
        }
    }


    public static void elementNotEqualsValueSelected(WebElement element, String text) {
        if (DropdownUtils.valorSeleccionadoCombo(element).trim().toUpperCase(Locale.ROOT).equals(text.trim().toUpperCase(Locale.ROOT))) {
            Assert.fail(element.getAttribute("value") + " contiene '" + text + "'");
        } else {
            log.debug("Seleccionado el valor: " + DropdownUtils.valorSeleccionadoCombo(element));
        }
    }


    public static void elementContainsValue(WebElement elementById, String text) {
        if (!elementById.getAttribute("value").trim().toUpperCase(Locale.ROOT).contains(text.trim().toUpperCase(Locale.ROOT))) {
            Assert.fail("Se esperaba '" + text + "' y se ha encontrado '" + elementById.getAttribute("value") + "'");
        } else {
            log.debug("Se ha encontrado el valor '" + text + "'");
        }
    }

    public static void estaBotonHabilitado(WebElement elementById) {
        if (!elementById.isEnabled()) {
            Assert.fail("El botón no está habilitado");
        }
    }

    public static void elementCheckSelected(WebElement element) {
        if (!element.isSelected()) {
            Assert.fail("El objeto no está seleccionado");
        } else {
            log.debug("Se ha seleccionado el objeto");
        }
    }

    public static void comboNoVacio(WebElement element) {
        if (DropdownUtils.valorSeleccionadoCombo(element).trim().toUpperCase(Locale.ROOT).contains("Seleccione".trim().toUpperCase(Locale.ROOT))) {
            Assert.fail("No se ha seleccionado ningún. Su valor es: " + DropdownUtils.valorSeleccionadoCombo(element));
        } else {
            log.debug("Seleccionado el valor: " + DropdownUtils.valorSeleccionadoCombo(element));
        }
    }

    public static void campoNoVacio(WebElement element) {
        if (element.getAttribute("value").equals("")) {
            Assert.fail("El elemento no tiene ningún valor. Valor actual del elemento = '" + element.getAttribute("value") + "'");
        } else {
            log.debug("Se valor actual del elemento es '" + element.getAttribute("value") + "'");
        }
    }

    public static void campoVacio(WebElement element) {
        if (!element.getText().equals("")) {
            Assert.fail("El elemento tiene informado un valor cuando debería estar vacío. Valor actual del elemento = '" + element.getText() + "'");
        } else {
            log.debug("Se valor actual del elemento es 'vacío'");
        }
    }

    public static void campoVacioValue(WebElement element) {
        if (!element.getAttribute("value").equals("")) {
            Assert.fail("El elemento tiene informado un valor cuando debería estar vacío. Valor actual del elemento = '" + element.getAttribute("value") + "'");
        } else {
            log.debug("Se valor actual del elemento es 'vacío'");
        }
    }

}
