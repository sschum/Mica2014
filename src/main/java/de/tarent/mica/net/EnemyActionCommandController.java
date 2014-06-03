package de.tarent.mica.net;

import java.net.URI;

import org.java_websocket.drafts.Draft;

import de.tarent.mica.model.Coord;
import de.tarent.mica.model.Field.Element;
import de.tarent.mica.model.element.Carrier;
import de.tarent.mica.model.element.Cruiser;
import de.tarent.mica.model.element.Destroyer;
import de.tarent.mica.model.element.SpyArea;
import de.tarent.mica.model.element.Submarine;

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
	 * Der Gegner hat Wildfire eingesetzt!
	 */
	public void enemyUsedWildfire() {
		world.registerEnemySpecialAttack(Cruiser.class);
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
		world.registerEnemySpecialAttack(Destroyer.class);
	}
	
	/**
	 * Der Spieler wurde von dem Gegner zerbombt...
	 * 
	 * @param coord Wo war der einschlag?
	 */
	void enemyClusterbombed(Coord coord) {
		world.registerEnemySpecialAttack(Carrier.class);
		
		/*
		 * Eine Clusterbomb fügt ein "Kreuzschaden" zu.
		 * 
		 *   0 1 2
		 * A   +
		 * B + c +
		 * C   +
		 */
		//Da man sich nicht darauf verlassen sollte, in welcher reihenfolge die Nachrichten eintreffen
		//werde ich das Kreuz als Fehlschlag eintragen, sofern kein Treffer verzeichnet ist
		
		//mittelpunkt
		if(!Element.TREFFER.equals(world.getEnemyView().get(coord))){
			world.registerEnemyMiss(coord);
		}
		//nord
		Coord nord = coord.getNorthNeighbor();
		try{
			if(!Element.TREFFER.equals(world.getEnemyView().get(nord))){
				world.registerEnemyMiss(nord);
			}
		}catch(IllegalArgumentException e){}
		//ost
		Coord ost = coord.getEastNeighbor();
		try{
			if(!Element.TREFFER.equals(world.getEnemyView().get(ost))){
				world.registerEnemyMiss(ost);
			}
		}catch(IllegalArgumentException e){}
		//sued
		Coord sued = coord.getSouthNeighbor();
		try{
			if(!Element.TREFFER.equals(world.getEnemyView().get(sued))){
				world.registerEnemyMiss(sued);
			}
		}catch(IllegalArgumentException e){}
		//west
		Coord west = coord.getWestNeighbor();
		try{
			if(!Element.TREFFER.equals(world.getEnemyView().get(west))){
				world.registerEnemyMiss(west);
			}
		}catch(IllegalArgumentException e){}
	}
	
	/**
	 * Der Gegner hat einen Torpedo benutzt!
	 */
	void enemyUsedTorpedo() {
		world.registerEnemySpecialAttack(Submarine.class);		
	}
}
