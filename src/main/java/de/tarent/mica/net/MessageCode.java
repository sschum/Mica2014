package de.tarent.mica.net;

/**
 * Dieses Enum beiinhaltet alle bekannten Server-Nachrichten.
 * 
 * @author rainu
 *
 */
enum MessageCode{
	BUSY(00),
	HELLO(01),
	WAIT_FOR_SECOND_PLAYER_CONNECT(02),
	NEW_NAME(03),
	ACCEPTED_AS_OBSERVER(04),

	PLACE_SHIPS(10),
	NEXT_SHIP(11),
	SHIP_READY(12),
	ALL_SHIPS_READY(13),
	DEFAULT_POSITIONS_ASSUMED(14),

	WAIT_FOR_OTHER_PLAYERS_MOVE(28),
	YOUR_TURN(29),
	ENEMY_SHIP_MISSED(30),
	ENEMY_SHIP_HIT(31),
	ENEMY_SHIP_SUNK(32),
	YOU_WIN(33),
	YOUR_SHIP_MISSED(34),
	YOUR_SHIP_HIT(35),
	YOUR_SHIP_SUNK(36),
	YOU_LOSE(37),
	GAME_OVER(38),

	CLUSTERBOMB(40),
	WILDFIRE(41),
	DRONE(42),
	TORPEDO(43),
	CLUSTERBOMBEE(44),
	WILDFIREEE(45),
	DRONEEE(46),
	TORPEDOEE(47),

	INVALID_SHIP(90),
	INVALID_MOVE(91),
	NOT_YOUR_TURN(92),
	ERROR(93),
	OUT_OF_SPECIALPOWERS(94),
	INVALID_TORPEDO_SOURCE(95);
	
	private int code;
	
	private MessageCode(int code){
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static MessageCode fromCode(int code){
		for(MessageCode mc : MessageCode.values()){
			if(mc.getCode() == code){
				return mc;
			}
		}
		
		return null;
	}
}