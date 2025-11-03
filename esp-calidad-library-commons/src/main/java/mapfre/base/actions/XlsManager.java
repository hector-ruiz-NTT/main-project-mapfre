//package mapfre.base.actions;
//
//
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.*;
//
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//
///**
// * Esta clase proporciona métodos para leer y escribir datos desde y hacia un archivo Excel (XLSX).
// */
//public class XlsManager {
//
//    public String path;
//    public FileInputStream fileInput = null;
//    public FileOutputStream fileOut =null;
//    private XSSFWorkbook workbook = null;
//    private XSSFSheet sheet = null;
//    private XSSFRow row   =null;
//    private XSSFCell cell = null;
//
//    /**
//     * Constructor de la clase XlsManager.
//     *
//     * @param path La ruta del archivo Excel.
//     */
//    public XlsManager(String path) {
//
//        this.path=path;
//        try {
//            fileInput = new FileInputStream(path);
//            workbook = new XSSFWorkbook(fileInput);
//            sheet = workbook.getSheetAt(0);
//            fileInput.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Verifica si una hoja de Excel con el nombre especificado existe en el documento.
//     *
//     * @param sheetName El nombre de la hoja de Excel a verificar.
//     * @return true si la hoja existe, false en caso contrario.
//     */
//    public boolean isSheetExist(String sheetName){
//        int index = workbook.getSheetIndex(sheetName);
//        if(index==-1){
//            index=workbook.getSheetIndex(sheetName.toUpperCase());
//            if(index==-1)
//                return false;
//            else
//                return true;
//        }
//        else
//            return true;
//    }
//
//    /**
//     * Agrega una nueva hoja de Excel con el nombre especificado.
//     *
//     * @param sheetName El nombre de la nueva hoja de Excel.
//     * @return true si la operación tiene éxito, false en caso contrario.
//     */
//    public boolean addSheet(String sheetName){
//        System.out.println("**************addSheet*********************");
//        FileOutputStream fileOut;
//        try {
//            workbook.createSheet(sheetName);
//            fileOut = new FileOutputStream(path);
//            workbook.write(fileOut);
//            fileOut.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * Elimina una hoja de Excel con el nombre especificado.
//     *
//     * @param sheetName El nombre de la hoja de Excel a eliminar.
//     * @return true si la operación tiene éxito, false en caso contrario.
//     */
//    public boolean removeSheet(String sheetName){
//        System.out.println("**************removeSheet*********************");
//        int index = workbook.getSheetIndex(sheetName);
//        if(index==-1)
//            return false;
//
//        FileOutputStream fileOut;
//        try {
//            workbook.removeSheetAt(index);
//            fileOut = new FileOutputStream(path);
//            workbook.write(fileOut);
//            fileOut.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * Obtiene el número de filas en una hoja de Excel.
//     *
//     * @param sheetName El nombre de la hoja de Excel.
//     * @return El número de filas en la hoja especificada.
//     */
//    public int getRowCount(String sheetName){
//        int index = workbook.getSheetIndex(sheetName);
//        if(index == -1) return 0;
//        sheet = workbook.getSheetAt(index);
//        int rowCount = sheet.getLastRowNum()+1;
//        return rowCount;
//    }
//
//    /**
//     * Obtiene el número de columnas en una hoja de Excel.
//     *
//     * @param sheetName El nombre de la hoja de Excel.
//     * @return El número de columnas en la hoja especificada.
//     */
//    public int getColumnCount(String sheetName){
//
//        if(!isSheetExist(sheetName))
//            return -1;
//
//        sheet = workbook.getSheet(sheetName);
//        row = sheet.getRow(0);
//
//        if(row==null)
//            return -1;
//
//        return row.getLastCellNum();
//    }
//
//    /**
//     * Agrega una nueva columna a una hoja de Excel con el nombre de columna especificado.
//     *
//     * @param sheetName El nombre de la hoja de Excel.
//     * @param colName   El nombre de la nueva columna.
//     * @return true si la operación tiene éxito, false en caso contrario.
//     */
//    public boolean addColumn(String sheetName, String colName){
//        System.out.println("**************addColumn*********************");
//        try{
//            fileInput = new FileInputStream(path);
//            workbook = new XSSFWorkbook(fileInput);
//            int index = workbook.getSheetIndex(sheetName);
//            if(index==-1)
//                return false;
//
//            XSSFCellStyle style = workbook.createCellStyle();
//            style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
//            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//
//            sheet=workbook.getSheetAt(index);
//
//            row = sheet.getRow(0);
//            if (row == null)
//                row = sheet.createRow(0);
//
//            if(row.getLastCellNum() == -1)
//                cell = row.createCell(0);
//            else
//                cell = row.createCell(row.getLastCellNum());
//
//            cell.setCellValue(colName);
//            cell.setCellStyle(style);
//
//            fileOut = new FileOutputStream(path);
//            workbook.write(fileOut);
//            fileOut.close();
//
//        }catch(Exception e){
//            e.printStackTrace();
//            return false;
//        }
//
//        return true;
//    }
//
//    /**
//     * Elimina una columna de una hoja de Excel por número de columna.
//     *
//     * @param sheetName El nombre de la hoja de Excel.
//     * @param colNum    El número de columna a eliminar.
//     * @return true si la operación tiene éxito, false en caso contrario.
//     */
//    public boolean removeColumn(String sheetName, int colNum){
//        try{
//            if(!isSheetExist(sheetName))
//                return false;
//            fileInput = new FileInputStream(path);
//            workbook = new XSSFWorkbook(fileInput);
//            sheet=workbook.getSheet(sheetName);
//            XSSFCellStyle style = workbook.createCellStyle();
//            style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
//            XSSFCreationHelper createHelper = workbook.getCreationHelper();
//            style.setFillPattern(FillPatternType.NO_FILL);
//
//            for(int i =0;i<getRowCount(sheetName);i++){
//                row=sheet.getRow(i);
//                if(row!=null){
//                    cell=row.getCell(colNum);
//                    if(cell!=null){
//                        cell.setCellStyle(style);
//                        row.removeCell(cell);
//                    }
//                }
//            }
//            fileOut = new FileOutputStream(path);
//            workbook.write(fileOut);
//            fileOut.close();
//        }
//        catch(Exception e){
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * Todo: Elimina una columna de una hoja de Excel por nombre de columna.
//     *
//     * @param sheetName El nombre de la hoja de Excel.
//     * @param colName    El nombre de columna a eliminar.
//     * @return true si la operación tiene éxito, false en caso contrario.
//     */
//
//
//
//    /**
//     * Busca el indice de la fila en la hoja de cálculo que contiene un nombre específico
//     * en la primera columna.
//     *
//     * @param sheet La hoja de cálculo en la que se realizará la búsqueda.
//     * @param targetName El nombre que se desea buscar en la primera columna.
//     * @return El índice de la fila que contiene el nombre especificado, o -1 si no se encuentra.
//     */
//    private int findRowIndexByName(Sheet sheet, String targetName) {
//        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
//            Row currentRow = sheet.getRow(i);
//            if (currentRow != null) {
//                Cell cell = currentRow.getCell(0); // Assuming the identifier is in the first column
//                if (cell != null && cell.getCellType() == CellType.STRING
//                        && cell.getStringCellValue().trim().equals(targetName.trim())) {
//                    return i;
//                }
//            }
//        }
//        return -1; // Return -1 if not found
//    }
//
//    /**
//     * Obtiene el valor de una celda en una hoja de Excel por número de columna y número de fila.
//     *
//     * @param sheetName El nombre de la hoja de Excel.
//     * @param colNum    El número de columna.
//     * @param rowNum    El número de fila.
//     * @return El valor de la celda en formato de cadena.
//     */
//    public String getCellData(String sheetName, int colNum, int rowNum){
//        try{
//            if(rowNum <=0)
//                return "";
//
//            int index = workbook.getSheetIndex(sheetName);
//
//            if(index==-1)
//                return "";
//
//            sheet = workbook.getSheetAt(index);
//            row = sheet.getRow(rowNum-1);
//            if(row==null)
//                return "";
//            cell = row.getCell(colNum);
//            if(cell==null)
//                return "";
//
//            if(cell.getCellType()==CellType.STRING)
//                return cell.getStringCellValue();
//            else if(cell.getCellType()==CellType.NUMERIC || cell.getCellType()==CellType.FORMULA ){
//                String cellText  = String.valueOf(cell.getNumericCellValue());
//                return cellText;
//            }else if(cell.getCellType()==CellType.BLANK)
//                return "";
//            else
//                return String.valueOf(cell.getBooleanCellValue());
//        }
//        catch(Exception e){
//
//            e.printStackTrace();
//            return "row "+rowNum+" or column "+colNum +" does not exist  in xls";
//        }
//    }
//
//
//
//
//    /**
//     * Obtiene el valor de una celda en una hoja de Excel por nombre de columna y número de fila.
//     *
//     * @param sheetName El nombre de la hoja de Excel.
//     * @param colName   El nombre de la columna.
//     * @param rowNum    El número de fila.
//     * @return El valor de la celda en formato de cadena.
//     */
//    public String getCellData(String sheetName, String colName, int rowNum){
//        try{
//            if(rowNum <=0)
//                return "";
//
//            int index = workbook.getSheetIndex(sheetName);
//            int col_Num=-1;
//            if(index==-1)
//                return "";
//
//            sheet = workbook.getSheetAt(index);
//            row=sheet.getRow(0);
//            for(int i=0;i<row.getLastCellNum();i++){
//                //System.out.println(row.getCell(i).getStringCellValue().trim());
//                if(row.getCell(i).getStringCellValue().trim().equals(colName.trim()))
//                    col_Num=i;
//            }
//            if(col_Num==-1)
//                return "";
//
//            sheet = workbook.getSheetAt(index);
//            row = sheet.getRow(rowNum);
//            if(row==null)
//                return "";
//            cell = row.getCell(col_Num);
//
//            if(cell==null)
//                return "";
//            //System.out.println(cell.getCellType());
//
//            if(cell.getCellType()== CellType.STRING)
//                return cell.getStringCellValue();
//            else if(cell.getCellType()==CellType.NUMERIC || cell.getCellType()==CellType.FORMULA){
//                double numericValue = cell.getNumericCellValue();
//                // Verifica si el número es un entero
//                if (numericValue == (int) numericValue) {
//                    // Si es un entero, devuelve sin decimales
//                    return String.valueOf((int) numericValue);
//                } else {
//                    // Si no es un entero, devuelve el valor tal cual
//                    return String.valueOf(numericValue);
//                }
//            }else if(cell.getCellType()==CellType.BLANK)
//                return "";
//            else
//                return String.valueOf(cell.getBooleanCellValue());
//
//        }
//        catch(Exception e){
//            e.printStackTrace();
//            return "row "+rowNum+" or column "+colName +" does not exist in xls";
//        }
//    }
//
//    public String getCellData(String sheetName, String colName, String rowNum) {
//        try {
//            if (rowNum == null || rowNum.trim().isEmpty())
//                return "";
//
//            int index = workbook.getSheetIndex(sheetName);
//            int col_Num = -1;
//            if (index == -1)
//                return "";
//
//            sheet = workbook.getSheetAt(index);
//            row = sheet.getRow(0);
//            for (int i = 0; i < row.getLastCellNum(); i++) {
//                if (row.getCell(i).getStringCellValue().trim().equals(colName.trim()))
//                    col_Num = i;
//            }
//            if (col_Num == -1)
//                return "";
//
//            sheet = workbook.getSheetAt(index);
//            int rowIndex = findRowIndexByName(sheet, rowNum);
//            if (rowIndex == -1)
//                return "";
//
//            row = sheet.getRow(rowIndex);
//            if (row == null)
//                return "";
//            cell = row.getCell(col_Num);
//
//            if (cell == null)
//                return "";
//
//            if (cell.getCellType() == CellType.STRING)
//                return cell.getStringCellValue();
//            else if (cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
//                String cellText = String.valueOf(cell.getNumericCellValue());
//                return cellText;
//            } else if (cell.getCellType() == CellType.BLANK)
//                return "";
//            else
//                return String.valueOf(cell.getBooleanCellValue());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Error: " + e.getMessage();
//        }
//    }
//
//
//    /**
//     * Establece el valor de una celda en una hoja de Excel por nombre de columna y número de fila.
//     *
//     * @param sheetName El nombre de la hoja de Excel.
//     * @param colName   El nombre de la columna.
//     * @param rowNum    El número de fila.
//     * @param data      El nuevo valor de la celda.
//     * @return true si la operación tiene éxito, false en caso contrario.
//     */
//    public boolean setCellData(String sheetName,String colName,int rowNum, String data){
//        try{
//
//            fileInput = new FileInputStream(path);
//            workbook = new XSSFWorkbook(fileInput);
//
//            if(rowNum<=0)
//                return false;
//
//            int index = workbook.getSheetIndex(sheetName);
//            int colNum=-1;
//            if(index==-1)
//                return false;
//
//            sheet = workbook.getSheetAt(index);
//            row=sheet.getRow(0);
//            for(int i=0;i<row.getLastCellNum();i++){
//                //System.out.println(row.getCell(i).getStringCellValue().trim());
//                if(row.getCell(i).getStringCellValue().trim().equals(colName))
//                    colNum=i;
//            }
//            if(colNum==-1)
//                return false;
//
//            sheet.autoSizeColumn(colNum);
//            row = sheet.getRow(rowNum);
//            if (row == null)
//                row = sheet.createRow(rowNum);
//
//            cell = row.getCell(colNum);
//            if (cell == null)
//                cell = row.createCell(colNum);
//
//            cell.setCellValue(data);
//            fileOut = new FileOutputStream(path);
//            workbook.write(fileOut);
//            fileOut.close();
//
//        }
//        catch(Exception e){
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }
//
//}
