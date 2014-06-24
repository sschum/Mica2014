package de.tarent.mica.model;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import javax.lang.model.SourceVersion;

import org.junit.Test;

import de.tarent.mica.model.Field.Element;
import de.tarent.mica.model.element.Ship;
import de.tarent.mica.model.element.Carrier;
import de.tarent.mica.model.element.Destroyer;
import de.tarent.mica.model.element.SpyArea;
import de.tarent.mica.model.element.Submarine;
import de.tarent.mica.model.element.Ship.Orientation;
import de.tarent.mica.model.element.UnknownShip;
import de.tarent.mica.util.Random;

public class WorldTest {

	@Test
	public void validateShipPosition_outOfBounce(){
		World world = new World(1, 1);
		
		Ship ship = new Carrier(Orientation.NORD, new Coord(0, 0));
		try{
			world.validateShipPosition(ship);
			fail("It should be thrown an IllegalArgumentException.");
		}catch(IllegalArgumentException e){}
	}
	
	@Test
	public void validateShipPosition_crossAnotherShip(){
		World world = new World(5, 5);
		
		Ship ship = new Carrier(Orientation.SUED, new Coord(2, 0));
		world.placeOwnShip(ship);
		try{
			world.validateShipPosition(ship);
			fail("It should be thrown an IllegalArgumentException.");
		}catch(IllegalArgumentException e){}
	}
	
	@Test
	public void placeOwnShip(){
		World world = new World(5, 5);
		
		Ship ship = new Carrier(Orientation.SUED, new Coord(2, 0));
		world.placeOwnShip(ship);
		
		assertEquals(
				"  0 1 2 3 4\n" +
				"0 ~ ~ * ~ ~\n" +
				"1 ~ ~ * ~ ~\n" +
				"2 ~ ~ * ~ ~\n" +
				"3 ~ ~ * ~ ~\n" +
				"4 ~ ~ * ~ ~\n", 
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
	public void registerSpy(){
		World world = new World(1, 1);
		Coord coord = new Coord(0, 0);
		world.registerSpy(new SpyArea(coord));
		
		assertEquals(Element.SPIONAGE, world.getEnemyField().get(coord));
	}
	
	@Test
	public void registerSpy_outOfBounce(){
		World world = new World(1, 1);
		Coord coord = new Coord(2, 2);
		
		try{
			world.registerSpy(new SpyArea(coord));
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
		World world = new World(2, 2);
		world.placeOwnShip(new Submarine(Orientation.SUED, new Coord(0, 0)));
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
	public void registerEnemyBurn(){
		World world = new World(5, 5);
		world.placeOwnShip(new Submarine(Orientation.SUED, new Coord(0,0)));
		world.registerEnemyBurn(new Coord(0,0));
		
		assertEquals(Element.TREFFER, world.getEnemyView().get(new Coord(0,0)));
		assertTrue(world.getShip(new Coord(0,0)).isBurning());
	}
	
	@Test
	public void registerEnemySunk(){
		World world = new World(5, 5);
		world.placeOwnShip(new Submarine(Orientation.SUED, new Coord(0,0)));
		
		world.registerEnemySunk(new Coord(0,0));
		assertEquals(Element.TREFFER, world.getEnemyView().get(new Coord(0,0)));
		assertEquals(Element.TREFFER, world.getEnemyView().get(new Coord(0,1)));
		assertTrue(world.getShip(new Coord(0,0)).isSunken());
	}
	
	@Test
	public void registerEnemySpy(){
		World world = new World(1, 1);
		Coord coord = new Coord(0, 0);
		world.registerEnemySpy(new SpyArea(coord));
		
		assertEquals(Element.SPIONAGE, world.getEnemyView().get(coord));
	}
	
	@Test
	public void registerEnemySpy_outOfBounce(){
		World world = new World(1, 1);
		Coord coord = new Coord(2, 2);
		
		try{
			world.registerEnemySpy(new SpyArea(coord));
			fail("It should be thrown an IllegalArgumentException.");
		}catch(IllegalArgumentException e){}
	}
	
	@Test
	public void getShip(){
		World world = new World(5, 5);
		Ship ship = new Submarine(Orientation.SUED, new Coord("00"));
		world.placeOwnShip(ship);
		
		assertSame(ship, world.getShip(new Coord("00")));
		assertSame(ship, world.getShip(new Coord("10")));
	}
	
	@Test
	public void getShipCoordinates(){
		World world = new World(5, 5);
		world.registerShip(new Coord(0, 0));
		Set<Coord> result = world.getShipCoordinates();
		assertEquals(1, result.size());
		assertTrue(result.contains(new Coord(0, 0)));
	}
	
	@Test
	public void getSpyAreas(){
		World world = new World(5, 5);
		world.registerSpy(new SpyArea(new Coord(0,0)));
		Set<SpyArea> result = world.getSpyAreas();
		assertEquals(1, result.size());
		assertTrue(result.contains(new SpyArea(new Coord(0,0))));
	}
	
	@Test
	public void getPotentialShips(){
		World world = new World(10, 10);
		world.registerHit(new Coord("01")); world.registerHit(new Coord("02")); world.registerHit(new Coord("03")); world.registerHit(new Coord("04")); world.registerHit(new Coord("05"));
		world.registerHit(new Coord("61")); world.registerHit(new Coord("62")); world.registerHit(new Coord("63")); world.registerHit(new Coord("64")); world.registerHit(new Coord("65"));
		world.registerHit(new Coord("21")); world.registerHit(new Coord("22")); world.registerHit(new Coord("23")); world.registerHit(new Coord("24")); 
		world.registerHit(new Coord("26")); world.registerHit(new Coord("27")); world.registerHit(new Coord("28")); world.registerHit(new Coord("29")); 
		world.registerHit(new Coord("81")); world.registerHit(new Coord("82")); world.registerHit(new Coord("83")); 
		world.registerHit(new Coord("41")); world.registerHit(new Coord("42")); world.registerHit(new Coord("43"));
		world.registerHit(new Coord("57")); world.registerHit(new Coord("58"));
		world.registerHit(new Coord("88")); world.registerHit(new Coord("89"));
		
		Set<Set<Coord>> result = world.getPotentialShips();
		
		assertEquals(8, result.size());
	}
		
	@Test
	public void registerSunk(){
		World world = new World(5, 5);
		world.registerHit(new Coord("02")); world.registerHit(new Coord("03")); world.registerHit(new Coord("01"));
		world.registerSunk(new Coord("01"));
		
		assertEquals(1, world.getEnemyShips().size());
		
		Ship ship = world.getEnemyShips().iterator().next();
		assertTrue(ship instanceof Destroyer);
		assertEquals(new Coord("01"), ship.getPosition());
		assertEquals(Orientation.OST, ship.getOrientation());
		assertEquals(Arrays.asList(new Coord("01"), new Coord("02"),new Coord("03")), ship.getSpace());
	}
	
	@Test
	public void deSerialize() throws Exception{
		final World world = new World(5, 5);
		final Random rnd = new Random();
		
		rnd.runXTimes(new Runnable() {
			@Override
			public void run() {
				Coord c = new Coord(rnd.nextInt(5), rnd.nextInt(5));
				
				world.registerEnemyMiss(c);
				world.registerMiss(c);
			}
		});
		
		String base64 = World.serialise(world);
		assertEquals(world.toString(), World.deserialise(base64).toString());
	}
}
