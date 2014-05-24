package de.tarent.mica.model.element;

import de.tarent.mica.model.Coord;

/**
 * Dieses Schiff stellt ein Träger dar.
 * 
 * @author rainu
 *
 */
public class Carrier extends AbstractShip {

	public Carrier(Orientation orientation, Coord position) {
		super(5, orientation, position);
	}

	
}
