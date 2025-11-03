package mapfre.general;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * This method is responsible for reading / extracting the data from the excel.
 * 
 * @author CEX
 */
public class ExcelData {
	
	//Vars.
	private static final String EXCEL_TRACES = "TestTraces.xlsx";
	
	//Name of the page of that excel.
	private String hoja;
	
	//Data is loaded in that array.
	private ArrayList<String> datosArray;
	private ArrayList<String> traceArray;
	
	//Constructors.
	public ExcelData(String hoja) {
		this.hoja = hoja;
		datosArray = new ArrayList<>();
		traceArray = new ArrayList<>();
	}
	public ExcelData() {
		this.hoja = "Global";
		datosArray = new ArrayList<>();
		traceArray = new ArrayList<>();
	}
	
	/**
	 * Load excel data in an Array and return it.
	 * 
	 * @return excel page data
	 * @throws Exception
	 * @author CEX
	 */
	@SuppressWarnings("unchecked")
	public List<String> getData(String aplicacion) throws Exception {
		try {

			//Initialize vars.
			//String filePath = ExcelData.getExcelPath(aplicacion.substring(0,5));
			String filePath = ExcelData.getExcelPath(aplicacion);
			FileInputStream fi = new FileInputStream(filePath);
			XSSFWorkbook libro = new XSSFWorkbook(fi);
			XSSFSheet hojaExcel = libro.getSheet(this.hoja);
				try {
					Properties datosLogin = ProjectProperties.getLoginProperties();
					Properties datosConfig = ProjectProperties.getConfigProperties();
					String entorno = datosConfig.getProperty("entorno");
					int numFila = 0;
					switch (entorno){
						case "INT":
							numFila = 1;
							break;
						case "PRE":
							numFila = 2;
							break;
						case "PRO":
							numFila = 3;
							break;
						case "PRD":
							numFila = 4;
							break;
						case "PN":
							numFila = 5;
							break;
				}
				//Row with data.
				XSSFRow filaDatos = hojaExcel.getRow(numFila);
				//Row count.
				int numColumnas = filaDatos.getPhysicalNumberOfCells();
				XSSFCell celdaDato;
				//Loop the rows to load data in the Array.
					for (int i = 0; i < numColumnas; i++) {
						//Get the cell with the data.
						celdaDato = filaDatos.getCell(i);
						//Load that data in the Array.
						//datosArray.add(celdaDato.getStringCellValue());
						datosArray.add(String.valueOf(celdaDato));
					}
					}  finally {
					//Closing the files.
					libro.close();
					fi.close();
					}
      
				} catch (Exception e) {
				System.out.println("EXCEPCION - EXCEL: "+e);
				}

		//Return the Array with data.
		return (List<String>) datosArray.clone();
	}
	
	/**
	 * Get the path of the file respect to the environment.
	 * 
	 * @return {String} The path of the excel file
	 * @throws Exception
	 * @author CEX
	 */
	private static String getExcelPath(String aplicacion) throws Exception {

		//Make name.
		Properties datosConfig = ProjectProperties.getConfigProperties();
		String entorno = datosConfig.getProperty("entorno");

		String fileName = aplicacion+".xlsx";
		//String fileName = "DatosTestCases-"+entorno+"-"+aplicacion+".xlsx";
		
		//Return name.
		//System.out.println("Ruta --> "+ProjectPaths.getResourcePath() + fileName);
		return ProjectPaths.getResourcePath() + fileName;
	}
	
	/**
	 * This method obtain all the traces in the excel.
	 * 
	 * @return A List<String> with the report text
	 * @throws IOException
	 * @author CEX
	 */
	@SuppressWarnings("unchecked")
	public List<String> getTraceData() throws IOException {
		//Initialize vars.
		String filePath = ProjectPaths.getResourcePath() + EXCEL_TRACES;
		FileInputStream fi = new FileInputStream(filePath);
		XSSFWorkbook libro = new XSSFWorkbook(fi);
		XSSFSheet hojaExcel = libro.getSheet(this.hoja);
		
		try {
			//Get the number of files.
			int rowCount = hojaExcel.getLastRowNum()-hojaExcel.getFirstRowNum();
			
			//Loop for rows.
			for(int i = 0; i < rowCount + 1; i++) {
				//Get the first row.
				Row row = hojaExcel.getRow(i);
				
				//Save the value of the first cell.
				traceArray.add(row.getCell(Final.ONE).getStringCellValue());
			}
		}finally {
			//Closing the files.
			libro.close();
			fi.close();
		}
		//Return the Array with data.
		return (List<String>) traceArray.clone();
	}

}