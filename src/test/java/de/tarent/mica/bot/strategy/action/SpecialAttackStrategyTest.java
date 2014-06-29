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
		@Override
		public String getShortName() {
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
		world.placeOwnShip(new Destroyer(Orientation.OST, new Coord("11")));
		world.placeOwnShip(new Destroyer(Orientation.OST, new Coord("30")));
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
		world.placeOwnShip(new Destroyer(Orientation.OST, new Coord("01")));
		world.placeOwnShip(new Destroyer(Orientation.SUED, new Coord("22")));
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
		world.placeOwnShip(new Destroyer(Orientation.OST, new Coord("01")));
		world.placeOwnShip(new Destroyer(Orientation.SUED, new Coord("22")));
		world.registerEnemySunk(new Coord("22"));
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
		world.placeOwnShip(new Destroyer(Orientation.OST, new Coord("11")));
		world.placeOwnShip(new Destroyer(Orientation.SUED, new Coord("30")));
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
		world.increaseSpecialAttack(Destroyer.class); //es wurde schon spoiniert
		world.placeOwnShip(new Destroyer(Orientation.OST, new Coord("11")));
		world.placeOwnShip(new Destroyer(Orientation.SUED, new Coord("32")));
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
		world.increaseSpecialAttack(Destroyer.class); //es wurde schon spoiniert
		world.placeOwnShip(new Destroyer(Orientation.OST, new Coord("11")));
		world.placeOwnShip(new Destroyer(Orientation.OST, new Coord("30")));
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
		world.increaseSpecialAttack(Destroyer.class);
		world.increaseSpecialAttack(Destroyer.class); //es wurde schon zweimal spoiniert
		world.placeOwnShip(new Destroyer(Orientation.OST, new Coord("11")));
		world.placeOwnShip(new Destroyer(Orientation.OST, new Coord("30")));
		assertEquals(0, toTest.getPossibleSpecialAttackCount(Destroyer.class, world));
	}
}
