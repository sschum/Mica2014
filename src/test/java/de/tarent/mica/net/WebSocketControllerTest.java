package de.tarent.mica.net;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.URISyntaxException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import de.tarent.mica.GameActionHandler;

public class WebSocketControllerTest {

	WebSocketController toTest;
	WebSocketController toTestSpy;
	
	@Before
	public void setup() throws URISyntaxException{
		toTest = new WebSocketController("", 1312);
		toTestSpy = spy(toTest);
	}
	
	@Test
	public void onOpen(){
		doNothing().when(toTestSpy).play();
		
		toTestSpy.onOpen(null);
		verify(toTestSpy).play();
	}
	
	@Test
	public void send(){
		toTestSpy.messageLog = new ArrayList<String>();
		try{
			toTestSpy.send("Msg");
		}catch(Exception e){}
		
		verify(toTestSpy).logSend(eq("Msg"));
	}
	
	@Test
	public void onMessage(){
		toTestSpy.messageLog = new ArrayList<String>();
		toTestSpy.dispatcher = mock(MessageDispatcher.class);
		
		toTestSpy.onMessage("Msg");
		verify(toTestSpy).logMessage(eq("Msg"));
		verify(toTestSpy.dispatcher).onMessage(eq("Msg"));
	}
	
	@Test
	public void play() throws InterruptedException{
		doReturn(true).when(toTestSpy).connectBlocking();
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				toTestSpy.play(null, mock(GameActionHandler.class));
			}
		});
		
		t.start();
		Thread.sleep(50);
		toTestSpy.stopPlay();
		t.join();
		
		verify(toTestSpy).connectBlocking();
	}
	
	@Test
	public void gameOver(){
		doNothing().when(toTestSpy).close();
		
		toTestSpy.actionHandler = mock(GameActionHandler.class);
		toTestSpy.gameOver(false);
		
		verify(toTestSpy).close();
	}
}
