package de.tarent.mica.net;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.java_websocket.drafts.Draft;

import de.tarent.mica.Action;
import de.tarent.mica.Action.Type;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.Field.Element;
import de.tarent.mica.model.element.Ship;
import de.tarent.mica.model.element.SpyArea;

/**
 * Diese ist ein Teil des {@link WebSocketController}s. Diese Klasse beinhaltet
 * alle Spieler spezifischen Command-Methoden.
 * 
 * @author rainu
 *
 */
abstract class PlayerActionCommandController extends GeneralCommandController {
	private List<Action> actionHistory;
	private List<Coord> hitHistory;
	
	PlayerActionCommandController(URI serverUri, Draft draft) {
		super(serverUri, draft);
	}

	@Override
	void started(int playerNumber) {
		super.started(playerNumber);

		actionHistory = new ArrayList<Action>();
		hitHistory = new ArrayList<Coord>();
	}
	
	void myTurn(){
		Action action = actionHandler.getNextAction(world);
		switch(action.getType()){
		case ATTACK:
			attack(action.getCoord()); break;
		case CLUSTERBOMB:
			clusterbomb(action.getCoord()); break;
		case SPY_DRONE:
			spyDrone(action.getCoord()); break;
		case WILDFIRE:
			wildfire(action.getCoord()); break;
		case TORPEDO_NORD:
		case TORPEDO_OST:
		case TORPEDO_SUED:
		case TORPEDO_WEST:
			torpedo(action.getType(), action.getCoord()); break;
		}
		
		actionHistory.add(action);
	}
	
	private void torpedo(Type type, Coord coord) {
		switch(type){
		case TORPEDO_NORD:
			send("N" + coord); return;
		case TORPEDO_OST:
			send("O" + coord); return;
		case TORPEDO_SUED:
			send("S" + coord); return;
		case TORPEDO_WEST:
			send("W" + coord); return;
		default:
			throw new IllegalStateException("This code schould be never reached!");	
		}
	}

	private void wildfire(Coord coord) {
		send("*" + coord);
	}

	private void spyDrone(Coord coord) {
		send("#" + coord);
	}

	private void clusterbomb(Coord coord) {
		send("+" + coord);
	}

	private void attack(Coord coord) {
		send(coord.toString());
	}
	
	/**
	 * Der Spieler hat getroffen...
	 * @param coord Wo hab ich getroffen (optional)
	 */
	void hit(Coord coord) {
		Action lastAction = actionHistory.get(actionHistory.size() - 1);
		
		if(coord != null) hitHistory.add(coord);
		else hitHistory.add(lastAction.getCoord());
		
		switch(lastAction.getType()){
		case ATTACK:
			hitAttack(lastAction);
			break;
			
		/*
		 * Eine Clusterbomb fügt ein "Kreuzschaden" zu.
		 * 
		 *   0 1 2
		 * A   +
		 * B + c +
		 * C   +
		 */
		case CLUSTERBOMB:
			hitClusterbomb(coord, lastAction);
			break;
		/*
		 * Bei den Torpedos weis ich, dass diese so weit laufen, bis sie auf
		 * ein Schiff stoßen oder ins leere lief. 
		 */
		case TORPEDO_NORD:
			hitTorpedoNord(coord, lastAction);
			break;
		case TORPEDO_OST:
			hitTorpedoOst(coord, lastAction);
			break;
		case TORPEDO_SUED:
			hitTorpedoSued(coord, lastAction);
			break;
		case TORPEDO_WEST:
			hitTorpedoWest(coord, lastAction);
			break;
		}
	}

	private void hitAttack(Action lastAction) {
		world.registerHit(lastAction.getCoord());
	}
	
	private void hitClusterbomb(Coord coord, Action lastAction) {
		//Da man sich nicht darauf verlassen sollte, in welcher reihenfolge die Nachrichten eintreffen
		//werde ich das Kreuz als Fehlschlag eintragen, sofern kein Treffer verzeichnet ist
		Coord lastCoord = lastAction.getCoord();
		
		//mittelpunkt
		if(!Element.TREFFER.equals(world.getEnemyField().get(lastCoord))){
			world.registerMiss(lastCoord);
		}
		//nord
		Coord nord = lastCoord.getNorthNeighbor();
		try{
			if(!Element.TREFFER.equals(world.getEnemyField().get(nord))){
				world.registerMiss(nord);
			}
		}catch(IllegalArgumentException e){}
		//ost
		Coord ost = lastCoord.getEastNeighbor();
		try{
			if(!Element.TREFFER.equals(world.getEnemyField().get(ost))){
				world.registerMiss(ost);
			}
		}catch(IllegalArgumentException e){}
		//sued
		Coord sued = lastCoord.getSouthNeighbor();
		try{
			if(!Element.TREFFER.equals(world.getEnemyField().get(sued))){
				world.registerMiss(sued);
			}
		}catch(IllegalArgumentException e){}
		//west
		Coord west = lastCoord.getWestNeighbor();
		try{
			if(!Element.TREFFER.equals(world.getEnemyField().get(west))){
				world.registerMiss(west);
			}
		}catch(IllegalArgumentException e){}
		
		if(coord != null){
			world.registerHit(coord);
		}
	}
	
