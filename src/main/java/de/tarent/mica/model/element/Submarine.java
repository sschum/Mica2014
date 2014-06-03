package de.tarent.mica.model.element;

import de.tarent.mica.model.Coord;

/**
 * Dieses Schiff stellt ein U-Boot dar.
 * 
 * @author rainu
 *
 */
@ShipStats(size = Submarine.SIZE)
public class Submarine extends Ship {
	static {
		Ship.registerShipClass(Submarine.class);
	}
	
	public static final int SIZE = 2;
	
	public Submarine(Orientation orientation, Coord position) {
		super(SIZE, orientation, position);
	}

}
