package de.tarent.mica.net;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.tarent.mica.Action;
import de.tarent.mica.GameActionHandler;
import de.tarent.mica.Action.Type;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.Fleet;
import de.tarent.mica.model.World;
import de.tarent.mica.model.element.Carrier;
import de.tarent.mica.model.element.Cruiser;
import de.tarent.mica.model.element.Destroyer;
import de.tarent.mica.model.element.Submarine;
import de.tarent.mica.model.element.Ship.Orientation;

public class PlayerActionCommandControllerTest {
	PlayerActionCommandController toTest;
	PlayerActionCommandController toTestSpy;
	GameActionHandler gameHandler;
	
	@Before
	public void setup() throws Exception{
		toTest = new WebSocketController("localhost", 1312);
		
		Fleet fleet = new Fleet();
		
		fleet.setCarrier1(new Carrier(Orientation.OST, new Coord("A1")));
		fleet.setCarrier2(new Carrier(Orientation.OST, new Coord("G1")));
		fleet.setCruiser1(new Cruiser(Orientation.OST, new Coord("C1")));
		fleet.setCruiser2(new Cruiser(Orientation.OST, new Coord("C6")));
		fleet.setDestroyer1(new Destroyer(Orientation.OST, new Coord("I1")));
		fleet.setDestroyer2(new Destroyer(Orientation.OST, new Coord("E1")));
		fleet.setSubmarine1(new Submarine(Orientation.OST, new Coord("F7")));
		fleet.setSubmarine2(new Submarine(Orientation.OST, new Coord("I8")));
		
		gameHandler = Mockito.mock(GameActionHandler.class);
		doReturn(fleet).when(gameHandler).getFleet();
		
		Utils.setPrivate("actionHistory", toTest, new ArrayList<Action>());
		Utils.setPrivate("hitHistory", toTest, new ArrayList<Action>());
		toTest.actionHandler = gameHandler;
		toTest.world = new World(10, 10);
		
		toTestSpy = Mockito.spy(toTest);
		doNothing().when(toTestSpy).send(anyString());
		doReturn(true).when(toTestSpy).connectBlocking();
	}
	
	@Test
	public void myTurn(){
		doReturn(new Action(Type.ATTACK, new Coord("A1"))).when(gameHandler).getNextAction(any(World.class));
		toTestSpy.myTurn();
		verify(toTestSpy).send(eq("A1"));
		
		doReturn(new Action(Type.CLUSTERBOMB, new Coord("A1"))).when(gameHandler).getNextAction(any(World.class));
		toTestSpy.myTurn();
		assertEquals(1, toTestSpy.world.getSpecialAttackCount(Carrier.class));
		verify(toTestSpy).send(eq("+A1"));
		
		doReturn(new Action(Type.SPY_DRONE, new Coord("A1"))).when(gameHandler).getNextAction(any(World.class));
		toTestSpy.myTurn();
		assertEquals(1, toTestSpy.world.getSpecialAttackCount(Destroyer.class));
		verify(toTestSpy).send(eq("#A1"));
		
		doReturn(new Action(Type.WILDFIRE, new Coord("A1"))).when(gameHandler).getNextAction(any(World.class));
		toTestSpy.myTurn();
		assertEquals(1, toTestSpy.world.getSpecialAttackCount(Cruiser.class));
		verify(toTestSpy).send(eq("*A1"));
		
		doReturn(new Action(Type.TORPEDO_NORD, new Coord("A1"))).when(gameHandler).getNextAction(any(World.class));
		toTestSpy.myTurn();
		assertEquals(1, toTestSpy.world.getSpecialAttackCount(Submarine.class));
		verify(toTestSpy).send(eq("NA1"));
		
		doReturn(new Action(Type.TORPEDO_OST, new Coord("A1"))).when(gameHandler).getNextAction(any(World.class));
		toTestSpy.myTurn();
		assertEquals(2, toTestSpy.world.getSpecialAttackCount(Submarine.class));
		verify(toTestSpy).send(eq("OA1"));
		
		doReturn(new Action(Type.TORPEDO_SUED, new Coord("A1"))).when(gameHandler).getNextAction(any(World.class));
		toTestSpy.myTurn();
		assertEquals(3, toTestSpy.world.getSpecialAttackCount(Submarine.class));
		verify(toTestSpy).send(eq("SA1"));
		
		doReturn(new Action(Type.TORPEDO_WEST, new Coord("A1"))).when(gameHandler).getNextAction(any(World.class));
		toTestSpy.myTurn();
		assertEquals(4, toTestSpy.world.getSpecialAttackCount(Submarine.class));
		verify(toTestSpy).send(eq("WA1"));
	}
	
	@Test
	public void hit_attack() throws Exception{
		World world = mock(World.class);
		List<Action> actionHistory = Collections.singletonList(new Action(Type.ATTACK, new Coord(0, 0)));
		toTest.world = world;
		Utils.setPrivate("actionHistory", toTest, actionHistory);
		
		toTest.hit(null);
		
		verify(world).registerHit(eq(actionHistory.get(0).getCoord()));
		List<Coord> hitCoords = (List<Coord>) Utils.getPrivate("hitHistory", toTest);
		assertEquals(1, hitCoords.size());
		assertEquals(new Coord(0, 0), hitCoords.get(0));
	}
	
	@Test
	public void hit_torpedoNord() throws Exception{
		World world = mock(World.class);
		List<Action> actionHistory = Collections.singletonList(new Action(Type.TORPEDO_NORD, new Coord(0, 10)));
		toTest.world = world;
		Utils.setPrivate("actionHistory", toTest, actionHistory);
		
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
		toTest.world = world;
		Utils.setPrivate("actionHistory", toTest, actionHistory);
		
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
		toTest.world = world;
		Utils.setPrivate("actionHistory", toTest, actionHistory);
		
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
		toTest.world = world;
		Utils.setPrivate("actionHistory", toTest, actionHistory);
		
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
		toTest.world = world;
		Utils.setPrivate("actionHistory", toTest, actionHistory);
		
		toTest.missed();
		
		verify(world).registerMiss(eq(actionHistory.get(0).getCoord()));
	}
	
	@Test
	public void missed_torpedoNord() throws Exception{
		World world = mock(World.class);
		doReturn(new Dimension(10, 10)).when(world).getWorldDimension();
		List<Action> actionHistory = Collections.singletonList(new Action(Type.TORPEDO_NORD, new Coord(0, 10)));
		toTest.world = world;
		Utils.setPrivate("actionHistory", toTest, actionHistory);
		
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
		toTest.world = world;
		Utils.setPrivate("actionHistory", toTest, actionHistory);
		
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
		toTest.world = world;
		Utils.setPrivate("actionHistory", toTest, actionHistory);
		
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
		toTest.world = world;
		Utils.setPrivate("actionHistory", toTest, actionHistory);
		
		toTest.missed();
		
		for(int x=9; x <= 0; x--){
			verify(world).registerHit(eq(new Coord(x, actionHistory.get(0).getCoord().getY())));
		}
	}
}
