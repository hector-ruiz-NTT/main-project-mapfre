package mapfre.general;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverBBDD {

	public Connection con;
	public Statement stmt;
	public ResultSet rs;

	/**
	 * Constructor for database connection, is necessary to pass the connection url 
	 * 

	 * @throws Exception 
	 */
	public DriverBBDD() throws Exception {
		try {
			String connectionUrl = "jdbc:sqlserver://SRV01063\\GENDESAR;"
				                    + "database=CEPBIOS_PREIMP;"
				                    + "user=USER_CEPBIOS;"
				                    + "password=USER_CEPBIOS;";
					
			con = DriverManager.getConnection(connectionUrl);
			
			//TeckelCom.reportOK("Se conecta a la BBDD...");
			Log.info("Se conecta a la BBDD...");
			
			// Handle any errors that may have occurred.
		} catch (SQLException e) {
			//TeckelCom.reportKO("No se puede conectar a la BBDD...");
			Log.info(e);
		}
	}

	
	/**
	 * Method to close the database query, does not close the connection
	 */
	public void closeQueriesBBDD() {

		// if the result of the query is different from null, we close the result
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				Log.info(e);
			}
		}
		// if the statement is different from null the statement is closed
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				Log.info(e);
			}
		}
	}

	/**
	 * Method to close the database connection
	 */
	public void closeConnectionBBDD() {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				Log.info(e);
			}
		}
	}

	/**
	 * Method to query an SQL statement database
	 * 
	 * @return -returns the resultSet of the statement execution
	 */
	public ResultSet resultQueryBBDD(String sql) {
		try {
			// Create and execute an SQL statement that returns some data.
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			// Handle any errors that may have occurred.
		} catch (SQLException e) {
			Log.info(e);
		}
		return rs;
	}

	/**
	 * Method that returns all rows of a query in map format
	 * 
	 * @param query - database query from which the rows will be obtained
	 * @return - Map - returns a map whose Key is the row number and whose values is
	 *         a String type list, this list contains the values of each column
	 * @throws SQLException
	 */
	public Map<Integer, List<String>> mapRowsBBDD(String query) throws SQLException {
		// method call to get the results of the query
		ResultSet result = resultQueryBBDD(query);
		// metadata is obtained for the treatment of the columns
		ResultSetMetaData columns = result.getMetaData();
		// map with the row -> values of each column in the row
		Map<Integer, List<String>> mapRow = new HashMap<>();
		// row number, starts at 1 as in database
		int numRow = 0;
		// list for header columns
		List<String> valueHeaderRow = new ArrayList<>();
		// Loop to get the values of each row
		while (result.next()) {
			List<String> valueRow = new ArrayList<>();
			// we go through each column to get the value
			for (int i = Final.ONE; i <= columns.getColumnCount(); i++) {
				// if row is 0 , save column header
				if (numRow == 0) {
					valueHeaderRow.add(columns.getColumnName(i));
				}
				String typeColumn = columns.getColumnTypeName(i);
				// with the valueRowType method we get the value of each column
				valueRow.add(valueRowType(result, i, typeColumn));
			}
			if (numRow == 0) {
				mapRow.put(numRow, valueHeaderRow);
				numRow++;
			}
			// save the values of the row
			mapRow.put(numRow, valueRow);
			numRow++;
		}

		// close the query results
		closeQueriesBBDD();
		return mapRow;

	}

	/**
	 * Method to obtain the values of a column passed by query with the name of the
	 * column to be searched, eg: select column1 from TableName
	 * 
	 * @param columnName - name of the column that we want to consult, if we want to
	 *                   limit the search to a specific number we must put "TOP +
	 *                   number to limit + name of the column",eg:("TOP 2 ID")
	 * @param tableName  - name of the table we want to check
	 * @return - returns a list with column values
	 * @throws SQLException
	 */
	public List<String> valuesResultBBDD(String columnName, String tableName) throws SQLException {

		String query = "SELECT " + columnName + " FROM " + tableName;
		// method call to get the results of the query
		ResultSet result = resultQueryBBDD(query);
		// metadata is obtained for the treatment of the columns
		ResultSetMetaData columns = result.getMetaData();
		String value = null;
		List<String> listValue = new ArrayList<>();
		while (result.next()) {
			String nameColumns = columns.getColumnTypeName(Final.ONE);
			value = valueRowType(result, Final.ONE, nameColumns);
			listValue.add(value);
		}
		// close the query results
		closeQueriesBBDD();
		// return list values of column
		return listValue;
	}

