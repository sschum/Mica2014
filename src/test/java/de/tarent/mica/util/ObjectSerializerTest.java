package de.tarent.mica.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.tarent.mica.model.Coord;
import de.tarent.mica.model.World;

public class ObjectSerializerTest {

	@Test
	public void deSerialize() throws Exception{
		final World world = new World(5, 5);
		final Random rnd = new Random();
		
		rnd.runXTimes(new Runnable() {
			@Override
			public void run() {
				Coord c = new Coord(rnd.nextInt(5), rnd.nextInt(5));
				
				world.registerEnemyMiss(c);
				world.registerMiss(c);
			}
		});
		
		String base64 = ObjectSerializer.serialise(world);
		assertEquals(world.toString(), ObjectSerializer.deserialise(base64).toString());
	}
}
