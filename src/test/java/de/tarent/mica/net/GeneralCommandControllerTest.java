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
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.Fleet;
import de.tarent.mica.model.World;
import de.tarent.mica.model.element.Carrier;
import de.tarent.mica.model.element.Cruiser;
import de.tarent.mica.model.element.Destroyer;
import de.tarent.mica.model.element.Submarine;
import de.tarent.mica.model.element.Ship.Orientation;

public class GeneralCommandControllerTest {
	GeneralCommandController toTest;
	GeneralCommandController toTestSpy;
	GameActionHandler gameHandler;
	
	@Before
	public void setup() throws Exception{
		toTest = new WebSocketController("localhost", 1312);
		
		Fleet fleet = new Fleet();
		
		fleet.setCarrier1(new Carrier(Orientation.OST, new Coord("01")));
		fleet.setCarrier2(new Carrier(Orientation.OST, new Coord("61")));
		fleet.setCruiser1(new Cruiser(Orientation.OST, new Coord("21")));
		fleet.setCruiser2(new Cruiser(Orientation.OST, new Coord("26")));
		fleet.setDestroyer1(new Destroyer(Orientation.OST, new Coord("81")));
		fleet.setDestroyer2(new Destroyer(Orientation.OST, new Coord("41")));
		fleet.setSubmarine1(new Submarine(Orientation.OST, new Coord("57")));
		fleet.setSubmarine2(new Submarine(Orientation.OST, new Coord("88")));
		
		gameHandler = Mockito.mock(GameActionHandler.class);
		doReturn(fleet).when(gameHandler).getFleet();
		toTest.ownName = "Rainu";
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
		toTest.started(1);
		
		World world = toTest.world;
		assertNotNull(world);
		assertEquals(new Dimension(16, 16), world.getWorldDimension());
	}
	
	@Test
	public void rename(){
		toTestSpy.rename();
		verify(toTestSpy).send(eq("rename Rainu"));
	}
	
	@Test
	public void placeShips() throws Exception{
		toTestSpy.started(1);
		toTestSpy.placeShips();
		
	    verify(toTestSpy).send(eq("01,02,03,04,05"));
	    verify(toTestSpy).send(eq("61,62,63,64,65"));
	    verify(toTestSpy).send(eq("21,22,23,24"));
	    verify(toTestSpy).send(eq("26,27,28,29"));
	    verify(toTestSpy).send(eq("81,82,83"));
	    verify(toTestSpy).send(eq("41,42,43"));
	    verify(toTestSpy).send(eq("57,58"));
	    verify(toTestSpy).send(eq("88,89"));
	}
}
