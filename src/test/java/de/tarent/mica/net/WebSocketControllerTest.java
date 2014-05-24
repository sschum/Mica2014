package de.tarent.mica.net;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.awt.Dimension;
import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.tarent.mica.Action;
import de.tarent.mica.GameActionHandler;
import de.tarent.mica.Action.Type;
import de.tarent.mica.GameActionHandler.Fleet;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.World;
import de.tarent.mica.model.ship.Carrier;
import de.tarent.mica.model.ship.Cruiser;
import de.tarent.mica.model.ship.Destroyer;
import de.tarent.mica.model.ship.Submarine;
import de.tarent.mica.model.ship.AbstractShip.Orientation;

public class WebSocketControllerTest {
	WebSocketController toTest;
	WebSocketController toTestSpy;
	GameActionHandler gameHandler;
	
	@Before
	public void setup() throws Exception{
		toTest = new WebSocketController("localhost", 1312);
		toTestSpy = Mockito.spy(toTest);
		doNothing().when(toTestSpy).send(anyString());
		doReturn(true).when(toTestSpy).connectBlocking();
		
		Fleet fleet = new Fleet();
		
		fleet.carrier1 = new Carrier(Orientation.OST, new Coord("A1"));
		fleet.carrier2 = new Carrier(Orientation.OST, new Coord("G1"));
		fleet.cruiser1 = new Cruiser(Orientation.OST, new Coord("C1"));
		fleet.cruiser2 = new Cruiser(Orientation.OST, new Coord("C6"));
		fleet.destroyer1 = new Destroyer(Orientation.OST, new Coord("I1"));
		fleet.destroyer2 = new Destroyer(Orientation.OST, new Coord("E1"));
		fleet.submarine1 = new Submarine(Orientation.OST, new Coord("F7"));
		fleet.submarine2 = new Submarine(Orientation.OST, new Coord("I8"));
		
		gameHandler = Mockito.mock(GameActionHandler.class);
		doReturn(fleet).when(gameHandler).getFleet();
		toTestSpy.play("Rainu", gameHandler);
	}		
	
	private Object getPrivate(String fieldName, Object object) throws Exception{
		Field field = object.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		try{
			return field.get(object);
		}finally{
			field.setAccessible(false);
		}
	}
	
	@Test
	public void play(){
		toTestSpy.play();
		verify(toTestSpy).send(eq("play"));
	}
	
	@Test
	public void started() throws Exception{
		assertNull(getPrivate("world", toTest));
		toTest.started();
		
		World world = (World)getPrivate("world", toTest);
		assertNotNull(world);
		assertEquals(new Dimension(10, 10), world.getWorldDimension());
	}
	
	@Test
	public void rename(){
		toTestSpy.rename();
		verify(toTestSpy).send(eq("rename Rainu"));
	}
	
	@Test
	public void placeShips() throws Exception{
		toTestSpy.started();
		toTestSpy.placeShips();
		
	    verify(toTestSpy).send(eq("A1,A2,A3,A4,A5"));
	    verify(toTestSpy).send(eq("G1,G2,G3,G4,G5"));
	    verify(toTestSpy).send(eq("C1,C2,C3,C4"));
	    verify(toTestSpy).send(eq("C6,C7,C8,C9"));
	    verify(toTestSpy).send(eq("I1,I2,I3"));
	    verify(toTestSpy).send(eq("E1,E2,E3"));
	    verify(toTestSpy).send(eq("F7,F8"));
	    verify(toTestSpy).send(eq("I8,I9"));
	}
	
	@Test
	public void myTurn(){
		doReturn(new Action(Type.ATTACK, new Coord("A1"))).when(gameHandler).getNextAction(any(World.class));
		toTestSpy.myTurn();
		verify(toTestSpy).send(eq("A1"));
		
		doReturn(new Action(Type.CLUSTERBOMB, new Coord("A1"))).when(gameHandler).getNextAction(any(World.class));
		toTestSpy.myTurn();
		verify(toTestSpy).send(eq("+A1"));
		
		doReturn(new Action(Type.SPY_DRONE, new Coord("A1"))).when(gameHandler).getNextAction(any(World.class));
		toTestSpy.myTurn();
		verify(toTestSpy).send(eq("#A1"));
		
		doReturn(new Action(Type.WILDFIRE, new Coord("A1"))).when(gameHandler).getNextAction(any(World.class));
		toTestSpy.myTurn();
		verify(toTestSpy).send(eq("*A1"));
		
		doReturn(new Action(Type.TORPEDO_NORD, new Coord("A1"))).when(gameHandler).getNextAction(any(World.class));
		toTestSpy.myTurn();
		verify(toTestSpy).send(eq("NA1"));
		
		doReturn(new Action(Type.TORPEDO_OST, new Coord("A1"))).when(gameHandler).getNextAction(any(World.class));
		toTestSpy.myTurn();
		verify(toTestSpy).send(eq("OA1"));
		
		doReturn(new Action(Type.TORPEDO_SUED, new Coord("A1"))).when(gameHandler).getNextAction(any(World.class));
		toTestSpy.myTurn();
		verify(toTestSpy).send(eq("SA1"));
		
		doReturn(new Action(Type.TORPEDO_WEST, new Coord("A1"))).when(gameHandler).getNextAction(any(World.class));
		toTestSpy.myTurn();
		verify(toTestSpy).send(eq("WA1"));
	}
}
