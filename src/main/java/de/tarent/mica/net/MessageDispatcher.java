package de.tarent.mica.net;

import static de.tarent.mica.net.MessageCode.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tarent.mica.model.Coord;
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
	private Pattern enemyHitMessagePattern = Pattern.compile("^[0-9]*:.*The enemy hits .* at ([A-Za-z]*[0-9]*)\\.$");
	private Pattern enemyMissMessagePattern = Pattern.compile("^[0-9]*:.*Enemy shoots at ([A-Za-z]*[0-9]*) and misses\\.$");

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
				case NEXT_SHIP:
				case SHIP_READY:
				case ALL_SHIPS_READY:
					//ignorierte Messages...
					return;
				case HELLO:
					controller.started();
					controller.rename();
					return;
				case PLACE_SHIPS:
					controller.placeShips();
					return;
				case INVALID_MOVE:
				case INVALID_TORPEDO_SOURCE:
				case OUT_OF_SPECIALPOWERS:
				case YOUR_TURN:
					controller.myTurn();
					return;
				case ENEMY_SHIP_HIT:
					controller.hit();
					return;
				case ENEMY_SHIP_MISSED:
					controller.missed();
					return;
				case YOUR_SHIP_HIT:
					matcher = enemyHitMessagePattern.matcher(message);
					matcher.matches();
					controller.enemyHit(new Coord(matcher.group(1)));
					return;
				case YOUR_SHIP_MISSED:
					matcher = enemyMissMessagePattern.matcher(message);
					matcher.matches();
					controller.enemyMissed(new Coord(matcher.group(1)));
					return;
				default:
					break;
				}
			}
		}
		
		Logger.warn("Unknkown message type: " + message);
	}
}
