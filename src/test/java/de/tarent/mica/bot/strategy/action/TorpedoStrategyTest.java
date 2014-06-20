package de.tarent.mica.bot.strategy.action;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.tarent.mica.Action;
import de.tarent.mica.Action.Type;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.World;
import de.tarent.mica.model.element.Ship.Orientation;
import de.tarent.mica.model.element.Submarine;

public class TorpedoStrategyTest {

	TorpedoStrategy toTest = new TorpedoStrategy();
	
	@Test
	public void collectTorpedoCoords(){
		/*
		 *   0 1 2 3 4 5
		 * A
		 * B   *
		 * C   *
		 * D       * *
		 * E
		 * 
		 */
		World world = new World(10, 10);
		world.placeOwnShip(new Submarine(Orientation.SUED, new Coord("11")));
		world.placeOwnShip(new Submarine(Orientation.OST, new Coord("33")));
		
		assertTrue(toTest.collectTorpedoCoords(world).isEmpty());
		
		/*
		 *   0 1 2 3 4 5
		 * A
		 * B   *
		 * C   *
		 * D       
		 * E   * *
		 * 
		 */
		world = new World(10, 10);
		world.placeOwnShip(new Submarine(Orientation.SUED, new Coord("11")));
		world.placeOwnShip(new Submarine(Orientation.OST, new Coord("41")));
		
		List<Coord> result = toTest.collectTorpedoCoords(world);
		assertEquals(4, result.size());
		assertTrue(result.contains(new Coord("11")));
		assertTrue(result.contains(new Coord("21")));
		assertTrue(result.contains(new Coord("41")));
		assertTrue(result.contains(new Coord("42")));
		
		/*
		 *   0 1 2 3 4 5
		 * A
		 * B   *   *
		 * C   *   *
		 * D       
		 * E  
		 * 
		 */
		world = new World(10, 10);
		world.placeOwnShip(new Submarine(Orientation.SUED, new Coord("11")));
		world.placeOwnShip(new Submarine(Orientation.SUED, new Coord("13")));
		
		result = toTest.collectTorpedoCoords(world);
		assertEquals(4, result.size());
		assertTrue(result.contains(new Coord("11")));
		assertTrue(result.contains(new Coord("21")));
		assertTrue(result.contains(new Coord("13")));
		assertTrue(result.contains(new Coord("23")));
		
		/*
		 *   0 1 2 3 4 5
		 * A   *
		 * B   * 
		 * C      
		 * D   *    
		 * E   *
		 * 
		 */
		world = new World(10, 10);
		world.placeOwnShip(new Submarine(Orientation.SUED, new Coord("01")));
		world.placeOwnShip(new Submarine(Orientation.SUED, new Coord("31")));
		
		result = toTest.collectTorpedoCoords(world);
		assertEquals(4, result.size());
		assertTrue(result.contains(new Coord("01")));
		assertTrue(result.contains(new Coord("11")));
		assertTrue(result.contains(new Coord("31")));
		assertTrue(result.contains(new Coord("41")));
	}
	
	@Test
	public void getAction(){
		/*
		 *   0 1 2 3 4 5
		 * A       *
		 * B   *   *
		 * C   *
		 * D
		 * E
		 * 
		 */
		World world = new World(5, 5);
		
		Action result = toTest.getAction(world, Arrays.asList(new Coord("11"), new Coord("21"), new Coord("03"), new Coord("13")));
		assertEquals(new Action(Type.TORPEDO_SUED, new Coord("03")), result);
		
		/*
		 *   0 1 2 3 4 5
		 * A       *
		 * B   *   *
		 * C   *     
		 * D
		 * E
		 * 
		 */
		world.registerMiss(new Coord("13"));
		world.registerMiss(new Coord("23"));
		result = toTest.getAction(world, Arrays.asList(new Coord("11"), new Coord("21"), new Coord("03"), new Coord("13")));
		assertEquals(new Action(Type.TORPEDO_WEST, new Coord("03")), result);
	}
}
