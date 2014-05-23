package de.tarent.mica;

import de.tarent.mica.model.ship.Carrier;
import de.tarent.mica.model.ship.Cruiser;
import de.tarent.mica.model.ship.Destroyer;
import de.tarent.mica.model.ship.Submarine;

/**
 * Dieses Interface definiert alle Aktionen, die ein 
 * Schiffeversenken-Server zur Verfügung stellt.
 * 
 * @author rainu
 *
 */
public interface Controller {
	public static class Fleet {
		public Carrier carrier1;
		public Carrier carrier2;
		public Cruiser cruiser1;
		public Cruiser cruiser2;
		public Destroyer destroyer1;
		public Destroyer destroyer2;
		public Submarine submarine1;
		public Submarine submarine2;
	}
	
	/**
	 * Eröffnet ein neues Spiel. Ab diesen Zeitpunkt können weitere
	 * Aktionen durchgeführt werden. Nach diesem Methodenaufruf, ist
	 * das Spiel zuende!
	 * 
	 * @param name Gewünschter Spielername.
	 * @param fleet Die eigene Flotte (und damit ihre Positionen)
	 * @param actionHandler Der Aktionhandler für dieses Spiel
	 */
	void play(String name, Fleet fleet, GameActionHandler actionHandler);
}
