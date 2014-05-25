package de.tarent.mica.net;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import de.tarent.mica.Action;
import de.tarent.mica.Action.Type;
import de.tarent.mica.Controller;
import de.tarent.mica.GameActionHandler;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.World;
import de.tarent.mica.model.element.AbstractShip.Orientation;
import de.tarent.mica.model.element.Carrier;
import de.tarent.mica.model.element.Cruiser;
import de.tarent.mica.model.element.Destroyer;
import de.tarent.mica.model.element.Submarine;
import de.tarent.mica.util.Logger;

/**
 * Diese Klasse ist dafür zuständig, mit dem SpielServer zu komunizieren.
 * 
 * @author rainu
 *
 */
public class WebSocketController extends EnemyActionCommandController implements Controller {
	private final MessageDispatcher dispatcher = new MessageDispatcher(this);
	
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
				return new Action(Type.CLUSTERBOMB, new Coord("C2"));
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
