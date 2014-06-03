package de.tarent.mica.model.element;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import de.tarent.mica.model.Coord;
import de.tarent.mica.model.element.Destroyer;
import de.tarent.mica.model.element.Ship.Orientation;

public class DestroyerTest {

	@Test
	public void getSpace_Nord(){
		Destroyer ship = new Destroyer(Orientation.NORD, new Coord(0, 2));
		List<Coord> result = ship.getSpace();
		
		assertEquals(ship.size, result.size());
		assertTrue(result.contains(new Coord(0, 2)));
		assertTrue(result.contains(new Coord(0, 1)));
		assertTrue(result.contains(new Coord(0, 0)));
	}
	
	@Test
	public void getSpace_Sued(){
		Destroyer ship = new Destroyer(Orientation.SUED, new Coord(0, 0));
		List<Coord> result = ship.getSpace();
		
		assertEquals(ship.size, result.size());
		assertTrue(result.contains(new Coord(0, 2)));
		assertTrue(result.contains(new Coord(0, 1)));
		assertTrue(result.contains(new Coord(0, 0)));
	}
	
	@Test
	public void getSpace_Ost(){
		Destroyer ship = new Destroyer(Orientation.OST, new Coord(0, 0));
		List<Coord> result = ship.getSpace();
		
		assertEquals(ship.size, result.size());
		assertTrue(result.contains(new Coord(2, 0)));
		assertTrue(result.contains(new Coord(1, 0)));
		assertTrue(result.contains(new Coord(0, 0)));
	}
	
	@Test
	public void getSpace_West(){
		Destroyer ship = new Destroyer(Orientation.WEST, new Coord(2, 0));
		List<Coord> result = ship.getSpace();
		
		assertEquals(ship.size, result.size());
		assertTrue(result.contains(new Coord(2, 0)));
		assertTrue(result.contains(new Coord(1, 0)));
		assertTrue(result.contains(new Coord(0, 0)));
	}
}
