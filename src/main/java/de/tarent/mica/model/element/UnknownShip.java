package de.tarent.mica.model.element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.tarent.mica.model.Coord;

/**
 * Ein Unkbekanntes Schiff... Seine größe ist unbekannt.
 * 
 * @author rainu
 *
 */
@ShipStats(size = UnknownShip.SIZE)
public class UnknownShip extends AbstractShip {
	static {
		AbstractShip.registerShipClass(UnknownShip.class);
	}
	
	public static final int SIZE = -1;
	
	public UnknownShip(Coord...coords) {
		super(SIZE, Orientation.UNBEKANNT, null);
		
		for(Coord c : coords){
			addAttackCoord(c);
		}
		if(attackCoords.size() > 0){
			position = coords[0];
		}
	}

	@Override
	public List<Coord> getSpace() {
		List<Coord> space = new ArrayList<Coord>(attackCoords);
		Collections.sort(space, Coord.COMPARATOR);
		
		return space;
	}
	
}
