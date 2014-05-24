package de.tarent.mica.model;

import static org.junit.Assert.*;

import javax.lang.model.SourceVersion;

import org.junit.Test;

import de.tarent.mica.model.Field.Element;
import de.tarent.mica.model.ship.AbstractShip;
import de.tarent.mica.model.ship.AbstractShip.Orientation;
import de.tarent.mica.model.ship.Carrier;
import de.tarent.mica.model.ship.Submarine;

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
			"A ~ ~ ~ ~ ~\n" +
			"B ~ ~ ~ ~ ~\n" +
			"C ~ ~ ~ ~ ~\n" +
			"D ~ ~ ~ ~ ~\n" +
			"E ~ ~ ~ ~ ~\n", 
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
				"A ~ ~ * ~ ~\n" +
				"B ~ ~ * ~ ~\n" +
				"C ~ ~ * ~ ~\n" +
				"D ~ ~ * ~ ~\n" +
				"E ~ ~ * ~ ~\n", 
			world.getOwnField().toString());
	}
	
	@Test
	public void registerHit(){
		World world = new World(1, 1);
		Coord coord = new Coord(0, 0);
		world.registerHit(coord);
		
		assertEquals(Element.TREFFER, world.getEnemyField().get(coord));
	}
	
	@Test
	public void registerHit_outOfBounce(){
		World world = new World(1, 1);
		Coord coord = new Coord(2, 2);
		
		try{
			world.registerHit(coord);
			fail("It should be thrown an IllegalArgumentException.");
		}catch(IllegalArgumentException e){}
	}
	
	@Test
	public void registerMiss(){
		World world = new World(1, 1);
		Coord coord = new Coord(0, 0);
		world.registerMiss(coord);
		
		assertEquals(Element.WASSER, world.getEnemyField().get(coord));
	}
	
	@Test
	public void registerMiss_outOfBounce(){
		World world = new World(1, 1);
		Coord coord = new Coord(2, 2);
		
		try{
			world.registerMiss(coord);
			fail("It should be thrown an IllegalArgumentException.");
		}catch(IllegalArgumentException e){}
	}
	
	@Test
	public void registerShip(){
		World world = new World(1, 1);
		Coord coord = new Coord(0, 0);
		world.registerShip(coord);
		
		assertEquals(Element.SCHIFF, world.getEnemyField().get(coord));
	}
	
	@Test
	public void registerShip_outOfBounce(){
		World world = new World(1, 1);
		Coord coord = new Coord(2, 2);
		
		try{
			world.registerShip(coord);
			fail("It should be thrown an IllegalArgumentException.");
		}catch(IllegalArgumentException e){}
	}
	
	@Test
	public void registerEnemyHit(){
		World world = new World(1, 1);
		Coord coord = new Coord(0, 0);
		world.registerEnemyHit(coord);
		
		assertEquals(Element.TREFFER, world.getEnemyView().get(coord));
	}
	
	@Test
	public void registerEnemyHit_outOfBounce(){
		World world = new World(1, 1);
		Coord coord = new Coord(2, 2);
		
		try{
			world.registerEnemyHit(coord);
			fail("It should be thrown an IllegalArgumentException.");
		}catch(IllegalArgumentException e){}
	}
	
	@Test
	public void registerEnemyMiss(){
		World world = new World(1, 1);
		Coord coord = new Coord(0, 0);
		world.registerEnemyMiss(coord);
		
		assertEquals(Element.WASSER, world.getEnemyView().get(coord));
	}
	
	@Test
	public void registerEnemyMiss_outOfBounce(){
		World world = new World(1, 1);
		Coord coord = new Coord(2, 2);
		
		try{
			world.registerEnemyMiss(coord);
			fail("It should be thrown an IllegalArgumentException.");
		}catch(IllegalArgumentException e){}
	}
	
	@Test
	public void registerEnemyShip(){
		World world = new World(1, 1);
		Coord coord = new Coord(0, 0);
		world.registerEnemyShip(coord);
		
		assertEquals(Element.SCHIFF, world.getEnemyView().get(coord));
	}
	
	@Test
	public void registerEnemyShip_outOfBounce(){
		World world = new World(1, 1);
		Coord coord = new Coord(2, 2);
		
		try{
			world.registerEnemyShip(coord);
			fail("It should be thrown an IllegalArgumentException.");
		}catch(IllegalArgumentException e){}
	}
	
	@Test
	public void getShip(){
		World world = new World(5, 5);
		AbstractShip ship = new Submarine(Orientation.SUED, new Coord("A0"));
		world.placeOwnShip(ship);
		
		assertSame(ship, world.getShip(new Coord("A0")));
		assertSame(ship, world.getShip(new Coord("B0")));
	}
}
