package mapfre.general;

public class Queries {

	// Para buscar las partidas creadas
	public static final String PARTIDAS_CREADAS = "SELECT * FROM [Partidas] "
												+ "where NumContrProv = 'PruebaTesting'"; 
	
	// Para buscar por firma
	public static final String PARTIDA_POR_FIRMA = "SELECT * FROM [Partidas] "
												+ "where Firma = ";
		

	// Para ver sus dats de emisiones
	public static final String DATOS_EMISIONES = "SELECT *  FROM [GEIsPartida] "
												+" where idPartida IN ("
													+ "SELECT idPartida  FROM [Partidas]  "
														+ "where NumContrProv = 'PruebaTesting')";

	
	 //Para eliminar las partidas tras realizar las pruebas 
	 public static final String ELIMINAR_PARTIDAS = "DELETE FROM [RBSA_Saldos] "
	 													+ "where idPartida IN ("
	 														+ "SELECT idPartida  FROM [Partidas]  "
	 															+ "where NumContrProv = 'PruebaTesting') "
		 										  + "GO "
		 										  + "DELETE FROM [GEIsPartida] "
		 											    + "where idPartida IN ("
		 											        + "SELECT idPartida  FROM [Partidas]  "
		 											        	+ "where NumContrProv = 'PruebaTesting') "
		 										  + "GO "
		 										  + "DELETE FROM [CEPBIOS_PREIMP].[dbo].[PartidasDocumentos] "
		 											  	+ "where idPartida IN ("
		 											  		+ "SELECT idPartida  FROM [Partidas]  "
		 											  			+ "where NumContrProv = 'PruebaTesting') "
		 										  + "GO "
		 										  + "DELETE FROM [Partidas] "
		 											  	+ "WHERE NumContrProv = 'PruebaTesting'"; 

	 //Para comprobar el saldo Filtro(1)
	 public static final String SALDO_BUSQUEDA1 = "USE CEPBIOS_PREIMP "
										 		+ "DECLARE @fecIni nvarchar(10) = 'fechaDesde' "
										 		+ "DECLARE @fecFin nvarchar(10) = 'fechaHasta' "
										 		+ "DECLARE @idEmplazamiento int= optionEmplazamiento "
										 		+ "Select Case when suma is null then 0 else suma END as SumaTotal FROM ( Select sum (CantidadDisponible) as suma from ( "
										 		+ "SELECT * FROM  "
										 		+ "(SELECT  "
										 		+ "(SELECT TOP (1) SUM(Volumen) FROM  RBSA_Saldos  "
										 		+ "WHERE RBSA_Saldos.IdPartida = p.IdPartida  "
										 		+ "and (RBSA_Saldos.IdEmplazamiento = @idEmplazamiento OR "
										 		+ "@idEmplazamiento IS NULL)  "
										 		+ " GROUP BY RBSA_Saldos.Fecha  "
										 		+ "ORDER BY RBSA_Saldos.Fecha DESC) AS CantidadDisponible  "
										 		+ "FROM Partidas p "
										 		+ "INNER JOIN GEIsPartida GS ON GS.idPartida = p.IdPartida  "
										 		+ "INNER JOIN Carburantes ON p.IdBiocarburante = Carburantes.IdCarburante  "
										 		+ "INNER JOIN Emplazamientos ON Emplazamientos.IdEmplazamiento = p.IdEmplazamiento  "
										 		+ "INNER JOIN Proveedores ON Proveedores.IdProveedor = p.IdProveedor "
										 		+ "INNER JOIN TipoRegimenVoluntario ON TipoRegimenVoluntario.TipoRegimen = p.CodCLHRegimenVoluntario  "
										 		+ "INNER JOIN Paises pa on p.IdPaisOrigenMateriaPrima=pa.IdPais "
										 		+ "inner join Paises ON paises.IdPais = p.IdPaisOrigenMateriaPrima  "
										 		+ "inner join MateriasPrimas on MateriasPrimas.IdMateriaPrima = p.IdMateriaPrima  "
										 		+ "WHERE p.IdPartida NOT IN (SELECT idPartida FROM [dbo].[BloqueoPartidas] WHERE Estado = 1) AND (p.IdProveedor = NULL  OR NULL  IS null) \r\n"
										 		+ "AND (p.CodCLHRegimenVoluntario = 'NULL' OR 'NULL' = 'null') AND (p.IdMateriaPrima = 'NULL' OR 'NULL' = 'null') AND (p.IdBiocarburante = NULL  OR NULL  IS null) "
										 		+ "AND ( ((SELECT TOP (1) RBSA_Saldos.Volumen         "
										 		+ "FROM RBSA_Saldos        "
										 		+ "WHERE (RBSA_Saldos.IdPartida = p.IdPartida) and RBSA_Saldos.IdEmplazamiento = 34 ORDER BY RBSA_Saldos.Fecha DESC )  > 0  "
										 		+ "OR 34  IS null)  OR  p.IdEmplazamiento =  34 ) AND ((( p.IdEmplazamiento != 34 )AND (p.Fecha <= @fecFin) "
										 		+ "AND (p.Fecha >= @fecIni))  "
										 		+ "OR (( p.IdEmplazamiento = 34)AND (p.Fecha <= @fecFin)  "
										 		+ "AND (p.Fecha >= @fecIni) AND EXISTS (Select * from RBSA_SALDOS rbs WHERE Fecha >= @fecIni and Fecha <= @fecFin and rbs.IdPartida=p.IdPartida))) AND p.Baja = 0) a  "
										 		+ " ) c ) sumaa";	 
	
	 //Para comprobar el saldo Filtro(1)
	 public static final String SALDO_BUSQUEDA2 = "USE CEPBIOS_PREIMP "
											 	+ "DECLARE @fecIni nvarchar(10) = 'fechaDesde' "
										 		+ "DECLARE @fecFin nvarchar(10) = 'fechaHasta' "
										 		+ "DECLARE @idEmplazamiento int= optionEmplazamiento "
										 		+ "DECLARE @idBiocarburante int= optionBiocarburante " 
										 		+ "Select Case when suma is null then 0 else suma END as SumaTotal FROM ( Select sum (CantidadDisponible) as suma from ( "
										 		+ "SELECT * FROM  "
										 		+ "(SELECT  "
										 		+ "(SELECT TOP (1) SUM(Volumen) FROM  RBSA_Saldos  "
										 		+ "WHERE RBSA_Saldos.IdPartida = p.IdPartida  "
										 		+ "and (RBSA_Saldos.IdEmplazamiento = @idEmplazamiento OR \r\n"
										 		+ "@idEmplazamiento IS NULL) \r\n"
										 		+ " GROUP BY RBSA_Saldos.Fecha \r\n"
										 		+ "ORDER BY RBSA_Saldos.Fecha DESC) AS CantidadDisponible \r\n"
										 		+ "FROM Partidas p\r\n"
										 		+ "INNER JOIN GEIsPartida GS ON GS.idPartida = p.IdPartida \r\n"
										 		+ "INNER JOIN Carburantes ON p.IdBiocarburante = Carburantes.IdCarburante \r\n"
										 		+ "INNER JOIN Emplazamientos ON Emplazamientos.IdEmplazamiento = p.IdEmplazamiento \r\n"
										 		+ "INNER JOIN Proveedores ON Proveedores.IdProveedor = p.IdProveedor \r\n"
										 		+ "INNER JOIN TipoRegimenVoluntario ON TipoRegimenVoluntario.TipoRegimen = p.CodCLHRegimenVoluntario \r\n"
										 		+ "INNER JOIN Paises pa on p.IdPaisOrigenMateriaPrima=pa.IdPais\r\n"
										 		+ "inner join Paises ON paises.IdPais = p.IdPaisOrigenMateriaPrima \r\n"
										 		+ "inner join MateriasPrimas on MateriasPrimas.IdMateriaPrima = p.IdMateriaPrima \r\n"
										 		+ "WHERE p.IdPartida NOT IN (SELECT idPartida FROM [dbo].[BloqueoPartidas] WHERE Estado = 1) \r\n"
										 		+ "AND (p.IdBiocarburante = @idBiocarburante)  AND ( ((SELECT TOP (1) RBSA_Saldos.Volumen         FROM RBSA_Saldos         WHERE (RBSA_Saldos.IdPartida = p.IdPartida) and RBSA_Saldos.IdEmplazamiento = 34 ORDER BY RBSA_Saldos.Fecha DESC )  > 0  OR 34  IS null)  OR  p.IdEmplazamiento =  34 ) AND ((( p.IdEmplazamiento != 34 )AND (p.Fecha <= @fecFin) \r\n"
										 		+ "AND (p.Fecha >= @fecIni)) \r\n"
										 		+ "OR (( p.IdEmplazamiento = 34)AND (p.Fecha <= @fecFin) \r\n"
										 		+ "AND (p.Fecha >= @fecIni) AND EXISTS (Select * from RBSA_SALDOS rbs WHERE Fecha >= @fecIni and Fecha <= @fecFin and rbs.IdPartida=p.IdPartida))) AND p.Baja = 0) a \r\n"
										 		+ " ) c ) sumaa";
	 
