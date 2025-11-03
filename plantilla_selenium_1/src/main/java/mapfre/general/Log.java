package mapfre.general;

import mapfre.utils.ExtentManager.ExtentManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import static mapfre.utils.ExtentManager.ExtentManager.getTest;
//import org.junit.Assert;


public class Log {
	
	private static final Logger LOGGER = LogManager.getLogger();

	//Private constructor is necessary (Sonar).
	private Log() {
		throw new IllegalStateException("Utility class");
	}
	
	/**
	 * Method used instead of println for the console output of the error messages
	 * 
	 * @param msn - String
	 * @author CEX
	 */
	public static void info(String msn) {
		Configurator.setRootLevel(Level.INFO);
		LOGGER.info(msn);
		try {
			if (getTest() != null) {
				getTest().info(msn);
			}
		} catch (Exception ignore) {}
	}

	public static void pass(String msn) {
		Configurator.setRootLevel(Level.INFO);
		LOGGER.info(msn);
		try {
			if (getTest() != null) {
				getTest().pass(msn);
			}
		} catch (Exception ignore) {}

	}

	public static void fail(String msn) throws InterruptedException {
		Configurator.setRootLevel(Level.ERROR);
		//LOGGER.info(msn);
		try {
			if (getTest() != null) {
				getTest().fail(msn);
			} else {
				LOGGER.error(msn);
			}
		} catch (Exception ignore) {
			LOGGER.error(msn);
		}
		//Assert.fail(msn);


	}

	/**
	 * Method used instead of println for the console output of the error messages
	 * 
	 //* @param msn - String
	 * @author CEX
	 */
	public static void info(Exception e) {
		Configurator.setRootLevel(Level.INFO);
		LOGGER.info(e);
	}


	
	/**
	 * Method used instead of println for the console output of the error messages
	 * 
	 * @param e - Exception
	 * @author CEX
	 */
	public void logger(Exception e) {
		LOGGER.info(e);
	}

	public static void infoHiloSec(String msn) {
		Configurator.setRootLevel(Level.INFO);
		LOGGER.info(msn);
	}
}