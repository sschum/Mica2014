package de.tarent.mica.model.element;

import de.tarent.mica.model.Coord;

/**
 * Dieses Schiff stellt ein Kreuzer dar.
 * 
 * @author rainu
 *
 */
@ShipStats(size = Cruiser.SIZE)
public class Cruiser extends Ship {
	static {
		Ship.registerShipClass(Cruiser.class);
	}
	
	public static final int SIZE = 4;
	
	public Cruiser(Orientation orientation, Coord position) {
		super(SIZE, orientation, position);
	}

}
