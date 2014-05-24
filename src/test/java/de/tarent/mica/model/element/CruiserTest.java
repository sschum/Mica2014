package de.tarent.mica.model.element;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import de.tarent.mica.model.Coord;
import de.tarent.mica.model.element.Cruiser;
import de.tarent.mica.model.element.AbstractShip.Orientation;

public class CruiserTest {

	@Test
	public void getSpace_Nord(){
		Cruiser ship = new Cruiser(Orientation.NORD, new Coord(0, 3));
		List<Coord> result = ship.getSpace();
		
		assertEquals(ship.size, result.size());
		assertTrue(result.contains(new Coord(0, 3)));
		assertTrue(result.contains(new Coord(0, 2)));
		assertTrue(result.contains(new Coord(0, 1)));
		assertTrue(result.contains(new Coord(0, 0)));
	}
	
	@Test
	public void getSpace_Sued(){
		Cruiser ship = new Cruiser(Orientation.SUED, new Coord(0, 0));
		List<Coord> result = ship.getSpace();
		
		assertEquals(ship.size, result.size());
		assertTrue(result.contains(new Coord(0, 3)));
		assertTrue(result.contains(new Coord(0, 2)));
		assertTrue(result.contains(new Coord(0, 1)));
		assertTrue(result.contains(new Coord(0, 0)));
	}
	
	@Test
	public void getSpace_Ost(){
		Cruiser ship = new Cruiser(Orientation.OST, new Coord(0, 0));
		List<Coord> result = ship.getSpace();
		
		assertEquals(ship.size, result.size());
		assertTrue(result.contains(new Coord(3, 0)));
		assertTrue(result.contains(new Coord(2, 0)));
		assertTrue(result.contains(new Coord(1, 0)));
		assertTrue(result.contains(new Coord(0, 0)));
	}
	
	@Test
	public void getSpace_West(){
		Cruiser ship = new Cruiser(Orientation.WEST, new Coord(3, 0));
		List<Coord> result = ship.getSpace();
		
		assertEquals(ship.size, result.size());
		assertTrue(result.contains(new Coord(3, 0)));
		assertTrue(result.contains(new Coord(2, 0)));
		assertTrue(result.contains(new Coord(1, 0)));
		assertTrue(result.contains(new Coord(0, 0)));
	}
}
