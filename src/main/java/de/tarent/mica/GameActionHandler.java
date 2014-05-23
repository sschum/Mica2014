package de.tarent.mica;


/**
 * Dieses Interface beinhaltet alle Spieleevents, die auftreten können.
 * 
 * @author rainu
 *
 */
public interface GameActionHandler {

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
