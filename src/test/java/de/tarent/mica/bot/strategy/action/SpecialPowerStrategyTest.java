package de.tarent.mica.bot.strategy.action;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Test;

import de.tarent.mica.model.Coord;
import de.tarent.mica.model.World;
import de.tarent.mica.model.element.Carrier;
import de.tarent.mica.model.element.Cruiser;
import de.tarent.mica.model.element.Destroyer;
import de.tarent.mica.model.element.Ship;
import de.tarent.mica.model.element.Submarine;
import de.tarent.mica.model.element.Ship.Orientation;
import de.tarent.mica.model.element.UnknownShip;

public class SpecialPowerStrategyTest {
	SpecialAttackStrategy carrierStrategy = mock(SpecialAttackStrategy.class);
	SpecialAttackStrategy cruiserStrategy = mock(SpecialAttackStrategy.class);
	SpecialAttackStrategy destroyerStrategy = mock(SpecialAttackStrategy.class);
	SpecialAttackStrategy submarineStrategy = mock(SpecialAttackStrategy.class);
	
	SpecialPowerStrategy toTest = new SpecialPowerStrategy(carrierStrategy, cruiserStrategy, destroyerStrategy, submarineStrategy);
	
	@Test
	public void getAllShipsUnderAttack(){
		/*
		 *   0 1 2 3 4
		 * 0
		 * 1 *       *
		 * 2 *       *
		 * 3         +
		 * 4 * *
		 * 
		 */
		
		World world = new World(5, 5);
		world.placeOwnShip(new Submarine(Orientation.SUED, new Coord("10")));
		world.placeOwnShip(new Submarine(Orientation.OST, new Coord("40")));
		world.placeOwnShip(new Destroyer(Orientation.SUED, new Coord("14")));
		world.registerEnemyHit(new Coord("34"));
		
		List<Ship> result = toTest.getAllShipsUnderAttack(world);
		assertEquals(1, result.size());
		assertTrue(result.get(0) instanceof Destroyer);
		
		//auch das ubot wurde getroffen
		world.registerEnemyHit(new Coord("20"));
		
		result = toTest.getAllShipsUnderAttack(world);
		assertEquals(2, result.size());
		assertTrue(result.get(0) instanceof Submarine);
		assertTrue(result.get(1) instanceof Destroyer);
		
		//auch das ubot wurde getroffen -> es brennt
		world.registerEnemyBurn(new Coord("40"));
		
		result = toTest.getAllShipsUnderAttack(world);
		assertEquals(3, result.size());
		assertTrue(result.get(0) instanceof Submarine);
		assertTrue(result.get(0).isBurning());
		assertTrue(result.get(1) instanceof Submarine);
		assertTrue(result.get(2) instanceof Destroyer);
	}
	
	@Test
	public void useStrategy(){
		World world = new World(5, 5);
		
		toTest.useStrategy(Carrier.class, world);
		verify(carrierStrategy).getActionDecision(same(world));
		
		toTest.useStrategy(Cruiser.class, world);
		verify(cruiserStrategy).getActionDecision(same(world));
		
		toTest.useStrategy(Destroyer.class, world);
		verify(destroyerStrategy).getActionDecision(same(world));
		
		toTest.useStrategy(Submarine.class, world);
		verify(submarineStrategy).getActionDecision(same(world));
		
		assertNull(toTest.useStrategy(UnknownShip.class, world));
	}
	
	@Test
	public void useMostSpecialAttack(){
		/*
		 *   0 1 2 3 4 5 6 7 8 9
		 * 0 * * * * * * * *
		 * 1 * * * * * * * *
		 * 2 * * * * * *
		 * 3 * * * *
		 * 4 * *
		 * 5
		 * 6
		 * 7
		 * 8
		 * 9
		 * 
		 */
		
		SpecialPowerStrategy spy = spy(toTest);
		doReturn(null).when(spy).useStrategy(any(Class.class), any(World.class));
		
		World world = new World(10, 10);
		world.placeOwnShip(new Submarine(Orientation.SUED, new Coord("06")));
		world.placeOwnShip(new Submarine(Orientation.SUED, new Coord("07")));
		
		spy.useMostSpecialAttack(world);
		verify(spy).useStrategy(eq(Submarine.class), same(world));
		
		world.placeOwnShip(new Destroyer(Orientation.SUED, new Coord("04")));
		world.placeOwnShip(new Destroyer(Orientation.SUED, new Coord("05")));
		
		spy.useMostSpecialAttack(world);
		verify(spy).useStrategy(eq(Destroyer.class), same(world));
		
		world.placeOwnShip(new Cruiser(Orientation.SUED, new Coord("02")));
		world.placeOwnShip(new Cruiser(Orientation.SUED, new Coord("03")));
		
		spy.useMostSpecialAttack(world);
		verify(spy).useStrategy(eq(Cruiser.class), same(world));
		
		world.placeOwnShip(new Carrier(Orientation.SUED, new Coord("00")));
		world.placeOwnShip(new Carrier(Orientation.SUED, new Coord("01")));
		
		spy.useMostSpecialAttack(world);
		verify(spy).useStrategy(eq(Carrier.class), same(world));
	}
}
