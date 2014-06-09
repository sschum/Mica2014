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
		world.placeOwnShip(new Submarine(Orientation.SUED, new Coord("B1")));
		world.placeOwnShip(new Submarine(Orientation.OST, new Coord("D3")));
		
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
		world.placeOwnShip(new Submarine(Orientation.SUED, new Coord("B1")));
		world.placeOwnShip(new Submarine(Orientation.OST, new Coord("E1")));
		
		List<Coord> result = toTest.collectTorpedoCoords(world);
		assertEquals(4, result.size());
		assertTrue(result.contains(new Coord("B1")));
		assertTrue(result.contains(new Coord("C1")));
		assertTrue(result.contains(new Coord("E1")));
		assertTrue(result.contains(new Coord("E2")));
		
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
		world.placeOwnShip(new Submarine(Orientation.SUED, new Coord("B1")));
		world.placeOwnShip(new Submarine(Orientation.SUED, new Coord("B3")));
		
		result = toTest.collectTorpedoCoords(world);
		assertEquals(4, result.size());
		assertTrue(result.contains(new Coord("B1")));
		assertTrue(result.contains(new Coord("C1")));
		assertTrue(result.contains(new Coord("B3")));
		assertTrue(result.contains(new Coord("C3")));
		
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
		world.placeOwnShip(new Submarine(Orientation.SUED, new Coord("A1")));
		world.placeOwnShip(new Submarine(Orientation.SUED, new Coord("D1")));
		
		result = toTest.collectTorpedoCoords(world);
		assertEquals(4, result.size());
		assertTrue(result.contains(new Coord("A1")));
		assertTrue(result.contains(new Coord("B1")));
		assertTrue(result.contains(new Coord("D1")));
		assertTrue(result.contains(new Coord("E1")));
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
		
		Action result = toTest.getAction(world, Arrays.asList(new Coord("B1"), new Coord("C1"), new Coord("A3"), new Coord("B3")));
		assertEquals(new Action(Type.TORPEDO_SUED, new Coord("A3")), result);
		
		/*
		 *   0 1 2 3 4 5
		 * A       *
		 * B   *   *
		 * C   *     
		 * D
		 * E
		 * 
		 */
		world.registerMiss(new Coord("B3"));
		world.registerMiss(new Coord("C3"));
		result = toTest.getAction(world, Arrays.asList(new Coord("B1"), new Coord("C1"), new Coord("A3"), new Coord("B3")));
		assertEquals(new Action(Type.TORPEDO_WEST, new Coord("A3")), result);
	}
}
