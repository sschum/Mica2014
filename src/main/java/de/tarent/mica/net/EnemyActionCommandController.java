package de.tarent.mica.net;

import java.net.URI;

import org.java_websocket.drafts.Draft;

import de.tarent.mica.model.Coord;
import de.tarent.mica.model.element.SpyArea;

/**
 * Diese ist ein Teil des {@link WebSocketController}s. Diese Klasse beinhaltet
 * alle Gegner spezifischen Command-Methoden.
 * 
 * @author rainu
 *
 */
abstract class EnemyActionCommandController extends PlayerActionCommandController {

	EnemyActionCommandController(URI serverUri, Draft draft) {
		super(serverUri, draft);
	}

	/**
	 * Der Gegner hat getroffen...
	 * 
	 * @param coord Wo?
	 */
	void enemyHit(Coord coord) {
		world.registerEnemyHit(coord);
	}
	
	/**
	 * Der Gegner hat getroffen... Das Schiff brennt...
	 * 
	 * @param coord Wo?
	 */
	void enemyBurnHit(Coord coord) {
		world.registerEnemyBurn(coord);
	}

	/**
	 * Der Gegner hat verfehlt...
	 * 
	 * @param coord Wo?
	 */
	void enemyMissed(Coord coord) {
		world.registerEnemyMiss(coord);
	}
	
	/**
	 * Ein Schiff des Spielers wurde versenkt...
	 * 
	 * @param coord Welches?
	 */
	void enemySunk(Coord coord) {
		world.registerEnemySunk(coord);
	}
	
	/**
	 * Der Spieler wurde von dem Gegner ausspioniert...
	 * 
	 * @param spyArea
	 */
	void enemySpy(SpyArea spyArea) {
		world.registerEnemySpy(spyArea);
	}
}
