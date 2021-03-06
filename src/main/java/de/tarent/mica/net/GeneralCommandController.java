package de.tarent.mica.net;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
	protected String playerName;
	protected String enemyName;
	protected GameActionHandler actionHandler;
	protected World world;
	protected List<GameStats> gameStats = new ArrayList<GameStats>();
	
	GeneralCommandController(URI serverUri, Draft draft) {
		super(serverUri, draft);
	}
	
	protected void reset() {
		this.world = null;
		
		GameStats stats = new GameStats();
		this.gameStats.add(stats);
	}

	protected GameStats getCurrentGameStats(){
		return gameStats.get(gameStats.size() - 1);
	}
	
	void play(){
		send("play");
	}
	
	void started(int playerNumber) {
		ownNumber = playerNumber;
	}
	
	void newGame(int rounds, String enemyName){
		this.enemyName = enemyName;
	}
	
	void renamed(int playerNumber, String playerName) {
		if(playerNumber == ownNumber){
			this.playerName = playerName;
		}else{
			this.enemyName = playerName;
		}
	}
	
	void rename() {
		if(playerName != null){
			send("rename " + playerName);
		}
	}
	
	void placeShips() {
		reset();
		
		Logger.info("Start round #" + gameStats.size());
		
		//spiel wurde gestartet...
		//welt kann initialisiert werden
		world = new World(16, 16);
		
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
