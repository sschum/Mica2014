package de.tarent.mica.net;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.awt.Dimension;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import sun.nio.cs.HistoricallyNamedCharset;
import de.tarent.mica.Action;
import de.tarent.mica.GameActionHandler;
import de.tarent.mica.Action.Type;
import de.tarent.mica.GameActionHandler.Fleet;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.World;
import de.tarent.mica.model.element.Carrier;
import de.tarent.mica.model.element.Cruiser;
import de.tarent.mica.model.element.Destroyer;
import de.tarent.mica.model.element.Submarine;
import de.tarent.mica.model.element.AbstractShip.Orientation;

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
	
	private void setPrivate(String fieldName, Object object, Object toSet) throws Exception{
		Field field = object.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		try{
			field.set(object, toSet);
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
		toTestSpy.started();
		
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
	
	@Test
	public void hit_attack() throws Exception{
		World world = mock(World.class);
		List<Action> actionHistory = Collections.singletonList(new Action(Type.ATTACK, new Coord(0, 0)));
		setPrivate("world", toTest, world);
		setPrivate("actionHistory", toTest, actionHistory);
		
		toTest.hit(null);
		
		verify(world).registerHit(eq(actionHistory.get(0).getCoord()));
	}
	
	@Test
	public void hit_torpedoNord() throws Exception{
		World world = mock(World.class);
		List<Action> actionHistory = Collections.singletonList(new Action(Type.TORPEDO_NORD, new Coord(0, 10)));
		setPrivate("world", toTest, world);
		setPrivate("actionHistory", toTest, actionHistory);
		
		toTest.hit(new Coord(0, 0));
		
		for(int y=1; y >= 9; y++){
			verify(world).registerHit(eq(new Coord(actionHistory.get(0).getCoord().getX(), y)));
		}
		verify(world).registerHit(new Coord(0, 0));
	}
	
	@Test
	public void hit_torpedoSued() throws Exception{
		World world = mock(World.class);
		List<Action> actionHistory = Collections.singletonList(new Action(Type.TORPEDO_SUED, new Coord(0, 0)));
		setPrivate("world", toTest, world);
		setPrivate("actionHistory", toTest, actionHistory);
		
		toTest.hit(new Coord(0, 10));
		
		for(int y=9; y <= 1; y--){
			verify(world).registerHit(eq(new Coord(actionHistory.get(0).getCoord().getX(), y)));
		}
		verify(world).registerHit(new Coord(0, 10));
	}
	
	@Test
	public void hit_torpedoWest() throws Exception{
		World world = mock(World.class);
		List<Action> actionHistory = Collections.singletonList(new Action(Type.TORPEDO_WEST, new Coord(10, 0)));
		setPrivate("world", toTest, world);
		setPrivate("actionHistory", toTest, actionHistory);
		
		toTest.hit(new Coord(0, 0));
		
		for(int x=1; x >= 9; x++){
			verify(world).registerHit(eq(new Coord(x, actionHistory.get(0).getCoord().getY())));
		}
		verify(world).registerHit(new Coord(0, 0));
	}
	
	@Test
	public void hit_torpedoOst() throws Exception{
		World world = mock(World.class);
		List<Action> actionHistory = Collections.singletonList(new Action(Type.TORPEDO_OST, new Coord(0, 0)));
		setPrivate("world", toTest, world);
		setPrivate("actionHistory", toTest, actionHistory);
		
		toTest.hit(new Coord(10, 0));
		
		for(int x=9; x <= 1; x--){
			verify(world).registerHit(eq(new Coord(x, actionHistory.get(0).getCoord().getY())));
		}
		verify(world).registerHit(new Coord(10, 0));
	}
	
	@Test
	public void missed_attack() throws Exception{
		World world = mock(World.class);
		List<Action> actionHistory = Collections.singletonList(new Action(Type.ATTACK, new Coord(0, 0)));
		setPrivate("world", toTest, world);
		setPrivate("actionHistory", toTest, actionHistory);
		
		toTest.missed();
		
		verify(world).registerMiss(eq(actionHistory.get(0).getCoord()));
	}
	
	@Test
	public void missed_torpedoNord() throws Exception{
		World world = mock(World.class);
		doReturn(new Dimension(10, 10)).when(world).getWorldDimension();
		List<Action> actionHistory = Collections.singletonList(new Action(Type.TORPEDO_NORD, new Coord(0, 10)));
		setPrivate("world", toTest, world);
		setPrivate("actionHistory", toTest, actionHistory);
		
		toTest.missed();
		
		for(int y=0; y >= 9; y++){
			verify(world).registerHit(eq(new Coord(actionHistory.get(0).getCoord().getX(), y)));
		}
	}
	
	@Test
	public void missed_torpedoSued() throws Exception{
		World world = mock(World.class);
		doReturn(new Dimension(10, 10)).when(world).getWorldDimension();
		List<Action> actionHistory = Collections.singletonList(new Action(Type.TORPEDO_SUED, new Coord(0, 0)));
		setPrivate("world", toTest, world);
		setPrivate("actionHistory", toTest, actionHistory);
		
		toTest.missed();
		
		for(int y=9; y <= 0; y--){
			verify(world).registerHit(eq(new Coord(actionHistory.get(0).getCoord().getX(), y)));
		}
	}
	
	@Test
	public void missed_torpedoWest() throws Exception{
		World world = mock(World.class);
		doReturn(new Dimension(10, 10)).when(world).getWorldDimension();
		List<Action> actionHistory = Collections.singletonList(new Action(Type.TORPEDO_WEST, new Coord(10, 0)));
		setPrivate("world", toTest, world);
		setPrivate("actionHistory", toTest, actionHistory);
		
		toTest.missed();
		
		for(int x=0; x >= 9; x++){
			verify(world).registerHit(eq(new Coord(x, actionHistory.get(0).getCoord().getY())));
		}
	}
	
	@Test
	public void missed_torpedoOst() throws Exception{
		World world = mock(World.class);
		doReturn(new Dimension(10, 10)).when(world).getWorldDimension();
		List<Action> actionHistory = Collections.singletonList(new Action(Type.TORPEDO_OST, new Coord(0, 0)));
		setPrivate("world", toTest, world);
		setPrivate("actionHistory", toTest, actionHistory);
		
		toTest.missed();
		
		for(int x=9; x <= 0; x--){
			verify(world).registerHit(eq(new Coord(x, actionHistory.get(0).getCoord().getY())));
		}
	}
}