	 //Para comprobar el saldo Filtro(1)
	 public static final String SALDO_BUSQUEDA3 = "USE CEPBIOS_PREIMP\r\n"
										 		+ "\r\n"
										 		+ "DECLARE @fecIni nvarchar(10) = 'fechaDesde' "
										 		+ "DECLARE @fecFin nvarchar(10) = 'fechaHasta' "
										 		+ "DECLARE @idEmplazamiento int= optionEmplazamiento "
										 		+ "DECLARE @idBiocarburante int= optionBiocarburante " 
										 		+ "DECLARE @idRegimen nvarchar(10) = 'optionRegimen' "
										 		+ "\r\n"
										 		+ "\r\n"
										 		+ "\r\n"
										 		+ "Select Case when suma is null then 0 else suma END as SumaTotal FROM ( Select sum (CantidadDisponible) as suma from (\r\n"
										 		+ "SELECT * FROM \r\n"
										 		+ "(SELECT \r\n"
										 		+ "(SELECT TOP (1) SUM(Volumen) FROM  RBSA_Saldos \r\n"
										 		+ "WHERE RBSA_Saldos.IdPartida = p.IdPartida \r\n"
										 		+ "and (RBSA_Saldos.IdEmplazamiento = @idEmplazamiento OR \r\n"
										 		+ "@idEmplazamiento IS NULL) \r\n"
										 		+ " GROUP BY RBSA_Saldos.Fecha \r\n"
										 		+ "ORDER BY RBSA_Saldos.Fecha DESC) AS CantidadDisponible \r\n"
										 		+ "FROM Partidas p\r\n"
										 		+ "INNER JOIN GEIsPartida GS ON GS.idPartida = p.IdPartida \r\n"
										 		+ "INNER JOIN Carburantes ON p.IdBiocarburante = Carburantes.IdCarburante \r\n"
										 		+ "INNER JOIN Emplazamientos ON Emplazamientos.IdEmplazamiento = p.IdEmplazamiento \r\n"
										 		+ "INNER JOIN Proveedores ON Proveedores.IdProveedor = p.IdProveedor \r\n"
										 		+ "INNER JOIN TipoRegimenVoluntario ON TipoRegimenVoluntario.TipoRegimen = p.CodCLHRegimenVoluntario \r\n"
										 		+ "INNER JOIN Paises pa on p.IdPaisOrigenMateriaPrima=pa.IdPais\r\n"
										 		+ "inner join Paises ON paises.IdPais = p.IdPaisOrigenMateriaPrima \r\n"
										 		+ "inner join MateriasPrimas on MateriasPrimas.IdMateriaPrima = p.IdMateriaPrima \r\n"
										 		+ "WHERE p.IdPartida NOT IN (SELECT idPartida FROM [dbo].[BloqueoPartidas] WHERE Estado = 1) \r\n"
										 		+ "AND (p.IdBiocarburante = @idBiocarburante)  \r\n"
										 		+ "AND (p.CodCLHRegimenVoluntario = @idRegimen) \r\n"
										 		+ "AND ( ((SELECT TOP (1) RBSA_Saldos.Volumen         FROM RBSA_Saldos         WHERE (RBSA_Saldos.IdPartida = p.IdPartida) and RBSA_Saldos.IdEmplazamiento = 34 ORDER BY RBSA_Saldos.Fecha DESC )  > 0  OR 34  IS null)  OR  p.IdEmplazamiento =  34 ) AND ((( p.IdEmplazamiento != 34 )AND (p.Fecha <= @fecFin) \r\n"
										 		+ "AND (p.Fecha >= @fecIni)) \r\n"
										 		+ "OR (( p.IdEmplazamiento = 34)AND (p.Fecha <= @fecFin) \r\n"
										 		+ "AND (p.Fecha >= @fecIni) AND EXISTS (Select * from RBSA_SALDOS rbs WHERE Fecha >= @fecIni and Fecha <= @fecFin and rbs.IdPartida=p.IdPartida))) AND p.Baja = 0) a \r\n"
										 		+ " ) c "
										 		+ ") sumaa";
	 
	 //Para comprobar el saldo Filtro(1)
	 public static final String SALDO_BUSQUEDA4 = "USE CEPBIOS_PREIMP\r\n"
										 		+ "\r\n"
										 		+ "DECLARE @fecIni nvarchar(10) = 'fechaDesde' "
										 		+ "DECLARE @fecFin nvarchar(10) = 'fechaHasta' "
										 		+ "DECLARE @idEmplazamiento int= optionEmplazamiento "
										 		+ "DECLARE @idBiocarburante int= optionBiocarburante " 
										 		+ "DECLARE @idRegimen nvarchar(10) = 'optionRegimen' "
										 		+ "DECLARE @idMateriaPrima int= optionMateriaPrima "
										 		+ "\r\n"
										 		+ "\r\n"
										 		+ "\r\n"
										 		+ "\r\n"
										 		+ "Select Case when suma is null then 0 else suma END as SumaTotal FROM ( Select sum (CantidadDisponible) as suma from (\r\n"
										 		+ "SELECT * FROM \r\n"
										 		+ "(SELECT \r\n"
										 		+ "(SELECT TOP (1) SUM(Volumen) FROM  RBSA_Saldos \r\n"
										 		+ "WHERE RBSA_Saldos.IdPartida = p.IdPartida \r\n"
										 		+ "and (RBSA_Saldos.IdEmplazamiento = @idEmplazamiento OR \r\n"
										 		+ "@idEmplazamiento IS NULL) \r\n"
										 		+ " GROUP BY RBSA_Saldos.Fecha \r\n"
										 		+ "ORDER BY RBSA_Saldos.Fecha DESC) AS CantidadDisponible \r\n"
										 		+ "FROM Partidas p\r\n"
										 		+ "INNER JOIN GEIsPartida GS ON GS.idPartida = p.IdPartida \r\n"
										 		+ "INNER JOIN Carburantes ON p.IdBiocarburante = Carburantes.IdCarburante \r\n"
										 		+ "INNER JOIN Emplazamientos ON Emplazamientos.IdEmplazamiento = p.IdEmplazamiento \r\n"
										 		+ "INNER JOIN Proveedores ON Proveedores.IdProveedor = p.IdProveedor \r\n"
										 		+ "INNER JOIN TipoRegimenVoluntario ON TipoRegimenVoluntario.TipoRegimen = p.CodCLHRegimenVoluntario \r\n"
										 		+ "INNER JOIN Paises pa on p.IdPaisOrigenMateriaPrima=pa.IdPais\r\n"
										 		+ "inner join Paises ON paises.IdPais = p.IdPaisOrigenMateriaPrima \r\n"
										 		+ "inner join MateriasPrimas on MateriasPrimas.IdMateriaPrima = p.IdMateriaPrima \r\n"
										 		+ "WHERE p.IdPartida NOT IN (SELECT idPartida FROM [dbo].[BloqueoPartidas] WHERE Estado = 1) \r\n"
										 		+ "AND (p.IdBiocarburante = @idBiocarburante)  \r\n"
										 		+ "AND (p.CodCLHRegimenVoluntario = @idRegimen) AND (p.IdMateriaPrima = @idMateriaPrima)\r\n"
										 		+ "AND ( ((SELECT TOP (1) RBSA_Saldos.Volumen         FROM RBSA_Saldos         WHERE (RBSA_Saldos.IdPartida = p.IdPartida) and RBSA_Saldos.IdEmplazamiento = 34 ORDER BY RBSA_Saldos.Fecha DESC )  > 0  OR 34  IS null)  OR  p.IdEmplazamiento =  34 ) AND ((( p.IdEmplazamiento != 34 )AND (p.Fecha <= @fecFin) \r\n"
										 		+ "AND (p.Fecha >= @fecIni)) \r\n"
										 		+ "OR (( p.IdEmplazamiento = 34)AND (p.Fecha <= @fecFin) \r\n"
										 		+ "AND (p.Fecha >= @fecIni) AND EXISTS (Select * from RBSA_SALDOS rbs WHERE Fecha >= @fecIni and Fecha <= @fecFin and rbs.IdPartida=p.IdPartida))) AND p.Baja = 0) a \r\n"
										 		+ " ) c "
										 		+ ")sumaa";
	 
	 //Para comprobar el saldo Filtro(1)
	 public static final String SALDO_BUSQUEDA5 = "USE CEPBIOS_PREIMP \r\n"
										 		+ "\r\n"
										 		+ "DECLARE @fecIni nvarchar(10) = 'fechaDesde' "
										 		+ "DECLARE @fecFin nvarchar(10) = 'fechaHasta' "
										 		+ "DECLARE @idEmplazamiento int= optionEmplazamiento "
										 		+ "DECLARE @idBiocarburante int= optionBiocarburante " 
										 		+ "DECLARE @idRegimen nvarchar(10) = 'optionRegimen' "
										 		+ "DECLARE @idMateriaPrima int= optionMateriaPrima "
										 		+ "DECLARE @idProveedor int= optionProveedor "
										 		+ "\r\n"
										 		+ "Select Case when suma is null then 0 else suma END as SumaTotal FROM ( Select sum (CantidadDisponible) as suma from (\r\n"
										 		+ "SELECT * FROM \r\n"
										 		+ "(SELECT \r\n"
										 		+ "(SELECT TOP (1) SUM(Volumen) FROM  RBSA_Saldos \r\n"
										 		+ "WHERE RBSA_Saldos.IdPartida = p.IdPartida \r\n"
										 		+ "and (RBSA_Saldos.IdEmplazamiento = @idEmplazamiento OR \r\n"
										 		+ "@idEmplazamiento IS NULL) \r\n"
										 		+ " GROUP BY RBSA_Saldos.Fecha \r\n"
										 		+ "ORDER BY RBSA_Saldos.Fecha DESC) AS CantidadDisponible \r\n"
										 		+ "FROM Partidas p\r\n"
										 		+ "INNER JOIN GEIsPartida GS ON GS.idPartida = p.IdPartida \r\n"
										 		+ "INNER JOIN Carburantes ON p.IdBiocarburante = Carburantes.IdCarburante \r\n"
										 		+ "INNER JOIN Emplazamientos ON Emplazamientos.IdEmplazamiento = p.IdEmplazamiento \r\n"
										 		+ "INNER JOIN Proveedores ON Proveedores.IdProveedor = p.IdProveedor \r\n"
										 		+ "INNER JOIN TipoRegimenVoluntario ON TipoRegimenVoluntario.TipoRegimen = p.CodCLHRegimenVoluntario \r\n"
										 		+ "INNER JOIN Paises pa on p.IdPaisOrigenMateriaPrima=pa.IdPais\r\n"
										 		+ "inner join Paises ON paises.IdPais = p.IdPaisOrigenMateriaPrima \r\n"
										 		+ "inner join MateriasPrimas on MateriasPrimas.IdMateriaPrima = p.IdMateriaPrima \r\n"
										 		+ "WHERE p.IdPartida NOT IN (SELECT idPartida FROM [dbo].[BloqueoPartidas] WHERE Estado = @idEmplazamiento) \r\n"
										 		+ "AND (p.IdBiocarburante = @idBiocarburante)  AND (p.IdProveedor = @idProveedor) \r\n"
										 		+ "AND (p.CodCLHRegimenVoluntario = @idRegimen) AND (p.IdMateriaPrima = @idMateriaPrima)\r\n"
										 		+ "AND ( ((SELECT TOP (1) RBSA_Saldos.Volumen         \r\n"
										 		+ "FROM RBSA_Saldos         \r\n"
										 		+ "WHERE (RBSA_Saldos.IdPartida = p.IdPartida) and RBSA_Saldos.IdEmplazamiento = @idEmplazamiento ORDER BY RBSA_Saldos.Fecha DESC )  > 0  OR @idEmplazamiento  IS null)  OR  p.IdEmplazamiento =  @idEmplazamiento ) AND ((( p.IdEmplazamiento != @idEmplazamiento )AND (p.Fecha <= @fecFin) \r\n"
										 		+ "AND (p.Fecha >= @fecIni)) \r\n"
										 		+ "OR (( p.IdEmplazamiento = @idEmplazamiento)AND (p.Fecha <= @fecFin) \r\n"
										 		+ "AND (p.Fecha >= @fecIni) AND EXISTS (Select * from RBSA_SALDOS rbs WHERE Fecha >= @fecIni and Fecha <= @fecFin and rbs.IdPartida=p.IdPartida))) AND p.Baja = 0) a \r\n"
										 		+ " ) c ) sumaa";
	 
