package de.tarent.mica.model.element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
public class UnknownShip extends Ship {
	static {
		Ship.registerShipClass(UnknownShip.class);
	}
	
	public static final int SIZE = -1;
	
	public UnknownShip(Coord...coords) {
		this(Arrays.asList(coords));
	}
	
	public UnknownShip(Collection<Coord> coords) {
		super(SIZE, Orientation.UNBEKANNT, null);
		
		for(Coord c : coords){
			addAttackCoord(c);
		}
		if(attackCoords.size() > 0){
			position = coords.iterator().next();
		}
	}

	@Override
	public List<Coord> getSpace() {
		List<Coord> space = new ArrayList<Coord>(attackCoords);
		Collections.sort(space, Coord.COMPARATOR);
		
		return space;
	}
	
	@Override
	public Coord getPosition() {
		return getSpace().get(0);
	}
	
	@Override
	public Orientation getOrientation() {
		if(getSpace().size() <= 1) return Orientation.UNBEKANNT;
		
		List<Coord> coords = getSpace();
		Coord c1 = coords.get(0);
		Coord c2 = coords.get(1);
		
		if(c1.getEastNeighbor().equals(c2)){
			return Orientation.OST;
		}else if(c1.getSouthNeighbor().equals(c2)){
			return Orientation.SUED;
		}else if(c1.getWestNeighbor().equals(c2)){
			return Orientation.WEST;
		}else if(c1.getNorthNeighbor().equals(c2)){
			return Orientation.NORD;
		}else {
			return Orientation.UNBEKANNT;
		}
	}
	
	public static Ship transformShip(UnknownShip ship, boolean isSunken){
		Orientation orientation = ship.getOrientation();
		int finalSize = ship.getSpace().size();
		
		Ship newShip = ShipFactory.build(ship.getPosition(), orientation, finalSize);
		
		if(isSunken) for(Coord shipCoord : ship.getSpace())
			newShip.addAttackCoord(shipCoord);
		
		return newShip;
	}
}
