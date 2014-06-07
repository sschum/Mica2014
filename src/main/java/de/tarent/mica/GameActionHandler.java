package de.tarent.mica;

import de.tarent.mica.model.Fleet;
import de.tarent.mica.model.GameStats;
import de.tarent.mica.model.World;


/**
 * Dieses Interface beinhaltet alle Spieleevents, die auftreten können.
 * 
 * @author rainu
 *
 */
public interface GameActionHandler {
	/**
	 * Diese Methode wird aufgerufen, wenn die eigene Flotte gesetzt werden soll.
	 * 
	 * @return Die eigene Flotte (inklusive Positionen der einzelnen Schiffe)
	 */
	public Fleet getFleet();
	
	/** TODO: Benötige ich diese Methode wirklich?
	 * Diese Methode wird aufgerufen, sobald der Gegner eine Aktion durchgeführt hat.
	 * 
	 * @param action Die Aktion, die der Gegner durchgeführt hat.
	 */
	public void handleEnemyAction(Action action);
	
	/**
	 * Diese Methode wird aufgerufen, wenn man nach dem nächsten Schritt gefragt wird.
	 * 
	 * @param world Die aktuelle Spielewelt. 
	 * @return Die nächste Aktion, die ausgeführt werden soll.
	 */
	public Action getNextAction(World world);
}
