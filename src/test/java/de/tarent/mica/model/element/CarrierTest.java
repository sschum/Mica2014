package de.tarent.mica.model.element;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import de.tarent.mica.model.Coord;
import de.tarent.mica.model.element.Carrier;
import de.tarent.mica.model.element.Ship.Orientation;

public class CarrierTest {

	@Test
	public void getSpace_Nord(){
		Carrier ship = new Carrier(Orientation.NORD, new Coord(0, 4));
		List<Coord> result = ship.getSpace();
		
		assertEquals(ship.size, result.size());
		assertTrue(result.contains(new Coord(0, 4)));
		assertTrue(result.contains(new Coord(0, 3)));
		assertTrue(result.contains(new Coord(0, 2)));
		assertTrue(result.contains(new Coord(0, 1)));
		assertTrue(result.contains(new Coord(0, 0)));
	}
	
	@Test
	public void getSpace_Sued(){
		Carrier ship = new Carrier(Orientation.SUED, new Coord(0, 0));
		List<Coord> result = ship.getSpace();
		
		assertEquals(ship.size, result.size());
		assertTrue(result.contains(new Coord(0, 4)));
		assertTrue(result.contains(new Coord(0, 3)));
		assertTrue(result.contains(new Coord(0, 2)));
		assertTrue(result.contains(new Coord(0, 1)));
		assertTrue(result.contains(new Coord(0, 0)));
	}
	
	@Test
	public void getSpace_Ost(){
		Carrier ship = new Carrier(Orientation.OST, new Coord(0, 0));
		List<Coord> result = ship.getSpace();
		
		assertEquals(ship.size, result.size());
		assertTrue(result.contains(new Coord(4, 0)));
		assertTrue(result.contains(new Coord(3, 0)));
		assertTrue(result.contains(new Coord(2, 0)));
		assertTrue(result.contains(new Coord(1, 0)));
		assertTrue(result.contains(new Coord(0, 0)));
	}
	
	@Test
	public void getSpace_West(){
		Carrier ship = new Carrier(Orientation.WEST, new Coord(4, 0));
		List<Coord> result = ship.getSpace();
		
		assertEquals(ship.size, result.size());
		assertTrue(result.contains(new Coord(4, 0)));
		assertTrue(result.contains(new Coord(3, 0)));
		assertTrue(result.contains(new Coord(2, 0)));
		assertTrue(result.contains(new Coord(1, 0)));
		assertTrue(result.contains(new Coord(0, 0)));
	}
}