	 //Para comprobar el saldo Filtro(1)
	 public static final String SALDO_BUSQUEDA6 = "USE CEPBIOS_PREIMP\r\n"
										 		+ "\r\n"
										 		+ "DECLARE @fecIni nvarchar(10) = 'fechaDesde' "
										 		+ "DECLARE @fecFin nvarchar(10) = 'fechaHasta' "
										 		+ "DECLARE @idBiocarburante int= optionBiocarburante " 
										 		+ "\r\n"
										 		+ "Select Case when suma is null then 0 else suma END as SumaTotal FROM ( Select sum (CantidadDisponible) as suma from(\r\n"
										 		+ "SELECT * FROM \r\n"
										 		+ "(SELECT  \r\n"
										 		+ "(SELECT TOP (1) SUM(Volumen) FROM  RBSA_Saldos \r\n"
										 		+ "WHERE RBSA_Saldos.IdPartida = p.IdPartida \r\n"
										 		+ "and (RBSA_Saldos.IdEmplazamiento = NULL OR \r\n"
										 		+ "NULL IS NULL) \r\n"
										 		+ " GROUP BY RBSA_Saldos.Fecha \r\n"
										 		+ "ORDER BY RBSA_Saldos.Fecha DESC) AS CantidadDisponible \r\n"
										 		+ "FROM Partidas p\r\n"
										 		+ "INNER JOIN GEIsPartida GS ON GS.idPartida = p.IdPartida \r\n"
										 		+ "INNER JOIN Carburantes ON p.IdBiocarburante = Carburantes.IdCarburante \r\n"
										 		+ "INNER JOIN Emplazamientos ON Emplazamientos.IdEmplazamiento = p.IdEmplazamiento \r\n"
										 		+ "INNER JOIN Proveedores ON Proveedores.IdProveedor = p.IdProveedor \r\n"
										 		+ "INNER JOIN TipoRegimenVoluntario ON TipoRegimenVoluntario.TipoRegimen = p.CodCLHRegimenVoluntario \r\n"
										 		+ "INNER JOIN Paises pa on p.IdPaisOrigenMateriaPrima=pa.IdPais \r\n"
										 		+ "inner join Paises ON paises.IdPais = p.IdPaisOrigenMateriaPrima \r\n"
										 		+ "inner join MateriasPrimas on MateriasPrimas.IdMateriaPrima = p.IdMateriaPrima \r\n"
										 		+ "WHERE p.IdPartida NOT IN (SELECT idPartida FROM [dbo].[BloqueoPartidas] WHERE Estado = 1) \r\n"
										 		+ "AND (p.IdBiocarburante = 8  OR 8  IS null) AND ( ((SELECT TOP (1) RBSA_Saldos.Volumen         FROM RBSA_Saldos         WHERE (RBSA_Saldos.IdPartida = p.IdPartida) and RBSA_Saldos.IdEmplazamiento = NULL ORDER BY RBSA_Saldos.Fecha DESC )  > 0  OR NULL  IS null)  OR  p.IdEmplazamiento =  NULL ) AND ((( p.IdEmplazamiento != 34 )AND (p.Fecha <= @fecFin) \r\n"
										 		+ "AND (p.Fecha >= @fecIni)) \r\n"
										 		+ "OR (( p.IdEmplazamiento = 34)AND (p.Fecha <= @fecFin) \r\n"
										 		+ "AND (p.Fecha >= @fecIni) AND EXISTS (Select * from RBSA_SALDOS rbs WHERE Fecha >= @fecIni and Fecha <= @fecFin and rbs.IdPartida=p.IdPartida))) AND p.Baja = 0) a \r\n"
										 		+ ")c "
										 		+ ") sumaa";
	 
	 //Para comprobar el saldo Filtro(1)
	 public static final String SALDO_BUSQUEDA7 = "USE CEPBIOS_PREIMP\r\n"
										 		+ "\r\n"
										 		+ "DECLARE @fecIni nvarchar(10) = 'fechaDesde' "
										 		+ "DECLARE @fecFin nvarchar(10) = 'fechaHasta' "
										 		+ "DECLARE @idProveedor int= optionProveedor "
										 		+ "\r\n"
										 		+ "\r\n"
										 		+ "Select Case when suma is null then 0 else suma END as SumaTotal FROM ( Select sum (CantidadDisponible) as suma from(\r\n"
										 		+ "SELECT * FROM \r\n"
										 		+ "(SELECT  \r\n"
										 		+ "(SELECT TOP (1) SUM(Volumen) FROM  RBSA_Saldos \r\n"
										 		+ "WHERE RBSA_Saldos.IdPartida = p.IdPartida \r\n"
										 		+ "and (RBSA_Saldos.IdEmplazamiento = NULL OR \r\n"
										 		+ "NULL IS NULL) \r\n"
										 		+ " GROUP BY RBSA_Saldos.Fecha \r\n"
										 		+ "ORDER BY RBSA_Saldos.Fecha DESC) AS CantidadDisponible \r\n"
										 		+ "FROM Partidas p \r\n"
										 		+ "INNER JOIN GEIsPartida GS ON GS.idPartida = p.IdPartida \r\n"
										 		+ "INNER JOIN Carburantes ON p.IdBiocarburante = Carburantes.IdCarburante \r\n"
										 		+ "INNER JOIN Emplazamientos ON Emplazamientos.IdEmplazamiento = p.IdEmplazamiento \r\n"
										 		+ "INNER JOIN Proveedores ON Proveedores.IdProveedor = p.IdProveedor \r\n"
										 		+ "INNER JOIN TipoRegimenVoluntario ON TipoRegimenVoluntario.TipoRegimen = p.CodCLHRegimenVoluntario \r\n"
										 		+ "INNER JOIN Paises pa on p.IdPaisOrigenMateriaPrima=pa.IdPais \r\n"
										 		+ "inner join Paises ON paises.IdPais = p.IdPaisOrigenMateriaPrima \r\n"
										 		+ "inner join MateriasPrimas on MateriasPrimas.IdMateriaPrima = p.IdMateriaPrima \r\n"
										 		+ "WHERE p.IdPartida NOT IN (SELECT idPartida FROM [dbo].[BloqueoPartidas] WHERE Estado = 1) \r\n"
										 		+ "AND (p.IdProveedor = @idProveedor)  AND ( ((SELECT TOP (1) RBSA_Saldos.Volumen         FROM RBSA_Saldos         WHERE (RBSA_Saldos.IdPartida = p.IdPartida) and RBSA_Saldos.IdEmplazamiento = NULL ORDER BY RBSA_Saldos.Fecha DESC )  > 0  OR NULL  IS null)  OR  p.IdEmplazamiento =  NULL ) AND ((( p.IdEmplazamiento != 34 )AND (p.Fecha <= @fecFin) \r\n"
										 		+ "AND (p.Fecha >= @fecIni)) \r\n"
										 		+ "OR (( p.IdEmplazamiento = 34)AND (p.Fecha <= @fecFin) \r\n"
										 		+ "AND (p.Fecha >= @fecIni) AND EXISTS (Select * from RBSA_SALDOS rbs WHERE Fecha >= @fecIni and Fecha <= @fecFin and rbs.IdPartida=p.IdPartida))) AND p.Baja = 0) a \r\n"
										 		+ ")c ) sumaa";
	 
