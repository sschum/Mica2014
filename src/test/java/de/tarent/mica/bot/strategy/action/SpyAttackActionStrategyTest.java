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

public class SpyAttackActionStrategyTest {
	SpyAttackStrategy toTest = new SpyAttackStrategy();
	
	@Test
	public void collectPossipleCoords(){
		/*
		 *  * => Coord
		 *  # => Spy (inklusive *)
		 *  
		 *   0 1 2 3 4 5
		 * A
		 * B   # # #
		 * C   # * #
		 * D   # # # 
		 * E
		 * 
		 */
		
		World world = new World(10, 10);
		SpyArea spy = new SpyArea(new Coord("C2"));
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
		
		spy = new SpyArea(new Coord("A0"));
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
		SpyArea spy = new SpyArea(new Coord("C2"), 3);
		world.registerSpy(spy);
		
		assertEquals(spy, toTest.nextSpyArea(world));
		
		//spy area bereits abgefarmt :D
		world.registerHit(new Coord("C2"));
		world.registerHit(new Coord("C1"));
		world.registerHit(new Coord("C3"));
		
		assertNull(toTest.nextSpyArea(world));
		
		SpyArea otherSpy = new SpyArea(new Coord("F9"), 3);
		world.registerSpy(otherSpy);
		
		assertEquals(otherSpy, toTest.nextSpyArea(world));
	}
	
	@Test
	public void nextAction(){
		SpyAttackStrategy spy = spy(toTest);
		doReturn(Collections.singletonList(new Coord("A1"))).when(spy).collectPossipleCoords(any(World.class), any(SpyArea.class));

		World world = new World(10, 10);
		assertEquals(new Action(Type.ATTACK, new Coord("A1")), spy.nextAction(world, null));
		
		world.registerHit(new Coord("A1"));
		assertNull(spy.nextAction(world, null));
	}
	
	@Test
	public void getActionDecision(){
		SpyAttackStrategy spy = spy(toTest);
		doReturn(null).when(spy).nextSpyArea(any(World.class));
		
		assertNull(spy.getActionDecision(null));
		
		doReturn(new SpyArea(null)).when(spy).nextSpyArea(any(World.class));
		doReturn(new Action(Type.ATTACK, new Coord("A1"))).when(spy).nextAction(any(World.class), any(SpyArea.class));
		assertEquals(spy.nextAction(null, null), spy.getActionDecision(null));
	}
}
