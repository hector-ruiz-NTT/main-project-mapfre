package mapfre.utils.functional_utils;



import mapfre.Browser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;

public class RobotUtils {

    private JSUtils jsUtils = new JSUtils();
    protected static WebDriver driver;


    public void informarLoginInt() {
       Browser.getDriver().findElement(By.id("details-button")).click();
        Browser.getDriver().findElement(By.id("proceed-link")).click();
        setClipboard("nsesusct");
    }

    public static void pulsarAbajoEnter(int x) {
        try {
            Thread.sleep(1000);
            Robot r = new Robot();
            for (int i = 0; i < x; i++) {
                r.keyPress(KeyEvent.VK_DOWN);
                Thread.sleep(100);
                r.keyRelease(KeyEvent.VK_DOWN);
            }
            r.keyPress(KeyEvent.VK_ENTER);
            Thread.sleep(100);
            r.keyRelease(KeyEvent.VK_ENTER);
        } catch (Exception e) {
            System.out.println("Ha fallado la copia");

        }
    }


    private static void setClipboard(String text) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transferable = null;

        transferable = new StringSelection(text);

        clipboard.setContents(transferable, null);
    }

    public static void mandarTextoEnter(String texto) {
        setClipboard(texto);
        try {
            Robot r = new Robot();
            Thread.sleep(10000);
            r.keyPress(KeyEvent.VK_CONTROL);
            r.keyPress(KeyEvent.VK_V);
            Thread.sleep(100);
            r.keyRelease(KeyEvent.VK_V);
            r.keyRelease(KeyEvent.VK_CONTROL);

            r.keyPress(KeyEvent.VK_ENTER);
            Thread.sleep(100);
            r.keyRelease(KeyEvent.VK_ENTER);

        } catch (Exception e) {
            System.out.println("Ha fallado la copia");
        }
    }


}
