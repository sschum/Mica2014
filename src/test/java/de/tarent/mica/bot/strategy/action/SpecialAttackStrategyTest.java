package de.tarent.mica.bot.strategy.action;

import static org.junit.Assert.*;

import org.junit.Test;

import de.tarent.mica.Action;
import de.tarent.mica.bot.strategy.StrategyStats;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.World;
import de.tarent.mica.model.element.Destroyer;
import de.tarent.mica.model.element.Ship.Orientation;

public class SpecialAttackStrategyTest {
	@StrategyStats(description = "Nur für Tests!")
	private static class MySpecialAttackStrategy extends SpecialAttackStrategy{
		@Override
		public Action getActionDecision(World world) {
			return null;
		}
	}
	
	SpecialAttackStrategy toTest = new MySpecialAttackStrategy();
	
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
		assertEquals(2, toTest.getPossibleSpecialAttackCount(Destroyer.class, world));
		
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
		assertEquals(1, toTest.getPossibleSpecialAttackCount(Destroyer.class, world));
		
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
		assertEquals(0, toTest.getPossibleSpecialAttackCount(Destroyer.class, world));
		
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
		assertEquals(0, toTest.getPossibleSpecialAttackCount(Destroyer.class, world));
		
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
		assertEquals(0, toTest.getPossibleSpecialAttackCount(Destroyer.class, world));
		
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
		assertEquals(1, toTest.getPossibleSpecialAttackCount(Destroyer.class, world));
		
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
		assertEquals(0, toTest.getPossibleSpecialAttackCount(Destroyer.class, world));
	}
}
