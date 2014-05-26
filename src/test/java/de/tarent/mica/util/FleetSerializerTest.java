package de.tarent.mica.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import de.tarent.mica.model.Coord;
import de.tarent.mica.model.Fleet;
import de.tarent.mica.model.element.Carrier;
import de.tarent.mica.model.element.Cruiser;
import de.tarent.mica.model.element.Destroyer;
import de.tarent.mica.model.element.Submarine;
import de.tarent.mica.model.element.AbstractShip.Orientation;

public class FleetSerializerTest {

	@Test
	public void de_serialize_empty() throws IOException{
		Fleet fleet = new Fleet();
		
		File testFile = File.createTempFile("battleship", ".fleet");
		testFile.deleteOnExit();
		
		FleetSerializer.serialize(fleet, testFile);
		assertEquals(fleet, FleetSerializer.deserialize(testFile));
	}
	
	@Test
	public void de_serialize() throws IOException{
		Fleet fleet = new Fleet();
		fleet.setCarrier1(new Carrier(Orientation.OST, new Coord("A1")));
		fleet.setCarrier2(new Carrier(Orientation.OST, new Coord("G1")));
		fleet.setCruiser1(new Cruiser(Orientation.OST, new Coord("C1")));
		fleet.setCruiser2(new Cruiser(Orientation.OST, new Coord("C6")));
		fleet.setDestroyer1(new Destroyer(Orientation.OST, new Coord("I1")));
		fleet.setDestroyer2(new Destroyer(Orientation.OST, new Coord("E1")));
		fleet.setSubmarine1(new Submarine(Orientation.OST, new Coord("F7")));
		fleet.setSubmarine2(new Submarine(Orientation.OST, new Coord("I8")));
		
		File testFile = File.createTempFile("battleship", ".fleet");
		testFile.deleteOnExit();
		
		FleetSerializer.serialize(fleet, testFile);
		assertEquals(fleet.toString(), FleetSerializer.deserialize(testFile).toString());
	}
}
