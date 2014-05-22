package de.tarent.mica.model.ship;

import java.util.ArrayList;
import java.util.List;

import de.tarent.mica.model.Coord;

/**
 * Diese Klasse representiert ein Schiff.
 * 
 * @author rainu
 *
 */
public abstract class AbstractShip {
	public static enum Orientation{
		NORD, OST, SUED, WEST, UNBEKANNT
	}
	
	protected final int size;
	protected Orientation orientation;
	protected Coord position;
	
	protected AbstractShip(int size, Orientation orientation, Coord position){
		this.size = size;
		this.orientation = orientation;
		this.position = position;
	}
	
	public List<Coord> getSpace(){
		List<Coord> result = new ArrayList<Coord>();
		
		//of curse the position itself ;)
		result.add(position);
		
		for(int i=0; i < size - 1; i++){
			Coord lastCoord = result.get(result.size() - 1);
			Coord c = null;
			switch (orientation) {
			case NORD:
				c = new Coord(lastCoord.getX(), lastCoord.getY() - 1);
				break;
			case OST:
				c = new Coord(lastCoord.getX() + 1, lastCoord.getY());
				break;
			case SUED:
				c = new Coord(lastCoord.getX(), lastCoord.getY() + 1);
				break;
			case WEST:
				c = new Coord(lastCoord.getX() - 1, lastCoord.getY());
				break;
			default:
				break;
			}
			
			result.add(c);
		}
		
		return result;
	}
	
}
