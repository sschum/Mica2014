package de.tarent.mica.bot.strategy.action;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import de.tarent.mica.Action;
import de.tarent.mica.Action.Type;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.World;
import de.tarent.mica.model.element.SpyArea;

public class SpyAttackStrategyTest {
	SpyAttackStrategy toTest = new SpyAttackStrategy();
	
	@Test
	public void collectPossipleCoords(){
		/*
		 *  * => Coord
		 *  # => Spy (inklusive *)
		 *  
		 *   0 1 2 3 4 5
		 * 0
		 * 1   # # #
		 * 2   # * #
		 * 3   # # # 
		 * 4
		 * 
		 */
		
		World world = new World(10, 10);
		SpyArea spy = new SpyArea(new Coord("22"));
		List<Coord> result = toTest.collectPossipleCoords(world, spy);
		
		//damit sichergestellt ist, dass da auch keine duplikate vorkommen...
		assertEquals(9, new HashSet<Coord>(result).size());	
		assertTrue(result.contains(spy.getCoord()));
		assertTrue(result.contains(spy.getCoord().getNorthNeighbor()));
		assertTrue(result.contains(spy.getCoord().getEastNeighbor()));
		assertTrue(result.contains(spy.getCoord().getSouthNeighbor()));
		assertTrue(result.contains(spy.getCoord().getWestNeighbor()));
		assertTrue(result.contains(spy.getCoord().getNorthNeighbor().getWestNeighbor()));
		assertTrue(result.contains(spy.getCoord().getNorthNeighbor().getEastNeighbor()));
		assertTrue(result.contains(spy.getCoord().getSouthNeighbor().getWestNeighbor()));
		assertTrue(result.contains(spy.getCoord().getSouthNeighbor().getEastNeighbor()));
		
		spy = new SpyArea(new Coord("00"));
		result = toTest.collectPossipleCoords(world, spy);
		
		//damit sichergestellt ist, dass da auch keine duplikate vorkommen...
		assertEquals(4, new HashSet<Coord>(result).size());	
		assertTrue(result.contains(spy.getCoord()));
		assertTrue(result.contains(spy.getCoord().getEastNeighbor()));
		assertTrue(result.contains(spy.getCoord().getSouthNeighbor()));
		assertTrue(result.contains(spy.getCoord().getSouthNeighbor().getEastNeighbor()));
	}
	
	@Test
	public void nextArea(){
		World world = new World(10, 10);
		SpyArea spy = new SpyArea(new Coord("22"), 3);
		world.registerSpy(spy);
		
		assertEquals(spy, toTest.nextSpyArea(world));
		
		//spy area bereits abgefarmt :D
		world.registerHit(new Coord("22"));
		world.registerHit(new Coord("21"));
		world.registerHit(new Coord("23"));
		
		assertNull(toTest.nextSpyArea(world));
		
		SpyArea otherSpy = new SpyArea(new Coord("99"), 3);
		world.registerSpy(otherSpy);
		
		assertEquals(otherSpy, toTest.nextSpyArea(world));
	}
	
	@Test
	public void nextAction(){
		SpyAttackStrategy spy = spy(toTest);
		doReturn(Collections.singletonList(new Coord("01"))).when(spy).collectPossipleCoords(any(World.class), any(SpyArea.class));

		World world = new World(10, 10);
		assertEquals(new Action(Type.ATTACK, new Coord("01")), spy.nextAction(world, null));
		
		world.registerHit(new Coord("01"));
		assertNull(spy.nextAction(world, null));
	}
	
	@Test
	public void getActionDecision(){
		SpyAttackStrategy spy = spy(toTest);
		doReturn(null).when(spy).nextSpyArea(any(World.class));
		
		assertNull(spy.getActionDecision(null));
		
		doReturn(new SpyArea(null)).when(spy).nextSpyArea(any(World.class));
		doReturn(new Action(Type.ATTACK, new Coord("01"))).when(spy).nextAction(any(World.class), any(SpyArea.class));
		assertEquals(spy.nextAction(null, null), spy.getActionDecision(null));
	}
}
