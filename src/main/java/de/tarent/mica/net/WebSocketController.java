package de.tarent.mica.net;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import de.tarent.mica.Action;
import de.tarent.mica.Action.Type;
import de.tarent.mica.Controller;
import de.tarent.mica.GameActionHandler;
import de.tarent.mica.GameActionHandler.Fleet;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.World;
import de.tarent.mica.model.element.Carrier;
import de.tarent.mica.model.element.Cruiser;
import de.tarent.mica.model.element.Destroyer;
import de.tarent.mica.model.element.SpyArea;
import de.tarent.mica.model.element.Submarine;
import de.tarent.mica.model.element.AbstractShip.Orientation;
import de.tarent.mica.util.Logger;

/**
 * Diese Klasse ist dafür zuständig, mit dem SpielServer zu komunizieren.
 * 
 * @author rainu
 *
 */
public class WebSocketController extends WebSocketClient implements Controller {
	private final MessageDispatcher dispatcher = new MessageDispatcher(this);
	
	private String name;
	private GameActionHandler actionHandler;
	private World world;
	private List<Action> actionHistory;
	
	public WebSocketController(String host, int port) throws URISyntaxException {
		super(new URI("ws://" + host + ":" + port + "/battle"), new Draft_17());
	}
	
	/* ************************
	 * WebSocket-Handler
	 * ************************
	 */
	
	@Override
	public void onOpen(ServerHandshake handshake) {
		Logger.debug("ServerHandshake: " + handshake);
		Logger.info("Start new game with as " + name + "...");
		play();
	}
	
	@Override
	public void onMessage(String message) {
		Logger.debug("Incoming Message: " + message);
		dispatcher.onMessage(message);	//der dispatcher kümmert sich um die nachrichtenverarbeitung
	}
	
	@Override
	public void onError(Exception e) {
		Logger.debug("Incoming Error.", e);
	}
	
	@Override
	public void onClose(int code, String reason, boolean remote) {
		Logger.debug("Server closed connection. Reason: " + reason);
	}
	
	/* **************
	 * Controller-Handler
	 * **************
	 */
	
