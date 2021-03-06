package de.tarent.mica.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class CoordTest {

	@Test
	public void toString_test(){
		assertEquals("D7", new Coord("D7").toString());
		assertEquals("D7", new Coord("D7").toString());
		assertEquals("D7", new Coord('D', '7').toString());
		assertEquals("D7", new Coord(7, 13).toString());
	}
	
	@Test
	public void getX(){
		assertEquals(1, new Coord("11").getX());
	}
	
	@Test
	public void getY(){
		assertEquals(11, new Coord("B1").getY());
	}
	
	@Test
	public void getYChar(){
		assertEquals('B', new Coord("B1").getYChar());
		assertEquals('B', new Coord("b1").getYChar());
	}
	
	@Test
	public void getXChar(){
		assertEquals('B', new Coord("1B").getXChar());
		assertEquals('B', new Coord("1b").getXChar());
	}
}
