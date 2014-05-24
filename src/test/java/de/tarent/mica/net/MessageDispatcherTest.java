package de.tarent.mica.net;

import static org.mockito.Mockito.*;
import static de.tarent.mica.net.MessageCode.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class MessageDispatcherTest {

	WebSocketController controller;
	MessageDispatcher toTest;
	
	@Before
	public void setup(){
		controller = Mockito.mock(WebSocketController.class);
		toTest = spy(new MessageDispatcher(controller));
	}
	
	private String msg(MessageCode code) {
		return code.getCode() + ": Text";
	}
	
	@Test
	public void onMessage(){
		toTest.onMessage(msg(HELLO));
		verify(controller).rename();
		
		toTest.onMessage(msg(PLACE_SHIPS));
		verify(controller).placeShips();
		
		toTest.onMessage(msg(YOUR_TURN));
		verify(controller).myTurn();
	}

}
