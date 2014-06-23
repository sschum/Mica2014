package de.tarent.mica.net;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import de.tarent.mica.model.Coord;
import de.tarent.mica.model.Field;
import de.tarent.mica.model.World;
import de.tarent.mica.model.Field.Element;
import de.tarent.mica.model.element.SpyArea;

public class EnemyActionCommandControllerTest {
	EnemyActionCommandController toTest;
	EnemyActionCommandController toTestSpy;
	
	@Before
	public void setup() throws URISyntaxException{
		toTest = new WebSocketController("", 1312);
		toTest.reset();
		toTest.world = mock(World.class, Mockito.RETURNS_DEEP_STUBS);
		
		toTestSpy = spy(toTest);
	}
	
	@Test
	public void enemyHit(){
		toTest.enemyHit(new Coord(0, 0));
		verify(toTest.world).registerEnemyHit(eq(new Coord(0, 0)));
	}
	
	@Test
	public void enemyBurnHit(){
		toTest.enemyBurnHit(new Coord(0, 0));
		verify(toTest.world).registerEnemyBurn(eq(new Coord(0, 0)));
	}
	
	@Test
	public void enemyMissed(){
		toTest.enemyMissed(new Coord(0, 0));
		verify(toTest.world).registerEnemyMiss(eq(new Coord(0, 0)));
	}
	
	@Test
	public void enemySunk(){
		toTest.enemySunk(new Coord(0, 0));
		verify(toTest.world).registerEnemySunk(eq(new Coord(0, 0)));
	}
	
	@Test
	public void enemySpy(){
		toTest.enemySpy(new SpyArea(new Coord(0, 0)));
		verify(toTest.world).registerEnemySpy(eq(new SpyArea(new Coord(0, 0))));
	}
	
	@Test
	public void enemyClusterbombed(){
		Field enemyView = mock(Field.class);
		doReturn(enemyView).when(toTest.world).getEnemyView();
		doReturn(Element.UNBEKANNT).when(enemyView).get(any(Coord.class));
		toTest.enemyClusterbombed(new Coord("11"));
		
		ArgumentCaptor<Coord> cap = ArgumentCaptor.forClass(Coord.class);
		verify(toTest.world, times(5)).registerEnemyMiss(cap.capture());
		assertEquals(new Coord("11"), cap.getAllValues().get(0));
		assertEquals(new Coord("01"), cap.getAllValues().get(1));
		assertEquals(new Coord("12"), cap.getAllValues().get(2));
		assertEquals(new Coord("21"), cap.getAllValues().get(3));
		assertEquals(new Coord("10"), cap.getAllValues().get(4));
	}
	
	@Test
	public void enemyClusterbombed_alreadyHit(){
		Field enemyView = mock(Field.class);
		doReturn(enemyView).when(toTest.world).getEnemyView();
		doReturn(Element.TREFFER).when(enemyView).get(any(Coord.class));
		toTest.enemyClusterbombed(new Coord("11"));
		
		verify(toTest.world, never()).registerEnemyMiss(any(Coord.class));
	}
}
