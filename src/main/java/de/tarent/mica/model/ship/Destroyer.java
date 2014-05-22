package de.tarent.mica.model.ship;

import de.tarent.mica.model.Coord;

/**
 * Dieses Schiff stellt ein Zerstörer dar.
 * 
 * @author rainu
 *
 */
public class Destroyer extends AbstractShip {

	public Destroyer(Orientation orientation, Coord position) {
		super(3, orientation, position);
	}

}
