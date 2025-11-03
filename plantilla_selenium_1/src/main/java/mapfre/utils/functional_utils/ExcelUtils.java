package mapfre.utils.functional_utils;

import mapfre.general.ProjectProperties;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class ExcelUtils {
    private static Logger log = LogManager.getLogger();


    public static int contarFilasExcel(String ruta) throws IOException, InvalidFormatException {
        int hoja = 0;
        Workbook workbook = WorkbookFactory.create(new FileInputStream(new File(ruta)));
        Sheet sheet = workbook.getSheetAt(hoja);
        int filasTotales = sheet.getLastRowNum();
        workbook.close();
        return filasTotales;
    }

    public static String obtainTableName(String testName){
        return "TD_"+testName.split("_")[0]+".xlsx";
    }

    public static String obtenerDato(String ruta,String columna) throws Exception {
        int row = 0;
        int col = 0;
        int hoja = 0;
        String value;
        Workbook workbook = WorkbookFactory.create(new FileInputStream(new File(ruta)));
        Sheet sheet = workbook.getSheetAt(hoja);

        //Se localiza la fila con la que trabajar
        while (row <= contarFilasExcel(ruta) && !sheet.getRow(row).getCell(0).getStringCellValue().equalsIgnoreCase(ProjectProperties.getConfigProperties().getProperty("entorno"))) {
            row++;
        }
        log.info("Fila correspondiente al entorno de " + ProjectProperties.getConfigProperties().getProperty("entorno") + ": " + row);

        //Se localiza la columna en la que buscar el dato
        while (sheet.getRow(0).getCell(col).getStringCellValue().equalsIgnoreCase("") || !sheet.getRow(0).getCell(col).getStringCellValue().trim().toUpperCase().equalsIgnoreCase(columna)) {
            col++;
        }
        log.info("Columna correspondiente al campo " + columna + ": " + col);

        //Se cambia el tipo de celda a String para estandarizar y evitar fallos y se devuelve el dato correcto
        sheet.getRow(row).getCell(col).setCellType(CellType.STRING);
        value = sheet.getRow(row).getCell(col).getStringCellValue();
        if (!value.isEmpty()){
            if (columna.contains("PASS") || columna.contains("pass") || columna.contains("Pass")) {
                log.info("Dato encontrado: ******");
            } else {
                log.info("Dato encontrado: " + value);
            }
        }
        //Se cierra el Workbook y se devuelve el valor encontrado
        workbook.close();
        return value;
    }

    public static void escribirDatoGeneral(String ruta,int minimo,int maximo,String columna, String value) throws IOException, InvalidFormatException {
        File fichero;
        String rutabase = ruta.substring(0, ruta.indexOf("CP"));
        String rutanueva = "";
        for (int i = minimo;i<=maximo;i++) {
            rutanueva = "";
            if (i<10) {
                rutanueva = rutabase+"CP00"+i+".xlsx";
            } else if (i>=10 && i<100) {
                rutanueva = rutabase+"CP0"+i+".xlsx";
            }
            fichero = new File(rutanueva);
            if (fichero.exists()) {
                ExcelUtils.escribirDato(rutanueva,columna,value);
            }
        }
    }

    public static void escribirDato(String ruta,String columna,String value) throws IOException, InvalidFormatException {
        int row = 0;
        int col = 0;
        int hoja = 0;
        Workbook workbook = WorkbookFactory.create(new FileInputStream(new File(ruta)));
        Sheet sheet = workbook.getSheetAt(hoja);

        //Se localiza la fila con la que trabajar
        while (row<=contarFilasExcel(ruta)&&!sheet.getRow(row).getCell(0).getStringCellValue().equalsIgnoreCase(ProjectProperties.getConfigProperties().getProperty("entorno"))) {
            row++;
        }
        log.info("Fila correspondiente al entorno de " + ProjectProperties.getConfigProperties().getProperty("entorno") + ": " + row);

        //Se localiza la columna en la que escribir el dato
        while (sheet.getRow(0).getCell(col).getStringCellValue().equalsIgnoreCase("")||!sheet.getRow(0).getCell(col).getStringCellValue().equalsIgnoreCase(columna)) {
            col++;
        }
        log.info("Columna correspondiente al campo " + columna + ": " + col);

        //Se cambia el tipo de celda a String para estandarizar y evitar fallos y se escribe el dato correcto
        sheet.getRow(row).getCell(col).setCellType(CellType.STRING);
        sheet.getRow(row).getCell(col).setCellValue(value);

        //Se guardan los cambios y se cierra el Workbook
        FileOutputStream fos = new FileOutputStream(new File(ruta));
        workbook.write(fos);
        workbook.close();

        log.info("Se escribe el valor " + value + " en la columna " + columna);
    }


}
