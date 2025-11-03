//package mapfre.base.actions;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import org.apache.poi.ss.usermodel.*;
//
//public class ExcelUtils {
//    public static String obtenerDato(String ruta,String columna) throws Exception {
//        int row = 0;
//        int col = 0;
//        int hoja = 0;
//        String value;
//        Workbook workbook = WorkbookFactory.create(new FileInputStream(new File(ruta)));
//        Sheet sheet = workbook.getSheetAt(hoja);
//
//        //Se localiza la fila con la que trabajar
//        while (row <= contarFilasExcel(ruta) && !sheet.getRow(row).getCell(0).getStringCellValue().equalsIgnoreCase(ProjectProperties.getConfigProperties().getProperty("entorno"))) {
//            row++;
//        }
//        Log.info("Fila correspondiente al entorno de " + ProjectProperties.getConfigProperties().getProperty("entorno") + ": " + row);
//
//        //Se localiza la columna en la que buscar el dato
//        while (sheet.getRow(0).getCell(col).getStringCellValue().equalsIgnoreCase("") || !sheet.getRow(0).getCell(col).getStringCellValue().trim().toUpperCase().equalsIgnoreCase(columna)) {
//            col++;
//        }
//        Log.info("Columna correspondiente al campo " + columna + ": " + col);
//
//        //Se cambia el tipo de celda a String para estandarizar y evitar fallos y se devuelve el dato correcto
//        sheet.getRow(row).getCell(col).setCellType(CellType.STRING);
//        value = sheet.getRow(row).getCell(col).getStringCellValue();
//        if (!value.isEmpty()){
//            if (columna.contains("PASS") || columna.contains("pass") || columna.contains("Pass")) {
//                Log.info("Dato encontrado: ******");
//            }else if(columna.contains("Url") || columna.contains("URL")|| columna.contains("url")){
//                if(ProjectProperties.getConfigProperties().getProperty("entorno").equals("PRO")){
//                    Log.info("Dato encontrado:  https://wportalinterno.es.mapfre.net/CCYCS_FWEB/inicio.jsf");
//                }else{
//                    Log.info("Dato encontrado:  https://wportalinterno.pre.mapfre.net/CCYCS_FWEB/inicio.jsf");
//                }
//
//            }
//            else {
//                Log.info("Dato encontrado: " + value);
//            }
//        }
//        //Se cierra el Workbook y se devuelve el valor encontrado
//        workbook.close();
//        return value;
//    }
//
//    public static int contarFilasExcel(String ruta) throws IOException, InvalidFormatException {
//        int hoja = 0;
//        Workbook workbook = WorkbookFactory.create(new FileInputStream(new File(ruta)));
//        Sheet sheet = workbook.getSheetAt(hoja);
//        int filasTotales = sheet.getLastRowNum();
//        workbook.close();
//        return filasTotales;
//    }
//}