	 //Para comprobar el saldo Filtro(1)
	 public static final String SALDO_BUSQUEDA8 = "USE CEPBIOS_PREIMP\r\n"
										 		+ "\r\n"
										 		+ "DECLARE @fecIni nvarchar(10) = 'fechaDesde' "
										 		+ "DECLARE @fecFin nvarchar(10) = 'fechaHasta' "
										 		+ "DECLARE @idRegimen nvarchar(10) = 'optionRegimen' "
										 		+ "\r\n"
										 		+ "\r\n"
										 		+ "Select Case when suma is null then 0 else suma END as SumaTotal FROM ( Select sum (CantidadDisponible) as suma from(\r\n"
										 		+ "SELECT * FROM \r\n"
										 		+ "(SELECT  \r\n"
										 		+ "(SELECT TOP (1) SUM(Volumen) FROM  RBSA_Saldos \r\n"
										 		+ "WHERE RBSA_Saldos.IdPartida = p.IdPartida \r\n"
										 		+ "and (RBSA_Saldos.IdEmplazamiento = NULL OR \r\n"
										 		+ "NULL IS NULL) \r\n"
										 		+ " GROUP BY RBSA_Saldos.Fecha \r\n"
										 		+ "ORDER BY RBSA_Saldos.Fecha DESC) AS CantidadDisponible \r\n"
										 		+ "FROM Partidas p\r\n"
										 		+ "INNER JOIN GEIsPartida GS ON GS.idPartida = p.IdPartida \r\n"
										 		+ "INNER JOIN Carburantes ON p.IdBiocarburante = Carburantes.IdCarburante \r\n"
										 		+ "INNER JOIN Emplazamientos ON Emplazamientos.IdEmplazamiento = p.IdEmplazamiento \r\n"
										 		+ "INNER JOIN Proveedores ON Proveedores.IdProveedor = p.IdProveedor \r\n"
										 		+ "INNER JOIN TipoRegimenVoluntario ON TipoRegimenVoluntario.TipoRegimen = p.CodCLHRegimenVoluntario \r\n"
										 		+ "INNER JOIN Paises pa on p.IdPaisOrigenMateriaPrima=pa.IdPais \r\n"
										 		+ "inner join Paises ON paises.IdPais = p.IdPaisOrigenMateriaPrima \r\n"
										 		+ "inner join MateriasPrimas on MateriasPrimas.IdMateriaPrima = p.IdMateriaPrima \r\n"
										 		+ "WHERE p.IdPartida NOT IN (SELECT idPartida FROM [dbo].[BloqueoPartidas] WHERE Estado = 1) \r\n"
										 		+ "AND (p.CodCLHRegimenVoluntario = @idRegimen)   AND ( ((SELECT TOP (1) RBSA_Saldos.Volumen         FROM RBSA_Saldos         WHERE (RBSA_Saldos.IdPartida = p.IdPartida) and RBSA_Saldos.IdEmplazamiento = NULL ORDER BY RBSA_Saldos.Fecha DESC )  > 0  OR NULL  IS null)  OR  p.IdEmplazamiento =  NULL ) AND ((( p.IdEmplazamiento != 34 )AND (p.Fecha <= @fecFin) \r\n"
										 		+ "AND (p.Fecha >= @fecIni)) \r\n"
										 		+ "OR (( p.IdEmplazamiento = 34)AND (p.Fecha <= @fecFin) \r\n"
										 		+ "AND (p.Fecha >= @fecIni) AND EXISTS (Select * from RBSA_SALDOS rbs WHERE Fecha >= @fecIni and Fecha <= @fecFin and rbs.IdPartida=p.IdPartida))) AND p.Baja = 0) a \r\n"
										 		+ ")c "
										 		+ ") sumaa";
	 
	 //Para comprobar el saldo Filtro(1)
	 public static final String SALDO_BUSQUEDA9 = "USE CEPBIOS_PREIMP"
											 	+ " DECLARE @fecIni nvarchar(10) = 'fechaDesde' "
										 		+ "DECLARE @fecFin nvarchar(10) = 'fechaHasta' "
										 		+ "DECLARE @idMateriaPrima int = optionMateriaPrima "
										 		+ "\r\n"
										 		+ "\r\n"
										 		+ "Select Case when suma is null then 0 else suma END as SumaTotal FROM ( Select sum (CantidadDisponible) as suma from(\r\n"
										 		+ "SELECT * FROM \r\n"
										 		+ "(SELECT  \r\n"
										 		+ "(SELECT TOP (1) SUM(Volumen) FROM  RBSA_Saldos \r\n"
										 		+ "WHERE RBSA_Saldos.IdPartida = p.IdPartida \r\n"
										 		+ "and (RBSA_Saldos.IdEmplazamiento = NULL OR \r\n"
										 		+ "NULL IS NULL) \r\n"
										 		+ " GROUP BY RBSA_Saldos.Fecha \r\n"
										 		+ "ORDER BY RBSA_Saldos.Fecha DESC) AS CantidadDisponible \r\n"
										 		+ "FROM Partidas p\r\n"
										 		+ "INNER JOIN GEIsPartida GS ON GS.idPartida = p.IdPartida \r\n"
										 		+ "INNER JOIN Carburantes ON p.IdBiocarburante = Carburantes.IdCarburante \r\n"
										 		+ "INNER JOIN Emplazamientos ON Emplazamientos.IdEmplazamiento = p.IdEmplazamiento \r\n"
										 		+ "INNER JOIN Proveedores ON Proveedores.IdProveedor = p.IdProveedor \r\n"
										 		+ "INNER JOIN TipoRegimenVoluntario ON TipoRegimenVoluntario.TipoRegimen = p.CodCLHRegimenVoluntario \r\n"
										 		+ "INNER JOIN Paises pa on p.IdPaisOrigenMateriaPrima=pa.IdPais \r\n"
										 		+ "inner join Paises ON paises.IdPais = p.IdPaisOrigenMateriaPrima \r\n"
										 		+ "inner join MateriasPrimas on MateriasPrimas.IdMateriaPrima = p.IdMateriaPrima \r\n"
										 		+ "WHERE p.IdPartida NOT IN (SELECT idPartida FROM [dbo].[BloqueoPartidas] WHERE Estado = 1) \r\n"
										 		+ "AND (p.IdMateriaPrima = '17')   AND ( ((SELECT TOP (1) RBSA_Saldos.Volumen         FROM RBSA_Saldos         WHERE (RBSA_Saldos.IdPartida = p.IdPartida) and RBSA_Saldos.IdEmplazamiento = NULL ORDER BY RBSA_Saldos.Fecha DESC )  > 0  OR NULL  IS null)  OR  p.IdEmplazamiento =  NULL ) AND ((( p.IdEmplazamiento != 34 )AND (p.Fecha <= @fecFin) \r\n"
										 		+ "AND (p.Fecha >= @fecIni)) \r\n"
										 		+ "OR (( p.IdEmplazamiento = 34)AND (p.Fecha <= @fecFin) \r\n"
										 		+ "AND (p.Fecha >= @fecIni) AND EXISTS (Select * from RBSA_SALDOS rbs WHERE Fecha >= @fecIni and Fecha <= @fecFin and rbs.IdPartida=p.IdPartida))) AND p.Baja = 0) a \r\n"
										 		+ ")c\r\n"
										 		+ " ) sumaa";
	 
	 //Para comprobar el saldo Filtro(1)
	 public static final String SALDO_BUSQUEDA10 = "USE CEPBIOS_PREIMP "
										 		+ " "
										 		+ "DECLARE @fecIni nvarchar(10) = 'fechaDesde' "
										 		+ "DECLARE @fecFin nvarchar(10) = 'fechaHasta' "
										 		+ "DECLARE @fecFinTrimester nvarchar(10) = 'fechaFinTrimestre' "
										 		+ " "
										 		+ "Select Case when suma is null then 0 else suma END as SumaTotal FROM ( SELECT sum (C.CantidadDisponible) as suma \r\n"
										 		+ "FROM "
										 		+ "( "
										 		+ " "
										 		+ "SELECT * FROM  "
										 		+ "(SELECT  "
										 		+ " "
										 		+ "(SELECT TOP (1) SUM(Volumen) FROM  RBSA_Saldos  "
										 		+ "WHERE RBSA_Saldos.IdPartida = p.IdPartida  "
										 		+ "and (RBSA_Saldos.IdEmplazamiento = NULL OR  "
										 		+ "NULL IS NULL)  "
										 		+ " GROUP BY RBSA_Saldos.Fecha  "
										 		+ "ORDER BY RBSA_Saldos.Fecha DESC) AS CantidadDisponible  "
										 		+ "FROM Partidas p "
										 		+ "INNER JOIN GEIsPartida GS ON GS.idPartida = p.IdPartida  "
										 		+ "INNER JOIN Carburantes ON p.IdBiocarburante = Carburantes.IdCarburante  "
										 		+ "INNER JOIN Emplazamientos ON Emplazamientos.IdEmplazamiento = p.IdEmplazamiento  "
										 		+ "INNER JOIN Proveedores ON Proveedores.IdProveedor = p.IdProveedor  "
										 		+ "INNER JOIN TipoRegimenVoluntario ON TipoRegimenVoluntario.TipoRegimen = p.CodCLHRegimenVoluntario  "
										 		+ "INNER JOIN Paises pa on p.IdPaisOrigenMateriaPrima=pa.IdPais  "
										 		+ "inner join Paises ON paises.IdPais = p.IdPaisOrigenMateriaPrima  "
										 		+ "inner join MateriasPrimas on MateriasPrimas.IdMateriaPrima = p.IdMateriaPrima  "
										 		+ "WHERE p.IdPartida NOT IN (SELECT idPartida FROM [dbo].[BloqueoPartidas] WHERE Estado = 1) AND (p.IdProveedor = NULL  OR NULL  IS null) \r\n"
										 		+ "AND (p.CodCLHRegimenVoluntario = 'NULL' OR 'NULL' = 'null') AND (p.IdMateriaPrima = 'NULL' OR 'NULL' = 'null') AND (p.IdBiocarburante = NULL  OR NULL  IS null) AND ( ((SELECT TOP (1) RBSA_Saldos.Volumen"
										 		+ "         FROM RBSA_Saldos         "
										 		+ "WHERE (RBSA_Saldos.IdPartida = p.IdPartida) and RBSA_Saldos.IdEmplazamiento = NULL ORDER BY RBSA_Saldos.Fecha DESC )  > 0  "
										 		+ "OR NULL  IS null)  OR  p.IdEmplazamiento =  NULL ) AND ((( p.IdEmplazamiento != 34 )AND (p.Fecha <= @fecFin)  "
										 		+ "AND (p.Fecha >= @fecIni))  "
										 		+ "OR (( p.IdEmplazamiento = 34)AND (p.Fecha <= @fecFinTrimester)  "
										 		+ "AND (p.Fecha >= @fecIni) AND EXISTS (Select * from RBSA_SALDOS rbs WHERE Fecha >= @fecIni and Fecha <= @fecFin and rbs.IdPartida=p.IdPartida))) AND p.Baja = 0) a \r\n"
										 		+ ") C "
										 		+ ") sumaa";
	 
