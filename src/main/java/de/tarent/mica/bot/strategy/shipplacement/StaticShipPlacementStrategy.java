package de.tarent.mica.bot.strategy.shipplacement;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import de.tarent.mica.bot.strategy.StrategyStats;
import de.tarent.mica.model.Fleet;
import de.tarent.mica.util.FleetSerializer;

/**
 * Diese Strategie ließt die Flottenpositionen aus Dateien aus. Wenn es mehrere
 * Dateien gibt, wird eine zufällig auserwählt.
 * 
 * @author rainu
 * 
 */
@StrategyStats(description = "Diese Strategie lie\u00dft die Flottenpositionen aus Dateien aus. Wenn es mehrere Dateien gibt, wird eine zuf\u00e4llig auserw\u00e4hlt.")
public class StaticShipPlacementStrategy extends ShipPlacementStrategy {
	private List<String> availableResourceURL = new ArrayList<String>();

	public StaticShipPlacementStrategy() {
		try {
			availableResourceURL.addAll(Arrays.asList(getResourceListing(
					StaticShipPlacementStrategy.class, "/static/fleet/")));
		} catch (Exception e) {
			throw new IllegalStateException(
					"This code should be never reached!", e);
		}
	}

	@Override
	public Fleet getFleet() {
		for(int i=0; i < 13; i++) Collections.shuffle(availableResourceURL);
		
		Random rnd = new Random();
		rnd.setSeed(System.currentTimeMillis());
		
		int max = rnd.nextInt() % 13;
		for(int i=0; i < max; i++) rnd.nextInt();

		String finalDestination = availableResourceURL.get(Math.abs(rnd.nextInt() % availableResourceURL.size()));
		
		try {
			if(finalDestination.startsWith("file:")){
				return FleetSerializer.deserialize(new File(new URL(finalDestination).toURI()));
			}else{
				return FleetSerializer.deserialize(getClass().getResourceAsStream(finalDestination));
			}
		} catch (Exception e) {
			throw new IllegalStateException("This code should be never reached because the fleet-files sould not be currupted!", e);
		}
	}

	/**
	 * List directory contents for a resource folder. Not recursive. This is
	 * basically a brute-force implementation. Works for regular files and also
	 * JARs.
	 * 
	 * @author Greg Briggs
	 *         (http://www.uofr.net/~greg/java/get-resource-listing.html)
	 * @param clazz
	 *            Any java class that lives in the same place as the resources
	 *            you want.
	 * @param path
	 *            Should end with "/", but not start with one.
	 * @return Just the name of each member item, not the full paths.
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	private String[] getResourceListing(Class clazz, String path)
			throws URISyntaxException, IOException {
		URL dirURL = clazz.getClassLoader().getResource(path);
		if (dirURL != null && dirURL.getProtocol().equals("file")) {
			/* A file path: easy enough */
			return new File(dirURL.toURI()).list();
		}

		if (dirURL == null) {
			/*
			 * In case of a jar file, we can't actually find a directory. Have
			 * to assume the same jar as clazz.
			 */
			String me = clazz.getName().replace(".", "/") + ".class";
			dirURL = clazz.getClassLoader().getResource(me);
		}

		if (dirURL.getProtocol().equals("jar")) {
			/* A JAR path */
			String jarPath = dirURL.getPath().substring(5,
					dirURL.getPath().indexOf("!")); // strip out only the JAR
													// file
			JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
			try{
				Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries
																// in jar
				Set<String> result = new HashSet<String>(); // avoid duplicates in
															// case it is a
															// subdirectory
				
				if(path.startsWith("/")){
					path = path.substring(1);
				}
				
				while (entries.hasMoreElements()) {
					String name = entries.nextElement().getName();
					if (name.startsWith(path)) { // filter according to the path
						String entry = name.substring(path.length());
						int checkSubdir = entry.indexOf("/");
						if (checkSubdir >= 0) {
							// if it is a subdirectory, we just return the directory
							// name
							entry = entry.substring(0, checkSubdir);
						}
						
						if(!entry.isEmpty()) result.add("/" + path + entry);
					}
				}
				return result.toArray(new String[result.size()]);
			}finally{
				try{ jar.close(); }catch(IOException e){}
			}
		}

		// [start].... mein Teil ...
		// Im Falle von Eclipse...

		if (dirURL.getProtocol().equals("file")) {
			// file:<projectDir>/target/classes/de/tarent/mica/bot/StaticShipPlacementStrategy.class
			String cutPart = clazz.getName();
			cutPart = cutPart.replace(".", "/") + ".class";
			
			String sUrl = dirURL.toExternalForm();
			sUrl = sUrl.replace(cutPart, "") + path;
			
			Set<String> result = new HashSet<String>();
			
			for(String r : new File(new URL(sUrl).toURI()).list()){
				result.add(sUrl + r);
			}
			
			return result.toArray(new String[result.size()]);
		}

		// [end] .... mein Teil ....
		throw new UnsupportedOperationException("Cannot list files for URL "+ dirURL);
	}
}