	@Override
	public void play(String name, GameActionHandler actionHandler) {
		this.name = name;
		this.actionHandler = actionHandler;
		try {
			connectBlocking();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/* ************
	 * Command-Methoden
	 * ************
	 */
	
	void play(){
		send("play");
	}
	
	void started() {
		//spiel wurde gestartet...
		//welt kann initialisiert werden
		world = new World(10, 10);
		actionHistory = new ArrayList<Action>();
	}
	
	void rename() {
		send("rename " + name);
	}
	
	void placeShips() {
		Fleet fleet = actionHandler.getFleet();
		sendFleetToServer(fleet);
		setFleetIntoModel(fleet);
		
		Logger.info("Player fleet configuration:\n" + world.getOwnField());
	}

	private void sendFleetToServer(Fleet fleet) {
		send(fleet.carrier1.getSpace().toString().replace("[", "").replace("]", "").replace(" ", ""));
		send(fleet.carrier2.getSpace().toString().replace("[", "").replace("]", "").replace(" ", ""));
		send(fleet.cruiser1.getSpace().toString().replace("[", "").replace("]", "").replace(" ", ""));
		send(fleet.cruiser2.getSpace().toString().replace("[", "").replace("]", "").replace(" ", ""));
		send(fleet.destroyer1.getSpace().toString().replace("[", "").replace("]", "").replace(" ", ""));
		send(fleet.destroyer2.getSpace().toString().replace("[", "").replace("]", "").replace(" ", ""));
		send(fleet.submarine1.getSpace().toString().replace("[", "").replace("]", "").replace(" ", ""));
		send(fleet.submarine2.getSpace().toString().replace("[", "").replace("]", "").replace(" ", ""));
	}

	private void setFleetIntoModel(Fleet fleet) {
		world.placeOwnShip(fleet.carrier1);
		world.placeOwnShip(fleet.carrier2);
		world.placeOwnShip(fleet.cruiser1);
		world.placeOwnShip(fleet.cruiser2);
		world.placeOwnShip(fleet.destroyer1);
		world.placeOwnShip(fleet.destroyer2);
		world.placeOwnShip(fleet.submarine1);
		world.placeOwnShip(fleet.submarine2);
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
		
		switch(lastAction.getType()){
		case ATTACK:
			world.registerHit(lastAction.getCoord()); break;
			
		/*
		 * Bei den Torpedos weis ich, dass diese so weit laufen, bis sie auf
		 * ein Schiff stoßen oder ins leere lief. 
		 */
		case TORPEDO_NORD:
			for(int y=lastAction.getCoord().getY() - 1; y > coord.getY(); y--){
				world.registerMiss(new Coord(coord.getX(), y));
			}
			world.registerHit(coord);
			break;
		case TORPEDO_OST:
			for(int x=lastAction.getCoord().getX() + 1; x < coord.getX(); x++){
				world.registerMiss(new Coord(x, coord.getY()));
			}
			world.registerHit(coord);
			break;
		case TORPEDO_SUED:
			for(int y=lastAction.getCoord().getY() + 1; y < coord.getY(); y++){
				world.registerMiss(new Coord(coord.getX(), y));
			}
			world.registerHit(coord);
			break;
		case TORPEDO_WEST:
			for(int x=lastAction.getCoord().getX() - 1; x > coord.getX(); x--){
				world.registerMiss(new Coord(x, coord.getY()));
			}
			world.registerHit(coord);
			break;
		}
	}

	/**
	 * Der Spieler hat verfehlt...
	 */
	void missed() {
		Action lastAction = actionHistory.get(actionHistory.size() - 1);
		
		switch(lastAction.getType()){
		case ATTACK:
			world.registerMiss(lastAction.getCoord()); break;
		/*
		 * Bei den Torpedos weis ich, dass diese so weit laufen, bis sie auf
		 * ein Schiff stoßen oder ins leere lief. 
		 */
		case TORPEDO_NORD:
			for(int y=lastAction.getCoord().getY() - 1; y >= 0; y--){
				world.registerMiss(new Coord(lastAction.getCoord().getX(), y));
			}
			break;
		case TORPEDO_OST:
			for(int x=lastAction.getCoord().getX() + 1; x < world.getWorldDimension().width; x++){
				world.registerMiss(new Coord(x, lastAction.getCoord().getY()));
			}
			break;
		case TORPEDO_SUED:
			for(int y=lastAction.getCoord().getY() + 1; y < world.getWorldDimension().height; y++){
				world.registerMiss(new Coord(lastAction.getCoord().getX(), y));
			}
			break;
		case TORPEDO_WEST:
			for(int x=lastAction.getCoord().getX() - 1; x >= 0; x--){
				world.registerMiss(new Coord(x, lastAction.getCoord().getY()));
			}
			break;
		}
	}
	
	/**
	 * Der Spieler hat den Gegner spioniert...
	 * 
	 * @param spyArea
	 */
	void spy(SpyArea spyArea) {
		world.registerSpy(spyArea);
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

	public static void main(String[] args) throws URISyntaxException {
		WebSocketController controller = new WebSocketController("localhost", 40000);
		controller.play("Rainu", new GameActionHandler() {
			
			@Override
			public void handleGameOver(boolean won) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void handleEnemyAction(Action action) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Action getNextAction(World world) {
				return new Action(Type.TORPEDO_WEST, new Coord("F7"));
			}
			
			@Override
			public Fleet getFleet() {
				Fleet fleet = new Fleet();
				
				fleet.carrier1 = new Carrier(Orientation.OST, new Coord("A1"));
				fleet.carrier2 = new Carrier(Orientation.OST, new Coord("G1"));
				fleet.cruiser1 = new Cruiser(Orientation.OST, new Coord("C1"));
				fleet.cruiser2 = new Cruiser(Orientation.OST, new Coord("C6"));
				fleet.destroyer1 = new Destroyer(Orientation.OST, new Coord("I1"));
				fleet.destroyer2 = new Destroyer(Orientation.OST, new Coord("E1"));
				fleet.submarine1 = new Submarine(Orientation.OST, new Coord("F7"));
				fleet.submarine2 = new Submarine(Orientation.OST, new Coord("I8"));
				
				return fleet;
			}
		});
	}
}
