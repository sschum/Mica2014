package de.tarent.mica.net;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;

import de.tarent.mica.GameActionHandler;
import de.tarent.mica.model.Fleet;
import de.tarent.mica.model.GameStats;
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
	protected GameActionHandler actionHandler;
	protected World world;
	protected GameStats gameStats = new GameStats();
	
	GeneralCommandController(URI serverUri, Draft draft) {
		super(serverUri, draft);
		
		reset(null);
	}
	
	protected void reset(GameActionHandler actionHandler) {
		this.world = null;
		this.gameStats = new GameStats();
		this.actionHandler = actionHandler;
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
			this.gameStats.setPlayerName(playerName);
		}else{
			this.gameStats.setEnemyName(playerName);
		}
	}
	
	void rename() {
		if(gameStats.getPlayerName() != null){
			send("rename " + gameStats.getPlayerName());
		}
	}
	
	void placeShips() {
		Fleet fleet = actionHandler.getFleet();
		sendFleetToServer(fleet);
		setFleetIntoModel(fleet);
		
		Logger.info("Player fleet configuration:\n" + world.getOwnField());
		
		StringBuffer sb = new StringBuffer("Special-Attacks:");
		sb.append("\n* Clusterbombs: ");
		sb.append(Ship.getTheoreticallySpecialAttacks(fleet.getCarrier1(), fleet.getCarrier2()));
		sb.append("\n* Wildfire: ");
		sb.append(Ship.getTheoreticallySpecialAttacks(fleet.getCruiser1(), fleet.getCruiser2()));
		sb.append("\n* Spy: ");
		sb.append(Ship.getTheoreticallySpecialAttacks(fleet.getDestroyer1(), fleet.getDestroyer2()));
		sb.append("\n* Torpedo: ");
		sb.append(Ship.getTheoreticallySpecialAttacks(fleet.getSubmarine1(), fleet.getSubmarine2()));
		
		Logger.info(sb.toString());
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
