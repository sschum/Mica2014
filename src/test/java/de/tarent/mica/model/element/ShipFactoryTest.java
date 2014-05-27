package de.tarent.mica.model.element;

import static org.junit.Assert.*;

import org.junit.Test;

import de.tarent.mica.model.Coord;
import de.tarent.mica.model.element.AbstractShip.Orientation;

public class ShipFactoryTest {
	
	@Test
	public void build(){
		AbstractShip ship = ShipFactory.build(new Coord("A1"), Orientation.SUED, AbstractShip.getSizeOf(Destroyer.class));
	
		assertTrue(ship instanceof Destroyer);
		assertEquals(Orientation.SUED, ship.getOrientation());
		assertEquals(new Coord("A1"), ship.getPosition());
		
		ship = ShipFactory.build(new Coord("A1"), Orientation.SUED, AbstractShip.getSizeOf(Carrier.class));
		
		assertTrue(ship instanceof Carrier);
		assertEquals(Orientation.SUED, ship.getOrientation());
		assertEquals(new Coord("A1"), ship.getPosition());
		
		ship = ShipFactory.build(new Coord("A1"), Orientation.SUED, AbstractShip.getSizeOf(Cruiser.class));
		
		assertTrue(ship instanceof Cruiser);
		assertEquals(Orientation.SUED, ship.getOrientation());
		assertEquals(new Coord("A1"), ship.getPosition());
		
		ship = ShipFactory.build(new Coord("A1"), Orientation.SUED, AbstractShip.getSizeOf(Submarine.class));
		
		assertTrue(ship instanceof Submarine);
		assertEquals(Orientation.SUED, ship.getOrientation());
		assertEquals(new Coord("A1"), ship.getPosition());
	}
}
