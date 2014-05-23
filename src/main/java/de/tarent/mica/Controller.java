package de.tarent.mica;


/**
 * Dieses Interface definiert alle Aktionen, die ein 
 * Schiffeversenken-Server zur Verfügung stellt.
 * 
 * @author rainu
 *
 */
public interface Controller {
	/**
	 * Eröffnet ein neues Spiel. Ab diesen Zeitpunkt können weitere
	 * Aktionen durchgeführt werden. Nach diesem Methodenaufruf, ist
	 * das Spiel zuende!
	 * 
	 * @param name Gewünschter Spielername.
	 * @param actionHandler Der Aktionhandler für dieses Spiel
	 */
	void play(String name, GameActionHandler actionHandler);
}
