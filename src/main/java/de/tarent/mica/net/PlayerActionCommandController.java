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
import de.tarent.mica.model.element.Carrier;
import de.tarent.mica.model.element.Destroyer;
import de.tarent.mica.model.element.Ship;
import de.tarent.mica.model.element.SpyArea;
import de.tarent.mica.model.element.Submarine;
import de.tarent.mica.util.Logger;

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
	private boolean repeatLastTurn = false;
	
	PlayerActionCommandController(URI serverUri, Draft draft) {
		super(serverUri, draft);
	}

	@Override
	protected void reset() {
		super.reset();
		
		repeatLastTurn = false;
	}
	
	@Override
	void placeShips() {
		super.placeShips();

		actionHistory = new ArrayList<Action>();
		hitHistory = new ArrayList<Coord>();
	}
	
	public void turnToSoon() {
		repeatLastTurn = true;
		decreasePlayerMoves();
	}
	
	void clusterbombed(boolean hit){
		world.increaseSpecialAttack(Carrier.class);
		
		//wenn eine Clusterbombe getroffen hat, wird das durch die Mthode "hit" abgedeckt!
		if(!hit){
			//wenn sie allerding gar nichts getroffen hat, muss sie wenigstens noch registriert werden
			Action lastAction = actionHistory.get(actionHistory.size() - 1);
			registerClusterbomb(null, lastAction);
			
			Logger.debug("World:\n" + world);
		}
	}
	
	void myTurn(){
		
		Action action = null;
		
		if(repeatLastTurn && !actionHistory.isEmpty()){
			action = actionHistory.get(actionHistory.size() - 1);
			repeatLastTurn = false;
		}else{
			action = actionHandler.getNextAction(world);
		}

		if(action == null){
			//ich hab keine chance mehr ;(
			send("13: Ich geb' off!");
			close();
			return;
		}
		
		switch(action.getType()){
		case ATTACK:
			attack(action.getCoord()); 
			break;
		case CLUSTERBOMB:
			clusterbomb(action.getCoord()); 
			break;
		case SPY_DRONE:
			spyDrone(action.getCoord()); 
			break;
		case WILDFIRE:
			wildfire(action.getCoord()); 
			break;
		case TORPEDO_NORD:
		case TORPEDO_OST:
		case TORPEDO_SUED:
		case TORPEDO_WEST:
			torpedo(action.getType(), action.getCoord()); 
			break;
		}
		
		actionHistory.add(action);
		increasePlayerMoves();
	}
	
	private void torpedo(Type type, Coord coord) {
		switch(type){
		case TORPEDO_NORD:
			send("N" + coord); break;
		case TORPEDO_OST:
			send("O" + coord); break;
		case TORPEDO_SUED:
			send("S" + coord); break;
		case TORPEDO_WEST:
			send("W" + coord); break;
		default:
			throw new IllegalStateException("This code should be never reached!");	
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
		hit(coord, null);
	}
	
	/**
	 * Der Spieler hat getroffen und das Schiff wurde versenkt
	 * 
	 * @param coord
	 * @param sunkenShipType
	 */
	void hit(Coord coord, String sunkenShipType) {
		Action lastAction = actionHistory.get(actionHistory.size() - 1);
		
		if(coord != null){
			hitHistory.add(coord);
		} else {
			switch(lastAction.getType()){
			case ATTACK:
			case CLUSTERBOMB:
			case WILDFIRE:
				//bei einem torpedo beinhaltet die letzte Aktion NICHT den Treffer
				hitHistory.add(lastAction.getCoord());
				break;
			}
		}
		
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
			registerClusterbomb(coord, lastAction);
			break;
		/*
		 * Bei den Torpedos weis ich, dass diese so weit laufen, bis sie auf
		 * ein Schiff stoßen oder ins leere lief. 
		 */
		case TORPEDO_NORD:
			if(coord != null) hitTorpedoNord(coord, lastAction);
			else hitTorpedoNord(hitHistory.get(hitHistory.size() - 1), lastAction);
			world.increaseSpecialAttack(Submarine.class);
			break;
		case TORPEDO_OST:
			if(coord != null) hitTorpedoOst(coord, lastAction);
			else hitTorpedoOst(hitHistory.get(hitHistory.size() - 1), lastAction);
			world.increaseSpecialAttack(Submarine.class);
			break;
		case TORPEDO_SUED:
			if(coord != null) hitTorpedoSued(coord, lastAction);
			else hitTorpedoSued(hitHistory.get(hitHistory.size() - 1), lastAction);
			world.increaseSpecialAttack(Submarine.class);
			break;
		case TORPEDO_WEST:
			if(coord != null) hitTorpedoWest(coord, lastAction);
			else hitTorpedoWest(hitHistory.get(hitHistory.size() - 1), lastAction);
			world.increaseSpecialAttack(Submarine.class);
			break;
		case WILDFIRE:
			//TODO: Schiff als brennend markieren?!
			world.increaseSpecialAttack(Carrier.class);
			break;
		}
		
		//hab ich diesen bereich spioniert? Wenn ja, ist dieser jetzt abgeräumt?
		checkSpyAreas();
		
		if(sunkenShipType != null){
			sunk(sunkenShipType);
		}
		if(lastAction.getType() == Type.ATTACK){
			//nach normalen attacken dürfen wir nocheinmal...
			myTurn();
		}
		
		Logger.debug("World:\n" + world);
	}

	private void hitAttack(Action lastAction) {
		world.registerHit(lastAction.getCoord());
	}
	
	private void registerClusterbomb(Coord coord, Action lastAction) {
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

	protected void checkSpyAreas() {
		for(SpyArea area : world.getSpyAreas()){
			Set<Coord> areaCoords = collectSpyAreaCoords(area);
			
			int hit = 0;
			for(Coord c : areaCoords){
				if(world.isInWorld(c) && world.getEnemyField().get(c) == Element.TREFFER){
					hit++;
				}
			}

			if(hit == area.getSegments()){
				//alles abgegrast!
				for(Coord c : areaCoords){
					if(world.isInWorld(c) && world.getEnemyField().get(c) == Element.UNBEKANNT){
						world.registerMiss(c);
					}
				}
			}
		}
	}

	protected Set<Coord> collectSpyAreaCoords(SpyArea area) {
		Set<Coord> areaCoords = new HashSet<Coord>();
		areaCoords.add(area.getCoord());
		areaCoords.add(area.getCoord().getNorthNeighbor());
		areaCoords.add(area.getCoord().getSouthNeighbor());
		areaCoords.add(area.getCoord().getEastNeighbor());
		areaCoords.add(area.getCoord().getWestNeighbor());
		areaCoords.add(area.getCoord().getNorthNeighbor().getWestNeighbor());
		areaCoords.add(area.getCoord().getNorthNeighbor().getEastNeighbor());
		areaCoords.add(area.getCoord().getSouthNeighbor().getWestNeighbor());
		areaCoords.add(area.getCoord().getSouthNeighbor().getEastNeighbor());
		return areaCoords;
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
			world.increaseSpecialAttack(Submarine.class);
			break;
		case TORPEDO_OST:
			missedTorpedoOst(lastAction);
			world.increaseSpecialAttack(Submarine.class);
			break;
		case TORPEDO_SUED:
			missedTorpedoSued(lastAction);
			world.increaseSpecialAttack(Submarine.class);
			break;
		case TORPEDO_WEST:
			missedTorpedoWest(lastAction);
			world.increaseSpecialAttack(Submarine.class);
			break;
		}
		
		Logger.debug("World:\n" + world);
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
		Action lastAction = actionHistory.get(actionHistory.size() - 1);
		if(lastAction.getType() == Type.ATTACK){
			//letzter angriff bekommt keine "hit"-Nachricht mehr!
			world.registerHit(lastAction.getCoord());
		}
		
		Coord lastHit = hitHistory.get(hitHistory.size() - 1);
		world.registerSunk(lastHit);

		//wenn ein schiff versunken wird, weis ich, das um ihn herum nichts sein kann
		//da man keine Schiffe nebeneinander platzieren kann/darf
		for(Ship ship : world.getEnemyShips()){
			if(!ship.isSunken()) continue;
			
			Set<Coord> frameCoords = ship.getFrameCoordinates();
			for(Coord c : frameCoords){
				if(	world.isInWorld(c) && 
					!bl.contains(world.getEnemyField().get(c))){
					
					world.registerNothing(c);
				}
			}
		}
	}
	
	/**
	 * Der Spieler hat den Gegner spioniert...
	 * 
	 * @param spyArea
	 */
	void spy(SpyArea spyArea) {
		/*
		 *  Wenn im Spionierten bereich bereits ZUVOR ein o. mehrere Schiffsektoren getroffen wurden,
		 *  werden vom Server NUR die anzahl unentdeckter(!) Sektoren mitgeteilt
		 */
		Set<Coord> spyCoords = collectSpyAreaCoords(spyArea);
		for(Coord c : spyCoords){
			if(world.isInWorld(c) && world.getEnemyField().get(c) == Element.TREFFER){
				spyArea.setSegments(spyArea.getSegments() + 1);
			}
		}
		
		
		world.registerSpy(spyArea);
		world.increaseSpecialAttack(Destroyer.class);
		checkSpyAreas();
		
		Logger.debug("World:\n" + world);
	}
	
	void increasePlayerMoves(){
		getCurrentGameStats().setPlayerMoves(getCurrentGameStats().getPlayerMoves() + 1);
	}
	
	void decreasePlayerMoves(){
		getCurrentGameStats().setPlayerMoves(getCurrentGameStats().getPlayerMoves() - 1);
	}
}
