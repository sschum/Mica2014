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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coord == null) ? 0 : coord.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Action other = (Action) obj;
		if (coord == null) {
			if (other.coord != null)
				return false;
		} else if (!coord.equals(other.coord))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return type + "@" + coord;
	}
}
