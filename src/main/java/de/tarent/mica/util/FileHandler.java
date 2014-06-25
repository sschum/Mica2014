package de.tarent.mica.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogManager;

/**
 * Eigener {@link java.util.logging.FileHandler} damit ich erweiterte FilePattern
 * definieren kann!
 * 
 * @author rainu
 *
 */
public class FileHandler extends java.util.logging.FileHandler {
	private static final String PROPERTY_PREFIX = FileHandler.class.getName();
	private static final String PATTERN_KEY = PROPERTY_PREFIX + ".pattern";

	public FileHandler() throws IOException, SecurityException {
		this(LogManager.getLogManager().getProperty(PATTERN_KEY));
	}

	public FileHandler(String pattern, boolean append) throws IOException,
			SecurityException {
		super(resolvePattern(pattern), append);
	}

	public FileHandler(String pattern, int limit, int count, boolean append)
			throws IOException, SecurityException {
		super(resolvePattern(pattern), limit, count, append);
	}

	public FileHandler(String pattern, int limit, int count)
			throws IOException, SecurityException {
		super(resolvePattern(pattern), limit, count);
	}

	public FileHandler(String pattern) throws IOException, SecurityException {
		super(resolvePattern(pattern));
	}

	private static String resolvePattern(String pattern) {
		//Eigene Platzhalter ersetzten...
		return pattern
				.replace("%T", new SimpleDateFormat("yyyy_MM_dd__HH:mm:ss.SSS").format(new Date()))
				.replace("%w", System.getProperty("user.dir"));
	}

}
