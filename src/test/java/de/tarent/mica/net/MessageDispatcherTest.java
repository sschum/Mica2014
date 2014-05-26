package de.tarent.mica.net;

import static org.mockito.Mockito.*;
import static de.tarent.mica.net.MessageCode.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.tarent.mica.model.Coord;
import de.tarent.mica.model.element.SpyArea;

public class MessageDispatcherTest {

	WebSocketController controller;
	MessageDispatcher toTest;
	
	@Before
	public void setup(){
		controller = Mockito.mock(WebSocketController.class);
		toTest = spy(new MessageDispatcher(controller));
	}
	
	private String msg(MessageCode code) {
		return msg(code, "Text");
	}
	
	private String msg(MessageCode code, String msg) {
		return code.getCode() + ":" + msg;
	}
	
	@Test
	public void onMessage(){
		toTest.onMessage(msg(HELLO, "Hello, player #1."));
		verify(controller).started(1);
		verify(controller).rename();
		
		toTest.onMessage(msg(PLACE_SHIPS));
		verify(controller).placeShips();
		
		toTest.onMessage(msg(YOUR_TURN));
		verify(controller).myTurn();
		
		toTest.onMessage(msg(ENEMY_SHIP_HIT));
		verify(controller).hit(eq((Coord)null));
		toTest.onMessage(msg(ENEMY_SHIP_HIT, "Enemy ship hit at F1."));
		verify(controller).hit(eq(new Coord("F1")));
		
		toTest.onMessage(msg(ENEMY_SHIP_MISSED));
		verify(controller).missed();
		toTest.onMessage(msg(TORPEDO));
		verify(controller, times(2)).missed();
		
		toTest.onMessage(msg(DRONE, "The drone found 3 ship segments at F1!"));
		verify(controller).spy(eq(new SpyArea(new Coord("F1"), 3)));
		
		toTest.onMessage(msg(YOUR_SHIP_HIT, "The enemy hits at F1."));
		verify(controller).enemyHit(eq(new Coord("F1")));
		toTest.onMessage(msg(YOUR_SHIP_HIT, "Your burned at F1!"));
		verify(controller).enemyBurnHit(eq(new Coord("F1")));
		
		toTest.onMessage(msg(YOUR_SHIP_MISSED, "Enemy shoots at F1 and misses."));
		verify(controller).enemyMissed(eq(new Coord("F1")));
		
		toTest.onMessage(msg(YOUR_SHIP_SUNK, "at F1!"));
		verify(controller).enemySunk(eq(new Coord("F1")));
		
		toTest.onMessage(msg(DRONEEE, "at F1!"));
		verify(controller).enemySpy(eq(new SpyArea(new Coord("F1"))));
	}

}
