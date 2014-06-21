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

import de.tarent.mica.Controller;
import de.tarent.mica.GameActionHandler;
import de.tarent.mica.model.GameStats;
import de.tarent.mica.util.Logger;

/**
 * Diese Klasse ist dafür zuständig, mit dem SpielServer zu komunizieren.
 * 
 * @author rainu
 * TODO: Erweiterte Logit nicht in diese Controllerhirarchie! (Hier nur Treffer auswerten alles andere jmd anderen überlassen)
 */
public class WebSocketController extends EnemyActionCommandController implements Controller {
	protected MessageDispatcher dispatcher = new MessageDispatcher(this);
	protected List<String> messageLog;
	protected GameStats gameStats = null;
	protected boolean isPlaying = false;
	
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
		Logger.debug("Outgoing Message: " + text);
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
		stopPlay();
	}
	
	protected synchronized void stopPlay(){
		this.isPlaying = false;
		this.notifyAll();
	}
	
	/* **************
	 * Controller-Handler
	 * **************
	 */
	
	@Override
	public synchronized GameStats play(String name, GameActionHandler actionHandler) {
		if(name != null)this.ownName = name;
		
		this.gameStats = null;
		this.actionHandler = actionHandler;
		this.isPlaying = true;
		
		try {
			connectBlocking();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		while(isPlaying){
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		
		return this.gameStats;
	}
	
	void gameOver(boolean won) {
		close();
		GameStats stats = new GameStats();
		stats.setWorld(world);
		stats.setPlayerName(ownName);
		stats.setEnemyName(enemyName);
		stats.setWon(won);
		
		this.gameStats = stats;
		
		stopPlay();
	}

}