	 //Para comprobar el saldo Filtro Bloq(1)
	 public static final String SALDO_BUSQUEDABLOQUEADA10 = " USE CEPBIOS_PREIMP "
									+" DECLARE @fecIni nvarchar(10) = 'fechaDesde' "
									+" DECLARE @fecFin nvarchar(10) = 'fechaHasta' "
									+" DECLARE @fecFinTrimester nvarchar(10) = 'fechaFinTrimestre' "
									+" "
									+" Select Case when suma is null then 0 else suma END as SumaTotal FROM ( "
									+" SELECT sum (C.CantidadDisponible) as suma "
									+" FROM"
									+" ( "
									+" "
									+" SELECT * FROM "
									+" (SELECT "
									+" "
									+"(SELECT TOP (1) SUM(Volumen) FROM  RBSA_Saldos "
									+" WHERE RBSA_Saldos.IdPartida = p.IdPartida "
									+" and (RBSA_Saldos.IdEmplazamiento = NULL OR "
									+" NULL IS NULL) "
									+" GROUP BY RBSA_Saldos.Fecha "
									+" ORDER BY RBSA_Saldos.Fecha DESC) AS CantidadDisponible "
									+" FROM Partidas p "
									+" INNER JOIN GEIsPartida GS ON GS.idPartida = p.IdPartida "
									+" INNER JOIN Carburantes ON p.IdBiocarburante = Carburantes.IdCarburante "
									+" INNER JOIN Emplazamientos ON Emplazamientos.IdEmplazamiento = p.IdEmplazamiento "
									+" INNER JOIN Proveedores ON Proveedores.IdProveedor = p.IdProveedor "
									+" INNER JOIN TipoRegimenVoluntario ON TipoRegimenVoluntario.TipoRegimen = p.CodCLHRegimenVoluntario "
									+" INNER JOIN Paises pa on p.IdPaisOrigenMateriaPrima=pa.IdPais "
									+" inner join Paises ON paises.IdPais = p.IdPaisOrigenMateriaPrima "
									+" inner join MateriasPrimas on MateriasPrimas.IdMateriaPrima = p.IdMateriaPrima "
									+" WHERE p.IdPartida IN (SELECT idPartida FROM [dbo].[BloqueoPartidas] WHERE Estado = 1) AND (p.IdProveedor = NULL  OR NULL  IS null) "
									+" AND (p.CodCLHRegimenVoluntario = 'NULL' OR 'NULL' = 'null') AND (p.IdMateriaPrima = 'NULL' OR 'NULL' = 'null') AND (p.IdBiocarburante = NULL  OR NULL  IS null) AND ( ((SELECT TOP (1) RBSA_Saldos.Volumen         FROM RBSA_Saldos         WHERE (RBSA_Saldos.IdPartida = p.IdPartida) and RBSA_Saldos.IdEmplazamiento = NULL ORDER BY RBSA_Saldos.Fecha DESC )  > 0  OR NULL  IS null)  OR  p.IdEmplazamiento =  NULL ) AND ((( p.IdEmplazamiento != 34 )AND (p.Fecha <= @fecFin) " 
									+" AND (p.Fecha >= @fecIni)) "
									+" OR (( p.IdEmplazamiento = 34)AND (p.Fecha <= @fecFinTrimester) "
									+" AND (p.Fecha >= @fecIni) AND EXISTS (Select * from RBSA_SALDOS rbs WHERE Fecha >= @fecIni and Fecha <= @fecFin and rbs.IdPartida=p.IdPartida))) AND p.Baja = 0) a "
									+" ) C "
									+" ) sumaa ";

	 //Para comprobar el saldo Filtro Bloq(1)
	 public static final String SALDO_BUSQUEDABLOQUEADA1 = " USE CEPBIOS_PREIMP " + 
	 		" DECLARE @fecIni nvarchar(10) = 'fechaDesde' " + 
	 		" DECLARE @fecFin nvarchar(10) = 'fechaHasta' " + 
	 		" DECLARE @idEmplazamiento int= optionEmplazamiento " +
	 		" Select Case when suma is null then 0 else suma END as SumaTotal FROM ( " + 
	 		" Select sum (CantidadDisponible) as suma from ( " + 
	 		" SELECT * FROM  " + 
	 		" (SELECT  " + 
	 		"( SELECT TOP (1) SUM(Volumen) FROM  RBSA_Saldos  " + 
	 		" WHERE RBSA_Saldos.IdPartida = p.IdPartida  " + 
	 		" and (RBSA_Saldos.IdEmplazamiento = @idEmplazamiento OR  " + 
	 		" @idEmplazamiento IS NULL)  " + 
	 		" GROUP BY RBSA_Saldos.Fecha  " + 
	 		" ORDER BY RBSA_Saldos.Fecha DESC) AS CantidadDisponible  " + 
	 		" FROM Partidas p " + 
	 		" INNER JOIN GEIsPartida GS ON GS.idPartida = p.IdPartida  " + 
	 		" INNER JOIN Carburantes ON p.IdBiocarburante = Carburantes.IdCarburante  " + 
	 		" INNER JOIN Emplazamientos ON Emplazamientos.IdEmplazamiento = p.IdEmplazamiento  " + 
	 		" INNER JOIN Proveedores ON Proveedores.IdProveedor = p.IdProveedor  " + 
	 		" INNER JOIN TipoRegimenVoluntario ON TipoRegimenVoluntario.TipoRegimen = p.CodCLHRegimenVoluntario  " + 
	 		" INNER JOIN Paises pa on p.IdPaisOrigenMateriaPrima=pa.IdPais " + 
	 		" inner join Paises ON paises.IdPais = p.IdPaisOrigenMateriaPrima  " + 
	 		" inner join MateriasPrimas on MateriasPrimas.IdMateriaPrima = p.IdMateriaPrima  " + 
	 		" WHERE p.IdPartida IN (SELECT idPartida FROM [dbo].[BloqueoPartidas] WHERE Estado = 1) AND (p.IdProveedor = NULL  OR NULL  IS null) " + 
	 		" AND (p.CodCLHRegimenVoluntario = 'NULL' OR 'NULL' = 'null') AND (p.IdMateriaPrima = 'NULL' OR 'NULL' = 'null') AND (p.IdBiocarburante = NULL  OR NULL  IS null) AND ( ((SELECT TOP (1) RBSA_Saldos.Volumen         FROM RBSA_Saldos         WHERE (RBSA_Saldos.IdPartida = p.IdPartida) and RBSA_Saldos.IdEmplazamiento = 34 ORDER BY RBSA_Saldos.Fecha DESC )  > 0  OR 34  IS null)  OR  p.IdEmplazamiento =  34 ) AND ((( p.IdEmplazamiento != 34 )AND (p.Fecha <= @fecFin)  " + 
	 		" AND (p.Fecha >= @fecIni))  " + 
	 		" OR (( p.IdEmplazamiento = 34)AND (p.Fecha <= @fecFin)  " + 
	 		" AND (p.Fecha >= @fecIni) AND EXISTS (Select * from RBSA_SALDOS rbs WHERE Fecha >= @fecIni and Fecha <= @fecFin and rbs.IdPartida=p.IdPartida))) AND p.Baja = 0) a  " + 
	 		" ) c  " + 
	 		" ) sumaa ";
	 
	 //Para comprobar el saldo Filtro Bloq(1)
	 public static final String SALDO_BUSQUEDABLOQUEADA2 = "USE CEPBIOS_PREIMP " + 
	 		" DECLARE @fecIni nvarchar(10) = 'fechaDesde' " + 
	 		" DECLARE @fecFin nvarchar(10) = 'fechaHasta' " + 
	 		" DECLARE @idEmplazamiento int= optionEmplazamiento " + 
	 		" DECLARE @idBiocarburante int= optionBiocarburante " +
	 		" Select Case when suma is null then 0 else suma END as SumaTotal FROM ( " + 
	 		" Select sum (CantidadDisponible) as suma from ( " + 
	 		" SELECT * FROM  " + 
	 		" (SELECT  " + 
	 		" (SELECT TOP (1) SUM(Volumen) FROM  RBSA_Saldos  " + 
	 		" WHERE RBSA_Saldos.IdPartida = p.IdPartida  " + 
	 		" and (RBSA_Saldos.IdEmplazamiento = @idEmplazamiento OR  " + 
	 		" @idEmplazamiento IS NULL)  " + 
	 		" GROUP BY RBSA_Saldos.Fecha  " + 
	 		" ORDER BY RBSA_Saldos.Fecha DESC) AS CantidadDisponible  " + 
	 		" FROM Partidas p " + 
	 		" INNER JOIN GEIsPartida GS ON GS.idPartida = p.IdPartida  " + 
	 		" INNER JOIN Carburantes ON p.IdBiocarburante = Carburantes.IdCarburante  " + 
	 		" INNER JOIN Emplazamientos ON Emplazamientos.IdEmplazamiento = p.IdEmplazamiento  " + 
	 		" INNER JOIN Proveedores ON Proveedores.IdProveedor = p.IdProveedor \r\n" + 
	 		" INNER JOIN TipoRegimenVoluntario ON TipoRegimenVoluntario.TipoRegimen = p.CodCLHRegimenVoluntario  " + 
	 		" INNER JOIN Paises pa on p.IdPaisOrigenMateriaPrima=pa.IdPais " + 
	 		" inner join Paises ON paises.IdPais = p.IdPaisOrigenMateriaPrima  " + 
	 		" inner join MateriasPrimas on MateriasPrimas.IdMateriaPrima = p.IdMateriaPrima  " + 
	 		" WHERE p.IdPartida IN (SELECT idPartida FROM [dbo].[BloqueoPartidas] WHERE Estado = 1)  " + 
	 		" AND (p.IdBiocarburante = @idBiocarburante)  AND ( ((SELECT TOP (1) RBSA_Saldos.Volumen"
	 		+ "         FROM RBSA_Saldos         "
	 		+ "WHERE (RBSA_Saldos.IdPartida = p.IdPartida) and RBSA_Saldos.IdEmplazamiento = 34 ORDER BY RBSA_Saldos.Fecha DESC )  > 0  OR 34  IS null)  "
	 		+ "OR  p.IdEmplazamiento =  34 ) AND ((( p.IdEmplazamiento != 34 )AND (p.Fecha <= @fecFin)  " + 
	 		" AND (p.Fecha >= @fecIni))  " + 
	 		" OR (( p.IdEmplazamiento = 34)AND (p.Fecha <= @fecFin)  " + 
	 		" AND (p.Fecha >= @fecIni) AND EXISTS (Select * from RBSA_SALDOS rbs WHERE Fecha >= @fecIni and Fecha <= @fecFin and rbs.IdPartida=p.IdPartida))) AND p.Baja = 0) a  " + 
	 		" ) c  " + 
	 		" ) sumaa ";
	 
