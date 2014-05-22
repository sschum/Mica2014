package de.tarent.mica.model;

import static org.junit.Assert.*;

import org.junit.Test;

import de.tarent.mica.model.ship.AbstractShip;
import de.tarent.mica.model.ship.AbstractShip.Orientation;
import de.tarent.mica.model.ship.Carrier;

public class WorldTest {

	@Test
	public void toString_test(){
		World world = new World(5, 5);
		
		assertEquals(
			"  0 1 2 3 4\n" +
			"A ? ? ? ? ?\n" +
			"B ? ? ? ? ?\n" +
			"C ? ? ? ? ?\n" +
			"D ? ? ? ? ?\n" +
			"E ? ? ? ? ?\n" +
			"\n" +
			"o)============(o\n" +
			"\n" +
			"  0 1 2 3 4\n" +
			"A ? ? ? ? ?\n" +
			"B ? ? ? ? ?\n" +
			"C ? ? ? ? ?\n" +
			"D ? ? ? ? ?\n" +
			"E ? ? ? ? ?\n", 
			world.toString());
	}
	
	@Test
	public void validateShipPosition_outOfBounce(){
		World world = new World(1, 1);
		
		AbstractShip ship = new Carrier(Orientation.NORD, new Coord(0, 0));
		try{
			world.validateShipPosition(ship);
			fail("It should be thrown an IllegalArgumentException.");
		}catch(IllegalArgumentException e){}
	}
	
	@Test
	public void validateShipPosition_crossAnotherShip(){
		World world = new World(5, 5);
		
		AbstractShip ship = new Carrier(Orientation.SUED, new Coord(2, 0));
		world.placeOwnShip(ship);
		try{
			world.validateShipPosition(ship);
			fail("It should be thrown an IllegalArgumentException.");
		}catch(IllegalArgumentException e){}
	}
	
	@Test
	public void placeOwnShip(){
		World world = new World(5, 5);
		
		AbstractShip ship = new Carrier(Orientation.SUED, new Coord(2, 0));
		world.placeOwnShip(ship);
		
		assertEquals(
				"  0 1 2 3 4\n" +
				"A ? ? * ? ?\n" +
				"B ? ? * ? ?\n" +
				"C ? ? * ? ?\n" +
				"D ? ? * ? ?\n" +
				"E ? ? * ? ?\n", 
			world.getOwnField().toString());
	}
}