	private void hitTorpedoNord(Coord coord, Action lastAction) {
		for(int y=lastAction.getCoord().getY() - 1; y > coord.getY(); y--){
			world.registerMiss(new Coord(coord.getX(), y));
		}
		world.registerHit(coord);
	}
	
	private void hitTorpedoOst(Coord coord, Action lastAction) {
		for(int x=lastAction.getCoord().getX() + 1; x < coord.getX(); x++){
			world.registerMiss(new Coord(x, coord.getY()));
		}
		world.registerHit(coord);
	}
	
	private void hitTorpedoSued(Coord coord, Action lastAction) {
		for(int y=lastAction.getCoord().getY() + 1; y < coord.getY(); y++){
			world.registerMiss(new Coord(coord.getX(), y));
		}
		world.registerHit(coord);
	}
	
	private void hitTorpedoWest(Coord coord, Action lastAction) {
		for(int x=lastAction.getCoord().getX() - 1; x > coord.getX(); x--){
			world.registerMiss(new Coord(x, coord.getY()));
		}
		world.registerHit(coord);
	}

	/**
	 * Der Spieler hat verfehlt...
	 */
	void missed() {
		Action lastAction = actionHistory.get(actionHistory.size() - 1);
		
		switch(lastAction.getType()){
		case ATTACK:
			missedAttack(lastAction); break;
		/*
		 * Bei den Torpedos weis ich, dass diese so weit laufen, bis sie auf
		 * ein Schiff stoßen oder ins leere lief. 
		 */
		case TORPEDO_NORD:
			missedTorpedoNord(lastAction);
			break;
		case TORPEDO_OST:
			missedTorpedoOst(lastAction);
			break;
		case TORPEDO_SUED:
			missedTorpedoSued(lastAction);
			break;
		case TORPEDO_WEST:
			missedTorpedoWest(lastAction);
			break;
		}
	}

	private void missedAttack(Action lastAction) {
		world.registerMiss(lastAction.getCoord());
	}

	private void missedTorpedoNord(Action lastAction) {
		for(int y=lastAction.getCoord().getY() - 1; y >= 0; y--){
			world.registerMiss(new Coord(lastAction.getCoord().getX(), y));
		}
	}

	private void missedTorpedoOst(Action lastAction) {
		for(int x=lastAction.getCoord().getX() + 1; x < world.getWorldDimension().width; x++){
			world.registerMiss(new Coord(x, lastAction.getCoord().getY()));
		}
	}

	private void missedTorpedoSued(Action lastAction) {
		for(int y=lastAction.getCoord().getY() + 1; y < world.getWorldDimension().height; y++){
			world.registerMiss(new Coord(lastAction.getCoord().getX(), y));
		}
	}

	private void missedTorpedoWest(Action lastAction) {
		for(int x=lastAction.getCoord().getX() - 1; x >= 0; x--){
			world.registerMiss(new Coord(x, lastAction.getCoord().getY()));
		}
	}
	
	/**
	 * Der Spieler hat ein Schiff versenkt.
	 * 
	 * @param shipType Welches Schiff wurde versenkt?
	 */
	private static final Set<Element> bl = Collections.unmodifiableSet(new HashSet<Element>(Arrays.asList(Element.SCHIFF, Element.TREFFER)));
	void sunk(String shipType) {
		//TODO: versuchen ein schiff brennen zu lassen und dann ein schiff zu versinken: In einer Runde zwei schiffe versenkt?!
		Coord lastHit = hitHistory.get(hitHistory.size() - 1);
		world.registerSunk(lastHit);
		
		//wenn ein schiff versunken wird, weis ich, das um ihn herum nichts sein kann
		//da man keine Schiffe nebeneinander platzieren kann/darf
		for(Ship ship : world.getEnemyShips()){
			if(!ship.isSunken()) continue;
			
			for(Coord c : ship.getSpace()){
				//NORD
				Coord neighbor = c.getNorthNeighbor();
				if(	world.isInWorld(neighbor) && 
					!bl.contains(world.getEnemyField().get(neighbor))){
					
					world.registerNothing(neighbor);
				}
				//OST
				neighbor = c.getEastNeighbor();
				if(	world.isInWorld(neighbor) && 
					!bl.contains(world.getEnemyField().get(neighbor))){
					
					world.registerNothing(neighbor);
				}
				//SUED
				neighbor = c.getSouthNeighbor();
				if(	world.isInWorld(neighbor) && 
					!bl.contains(world.getEnemyField().get(neighbor))){
					
					world.registerNothing(neighbor);
				}
				//WEST
				neighbor = c.getWestNeighbor();
				if(	world.isInWorld(neighbor) && 
					!bl.contains(world.getEnemyField().get(neighbor))){
					
					world.registerNothing(neighbor);
				}
			}
		}
		
		System.out.println(world);
	}
	
	/**
	 * Der Spieler hat den Gegner spioniert...
	 * 
	 * @param spyArea
	 */
	void spy(SpyArea spyArea) {
		world.registerSpy(spyArea);
	}
}