	 //Para comprobar el saldo Filtro Bloq(1)
	 public static final String SALDO_BUSQUEDABLOQUEADA3 = " USE CEPBIOS_PREIMP " + 
	 		" DECLARE @fecIni nvarchar(10) = 'fechaDesde' " + 
	 		" DECLARE @fecFin nvarchar(10) = 'fechaHasta' " + 
	 		" DECLARE @idEmplazamiento int= optionEmplazamiento" + 
	 		" DECLARE @idBiocarburante int= optionBiocarburante" + 
	 		" DECLARE @idRegimen nvarchar(10) = 'optionRegimen'" + 
	 		"\r\n" + 
	 		"\r\n" + 
	 		"\r\n" + 
	 		"Select Case when suma is null then 0 else suma END as SumaTotal FROM (\r\n" + 
	 		"Select sum (CantidadDisponible) as suma from (\r\n" + 
	 		"SELECT * FROM \r\n" + 
	 		"(SELECT \r\n" + 
	 		"(SELECT TOP (1) SUM(Volumen) FROM  RBSA_Saldos \r\n" + 
	 		"WHERE RBSA_Saldos.IdPartida = p.IdPartida \r\n" + 
	 		"and (RBSA_Saldos.IdEmplazamiento = @idEmplazamiento OR \r\n" + 
	 		"@idEmplazamiento IS NULL) \r\n" + 
	 		" GROUP BY RBSA_Saldos.Fecha \r\n" + 
	 		"ORDER BY RBSA_Saldos.Fecha DESC) AS CantidadDisponible \r\n" + 
	 		"FROM Partidas p\r\n" + 
	 		"INNER JOIN GEIsPartida GS ON GS.idPartida = p.IdPartida \r\n" + 
	 		"INNER JOIN Carburantes ON p.IdBiocarburante = Carburantes.IdCarburante \r\n" + 
	 		"INNER JOIN Emplazamientos ON Emplazamientos.IdEmplazamiento = p.IdEmplazamiento \r\n" + 
	 		"INNER JOIN Proveedores ON Proveedores.IdProveedor = p.IdProveedor \r\n" + 
	 		"INNER JOIN TipoRegimenVoluntario ON TipoRegimenVoluntario.TipoRegimen = p.CodCLHRegimenVoluntario \r\n" + 
	 		"INNER JOIN Paises pa on p.IdPaisOrigenMateriaPrima=pa.IdPais\r\n" + 
	 		"inner join Paises ON paises.IdPais = p.IdPaisOrigenMateriaPrima \r\n" + 
	 		"inner join MateriasPrimas on MateriasPrimas.IdMateriaPrima = p.IdMateriaPrima \r\n" + 
	 		"WHERE p.IdPartida IN (SELECT idPartida FROM [dbo].[BloqueoPartidas] WHERE Estado = 1) \r\n" + 
	 		"AND (p.IdBiocarburante = @idBiocarburante)  \r\n" + 
	 		"AND (p.CodCLHRegimenVoluntario = @idRegimen) \r\n" + 
	 		"AND ( ((SELECT TOP (1) RBSA_Saldos.Volumen         FROM RBSA_Saldos         WHERE (RBSA_Saldos.IdPartida = p.IdPartida) and RBSA_Saldos.IdEmplazamiento = 34 ORDER BY RBSA_Saldos.Fecha DESC )  > 0  OR 34  IS null)  OR  p.IdEmplazamiento =  34 ) AND ((( p.IdEmplazamiento != 34 )AND (p.Fecha <= @fecFin) \r\n" + 
	 		"AND (p.Fecha >= @fecIni)) \r\n" + 
	 		"OR (( p.IdEmplazamiento = 34)AND (p.Fecha <= @fecFin) \r\n" + 
	 		"AND (p.Fecha >= @fecIni) AND EXISTS (Select * from RBSA_SALDOS rbs WHERE Fecha >= @fecIni and Fecha <= @fecFin and rbs.IdPartida=p.IdPartida))) AND p.Baja = 0) a \r\n" + 
	 		" ) c \r\n" + 
	 		" ) sumaa ";
	 
