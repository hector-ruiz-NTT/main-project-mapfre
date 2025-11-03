package mapfre.utils.functional_utils;


import mapfre.Browser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
//import org.junit.Assert;
import org.testng.Assert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class PDFutils {
    String x;
    private static Logger log = LogManager.getLogger();

    public String copiarTodo() {
        int i = 0;
        try {
            Thread.sleep(10000);
        } catch (Exception e) {
        }
        x = "";
        setClipboard();
        do {
            i++;
            seleccionarTodo();
            // para
            //if (NseBaseTest1.getDriver().getTitle().equals("Fichero.pdf") || NseBaseTest1.getDriver().getTitle().equals("") )
            if (Browser.getDriver().getTitle().equals("Fichero.pdf") || Browser.getDriver().getTitle().equals("") )
                copiar2();
            else
                copiar3();
            if (i == 10) Assert.fail("Se ha intentado copiar el PDF demasiadas veces ");
        } while (comprobarClipboard());
        return x;
    }

    private void seleccionarTodo() {
        try {

            Thread.sleep(2000);
            Robot r = new Robot();
            r.keyPress(KeyEvent.VK_CONTROL);
            r.keyPress(KeyEvent.VK_A);
            r.keyRelease(KeyEvent.VK_A);
            r.keyRelease(KeyEvent.VK_CONTROL);

        } catch (Exception e) {
            log.info("Ha fallado la captura del pdf");
        }
    }

    private void copiar1() {
        try {
            Robot r = new Robot();
            Thread.sleep(2000);
            r.keyPress(KeyEvent.VK_CONTEXT_MENU);
            r.keyRelease(KeyEvent.VK_CONTEXT_MENU);
            Thread.sleep(2000);
            r.keyPress(KeyEvent.VK_ENTER);
            r.keyRelease(KeyEvent.VK_ENTER);
            r.keyPress(KeyEvent.VK_ENTER);
            r.keyRelease(KeyEvent.VK_ENTER);

        } catch (Exception e) {
            log.info("Ha fallado la copia");
        }
    }

    private void copiar2() {
        try {
            Robot r = new Robot();
            Thread.sleep(1000);

            int alto = Browser.getDriver().manage().window().getSize().getHeight();
            int ancho = Browser.getDriver().manage().window().getSize().getWidth();
            for (int i = 0; i < 3; i++) {
                r.mouseMove((ancho / 2), (alto / 2 - 100 * i));
                Thread.sleep(300);
            }
            r.mousePress(InputEvent.BUTTON3_DOWN_MASK);
            r.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
            Thread.sleep(2000);
            r.keyPress(KeyEvent.VK_ENTER);
            r.keyRelease(KeyEvent.VK_ENTER);
            r.keyPress(KeyEvent.VK_ENTER);
            r.keyRelease(KeyEvent.VK_ENTER);
        } catch (Exception e) {
            log.info("Ha fallado la copia");
        }
    }

    private void copiar3() {
        try {
            Robot r = new Robot();
            Thread.sleep(1000);

            int alto = Browser.getDriver().manage().window().getSize().getHeight();
            int ancho = Browser.getDriver().manage().window().getSize().getWidth();
            for (int i = 0; i < 3; i++) {
                r.mouseMove((ancho / 2), (alto / 2 + 75 * i));
                Thread.sleep(300);
            }
            r.mousePress(InputEvent.BUTTON3_DOWN_MASK);
            r.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
            Thread.sleep(2000);
            r.keyPress(KeyEvent.VK_ENTER);
            r.keyRelease(KeyEvent.VK_ENTER);
            r.keyPress(KeyEvent.VK_ENTER);
            r.keyRelease(KeyEvent.VK_ENTER);
        } catch (Exception e) {
            log.info("Ha fallado la copia");
        }
    }


    private void setClipboard() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transferable = null;

        transferable = new StringSelection("clipboard limpio");

        clipboard.setContents(transferable, null);
    }

    private boolean comprobarClipboard() {
        x = "";
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable contents = clipboard.getContents(null);
            x = (String) contents.getTransferData(DataFlavor.stringFlavor);
        } catch (Exception e) {
            log.info("Ha fallado la comprobación del clipboard");
        }
        return x.equals("clipboard limpio");
    }


    public String leerPDF(String rutaPDF) {
        PDDocument pdDocument = null;
        String parsedText = null;
        try {
            pdDocument = PDDocument.load(new File(rutaPDF));

            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setStartPage(1);
            pdfStripper.setEndPage(5);
            parsedText = pdfStripper.getText(pdDocument);

            //System.out.println(parsedText);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (pdDocument != null) {
                try {
                    pdDocument.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return parsedText;
    }
}
