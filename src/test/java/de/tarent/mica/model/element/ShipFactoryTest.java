package de.tarent.mica.model.element;

import static org.junit.Assert.*;

import org.junit.Test;

import de.tarent.mica.model.Coord;
import de.tarent.mica.model.element.Ship.Orientation;

public class ShipFactoryTest {
	
	@Test
	public void build(){
		Ship ship = ShipFactory.build(new Coord("A1"), Orientation.SUED, Ship.getSizeOf(Destroyer.class));
	
		assertTrue(ship instanceof Destroyer);
		assertEquals(Orientation.SUED, ship.getOrientation());
		assertEquals(new Coord("A1"), ship.getPosition());
		
		ship = ShipFactory.build(new Coord("A1"), Orientation.SUED, Ship.getSizeOf(Carrier.class));
		
		assertTrue(ship instanceof Carrier);
		assertEquals(Orientation.SUED, ship.getOrientation());
		assertEquals(new Coord("A1"), ship.getPosition());
		
		ship = ShipFactory.build(new Coord("A1"), Orientation.SUED, Ship.getSizeOf(Cruiser.class));
		
		assertTrue(ship instanceof Cruiser);
		assertEquals(Orientation.SUED, ship.getOrientation());
		assertEquals(new Coord("A1"), ship.getPosition());
		
		ship = ShipFactory.build(new Coord("A1"), Orientation.SUED, Ship.getSizeOf(Submarine.class));
		
		assertTrue(ship instanceof Submarine);
		assertEquals(Orientation.SUED, ship.getOrientation());
		assertEquals(new Coord("A1"), ship.getPosition());
	}
}
