package de.tarent.mica.net;

import java.net.URI;
import java.net.URISyntaxException;

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
import de.tarent.mica.model.ship.Carrier;
import de.tarent.mica.model.ship.Destroyer;
import de.tarent.mica.model.ship.Submarine;
import de.tarent.mica.model.ship.AbstractShip.Orientation;
import de.tarent.mica.model.ship.Cruiser;
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
				// TODO Auto-generated method stub
				return null;
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
