package mapfre.general;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public final class ProjectProperties {
	
	//Vars.
	private static String rootPath = ProjectPaths.getRootDirectory();
	private static Properties datosConfig = getProperties("config");
	private static Properties datosLogin = getProperties("login");

	//Private constructor is necessary (Sonar).
	private ProjectProperties() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Return the config properties.
	 * 
	 * @return {Properties} the element with the properties
	 * @author CEX
	 */
	public static Properties getConfigProperties() {
		return datosConfig;
	}
	
	/**
	 * Return the login properties.
	 * 
	 * @return {Properties} the element with the properties
	 * @author CEX
	 */
	public static Properties getLoginProperties() {
		return datosLogin;
	}
	
	/**
	 * This method takes care of extracting the URL.
	 * 
	 * @param urlId
	 * @return
	 * @author CEX
	 */
	public static String getUrl(String urlId) {
		return (String) datosLogin.get(urlId);
	}

	/**
	 * Method to load the Properties files and their data.
	 * 
	 * @return
	 * @author CEX
	 */
	public static Properties getProperties(String opcion) {
		//Initialize vars.
		Properties prop = new Properties();
		
		//It is necessary null var initializing.
		File file = null;
		file = new File(rootPath + "/properties/" + opcion + ".properties");
		//file = new File(rootPath + "\\properties\\" + opcion + ".properties");
		
		//Get the input bytes of a file on a file system.
		FileInputStream fileInput = null;

		//Open file.
		try {
			fileInput = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			Log.info(e);
		}

		//Load properties.
		try {
			prop.load(fileInput);
		} catch (IOException e) {
			Log.info(e);
		}
		
		//Return properties.
		return prop;
	}
}
