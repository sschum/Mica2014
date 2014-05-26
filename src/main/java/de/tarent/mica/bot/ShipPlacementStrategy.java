package de.tarent.mica.bot;

import de.tarent.mica.model.Fleet;

/**
 * Eine Strategie, wie man Schiffe platziert.
 * 
 * @author rainu
 *
 */
public interface ShipPlacementStrategy {

	/**
	 * Liefert eine Flotte. Es muss nicht gewärleistet sein, dass immer die 
	 * gleiche zurückgeliefert wird!
	 */
	public Fleet getFleet();
}
