package de.tarent.mica.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class WorldTest {

	@Test
	public void toString_test(){
		World world = new World(5, 5);
		
		assertEquals(
			"  0 1 2 3 4\n" +
			"A ? ? ? ? ?\n" +
			"B ? ? ? ? ?\n" +
			"C ? ? ? ? ?\n" +
			"D ? ? ? ? ?\n" +
			"E ? ? ? ? ?\n" +
			"\n" +
			"o)============(o\n" +
			"\n" +
			"  0 1 2 3 4\n" +
			"A ? ? ? ? ?\n" +
			"B ? ? ? ? ?\n" +
			"C ? ? ? ? ?\n" +
			"D ? ? ? ? ?\n" +
			"E ? ? ? ? ?\n", 
			world.toString());
	}
}
