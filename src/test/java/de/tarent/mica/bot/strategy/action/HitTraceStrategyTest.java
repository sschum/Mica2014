package de.tarent.mica.bot.strategy.action;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Ignore;
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
		Action result = toTest.getActionForMultipleCoords(world, list(new Coord("00"), new Coord("01")));
		assertEquals(new Coord("02"), result.getCoord());
		
		result = toTest.getActionForMultipleCoords(world, list(new Coord("03"), new Coord("04")));
		assertEquals(new Coord("02"), result.getCoord());
		
		result = toTest.getActionForMultipleCoords(world, list(new Coord("00"), new Coord("10")));
		assertEquals(new Coord("20"), result.getCoord());
		
		result = toTest.getActionForMultipleCoords(world, list(new Coord("30"), new Coord("40")));
		assertEquals(new Coord("20"), result.getCoord());
		
		//zwischenraum
		result = toTest.getActionForMultipleCoords(world, list(new Coord("00"), new Coord("02")));
		assertEquals(new Coord("01"), result.getCoord());
		
		result = toTest.getActionForMultipleCoords(world, list(new Coord("00"), new Coord("20")));
		assertEquals(new Coord("10"), result.getCoord());
		
		result = toTest.getActionForMultipleCoords(world, list(new Coord("00"), new Coord("03")));
		assertTrue(	new Coord("01").equals(result.getCoord()) ||
					new Coord("02").equals(result.getCoord()));
		
		result = toTest.getActionForMultipleCoords(world, list(new Coord("00"), new Coord("30")));
		assertTrue(	new Coord("10").equals(result.getCoord()) ||
					new Coord("20").equals(result.getCoord()));
		
		//mitte
		result = toTest.getActionForMultipleCoords(world, list(new Coord("01"), new Coord("02")));
		assertTrue(	new Coord("00").equals(result.getCoord()) ||
					new Coord("03").equals(result.getCoord()));
		
		result = toTest.getActionForMultipleCoords(world, list(new Coord("10"), new Coord("20")));
		assertTrue(	new Coord("00").equals(result.getCoord()) ||
					new Coord("30").equals(result.getCoord()));
		
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
		
		world.registerHit(new Coord("01")); world.registerHit(new Coord("02")); world.registerHit(new Coord("03"));
		result = toTest.getActionForMultipleCoords(world, list(new Coord("01"), new Coord("02"), new Coord("03")));
		assertTrue(result.toString(),
					new Coord("00").equals(result.getCoord()) ||
					new Coord("04").equals(result.getCoord()));
		
		
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
		world.registerHit(new Coord("11")); world.registerHit(new Coord("21")); world.registerHit(new Coord("31"));
		result = toTest.getActionForMultipleCoords(world, list(new Coord("11"), new Coord("21"), new Coord("31")));
		assertTrue(result.toString(),
					new Coord("01").equals(result.getCoord()) ||
					new Coord("41").equals(result.getCoord()));
		
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
		world.registerHit(new Coord("01")); world.registerHit(new Coord("11")); world.registerHit(new Coord("21"));
		result = toTest.getActionForMultipleCoords(world, list(new Coord("01"), new Coord("11"), new Coord("21")));
		assertEquals(new Coord("31"), result.getCoord());
		
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
		world.registerHit(new Coord("21")); world.registerHit(new Coord("31")); world.registerHit(new Coord("41"));
		result = toTest.getActionForMultipleCoords(world, list(new Coord("21"), new Coord("31"), new Coord("41")));
		assertEquals(new Coord("11"), result.getCoord());
		
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
		world.registerMiss(new Coord("01")); world.registerHit(new Coord("11")); world.registerHit(new Coord("21"));
		result = toTest.getActionForMultipleCoords(world, list(new Coord("11"), new Coord("21")));
		assertEquals(new Coord("31"), result.getCoord());
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
		
		ships.add(new UnknownShip(new Coord("00")));
		ships.add(new UnknownShip(new Coord("10")));
		
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
	
	@Test
	@Ignore("Bitte fix mich!") //TODO: Schiffe die "um die ecke" liegen dürfen nicht vorkommen...
	public void getActionDecision_nearShips(){
		/*  0 1 2 3
		 * 0  
		 * 1  * <= erster hit 
		 * 2                  
		 * 3  * <= zweiter hit
		 * 
		 * Diese Konstelation kann ZWEI sein ABER auch ein schiff. In der
		 * Welt, werden diese ersteinmal als ZWEI dargestellt. Die Chancen
		 * stehen hier aber gut, dass es sich um EIN schiff handelt...
		 */
		
		World world = new World(4, 4);
		world.registerHit(new Coord("11"));
		world.registerHit(new Coord("31"));
		
		assertEquals(new Coord("21"), toTest.getActionDecision(world).getCoord());
	}
}
