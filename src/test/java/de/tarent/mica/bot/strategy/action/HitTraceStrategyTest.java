package de.tarent.mica.bot.strategy.action;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.mockito.Mockito;

import de.tarent.mica.Action;
import de.tarent.mica.Action.Type;
import de.tarent.mica.bot.strategy.action.HitTraceStrategy;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.World;
import de.tarent.mica.model.element.Ship;
import de.tarent.mica.model.element.UnknownShip;

public class HitTraceStrategyTest {
	HitTraceStrategy toTest = new HitTraceStrategy();
	
	@Test
	public void getActionForSingleCoord(){
		World world = new World(3, 3);
		Coord c = new Coord(1, 1);
		
		for(int i=0; i < 50; i++){
			Action action = toTest.getActionForSingleCoord(world, c);
			assertTrue(	"Coord: " + c + "\n" + action,
						action.getCoord().equals(c.getNorthNeighbor()) ||
						action.getCoord().equals(c.getEastNeighbor()) ||
						action.getCoord().equals(c.getSouthNeighbor()) ||
						action.getCoord().equals(c.getWestNeighbor()));
		}
		
		world.registerMiss(c.getNorthNeighbor());
		world.registerMiss(c.getSouthNeighbor());
		for(int i=0; i < 50; i++){
			Action action = toTest.getActionForSingleCoord(world, c);
			assertTrue(	"Coord: " + c + "\n" + action,
						action.getCoord().equals(c.getEastNeighbor()) ||
						action.getCoord().equals(c.getWestNeighbor()));
		}
	}
	
	private List<Coord> list(Coord...coords){
		List<Coord> result = Arrays.asList(coords);
		
		for(int i=0; i < 13; i++) Collections.shuffle(result);
		
		return result;
	}
	
	@Test
	public void getActionForMultipleCoords(){
		/*
		 *   0 1 2 3 4
		 * A 
		 * B 
		 * C 
		 * D 
		 * E 
		 * 
		 */
		World world = new World(5, 5);
		
		//rand-test
		Action result = toTest.getActionForMultipleCoords(world, list(new Coord("A0"), new Coord("A1")));
		assertEquals(new Coord("A2"), result.getCoord());
		
		result = toTest.getActionForMultipleCoords(world, list(new Coord("A3"), new Coord("A4")));
		assertEquals(new Coord("A2"), result.getCoord());
		
		result = toTest.getActionForMultipleCoords(world, list(new Coord("A0"), new Coord("B0")));
		assertEquals(new Coord("C0"), result.getCoord());
		
		result = toTest.getActionForMultipleCoords(world, list(new Coord("D0"), new Coord("E0")));
		assertEquals(new Coord("C0"), result.getCoord());
		
		//zwischenraum
		result = toTest.getActionForMultipleCoords(world, list(new Coord("A0"), new Coord("A2")));
		assertEquals(new Coord("A1"), result.getCoord());
		
		result = toTest.getActionForMultipleCoords(world, list(new Coord("A0"), new Coord("C0")));
		assertEquals(new Coord("B0"), result.getCoord());
		
		result = toTest.getActionForMultipleCoords(world, list(new Coord("A0"), new Coord("A3")));
		assertTrue(	new Coord("A1").equals(result.getCoord()) ||
					new Coord("A2").equals(result.getCoord()));
		
		result = toTest.getActionForMultipleCoords(world, list(new Coord("A0"), new Coord("D0")));
		assertTrue(	new Coord("B0").equals(result.getCoord()) ||
					new Coord("C0").equals(result.getCoord()));
		
		//mitte
		result = toTest.getActionForMultipleCoords(world, list(new Coord("A1"), new Coord("A2")));
		assertTrue(	new Coord("A0").equals(result.getCoord()) ||
					new Coord("A3").equals(result.getCoord()));
		
		result = toTest.getActionForMultipleCoords(world, list(new Coord("B0"), new Coord("C0")));
		assertTrue(	new Coord("A0").equals(result.getCoord()) ||
					new Coord("D0").equals(result.getCoord()));
		
		//schiffe mit der größer >= 3
		/*
		 *   0 1 2 3 4
		 * A 
		 * B   * * * 
		 * C 
		 * D 
		 * E 
		 * 
		 */
		
		world.registerHit(new Coord("A1")); world.registerHit(new Coord("A2")); world.registerHit(new Coord("A3"));
		result = toTest.getActionForMultipleCoords(world, list(new Coord("A1"), new Coord("A2"), new Coord("A3")));
		assertTrue(result.toString(),
					new Coord("A0").equals(result.getCoord()) ||
					new Coord("A4").equals(result.getCoord()));
		
		
		/*
		 *   0 1 2 3 4
		 * A 
		 * B   * 
		 * C   *
		 * D   *
		 * E 
		 * 
		 */
		world = new World(5, 5);
		world.registerHit(new Coord("B1")); world.registerHit(new Coord("C1")); world.registerHit(new Coord("D1"));
		result = toTest.getActionForMultipleCoords(world, list(new Coord("B1"), new Coord("C1"), new Coord("D1")));
		assertTrue(result.toString(),
					new Coord("A1").equals(result.getCoord()) ||
					new Coord("E1").equals(result.getCoord()));
		
		/*
		 *   0 1 2 3 4
		 * A   *
		 * B   * 
		 * C   *
		 * D   
		 * E 
		 * 
		 */
		world = new World(5, 5);
		world.registerHit(new Coord("A1")); world.registerHit(new Coord("B1")); world.registerHit(new Coord("C1"));
		result = toTest.getActionForMultipleCoords(world, list(new Coord("A1"), new Coord("B1"), new Coord("C1")));
		assertEquals(new Coord("D1"), result.getCoord());
		
		/*
		 *   0 1 2 3 4
		 * A   
		 * B    
		 * C   *
		 * D   *
		 * E   *
		 * 
		 */
		world = new World(5, 5);
		world.registerHit(new Coord("C1")); world.registerHit(new Coord("D1")); world.registerHit(new Coord("E1"));
		result = toTest.getActionForMultipleCoords(world, list(new Coord("C1"), new Coord("D1"), new Coord("E1")));
		assertEquals(new Coord("B1"), result.getCoord());
		
		/*
		 *   0 1 2 3 4
		 * A   ~
		 * B   * 
		 * C   *
		 * D   
		 * E 
		 * 
		 */
		world = new World(5, 5);
		world.registerMiss(new Coord("A1")); world.registerHit(new Coord("B1")); world.registerHit(new Coord("C1"));
		result = toTest.getActionForMultipleCoords(world, list(new Coord("B1"), new Coord("C1")));
		assertEquals(new Coord("D1"), result.getCoord());
	}

