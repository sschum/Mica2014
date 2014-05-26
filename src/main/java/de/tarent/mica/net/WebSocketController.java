package de.tarent.mica.net;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import de.tarent.mica.Action;
import de.tarent.mica.Action.Type;
import de.tarent.mica.Controller;
import de.tarent.mica.GameActionHandler;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.Fleet;
import de.tarent.mica.model.GameStats;
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
	protected MessageDispatcher dispatcher = new MessageDispatcher(this);
	protected List<String> messageLog;
	
	public WebSocketController(String host, int port) throws URISyntaxException {
		super(new URI("ws://" + host + ":" + port + "/battle"), new Draft_17());
	}

	/**
	 * Schreibt den protokolierten Nachrichtenaustausch in eine Datei.
	 * 
	 * @param dest
	 * @throws IOException 
	 */
	public void writeMessageLog(File dest) throws IOException {
		BufferedWriter bw = null;
		try{
			bw = new BufferedWriter(new FileWriter(dest));
			for(String msg : messageLog){
				bw.write(msg + "\n");
			}
			bw.flush();
		}finally{
			if(bw != null) try{ bw.close(); }catch(IOException e){}
		}
	}
	
	protected void logMessage(String message){
		messageLog.add("[R]" + message);
	}
	protected void logSend(String message){
		messageLog.add("[S]" + message);
	}
	
	/* ************************
	 * WebSocket-Handler
	 * ************************
	 */
	
	@Override
	public void onOpen(ServerHandshake handshake) {
		messageLog = Collections.synchronizedList(new ArrayList<String>());
		
		Logger.debug("ServerHandshake: " + handshake);
		Logger.info("Start new game with as " + ownName + "...");
		play();
	}
	
	@Override
	public void send(String text) throws NotYetConnectedException {
		logSend(text);
		super.send(text);
	}
	
	@Override
	public void onMessage(String message) {
		logMessage(message);
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
		if(name != null)this.ownName = name;
		
		this.actionHandler = actionHandler;
		
		try {
			connectBlocking();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	void gameOver(boolean won) {
		close();
		GameStats stats = new GameStats();
		stats.setWorld(world);
		stats.setPlayerName(ownName);
		stats.setEnemyName(enemyName);
		stats.setWon(won);
		
		actionHandler.handleGameOver(stats);
	}

	public static void main(String[] args) throws URISyntaxException, IOException {
		final WebSocketController controller = new WebSocketController("localhost", 40000);
		controller.play("Rainu", new GameActionHandler() {
			
			@Override
			public void handleGameOver(GameStats stats) {
				try {
					controller.writeMessageLog(new File("/tmp/RayShip_" + stats.getPlayerName() + "_vs_" + stats.getEnemyName() + ".log"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@Override
			public void handleEnemyAction(Action action) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Action getNextAction(World world) {
				return new Action(Type.CLUSTERBOMB, new Coord("E2"));
			}
			
			@Override
			public Fleet getFleet() {
				Fleet fleet = new Fleet();
				
				fleet.setCarrier1(new Carrier(Orientation.OST, new Coord("A1")));
				fleet.setCarrier2(new Carrier(Orientation.OST, new Coord("G1")));
				fleet.setCruiser1(new Cruiser(Orientation.OST, new Coord("C1")));
				fleet.setCruiser2(new Cruiser(Orientation.OST, new Coord("C6")));
				fleet.setDestroyer1(new Destroyer(Orientation.OST, new Coord("I1")));
				fleet.setDestroyer2(new Destroyer(Orientation.OST, new Coord("E1")));
				fleet.setSubmarine1(new Submarine(Orientation.OST, new Coord("F7")));
				fleet.setSubmarine2(new Submarine(Orientation.OST, new Coord("I8")));
				
				return fleet;
			}
		});
	}
}
