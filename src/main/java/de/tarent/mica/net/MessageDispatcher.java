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
class MessageDispatcher {
	private Pattern messagePattern = Pattern.compile("^([0-9]*):.*$");
	private Pattern helloMessagePattern = Pattern.compile("^[0-9]*:.*Hello, player #([0-9]*).$");
	private Pattern renameMessagePattern = Pattern.compile("^[0-9]*:.*Player '#([0-9]*)' is now known as '(.*)'.$");
	private Pattern hitMessagePattern = Pattern.compile("^[0-9]*:.*Enemy ship hit at ([0-9A-Za-z]{2})\\.$");
	private Pattern sunkMessagePattern = Pattern.compile("^[0-9]*:.*Enemy (.*) sunk!$");
	private Pattern enemyHitMessagePattern = Pattern.compile("^[0-9]*:.*The enemy hits .*at ([0-9A-Za-z]{2})\\.$");
	private Pattern enemyHitBurnedMessagePattern = Pattern.compile("^[0-9]*:.*Your .*burned at ([0-9A-Za-z]{2})!$");
	private Pattern enemyMissMessagePattern = Pattern.compile("^[0-9]*:.*Enemy shoots at ([0-9A-Za-z]{2}) and misses\\.$");
	private Pattern playerSunkMessagePattern = Pattern.compile("^[0-9]*:.*at ([0-9A-Za-z]{2})!.*$");
	private Pattern spyMesssagePattern = Pattern.compile("^[0-9]*:.*The drone found ([0-9]*) ship segments at ([0-9A-Za-z]{2})!$");
	private Pattern enemySpyMessagePattern = Pattern.compile("^[0-9]*:.*at ([0-9A-Za-z]{2})!.*$");
	private Pattern enemyClusterMessagePattern = Pattern.compile("^[0-9]*:.*clusterbomb.*at ([0-9A-Za-z]{2})!$");

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
				case WAIT_FOR_SECOND_PLAYER_CONNECT:
				case WAIT_FOR_OTHER_PLAYERS_MOVE:
				case NEXT_SHIP:
				case SHIP_READY:
				case ALL_SHIPS_READY:
					//ignorierte Messages...
					return;
				case NEW_NAME:
					matcher = renameMessagePattern.matcher(message);
					matcher.matches();
					
					controller.renamed(Integer.parseInt(matcher.group(1)), matcher.group(2));
					return;
				case HELLO:
					matcher = helloMessagePattern.matcher(message);
					matcher.matches();
					
					controller.started(Integer.parseInt(matcher.group(1)));
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
				case NOT_YOUR_TURN:
					controller.turnToSoon();
					return;
				case CLUSTERBOMB:
				case ENEMY_SHIP_HIT:
					matcher = hitMessagePattern.matcher(message);
					if(matcher.matches()){
						controller.hit(new Coord(matcher.group(1)));
					}else{
						controller.hit(null);
					}
					controller.myTurn();
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
				case CLUSTERBOMBEE:
					matcher = enemyClusterMessagePattern.matcher(message);
					matcher.matches();
					controller.enemyClusterbombed(new Coord(matcher.group(1)));
					return;
				case TORPEDOEE: //TODO: wenn man von einem Torpedo getroffen wurde, weis man zum. dass in den Himelsrichtungen irgendow ein U-Bot sein muss!
					controller.enemyUsedTorpedo();
					return;
				case WILDFIREEE: //wird durch den YOUR_SHIP_HIT abgebildet (wenn es brennt war es eine Wildfire-Attacke!)
					controller.enemyUsedWildfire();
					return;
				case ENEMY_SHIP_SUNK:
					matcher = sunkMessagePattern.matcher(message);
					matcher.matches();
					controller.hit(null); 	//wir bekommen beim "finalen Schuss" keine seperate Nachricht...
					controller.sunk(matcher.group(1));
					controller.myTurn();	//auch nachdem ein schiff gesunken ist, sind wir am zug
					return;
				case YOU_WIN:
				case GAME_OVER:
					controller.gameOver(true);
					return;
				case YOU_LOSE:
					controller.gameOver(false);
					return;
				default:
					break;
				}
			}
		}
		
		Logger.warn("Unknkown message type: " + message);
	}
}
