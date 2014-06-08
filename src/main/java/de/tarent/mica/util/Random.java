package de.tarent.mica.util;


/**
 * Eigene Random-Klasse, die zusätzliche funktionalitäten als {@link java.util.Random} bietet.
 * 
 * @author rainu
 *
 */
public class Random extends java.util.Random {
	private static final long serialVersionUID = 1L;

	/**
	 * Durchläuft das {@link Runnable} zufällige mal...
	 * 
	 * @param toRun
	 */
	public void runXTimes(Runnable toRun){
		setSeed(13121989 * System.currentTimeMillis());
		
		for(int i = nextInt() % 13; i >= 0; i--){
			toRun.run();
		}
	}
}