// ******************** PRIVATE METHODS**************************//

	/**
	 * Method to obtain the value of the column depending on the type of
	 * data created in BBDD
	 * 
	 * @param result      -query row
	 * @param numCol      - column number to consult
	 * @param nameColumns - data type of column value
	 * @return -returns the value of the column in String format
	 * @throws SQLException
	 */
	private static String valueRowType(ResultSet result, int numCol, String nameColumns) throws SQLException {

		String value;
		String format = nameColumns.toLowerCase();
		
		// switch for data processing
		switch (format) {
		case "int":
				value = String.valueOf(result.getInt(numCol));
				break;
		case "varchar":
				value = result.getString(numCol);
				break;
		case "char":
				value = result.getString(numCol);
				break;
		case "date":
				value = result.getString(numCol);
				break;	
		case "varchar2":
			value = result.getString(numCol);
			break;	
		case "number":
			value = result.getString(numCol);
			break;	
		default:
				value = result.getString(numCol);
				Log.info("No se ha reconocido el tipo: " + format);
				break;
		}

		return value;
	}

	
	
	/**
	 * Method that obtains the values of the first row in a comma separated text
	 * string
	 * 
	 * @param query - database query from which the rows will be obtained
	 * @return - concatenated value result
	 */
	public String obtainsFirstRowValue(String query) {
		String result = "";
		try {
			Map<Integer, List<String>> rowsMap;
			rowsMap = mapRowsBBDD(query);
			List<String> values = rowsMap.get(Final.ONE);
			result = concatResultString(values);
			
		} catch (SQLException e) {
			Log.info(e);
		}

		return result;
	}

	/**
	 * Method to obtain the values of a column that contain a key value
	 * 
	 * @param topNumber - limit of results obtained in the consultation, if you miss
	 *                  0 it will not be limited
	 * @param colName   - column name we want to check
	 * @param tableBBDD - name of the database table that we want to consult
	 * @param valSearch - key value for the search
	 * @return - returns the values obtained that contain the keyword, are
	 *         concatenated and separated by ";"
	 * @throws SQLException
	 */
	public String containsValueColumn(int topNumber, String colName, String tableBBDD, String valSearch) {

		String result = null;
		String column = colName;
		try {
			// if the topNumber is nonzero, we concatenate the reserved word "TOP" plus the
			// number to which we will limit the query, followed by the column name
			if (topNumber > Final.ZERO) {
				column = "TOP " + topNumber + " " + column;
			}
			// We prepare the query with the name of the table and the condition where
			tableBBDD = tableBBDD + " WHERE " + colName + " like ('%" + valSearch + "%')";
			List<String> valCol = valuesResultBBDD(column, tableBBDD);
			// if we get results, we prepare the concatenation of the values
			if (!valCol.isEmpty()) {
				result = concatResultString(valCol);
			}
		} catch (SQLException e) {
			Log.info(e);
		}

		return result;

	}

	/**
	 * Method to concatenate the results separated by ";"
	 * 
	 * @param values - list of values to concatenate the results
	 * @return - returns the string formed separated with ";"
	 * 
	 */
	private static String concatResultString(List<String> values) {
		String result = "";
		int tamSize = values.size();
		// we go through the row list
		for (int i = 0; i < tamSize; i++) {
			String val = values.get(i);
			// if it is an intermediate column, a comma with space is concatenated
			if (i > 0) {
				val = ";" + val;
			}
			// we concatenate the value obtained from each column
			result = result.concat(val);
		}
		return result;
	}
	
	/**
	 * Method to query the number of Partidas
	 * 
	 * @param
	 * @return -returns the resultSet of the statement execution
	 */
	public ResultSet checkNumPartidas(String date1, String date2) {
		String sql = "SELECT COUNT(*) FROM [Partidas] "
						+ "WHERE Fecha >=  CAST('" + date1 + "' AS DATETIME) "
						+ "AND Fecha <=  CAST('" + date2 + "' AS DATETIME) "
						+ "AND Baja = 0;";
		try {
			// Create and execute an SQL statement that returns some data.
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);

		} catch (SQLException e) {
			Log.info(e);
		}
		return rs;
	}

	/**
	 * Method to query to get Saldo
	 * 
	 * @param
	 * @return -returns the resultSet of the statement execution
	 * @throws Exception 
	 */
	public String getSaldoTotal(String numquery, String fechaDesde, String fechaHasta, String emplazamiento, String biocarburante, String regvoluntario, String matprima, String proveedor,String fechaFinTrimestre) throws Exception {
		
		String saldo;
		String sql = "";
		
		switch(numquery)  {
			case "1":
				 sql= Queries.SALDO_BUSQUEDA1
				 	.replace("fechaDesde", fechaDesde)
				 	.replace("fechaHasta", fechaHasta)
				 	.replace("optionEmplazamiento", emplazamiento);
				 break;
			case "2":
				 sql= Queries.SALDO_BUSQUEDA2
				 	.replace("fechaDesde", fechaDesde)
				 	.replace("fechaHasta", fechaHasta)
				 	.replace("optionEmplazamiento", emplazamiento)
				 	.replace("optionBiocarburante", biocarburante);
				  break;
			case "3":
				 sql= Queries.SALDO_BUSQUEDA3
				 	.replace("fechaDesde", fechaDesde)
				 	.replace("fechaHasta", fechaHasta)
				 	.replace("optionEmplazamiento", emplazamiento)
				 	.replace("optionBiocarburante", biocarburante)
				 	.replace("optionRegimen", regvoluntario);
				  break;
			case "4":
				 sql= Queries.SALDO_BUSQUEDA4
					.replace("fechaDesde", fechaDesde)
					.replace("fechaHasta", fechaHasta)
					.replace("optionEmplazamiento", emplazamiento)
					.replace("optionBiocarburante", biocarburante)
					.replace("optionRegimen", regvoluntario)
					.replace("optionMateriaPrima", matprima);
				  break;
			case "5":
				 sql= Queries.SALDO_BUSQUEDA5
					.replace("fechaDesde", fechaDesde)
					.replace("fechaHasta", fechaHasta)
					.replace("optionEmplazamiento", emplazamiento)
					.replace("optionBiocarburante", biocarburante)
					.replace("optionRegimen", regvoluntario)
					.replace("optionMateriaPrima", matprima)
					.replace("optionProveedor", proveedor);
				  break;
			case "6":
				 sql= Queries.SALDO_BUSQUEDA6
					.replace("fechaDesde", fechaDesde)
					.replace("fechaHasta", fechaHasta)
					.replace("optionBiocarburante", biocarburante);
				  break;
			case "7":
				 sql= Queries.SALDO_BUSQUEDA7
				 	.replace("fechaDesde", fechaDesde)
				 	.replace("fechaHasta", fechaHasta)
				 	.replace("optionProveedor", proveedor);
				  break;
			case "8":
				 sql= Queries.SALDO_BUSQUEDA8
				 	.replace("fechaDesde", fechaDesde)
				 	.replace("fechaHasta", fechaHasta)
				 	.replace("optionRegimen", regvoluntario);
				  break;
			case "9":
				 sql= Queries.SALDO_BUSQUEDA9
				 	.replace("fechaDesde", fechaDesde)
				 	.replace("fechaHasta", fechaHasta)
				 	.replace("optionMateriaPrima", matprima);
				  break;
			case "10":
				 sql= Queries.SALDO_BUSQUEDA10
				 	.replace("fechaDesde", fechaDesde)
				 	.replace("fechaHasta", fechaHasta)
				 	.replace("fechaFinTrimestre", fechaFinTrimestre);
				  break;
			case "210":
				sql= Queries.SALDO_BUSQUEDABLOQUEADA10
				 	.replace("fechaDesde", fechaDesde)
				 	.replace("fechaHasta", fechaHasta)
				 	.replace("fechaFinTrimestre", fechaFinTrimestre);
				  break;
			case "209":
				sql= Queries.SALDO_BUSQUEDABLOQUEADA9
						.replace("fechaDesde", fechaDesde)
						.replace("fechaHasta", fechaHasta)
						.replace("optionMateriaPrima", matprima);
				  break;
			case "208":
				sql= Queries.SALDO_BUSQUEDABLOQUEADA8
					.replace("fechaDesde", fechaDesde)
					.replace("fechaHasta", fechaHasta)
					.replace("optionRegimen", regvoluntario);
				  break;
			case "207":
				sql= Queries.SALDO_BUSQUEDABLOQUEADA7
					.replace("fechaDesde", fechaDesde)
					.replace("fechaHasta", fechaHasta)
					.replace("optionProveedor", proveedor);
				  break;
			case "206":
				sql= Queries.SALDO_BUSQUEDABLOQUEADA6
					.replace("fechaDesde", fechaDesde)
					.replace("fechaHasta", fechaHasta)
					.replace("optionBiocarburante", biocarburante);
				  break;
			case "205":
				sql= Queries.SALDO_BUSQUEDABLOQUEADA5
					.replace("fechaDesde", fechaDesde)
					.replace("fechaHasta", fechaHasta)
					.replace("optionEmplazamiento", emplazamiento)
					.replace("optionBiocarburante", biocarburante)
					.replace("optionRegimen", regvoluntario)
					.replace("optionMateriaPrima", matprima)
					.replace("optionProveedor", proveedor);
				  break;
			case "204":
				sql= Queries.SALDO_BUSQUEDABLOQUEADA4
					.replace("fechaDesde", fechaDesde)
					.replace("fechaHasta", fechaHasta)
					.replace("optionEmplazamiento", emplazamiento)
					.replace("optionBiocarburante", biocarburante)
					.replace("optionRegimen", regvoluntario)
					.replace("optionMateriaPrima", matprima);
				  break;
			case "203":
				sql= Queries.SALDO_BUSQUEDABLOQUEADA3
					.replace("fechaDesde", fechaDesde)
					.replace("fechaHasta", fechaHasta)
					.replace("optionEmplazamiento", emplazamiento)
					.replace("optionBiocarburante", biocarburante)
					.replace("optionRegimen", regvoluntario);
				  break;
			case "202":
				sql= Queries.SALDO_BUSQUEDABLOQUEADA2
					 .replace("fechaDesde", fechaDesde)
					 .replace("fechaHasta", fechaHasta)
					 .replace("optionEmplazamiento", emplazamiento)
					 .replace("optionBiocarburante", biocarburante);
				  break;
			case "201":
				sql= Queries.SALDO_BUSQUEDABLOQUEADA1
					.replace("fechaDesde", fechaDesde)
					.replace("fechaHasta", fechaHasta)
					.replace("optionEmplazamiento", emplazamiento);
				  break;
			default:
				//TeckelCom.reportPredictedKO("No query valida");
				Log.info("No query valida");
				break;
		}
		
		// Create and execute an SQL statement that returns some data.
		stmt = con.createStatement();
		rs = stmt.executeQuery(sql);
//		if(obtainsFirstRowValue(sql)==null || obtainsFirstRowValue(sql).equals("")) {
			saldo = obtainsFirstRowValue(sql);
//		}else {
//			saldo = "0,000" ;
//		}
		
//		double doublesaldo = Double.valueOf(saldo);
		
	
		return saldo;
	}
	

}
