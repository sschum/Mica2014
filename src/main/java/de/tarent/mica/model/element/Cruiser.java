package de.tarent.mica.model.element;

import de.tarent.mica.model.Coord;

/**
 * Dieses Schiff stellt ein Kreuzer dar.
 * 
 * @author rainu
 *
 */
public class Cruiser extends AbstractShip {

	public Cruiser(Orientation orientation, Coord position) {
		super(4, orientation, position);
	}

}
