package de.tarent.mica.model.element;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import de.tarent.mica.model.Coord;
import de.tarent.mica.model.element.Submarine;
import de.tarent.mica.model.element.Ship.Orientation;

public class SubmarineTest {

	@Test
	public void getSpace_Nord(){
		Submarine ship = new Submarine(Orientation.NORD, new Coord(0, 1));
		List<Coord> result = ship.getSpace();
		
		assertEquals(ship.size, result.size());
		assertTrue(result.contains(new Coord(0, 1)));
		assertTrue(result.contains(new Coord(0, 0)));
	}
	
	@Test
	public void getSpace_Sued(){
		Submarine ship = new Submarine(Orientation.SUED, new Coord(0, 0));
		List<Coord> result = ship.getSpace();
		
		assertEquals(ship.size, result.size());
		assertTrue(result.contains(new Coord(0, 1)));
		assertTrue(result.contains(new Coord(0, 0)));
	}
	
	@Test
	public void getSpace_Ost(){
		Submarine ship = new Submarine(Orientation.OST, new Coord(0, 0));
		List<Coord> result = ship.getSpace();
		
		assertEquals(ship.size, result.size());
		assertTrue(result.contains(new Coord(1, 0)));
		assertTrue(result.contains(new Coord(0, 0)));
	}
	
	@Test
	public void getSpace_West(){
		Submarine ship = new Submarine(Orientation.WEST, new Coord(1, 0));
		List<Coord> result = ship.getSpace();
		
		assertEquals(ship.size, result.size());
		assertTrue(result.contains(new Coord(1, 0)));
		assertTrue(result.contains(new Coord(0, 0)));
	}
}
