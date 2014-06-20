package de.tarent.mica.model;

import static de.tarent.mica.model.Field.Element.*;
import static org.junit.Assert.*;

import java.awt.Dimension;
import java.util.Set;

import org.junit.Test;

import de.tarent.mica.model.Field.Element;

public class FieldTest {

	@Test
	public void toString_Test(){
		Field field = new Field(new Element[][]{
				new Element[]{UNBEKANNT, WASSER, WASSER, WASSER, WASSER, WASSER, WASSER, WASSER, WASSER, SCHIFF},
				new Element[]{WASSER, UNBEKANNT, WASSER, WASSER, WASSER, WASSER, WASSER, WASSER, SCHIFF, WASSER},
				new Element[]{WASSER, WASSER, UNBEKANNT, WASSER, WASSER, WASSER, WASSER, SCHIFF, WASSER, WASSER},
				new Element[]{WASSER, WASSER, WASSER, UNBEKANNT, WASSER, WASSER, SCHIFF, WASSER, WASSER, WASSER},
				new Element[]{WASSER, WASSER, WASSER, WASSER, UNBEKANNT, SCHIFF, WASSER, WASSER, WASSER, WASSER},
				new Element[]{TREFFER, TREFFER, TREFFER, TREFFER, SCHIFF, UNBEKANNT, TREFFER, TREFFER, TREFFER, TREFFER},
				new Element[]{WASSER, WASSER, WASSER, SCHIFF, WASSER, WASSER, UNBEKANNT, WASSER, WASSER, WASSER},
				new Element[]{WASSER, WASSER, SCHIFF, WASSER, WASSER, WASSER, WASSER, UNBEKANNT, WASSER, WASSER},
				new Element[]{WASSER, SCHIFF, WASSER, WASSER, WASSER, WASSER, WASSER, WASSER, UNBEKANNT, WASSER},
				new Element[]{SCHIFF, WASSER, WASSER, WASSER, WASSER, WASSER, WASSER, WASSER, WASSER, UNBEKANNT},
		});
		
		assertEquals(
				"  0 1 2 3 4 5 6 7 8 9\n" + 
				"0 ? ~ ~ ~ ~ ~ ~ ~ ~ *\n"+ 
				"1 ~ ? ~ ~ ~ ~ ~ ~ * ~\n"+ 
				"2 ~ ~ ? ~ ~ ~ ~ * ~ ~\n"+ 
				"3 ~ ~ ~ ? ~ ~ * ~ ~ ~\n"+ 
				"4 ~ ~ ~ ~ ? * ~ ~ ~ ~\n"+ 
				"5 + + + + * ? + + + +\n"+ 
				"6 ~ ~ ~ * ~ ~ ? ~ ~ ~\n"+ 
				"7 ~ ~ * ~ ~ ~ ~ ? ~ ~\n"+ 
				"8 ~ * ~ ~ ~ ~ ~ ~ ? ~\n"+ 
				"9 * ~ ~ ~ ~ ~ ~ ~ ~ ?\n", 
				field.toString());
	}
	
	@Test
	public void testEmpty(){
		assertEquals("  0 1\n" +
					 "0 ? ?\n" +
					 "1 ? ?\n",
					 new Field(2, 2).toString());
	}
	
	@Test
	public void testInvalidDimension(){
		try{
			new Field(0, 0);
			fail("It should be thrown an IllegalArgumentException.");
		}catch(IllegalArgumentException e){}
	}
	
	@Test
	public void set(){
		Field field = new Field(2, 2);
		field.set(new Coord("01"), SCHIFF);
		field.set(new Coord("10"), WASSER);
		
		assertEquals(
				"  0 1\n" +
				"0 ? *\n" +
				"1 ~ ?\n", 
				field.toString());
	}
	
	@Test
	public void set_invalidCoord(){
		try{
			new Field(1,1).set(new Coord(1, 1), WASSER);
			fail("It should be thrown an IllegalArgumentException.");
		}catch(IllegalArgumentException e){}
	}
	
	@Test
	public void set_nullElement(){
		try{
			new Field(1,1).set(new Coord(0, 0), null);
			fail("It should be thrown an NullPointerException.");
		}catch(NullPointerException e){}
	}
	
	@Test
	public void get(){
		Field field = new Field(new Element[][]{
				new Element[]{UNBEKANNT, WASSER},
				new Element[]{WASSER, SCHIFF},
		});
		
		assertEquals(UNBEKANNT, field.get(new Coord("00")));
		assertEquals(WASSER, field.get(new Coord("01")));
		assertEquals(WASSER, field.get(new Coord("10")));
		assertEquals(SCHIFF, field.get(new Coord("11")));
	}
	
	@Test
	public void get_invalidCoord(){
		try{
			new Field(1,1).get(new Coord(1, 1));
			fail("It should be thrown an IllegalArgumentException.");
		}catch(IllegalArgumentException e){}
	}
	
	@Test
	public void getCoordinatesFor(){
		Field field = new Field(new Element[][]{
				new Element[]{UNBEKANNT, WASSER},
				new Element[]{WASSER, SCHIFF},
		});
		
		Set<Coord> result = field.getCoordinatesFor(UNBEKANNT);
		assertTrue(result.contains(new Coord("00")));
		
		result = field.getCoordinatesFor(WASSER);
		assertTrue(result.contains(new Coord("01")));
		assertTrue(result.contains(new Coord("10")));
		
		result = field.getCoordinatesFor(SCHIFF);
		assertTrue(result.contains(new Coord("11")));
	}
	
	@Test
	public void getDimension(){
		assertEquals(new Dimension(1, 2), new Field(2, 1).getDimension());
	}
}
