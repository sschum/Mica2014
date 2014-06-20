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
import de.tarent.mica.model.element.Ship;
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
		world = new World(16, 16);
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
		for(Ship s : fleet.getAll()){
			send(s.getSpace().toString().replace("[", "").replace("]", "").replace(" ", ""));
		}
	}

	private void setFleetIntoModel(Fleet fleet) {
		for(Ship s : fleet.getAll()){
			world.placeOwnShip(s);
		}
	}
}
