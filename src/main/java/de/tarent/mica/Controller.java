package de.tarent.mica;

import java.util.List;

import de.tarent.mica.model.GameStats;


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
	 * @return Die Spielergebnise
	 */
	List<GameStats> play(String name, GameActionHandler actionHandler);
}
