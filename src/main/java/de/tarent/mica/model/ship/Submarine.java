package de.tarent.mica.model.ship;

import de.tarent.mica.model.Coord;

/**
 * Dieses Schiff stellt ein U-Boot dar.
 * 
 * @author rainu
 *
 */
public class Submarine extends AbstractShip {

	public Submarine(Orientation orientation, Coord position) {
		super(2, orientation, position);
	}

}