	@Test
	public void shouldAttack(){
		Ship aShip = mock(Ship.class);
		doReturn(false).when(aShip).isSunken();
		doReturn(false).when(aShip).isBurning();
		
		assertTrue(new HitTraceStrategy(true).shouldAttack(aShip));
		
		doReturn(true).when(aShip).isBurning();
		assertFalse(new HitTraceStrategy(true).shouldAttack(aShip));
		
		doReturn(false).when(aShip).isBurning();
		doReturn(true).when(aShip).isSunken();
		assertFalse(new HitTraceStrategy(true).shouldAttack(aShip));
		assertFalse(new HitTraceStrategy(false).shouldAttack(aShip));
	}
	
	@Test
	public void getActionDecision(){
		HitTraceStrategy spy = Mockito.spy(toTest);
		doReturn(new Action(Type.ATTACK, new Coord(0, 0))).when(spy).getActionForMultipleCoords(any(World.class), anyList());
		doReturn(new Action(Type.ATTACK, new Coord(0, 0))).when(spy).getActionForSingleCoord(any(World.class), any(Coord.class));
		
		//kein schiff getroffen bis jetzt
		World world = mock(World.class);
		
		Set<Ship> ships = new HashSet<Ship>();
		doReturn(ships).when(world).getEnemyShips();
		
		Action result = spy.getActionDecision(world);
		assertNull(result);
		
		ships.add(new UnknownShip(new Coord("A0")));
		ships.add(new UnknownShip(new Coord("B0")));
		
		result = spy.getActionDecision(world);
		assertNotNull(result);
		assertSame(spy.getActionForSingleCoord(null, null), result);
		
		ships.clear();
		Ship aSunkenShip = mock(Ship.class);
		doReturn(true).when(aSunkenShip).isSunken();
		ships.add(aSunkenShip);
		result = spy.getActionDecision(world);
		assertNull(result);
	}
	
}
