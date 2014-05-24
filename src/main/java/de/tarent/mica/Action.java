package de.tarent.mica;

import de.tarent.mica.model.Coord;

/**
 * Diese Klasse representiert eine Aktion, die ein Spieler durchführen kann.
 * 
 * @author rainu
 *
 */
public class Action {
	public static enum Type {
		MISSED, ATTACK, CLUSTERBOMB, WILDFIRE, SPY_DRONE, 
		TORPEDO_NORD, TORPEDO_OST, TORPEDO_SUED, TORPEDO_WEST
	}
	
	private final Type type;
	private final Coord coord;
	
	public Action(Type type, Coord coord) {
		this.type = type;
		this.coord = coord;
	}
	
	public Type getType() {
		return type;
	}
	public Coord getCoord() {
		return coord;
	}
	
	@Override
	public String toString() {
		return type + "@" + coord;
	}
}
