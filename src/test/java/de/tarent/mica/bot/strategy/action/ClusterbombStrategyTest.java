package de.tarent.mica.bot.strategy.action;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import de.tarent.mica.Action;
import de.tarent.mica.Action.Type;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.World;

public class ClusterbombStrategyTest {
	ClusterbombStrategy toTest = new ClusterbombStrategy();
	
	@Test
	public void nextSpyAction(){
		toTest = new ClusterbombStrategy();	//keine bombenpunkte definiert
		
		assertNull(toTest.nextBombAction(null));
		
		toTest = new ClusterbombStrategy(new Coord("01"), new Coord("02"));
		Action result = toTest.nextBombAction(null);
		
		assertEquals(Type.CLUSTERBOMB, result.getType());
		assertEquals(new Coord("01"), result.getCoord());
		
		result = toTest.nextBombAction(null);
		
		assertEquals(Type.CLUSTERBOMB, result.getType());
		assertEquals(new Coord("02"), result.getCoord());
	}
	
	@Test
	public void getActionDecision(){
		ClusterbombStrategy bomb = spy(toTest);
		doReturn(false).when(bomb).canSpy(any(World.class));
		
		assertNull(bomb.getActionDecision(null));
		
		doReturn(true).when(bomb).canSpy(any(World.class));
		doReturn(new Action(Type.CLUSTERBOMB, new Coord("01"))).when(bomb).nextBombAction(any(World.class));
		
		assertEquals(bomb.nextBombAction(null), bomb.getActionDecision(null));
	}
}
