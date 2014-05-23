package de.tarent.mica;

import de.tarent.mica.model.ship.Carrier;
import de.tarent.mica.model.ship.Cruiser;
import de.tarent.mica.model.ship.Destroyer;
import de.tarent.mica.model.ship.Submarine;


/**
 * Dieses Interface beinhaltet alle Spieleevents, die auftreten können.
 * 
 * @author rainu
 *
 */
public interface GameActionHandler {
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
	 * Diese Methode wird aufgerufen, wenn die eigene Flotte gesetzt werden soll.
	 * 
	 * @return Die eigene Flotte (inklusive Positionen der einzelnen Schiffe)
	 */
	public Fleet getFleet();
	
	/**
	 * Diese Methode wird aufgerufen, sobald der Gegner eine Aktion durchgeführt hat.
	 * 
	 * @param action Die Aktion, die der Gegner durchgeführt hat.
	 */
	public void handleEnemyAction(Action action);
	
	/**
	 * Diese Methode wird aufgerufen, wenn man nach dem nächsten Schritt gefragt wird.
	 * 
	 * @return Die nächste Aktion, die ausgeführt werden soll.
	 */
	public Action getNextAction();
	
	/**
	 * Diese Methode wird aufgerufen, wenn das Spiel zuende ist.
	 * 
	 * @param win True wenn der Spieler gewonnen hat. Andernfals false.
	 */
	public void handleGameOver(boolean won);
}
