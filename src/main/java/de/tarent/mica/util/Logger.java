package de.tarent.mica.util;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.LogManager;

public class Logger {
	private static java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(de.tarent.mica.util.Logger.class.getName());
	
	static {
		String loggingfile = System.getProperty("java.util.logging.config.file");
		if (loggingfile == null || loggingfile.isEmpty()) {
			URL url = null;
			try {
				url = Logger.class.getClassLoader().getResource("logging.properties");
				if (url == null) {
					System.err.println("Cannot find logging.properties.");
				} else {
					LogManager.getLogManager().readConfiguration(url.openStream());
				}
			} catch (Exception e) {
				System.err.println("Error reading logging.properties from '" + url + "': " + e);
			}
		}
	}
	
	public static void debug(String message){
		debug(message, null);
	}
	
	public static void debug(String message, Throwable t){
		if(t == null) LOG.log(Level.FINER, message);
		else LOG.log(Level.FINER, message, t);
	}

	public static void info(String message) {
		info(message, null);
	}
	
	public static void info(String message, Throwable t) {
		if(t == null) LOG.log(Level.INFO, message);
		else LOG.log(Level.INFO, message, t);
	}

	public static void warn(String message) {
		info(message, null);
	}
	
	public static void warn(String message, Throwable t) {
		if(t == null) LOG.log(Level.WARNING, message);
		else LOG.log(Level.WARNING, message, t);
	}
}