	 //Para comprobar el saldo Filtro Bloq(1)
	 public static final String SALDO_BUSQUEDABLOQUEADA4 = "USE CEPBIOS_PREIMP\r\n" + 
	 		"\r\n" + 
	 		"DECLARE @fecIni nvarchar(10) = 'fechaDesde'\r\n" + 
	 		"DECLARE @fecFin nvarchar(10) = 'fechaHasta'\r\n" + 
	 		"DECLARE @idEmplazamiento int= optionEmplazamiento" + 
	 		"DECLARE @idBiocarburante int=optionBiocarburante" + 
	 		"DECLARE @idRegimen nvarchar(10) = 'optionRegimen'" + 
	 		"DECLARE @idMateriaPrima int= optionMateriaPrima" + 
	 		"\r\n" + 
	 		"\r\n" + 
	 		"\r\n" + 
	 		"\r\n" + 
	 		"Select Case when suma is null then 0 else suma END as SumaTotal FROM (\r\n" + 
	 		"Select sum (CantidadDisponible) as suma from (\r\n" + 
	 		"SELECT * FROM \r\n" + 
	 		"(SELECT \r\n" + 
	 		"(SELECT TOP (1) SUM(Volumen) FROM  RBSA_Saldos \r\n" + 
	 		"WHERE RBSA_Saldos.IdPartida = p.IdPartida \r\n" + 
	 		"and (RBSA_Saldos.IdEmplazamiento = @idEmplazamiento OR \r\n" + 
	 		"@idEmplazamiento IS NULL) \r\n" + 
	 		" GROUP BY RBSA_Saldos.Fecha \r\n" + 
	 		"ORDER BY RBSA_Saldos.Fecha DESC) AS CantidadDisponible \r\n" + 
	 		"FROM Partidas p\r\n" + 
	 		"INNER JOIN GEIsPartida GS ON GS.idPartida = p.IdPartida \r\n" + 
	 		"INNER JOIN Carburantes ON p.IdBiocarburante = Carburantes.IdCarburante \r\n" + 
	 		"INNER JOIN Emplazamientos ON Emplazamientos.IdEmplazamiento = p.IdEmplazamiento \r\n" + 
	 		"INNER JOIN Proveedores ON Proveedores.IdProveedor = p.IdProveedor \r\n" + 
	 		"INNER JOIN TipoRegimenVoluntario ON TipoRegimenVoluntario.TipoRegimen = p.CodCLHRegimenVoluntario \r\n" + 
	 		"INNER JOIN Paises pa on p.IdPaisOrigenMateriaPrima=pa.IdPais\r\n" + 
	 		"inner join Paises ON paises.IdPais = p.IdPaisOrigenMateriaPrima \r\n" + 
	 		"inner join MateriasPrimas on MateriasPrimas.IdMateriaPrima = p.IdMateriaPrima \r\n" + 
	 		"WHERE p.IdPartida IN (SELECT idPartida FROM [dbo].[BloqueoPartidas] WHERE Estado = 1) \r\n" + 
	 		"AND (p.IdBiocarburante = @idBiocarburante)  \r\n" + 
	 		"AND (p.CodCLHRegimenVoluntario = @idRegimen) AND (p.IdMateriaPrima = @idMateriaPrima)\r\n" + 
	 		"AND ( ((SELECT TOP (1) RBSA_Saldos.Volumen         FROM RBSA_Saldos         WHERE (RBSA_Saldos.IdPartida = p.IdPartida) and RBSA_Saldos.IdEmplazamiento = 34 ORDER BY RBSA_Saldos.Fecha DESC )  > 0  OR 34  IS null)  OR  p.IdEmplazamiento =  34 ) AND ((( p.IdEmplazamiento != 34 )AND (p.Fecha <= @fecFin) \r\n" + 
	 		"AND (p.Fecha >= @fecIni)) \r\n" + 
	 		"OR (( p.IdEmplazamiento = 34)AND (p.Fecha <= @fecFin) \r\n" + 
	 		"AND (p.Fecha >= @fecIni) AND EXISTS (Select * from RBSA_SALDOS rbs WHERE Fecha >= @fecIni and Fecha <= @fecFin and rbs.IdPartida=p.IdPartida))) AND p.Baja = 0) a \r\n" + 
	 		" ) c \r\n" + 
	 		" ) sumaa\r\n" + 
	 		"";
	 //Para comprobar el saldo Filtro Bloq(1)
	 public static final String SALDO_BUSQUEDABLOQUEADA5 = "USE CEPBIOS_PREIMP\r\n " + 
	 		"\r\n" + 
	 		"DECLARE @fecIni nvarchar(10) = 'fechaDesde' " + 
	 		"DECLARE @fecFin nvarchar(10) = 'fechaHasta' " + 
	 		"DECLARE @idEmplazamiento int= optionEmplazamiento " + 
	 		"DECLARE @idBiocarburante int= optionBiocarburante " + 
	 		"DECLARE @idRegimen nvarchar(10) = 'optionRegimen' " + 
	 		"DECLARE @idMateriaPrima int= optionMateriaPrima " + 
	 		"DECLARE @idProveedor int= optionProveedor " + 
	 		"\r\n" + 
	 		"\r\n" + 
	 		"\r\n" + 
	 		"\r\n" + 
	 		"Select Case when suma is null then 0 else suma END as SumaTotal FROM (\r\n" + 
	 		"Select sum (CantidadDisponible) as suma from (\r\n" + 
	 		"SELECT * FROM \r\n" + 
	 		"(SELECT \r\n" + 
	 		"(SELECT TOP (1) SUM(Volumen) FROM  RBSA_Saldos \r\n" + 
	 		"WHERE RBSA_Saldos.IdPartida = p.IdPartida \r\n" + 
	 		"and (RBSA_Saldos.IdEmplazamiento = @idEmplazamiento OR \r\n" + 
	 		"@idEmplazamiento IS NULL) \r\n" + 
	 		" GROUP BY RBSA_Saldos.Fecha \r\n" + 
	 		"ORDER BY RBSA_Saldos.Fecha DESC) AS CantidadDisponible \r\n" + 
	 		"FROM Partidas p\r\n" + 
	 		"INNER JOIN GEIsPartida GS ON GS.idPartida = p.IdPartida \r\n" + 
	 		"INNER JOIN Carburantes ON p.IdBiocarburante = Carburantes.IdCarburante \r\n" + 
	 		"INNER JOIN Emplazamientos ON Emplazamientos.IdEmplazamiento = p.IdEmplazamiento \r\n" + 
	 		"INNER JOIN Proveedores ON Proveedores.IdProveedor = p.IdProveedor \r\n" + 
	 		"INNER JOIN TipoRegimenVoluntario ON TipoRegimenVoluntario.TipoRegimen = p.CodCLHRegimenVoluntario \r\n" + 
	 		"INNER JOIN Paises pa on p.IdPaisOrigenMateriaPrima=pa.IdPais\r\n" + 
	 		"inner join Paises ON paises.IdPais = p.IdPaisOrigenMateriaPrima \r\n" + 
	 		"inner join MateriasPrimas on MateriasPrimas.IdMateriaPrima = p.IdMateriaPrima \r\n" + 
	 		"WHERE p.IdPartida IN (SELECT idPartida FROM [dbo].[BloqueoPartidas] WHERE Estado = 1) \r\n" + 
	 		"AND (p.IdBiocarburante = @idBiocarburante)  AND (p.IdProveedor = @idProveedor) \r\n" + 
	 		"AND (p.CodCLHRegimenVoluntario = @idRegimen) AND (p.IdMateriaPrima = @idMateriaPrima)\r\n" + 
	 		"AND ( ((SELECT TOP (1) RBSA_Saldos.Volumen         FROM RBSA_Saldos         WHERE (RBSA_Saldos.IdPartida = p.IdPartida) and RBSA_Saldos.IdEmplazamiento = 34 ORDER BY RBSA_Saldos.Fecha DESC )  > 0  OR 34  IS null)  OR  p.IdEmplazamiento =  34 ) AND ((( p.IdEmplazamiento != 34 )AND (p.Fecha <= @fecFin) \r\n" + 
	 		"AND (p.Fecha >= @fecIni)) \r\n" + 
	 		"OR (( p.IdEmplazamiento = 34)AND (p.Fecha <= @fecFin) \r\n" + 
	 		"AND (p.Fecha >= @fecIni) AND EXISTS (Select * from RBSA_SALDOS rbs WHERE Fecha >= @fecIni and Fecha <= @fecFin and rbs.IdPartida=p.IdPartida))) AND p.Baja = 0) a \r\n" + 
	 		" ) c \r\n" + 
	 		" ) sumaa\r\n" + 
	 		"";
	 
	 //Para comprobar el saldo Filtro Bloq(1)
	 public static final String SALDO_BUSQUEDABLOQUEADA6 = "USE CEPBIOS_PREIMP\r\n" + 
	 		"\r\n" + 
	 		"DECLARE @fecIni nvarchar(10) = 'fechaDesde' " + 
	 		"DECLARE @fecFin nvarchar(10) = 'fechaHasta' " +
	 		"DECLARE @idBiocarburante int= optionBiocarburante  " + 
	 		"\r\n" + 
	 		"Select Case when suma is null then 0 else suma END as SumaTotal FROM (\r\n" + 
	 		"Select sum (CantidadDisponible) as suma from (\r\n" + 
	 		"SELECT * FROM \r\n" + 
	 		"(SELECT  \r\n" + 
	 		"(SELECT TOP (1) SUM(Volumen) FROM  RBSA_Saldos \r\n" + 
	 		"WHERE RBSA_Saldos.IdPartida = p.IdPartida \r\n" + 
	 		"and (RBSA_Saldos.IdEmplazamiento = NULL OR \r\n" + 
	 		"NULL IS NULL) \r\n" + 
	 		" GROUP BY RBSA_Saldos.Fecha \r\n" + 
	 		"ORDER BY RBSA_Saldos.Fecha DESC) AS CantidadDisponible \r\n" + 
	 		"FROM Partidas p\r\n" + 
	 		"INNER JOIN GEIsPartida GS ON GS.idPartida = p.IdPartida \r\n" + 
	 		"INNER JOIN Carburantes ON p.IdBiocarburante = Carburantes.IdCarburante \r\n" + 
	 		"INNER JOIN Emplazamientos ON Emplazamientos.IdEmplazamiento = p.IdEmplazamiento \r\n" + 
	 		"INNER JOIN Proveedores ON Proveedores.IdProveedor = p.IdProveedor \r\n" + 
	 		"INNER JOIN TipoRegimenVoluntario ON TipoRegimenVoluntario.TipoRegimen = p.CodCLHRegimenVoluntario \r\n" + 
	 		"INNER JOIN Paises pa on p.IdPaisOrigenMateriaPrima=pa.IdPais \r\n" + 
	 		"inner join Paises ON paises.IdPais = p.IdPaisOrigenMateriaPrima \r\n" + 
	 		"inner join MateriasPrimas on MateriasPrimas.IdMateriaPrima = p.IdMateriaPrima \r\n" + 
	 		"WHERE p.IdPartida IN (SELECT idPartida FROM [dbo].[BloqueoPartidas] WHERE Estado = 1) \r\n" + 
	 		"AND (p.IdBiocarburante = 8  OR 8  IS null) AND ( ((SELECT TOP (1) RBSA_Saldos.Volumen         FROM RBSA_Saldos         WHERE (RBSA_Saldos.IdPartida = p.IdPartida) and RBSA_Saldos.IdEmplazamiento = NULL ORDER BY RBSA_Saldos.Fecha DESC )  > 0  OR NULL  IS null)  OR  p.IdEmplazamiento =  NULL ) AND ((( p.IdEmplazamiento != 34 )AND (p.Fecha <= @fecFin) \r\n" + 
	 		"AND (p.Fecha >= @fecIni)) \r\n" + 
	 		"OR (( p.IdEmplazamiento = 34)AND (p.Fecha <= @fecFin) \r\n" + 
	 		"AND (p.Fecha >= @fecIni) AND EXISTS (Select * from RBSA_SALDOS rbs WHERE Fecha >= @fecIni and Fecha <= @fecFin and rbs.IdPartida=p.IdPartida))) AND p.Baja = 0) a \r\n" + 
	 		")c \r\n" + 
	 		" ) sumaa\r\n" + 
	 		"";
	 
