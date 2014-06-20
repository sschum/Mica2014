package de.tarent.mica.bot.strategy.action;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import de.tarent.mica.Action;
import de.tarent.mica.Action.Type;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.World;

public class SpyStrategyTest {
	SpyStrategy toTest = new SpyStrategy();
	
	@Test
	public void nextSpyAction(){
		toTest = new SpyStrategy();	//keine spionagepunkte definiert
		
		assertNull(toTest.nextSpyAction(null));
		
		toTest = new SpyStrategy(new Coord("01"), new Coord("02"));
		Action result = toTest.nextSpyAction(null);
		
		assertEquals(Type.SPY_DRONE, result.getType());
		assertEquals(new Coord("01"), result.getCoord());
		
		result = toTest.nextSpyAction(null);
		
		assertEquals(Type.SPY_DRONE, result.getType());
		assertEquals(new Coord("02"), result.getCoord());
	}
	
	@Test
	public void getActionDecision(){
		SpyStrategy spy = spy(toTest);
		doReturn(false).when(spy).canSpy(any(World.class));
		
		assertNull(spy.getActionDecision(null));
		
		doReturn(true).when(spy).canSpy(any(World.class));
		doReturn(new Action(Type.SPY_DRONE, new Coord("01"))).when(spy).nextSpyAction(any(World.class));
		
		assertEquals(spy.nextSpyAction(null), spy.getActionDecision(null));
	}
}
