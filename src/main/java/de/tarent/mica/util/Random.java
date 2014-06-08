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
		final int max = Math.abs(nextInt() % 13);
		for(int i = max; i >= 0; i--){
			toRun.run();
		}
	}
	
	@Override
	public int nextInt() {
		setSeed(13121989 * System.nanoTime());
		
		for(int i = super.nextInt() % 13; i >= 0; i--){
			super.nextInt();
		}
		
		return super.nextInt();
	}
	
	@Override
	public boolean nextBoolean() {
		return nextInt() % 2 == 0;
	}

	/**
	 * Liefert ein Zufälliges Element aus dem gegebenen Array.
	 * 
	 * @param values
	 * @return
	 */
	public <T> T choose(T[] values) {
		int index = Math.abs(nextInt() % values.length);
		return values[index];
	}
}
