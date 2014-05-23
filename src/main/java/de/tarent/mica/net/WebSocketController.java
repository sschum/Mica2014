package de.tarent.mica.net;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import de.tarent.mica.Controller;
import de.tarent.mica.GameActionHandler;
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
	
	void rename() {
		send("rename " + name);
	}
	
	void placeShips() {
		// TODO Auto-generated method stub
		
	}
	
	
	public static void main(String[] args) throws URISyntaxException {
		WebSocketController controller = new WebSocketController("localhost", 40000);
		controller.play("Rainu", null);
	}

	
}
