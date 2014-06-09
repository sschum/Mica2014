package de.tarent.mica.bot.strategy.action;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import de.tarent.mica.Action;
import de.tarent.mica.Action.Type;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.World;
import de.tarent.mica.model.element.Destroyer;
import de.tarent.mica.model.element.Ship.Orientation;

public class SpyStrategyTest {
	SpyStrategy toTest = new SpyStrategy();
	
	@Test
	public void canSpy(){
		World world = new World(10, 10);
		
		/*
		 *   0 1 2 3 4
		 * A
		 * B   * * *
		 * C
		 * D * * *
		 * E
		 */
		world.placeOwnShip(new Destroyer(Orientation.OST, new Coord("B1")));
		world.placeOwnShip(new Destroyer(Orientation.OST, new Coord("D0")));
		assertTrue(toTest.canSpy(world));
		
		/*
		 *   0 1 2 3 4
		 * A   * * *
		 * B   
		 * C     *
		 * D     *
		 * E     *
		 */
		world = new World(10, 10);
		world.placeOwnShip(new Destroyer(Orientation.OST, new Coord("A1")));
		world.placeOwnShip(new Destroyer(Orientation.SUED, new Coord("C2")));
		assertTrue(toTest.canSpy(world));
		
		/*
		 *   0 1 2 3 4
		 * A   * * *
		 * B   
		 * C     *
		 * D     *  <= gesunken!
		 * E     *
		 */
		world = new World(10, 10);
		world.placeOwnShip(new Destroyer(Orientation.OST, new Coord("A1")));
		world.placeOwnShip(new Destroyer(Orientation.SUED, new Coord("C2")));
		world.registerEnemySunk(new Coord("C2"));
		assertFalse(toTest.canSpy(world));
		
		/*
		 *   0 1 2 3 4
		 * A
		 * B   * * *
		 * C 
		 * D * 
		 * E *
		 * F *
		 */
		world = new World(10, 10);
		world.placeOwnShip(new Destroyer(Orientation.OST, new Coord("B1")));
		world.placeOwnShip(new Destroyer(Orientation.SUED, new Coord("D0")));
		assertFalse(toTest.canSpy(world));
		
		/*
		 *   0 1 2 3 4
		 * A
		 * B   * * *
		 * C 
		 * D     * 
		 * E     *
		 * F     *
		 */
		world = new World(10, 10);
		world.registerSpecialAttack(Destroyer.class); //es wurde schon spoiniert
		world.placeOwnShip(new Destroyer(Orientation.OST, new Coord("B1")));
		world.placeOwnShip(new Destroyer(Orientation.SUED, new Coord("D2")));
		assertFalse(toTest.canSpy(world));
		
		/*
		 *   0 1 2 3 4
		 * A
		 * B   * * *
		 * C
		 * D * * *
		 * E
		 */
		world = new World(10, 10);
		world.registerSpecialAttack(Destroyer.class); //es wurde schon spoiniert
		world.placeOwnShip(new Destroyer(Orientation.OST, new Coord("B1")));
		world.placeOwnShip(new Destroyer(Orientation.OST, new Coord("D0")));
		assertTrue(toTest.canSpy(world));
		
		/*
		 *   0 1 2 3 4
		 * A
		 * B   * * *
		 * C
		 * D * * *
		 * E
		 */
		world = new World(10, 10);
		world.registerSpecialAttack(Destroyer.class);
		world.registerSpecialAttack(Destroyer.class); //es wurde schon zweimal spoiniert
		world.placeOwnShip(new Destroyer(Orientation.OST, new Coord("B1")));
		world.placeOwnShip(new Destroyer(Orientation.OST, new Coord("D0")));
		assertFalse(toTest.canSpy(world));
	}
	
	@Test
	public void nextSpyAction(){
		toTest = new SpyStrategy();	//keine spionagepunkte definiert
		
		assertNull(toTest.nextSpyAction(null));
		
		toTest = new SpyStrategy(new Coord("A1"), new Coord("A2"));
		Action result = toTest.nextSpyAction(null);
		
		assertEquals(Type.SPY_DRONE, result.getType());
		assertEquals(new Coord("A1"), result.getCoord());
		
		result = toTest.nextSpyAction(null);
		
		assertEquals(Type.SPY_DRONE, result.getType());
		assertEquals(new Coord("A2"), result.getCoord());
	}
	
	@Test
	public void getActionDecision(){
		SpyStrategy spy = spy(toTest);
		doReturn(false).when(spy).canSpy(any(World.class));
		
		assertNull(spy.getActionDecision(null));
		
		doReturn(true).when(spy).canSpy(any(World.class));
		doReturn(new Action(Type.SPY_DRONE, new Coord("A1"))).when(spy).nextSpyAction(any(World.class));
		
		assertEquals(spy.nextSpyAction(null), spy.getActionDecision(null));
	}
}
