package de.tarent.mica.net;

import static de.tarent.mica.net.MessageCode.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tarent.mica.model.Coord;
import de.tarent.mica.model.element.SpyArea;
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
	private Pattern hitMessagePattern = Pattern.compile("^[0-9]*:.*Enemy ship hit at ([A-Za-z]*[0-9]*)\\.$");
	private Pattern enemyHitMessagePattern = Pattern.compile("^[0-9]*:.*The enemy hits .*at ([A-Za-z]*[0-9]*)\\.$");
	private Pattern enemyHitBurnedMessagePattern = Pattern.compile("^[0-9]*:.*Your .*burned at ([A-Za-z]*[0-9]*)!$");
	private Pattern enemyMissMessagePattern = Pattern.compile("^[0-9]*:.*Enemy shoots at ([A-Za-z]*[0-9]*) and misses\\.$");
	private Pattern playerSunkMessagePattern = Pattern.compile("^[0-9]*:.*at ([A-Za-z]*[0-9]*)!.*$");
	private Pattern spyMesssagePattern = Pattern.compile("^[0-9]*:.*The drone found ([0-9]*) ship segments at ([A-Za-z]*[0-9]*)!$");
	private Pattern enemySpyMessagePattern = Pattern.compile("^[0-9]*:.*at ([A-Za-z]*[0-9]*)!.*$");

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
				case WAIT_FOR_OTHER_PLAYERS_MOVE:
				case NEXT_SHIP:
				case SHIP_READY:
				case ALL_SHIPS_READY:
				case TORPEDOEE:
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
					matcher = hitMessagePattern.matcher(message);
					if(matcher.matches()){
						controller.hit(new Coord(matcher.group(1)));
					}else{
						controller.hit(null);
					}
					return;
				case TORPEDO:
				case ENEMY_SHIP_MISSED:
					controller.missed();
					return;
				case DRONE:
					matcher = spyMesssagePattern.matcher(message);
					matcher.matches();
					controller.spy(new SpyArea(new Coord(matcher.group(2)), Integer.parseInt(matcher.group(1))));
					return;
				case YOUR_SHIP_HIT:
					matcher = enemyHitMessagePattern.matcher(message);
					if(matcher.matches()){
						controller.enemyHit(new Coord(matcher.group(1)));
					}else{
						matcher = enemyHitBurnedMessagePattern.matcher(message);
						matcher.matches();
						controller.enemyBurnHit(new Coord(matcher.group(1)));
					}
					return;
				case YOUR_SHIP_MISSED:
					matcher = enemyMissMessagePattern.matcher(message);
					matcher.matches();
					controller.enemyMissed(new Coord(matcher.group(1)));
					return;
				case YOUR_SHIP_SUNK:
					matcher = playerSunkMessagePattern.matcher(message);
					matcher.matches();
					controller.enemySunk(new Coord(matcher.group(1)));
					return;
				case DRONEEE:
					matcher = enemySpyMessagePattern.matcher(message);
					matcher.matches();
					controller.enemySpy(new SpyArea(new Coord(matcher.group(1))));
					return;
				default:
					break;
				}
			}
		}
		
		Logger.warn("Unknkown message type: " + message);
	}
}
