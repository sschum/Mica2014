package de.tarent.mica.model.element;

import de.tarent.mica.model.Coord;

/**
 * Dieses Schiff stellt ein Träger dar.
 * 
 * @author rainu
 *
 */
@ShipStats(size = Carrier.SIZE)
public class Carrier extends AbstractShip {
	static {
		AbstractShip.registerShipClass(Carrier.class);
	}
	
	public static final int SIZE = 5;
	
	public Carrier(Orientation orientation, Coord position) {
		super(SIZE, orientation, position);
	}

	
}
