package de.tarent.mica.model.ship;

import de.tarent.mica.model.Coord;

/**
 * Dieses Schiff stellt ein Träger dar.
 * 
 * @author rainu
 *
 */
public class Carrier extends AbstractShip {

	protected Carrier(Orientation orientation, Coord position) {
		super(5, orientation, position);
	}

	
}
