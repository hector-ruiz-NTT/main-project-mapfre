package mapfre.general;

import java.io.File;
import java.util.Properties;

public final class ProjectPaths {
	
	//Vars.
	private static Properties datosConfig = ProjectProperties.getConfigProperties();
	
	//Private constructor is necessary (Sonar).
	private ProjectPaths() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Get the root path of the project.
	 * 
	 * @return {String} root path
	 * @throws Exception
	 * @author CEX
	 */
	public static String getRootDirectory() {
		//Initialize vars.
		File rootDir = new File("");
		String path;

		//Get path.
		path = rootDir.getAbsolutePath();

		//Return path.
		return path;
	}

	/** 
	 * Get download path.
	 * 
	 * @return rootPath
	 * @author CEX
	 */
	public static String getDownloadPath() {
		//Get rootPath.
		String rootPath = getRootDirectory();

		//Get sure datosConfig have value.
		if(datosConfig == null) {
			datosConfig = ProjectProperties.getConfigProperties();
		}

		//Generate download Path.
		return rootPath + datosConfig.getProperty("downloadPath") + BaseLocal.idCasoPrueba + "\\";
	}

	/** 
	 * Get file path.
	 * 
	 * @return rootPath
	 * @author CEX
	 */
	public static String getfilePath() {
		//Get rootPath.
		String rootPath = getRootDirectory();

		//Get sure datosConfig have value.
		if(datosConfig == null) {
			datosConfig = ProjectProperties.getConfigProperties();
		}

		//Generate download Path.
		return rootPath + datosConfig.getProperty("filePath");
	}

	/** 
	 * Get resource path.
	 * 
	 * @return rootPath
	 * @author CEX
	 */
	public static String getResourcePath() {
		//Get rootPath.
		String rootPath = getRootDirectory();

		//Get sure datosConfig have value.
		if(datosConfig == null) {
			datosConfig = ProjectProperties.getConfigProperties();
		}
		
		//Generate download Path.
		return rootPath + datosConfig.getProperty("resourcePath");
	}

	/** 
	 * Get Teckel conf file path.
	 * 
	 * @return rootPath
	 * @author CEX
	 */
	public static String getTeckelConfPath() {
		//Get rootPath.
		String rootPath = getRootDirectory();

		//Get sure datosConfig have value.
		if(datosConfig == null) {
			datosConfig = ProjectProperties.getConfigProperties();
		}

		//Generate Teckel conf file Path.
		return rootPath + datosConfig.getProperty("teckelConfPath");
	}
	
}
