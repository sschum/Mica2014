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
import de.tarent.mica.model.element.Ship.Orientation;

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
		fleet.setCarrier1(new Carrier(Orientation.OST, new Coord("01")));
		fleet.setCarrier2(new Carrier(Orientation.OST, new Coord("61")));
		fleet.setCruiser1(new Cruiser(Orientation.OST, new Coord("21")));
		fleet.setCruiser2(new Cruiser(Orientation.OST, new Coord("26")));
		fleet.setDestroyer1(new Destroyer(Orientation.OST, new Coord("81")));
		fleet.setDestroyer2(new Destroyer(Orientation.OST, new Coord("41")));
		fleet.setSubmarine1(new Submarine(Orientation.OST, new Coord("57")));
		fleet.setSubmarine2(new Submarine(Orientation.OST, new Coord("88")));
		
		File testFile = File.createTempFile("battleship", ".fleet");
		testFile.deleteOnExit();
		
		FleetSerializer.serialize(fleet, testFile);
		assertEquals(fleet.toString(), FleetSerializer.deserialize(testFile).toString());
	}
}
