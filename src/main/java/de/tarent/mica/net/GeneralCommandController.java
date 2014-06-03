package de.tarent.mica.net;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;

import de.tarent.mica.Action;
import de.tarent.mica.GameActionHandler;
import de.tarent.mica.model.Fleet;
import de.tarent.mica.model.World;
import de.tarent.mica.util.Logger;

/**
 * Dies ist die unterste Ebene des {@link WebSocketController}s. Hier sind alle
 * allgemeinen Command-Methoden definiert.
 * 
 * @author rainu
 *
 */
abstract class GeneralCommandController extends WebSocketClient {
	protected int ownNumber;
	protected String ownName;
	protected String enemyName;
	protected GameActionHandler actionHandler;
	protected World world;
	
	GeneralCommandController(URI serverUri, Draft draft) {
		super(serverUri, draft);
	}

	void play(){
		send("play");
	}
	
	void started(int playerNumber) {
		//spiel wurde gestartet...
		//welt kann initialisiert werden
		world = new World(10, 10);
		ownNumber = playerNumber;
	}
	
	void renamed(int playerNumber, String playerName) {
		if(playerNumber == ownNumber){
			this.ownName = playerName;
		}else{
			this.enemyName = playerName;
		}
	}
	
	void rename() {
		if(ownName != null){
			send("rename " + ownName);
		}
	}
	
	void placeShips() {
		Fleet fleet = actionHandler.getFleet();
		sendFleetToServer(fleet);
		setFleetIntoModel(fleet);
		
		Logger.info("Player fleet configuration:\n" + world.getOwnField());
	}

	private void sendFleetToServer(Fleet fleet) {
		send(fleet.getCarrier1().getSpace().toString().replace("[", "").replace("]", "").replace(" ", ""));
		send(fleet.getCarrier2().getSpace().toString().replace("[", "").replace("]", "").replace(" ", ""));
		send(fleet.getCruiser1().getSpace().toString().replace("[", "").replace("]", "").replace(" ", ""));
		send(fleet.getCruiser2().getSpace().toString().replace("[", "").replace("]", "").replace(" ", ""));
		send(fleet.getDestroyer1().getSpace().toString().replace("[", "").replace("]", "").replace(" ", ""));
		send(fleet.getDestroyer2().getSpace().toString().replace("[", "").replace("]", "").replace(" ", ""));
		send(fleet.getSubmarine1().getSpace().toString().replace("[", "").replace("]", "").replace(" ", ""));
		send(fleet.getSubmarine2().getSpace().toString().replace("[", "").replace("]", "").replace(" ", ""));
	}

	private void setFleetIntoModel(Fleet fleet) {
		world.placeOwnShip(fleet.getCarrier1());
		world.placeOwnShip(fleet.getCarrier2());
		world.placeOwnShip(fleet.getCruiser1());
		world.placeOwnShip(fleet.getCruiser2());
		world.placeOwnShip(fleet.getDestroyer1());
		world.placeOwnShip(fleet.getDestroyer2());
		world.placeOwnShip(fleet.getSubmarine1());
		world.placeOwnShip(fleet.getSubmarine2());
	}
}
