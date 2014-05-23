package de.tarent.mica.net;

import static de.tarent.mica.net.MessageCode.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tarent.mica.util.Logger;

/**
 * Diese Klasse ist dafür zuständig, bei eingehenden Nachrichten entsprechend
 * Aktionen zu verteilen.
 * 
 * @author rainu
 *
 */
public class MessageDispatcher {
	private Pattern messagePattern = Pattern.compile("^([0-9]*):.*$");

	private WebSocketController controller;
	
	MessageDispatcher(WebSocketController controller){
		this.controller = controller;
	}
	
	public void onMessage(String message){
		Matcher matcher = messagePattern.matcher(message);
		
		if(matcher.matches()){
			int msgCode = Integer.parseInt(matcher.group(1));
			MessageCode mc = MessageCode.fromCode(msgCode);
			if(mc != null){
				switch(mc){
				case NEW_NAME:
				case WAIT_FOR_SECOND_PLAYER_CONNECT:
					//ignorierte Messages...
					return;
				case HELLO:
					controller.rename();
					return;
				case PLACE_SHIPS:
					controller.placeShips();
					return;
				default:
					break;
				}
			}
		}
		
		Logger.warn("Unknkown message type: " + message);
	}
}