	 //Para comprobar el saldo Filtro Bloq(1)
	 public static final String SALDO_BUSQUEDABLOQUEADA7 = "USE CEPBIOS_PREIMP\r\n" + 
	 		"\r\n" + 
	 		"DECLARE @fecIni nvarchar(10) = 'fechaDesde' \r\n" + 
	 		"DECLARE @fecFin nvarchar(10) = 'fechaHasta'  \r\n" + 
	 		"DECLARE @idProveedor int= optionProveedor  " + 
	 		"\r\n" + 
	 		"\r\n" + 
	 		"Select Case when suma is null then 0 else suma END as SumaTotal FROM (\r\n" + 
	 		"Select sum (CantidadDisponible) as suma from (\r\n" + 
	 		"SELECT * FROM \r\n" + 
	 		"(SELECT  \r\n" + 
	 		"(SELECT TOP (1) SUM(Volumen) FROM  RBSA_Saldos \r\n" + 
	 		"WHERE RBSA_Saldos.IdPartida = p.IdPartida \r\n" + 
	 		"and (RBSA_Saldos.IdEmplazamiento = NULL OR \r\n" + 
	 		"NULL IS NULL) \r\n" + 
	 		" GROUP BY RBSA_Saldos.Fecha \r\n" + 
	 		"ORDER BY RBSA_Saldos.Fecha DESC) AS CantidadDisponible \r\n" + 
	 		"FROM Partidas p\r\n" + 
	 		"INNER JOIN GEIsPartida GS ON GS.idPartida = p.IdPartida \r\n" + 
	 		"INNER JOIN Carburantes ON p.IdBiocarburante = Carburantes.IdCarburante \r\n" + 
	 		"INNER JOIN Emplazamientos ON Emplazamientos.IdEmplazamiento = p.IdEmplazamiento \r\n" + 
	 		"INNER JOIN Proveedores ON Proveedores.IdProveedor = p.IdProveedor \r\n" + 
	 		"INNER JOIN TipoRegimenVoluntario ON TipoRegimenVoluntario.TipoRegimen = p.CodCLHRegimenVoluntario \r\n" + 
	 		"INNER JOIN Paises pa on p.IdPaisOrigenMateriaPrima=pa.IdPais \r\n" + 
	 		"inner join Paises ON paises.IdPais = p.IdPaisOrigenMateriaPrima \r\n" + 
	 		"inner join MateriasPrimas on MateriasPrimas.IdMateriaPrima = p.IdMateriaPrima \r\n" + 
	 		"WHERE p.IdPartida IN (SELECT idPartida FROM [dbo].[BloqueoPartidas] WHERE Estado = 1) \r\n" + 
	 		"AND (p.IdProveedor = @idProveedor)  AND ( ((SELECT TOP (1) RBSA_Saldos.Volumen         FROM RBSA_Saldos         WHERE (RBSA_Saldos.IdPartida = p.IdPartida) and RBSA_Saldos.IdEmplazamiento = NULL ORDER BY RBSA_Saldos.Fecha DESC )  > 0  OR NULL  IS null)  OR  p.IdEmplazamiento =  NULL ) AND ((( p.IdEmplazamiento != 34 )AND (p.Fecha <= @fecFin) \r\n" + 
	 		"AND (p.Fecha >= @fecIni)) \r\n" + 
	 		"OR (( p.IdEmplazamiento = 34)AND (p.Fecha <= @fecFin) \r\n" + 
	 		"AND (p.Fecha >= @fecIni) AND EXISTS (Select * from RBSA_SALDOS rbs WHERE Fecha >= @fecIni and Fecha <= @fecFin and rbs.IdPartida=p.IdPartida))) AND p.Baja = 0) a \r\n" + 
	 		")c \r\n" + 
	 		" ) sumaa\r\n" + 
	 		"";
	 
	 //Para comprobar el saldo Filtro Bloq(1)
	 public static final String SALDO_BUSQUEDABLOQUEADA8 = "USE CEPBIOS_PREIMP\r\n" + 
	 		"\r\n" + 
	 		"DECLARE @fecIni nvarchar(10) = 'fechaDesde' \r\n" + 
	 		"DECLARE @fecFin nvarchar(10) = 'fechaHasta'  \r\n" + 
	 		"DECLARE @idRegimen nvarchar(10) = 'optionRegimen'  " + 
	 		"\r\n" + 
	 		"\r\n" + 
	 		"Select Case when suma is null then 0 else suma END as SumaTotal FROM (\r\n" + 
	 		"Select sum (CantidadDisponible) as suma from (\r\n" + 
	 		"SELECT * FROM \r\n" + 
	 		"(SELECT  \r\n" + 
	 		"(SELECT TOP (1) SUM(Volumen) FROM  RBSA_Saldos \r\n" + 
	 		"WHERE RBSA_Saldos.IdPartida = p.IdPartida \r\n" + 
	 		"and (RBSA_Saldos.IdEmplazamiento = NULL OR \r\n" + 
	 		"NULL IS NULL) \r\n" + 
	 		" GROUP BY RBSA_Saldos.Fecha \r\n" + 
	 		"ORDER BY RBSA_Saldos.Fecha DESC) AS CantidadDisponible \r\n" + 
	 		"FROM Partidas p\r\n" + 
	 		"INNER JOIN GEIsPartida GS ON GS.idPartida = p.IdPartida \r\n" + 
	 		"INNER JOIN Carburantes ON p.IdBiocarburante = Carburantes.IdCarburante \r\n" + 
	 		"INNER JOIN Emplazamientos ON Emplazamientos.IdEmplazamiento = p.IdEmplazamiento \r\n" + 
	 		"INNER JOIN Proveedores ON Proveedores.IdProveedor = p.IdProveedor \r\n" + 
	 		"INNER JOIN TipoRegimenVoluntario ON TipoRegimenVoluntario.TipoRegimen = p.CodCLHRegimenVoluntario \r\n" + 
	 		"INNER JOIN Paises pa on p.IdPaisOrigenMateriaPrima=pa.IdPais \r\n" + 
	 		"inner join Paises ON paises.IdPais = p.IdPaisOrigenMateriaPrima \r\n" + 
	 		"inner join MateriasPrimas on MateriasPrimas.IdMateriaPrima = p.IdMateriaPrima \r\n" + 
	 		"WHERE p.IdPartida IN (SELECT idPartida FROM [dbo].[BloqueoPartidas] WHERE Estado = 1) \r\n" + 
	 		"AND (p.CodCLHRegimenVoluntario = @idRegimen)   AND ( ((SELECT TOP (1) RBSA_Saldos.Volumen         FROM RBSA_Saldos         WHERE (RBSA_Saldos.IdPartida = p.IdPartida) and RBSA_Saldos.IdEmplazamiento = NULL ORDER BY RBSA_Saldos.Fecha DESC )  > 0  OR NULL  IS null)  OR  p.IdEmplazamiento =  NULL ) AND ((( p.IdEmplazamiento != 34 )AND (p.Fecha <= @fecFin) \r\n" + 
	 		"AND (p.Fecha >= @fecIni)) \r\n" + 
	 		"OR (( p.IdEmplazamiento = 34)AND (p.Fecha <= @fecFin) \r\n" + 
	 		"AND (p.Fecha >= @fecIni) AND EXISTS (Select * from RBSA_SALDOS rbs WHERE Fecha >= @fecIni and Fecha <= @fecFin and rbs.IdPartida=p.IdPartida))) AND p.Baja = 0) a \r\n" + 
	 		")c \r\n" + 
	 		" ) sumaa\r\n" + 
	 		"";
	 
	 //Para comprobar el saldo Filtro Bloq(1)
	 public static final String SALDO_BUSQUEDABLOQUEADA9 = "USE CEPBIOS_PREIMP\r\n" + 
		 	"\r\n" +
		 	"DECLARE @fecIni nvarchar(10) = 'fechaDesde' \r\n" + 
	 		"DECLARE @fecFin nvarchar(10) = 'fechaHasta'  \r\n" +  
	 		"DECLARE @idMateriaPrima int= optionMateriaPrima  " + 
	 		"\r\n" + 
	 		"Select Case when suma is null then 0 else suma END as SumaTotal FROM (\r\n" + 
	 		"Select sum (CantidadDisponible) as suma from (\r\n" + 
	 		"SELECT * FROM \r\n" + 
	 		"(SELECT  \r\n" + 
	 		"(SELECT TOP (1) SUM(Volumen) FROM  RBSA_Saldos \r\n" + 
	 		"WHERE RBSA_Saldos.IdPartida = p.IdPartida \r\n" + 
	 		"and (RBSA_Saldos.IdEmplazamiento = NULL OR \r\n" + 
	 		"NULL IS NULL) \r\n" + 
	 		" GROUP BY RBSA_Saldos.Fecha \r\n" + 
	 		"ORDER BY RBSA_Saldos.Fecha DESC) AS CantidadDisponible \r\n" + 
	 		"FROM Partidas p\r\n" + 
	 		"INNER JOIN GEIsPartida GS ON GS.idPartida = p.IdPartida \r\n" + 
	 		"INNER JOIN Carburantes ON p.IdBiocarburante = Carburantes.IdCarburante \r\n" + 
	 		"INNER JOIN Emplazamientos ON Emplazamientos.IdEmplazamiento = p.IdEmplazamiento \r\n" + 
	 		"INNER JOIN Proveedores ON Proveedores.IdProveedor = p.IdProveedor \r\n" + 
	 		"INNER JOIN TipoRegimenVoluntario ON TipoRegimenVoluntario.TipoRegimen = p.CodCLHRegimenVoluntario \r\n" + 
	 		"INNER JOIN Paises pa on p.IdPaisOrigenMateriaPrima=pa.IdPais \r\n" + 
	 		"inner join Paises ON paises.IdPais = p.IdPaisOrigenMateriaPrima \r\n" + 
	 		"inner join MateriasPrimas on MateriasPrimas.IdMateriaPrima = p.IdMateriaPrima \r\n" + 
	 		"WHERE p.IdPartida IN (SELECT idPartida FROM [dbo].[BloqueoPartidas] WHERE Estado = 1) \r\n" + 
	 		"AND (p.IdMateriaPrima = @idMateriaPrima) \r\n" + 
	 		"AND ( ((SELECT TOP (1) RBSA_Saldos.Volumen         FROM RBSA_Saldos         WHERE (RBSA_Saldos.IdPartida = p.IdPartida) and RBSA_Saldos.IdEmplazamiento = NULL ORDER BY RBSA_Saldos.Fecha DESC )  > 0  OR NULL  IS null)  OR  p.IdEmplazamiento =  NULL ) AND ((( p.IdEmplazamiento != 34 )AND (p.Fecha <= @fecFin) \r\n" + 
	 		"AND (p.Fecha >= @fecIni)) \r\n" + 
	 		"OR (( p.IdEmplazamiento = 34)AND (p.Fecha <= @fecFin) \r\n" + 
	 		"AND (p.Fecha >= @fecIni) AND EXISTS (Select * from RBSA_SALDOS rbs WHERE Fecha >= @fecIni and Fecha <= @fecFin and rbs.IdPartida=p.IdPartida))) AND p.Baja = 0) a \r\n" + 
	 		")c \r\n" + 
	 		" ) sumaa\r\n" + 
	 		"";
	 
}
