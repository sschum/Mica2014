package de.tarent.mica.model.element;

import de.tarent.mica.model.Coord;

/**
 * Dieses Schiff stellt ein Zerstörer dar.
 * 
 * @author rainu
 *
 */
@ShipStats(size = Destroyer.SIZE)
public class Destroyer extends Ship {
	static {
		Ship.registerShipClass(Destroyer.class);
	}
	
	public static final int SIZE = 3;
	
	public Destroyer(Orientation orientation, Coord position) {
		super(SIZE, orientation, position);
	}

}
