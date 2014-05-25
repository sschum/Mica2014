package de.tarent.mica.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.awt.Dimension;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.tarent.mica.GameActionHandler;
import de.tarent.mica.GameActionHandler.Fleet;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.World;
import de.tarent.mica.model.element.Carrier;
import de.tarent.mica.model.element.Cruiser;
import de.tarent.mica.model.element.Destroyer;
import de.tarent.mica.model.element.Submarine;
import de.tarent.mica.model.element.AbstractShip.Orientation;

public class GeneralCommandControllerTest {
	GeneralCommandController toTest;
	GeneralCommandController toTestSpy;
	GameActionHandler gameHandler;
	
	@Before
	public void setup() throws Exception{
		toTest = new WebSocketController("localhost", 1312);
		
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
		toTest.name = "Rainu";
		toTest.actionHandler = gameHandler;
		
		toTestSpy = Mockito.spy(toTest);
		doNothing().when(toTestSpy).send(anyString());
		doReturn(true).when(toTestSpy).connectBlocking();
	}
	
	@Test
	public void play(){
		toTestSpy.play();
		verify(toTestSpy).send(eq("play"));
	}
	
	@Test
	public void started() throws Exception{
		assertNull(toTest.world);
		toTest.started();
		
		World world = toTest.world;
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
}
