package de.tarent.mica.bot.strategy.shipplacement;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import de.tarent.mica.bot.strategy.StrategyStats;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.Fleet;
import de.tarent.mica.model.element.Ship.Orientation;
import de.tarent.mica.model.element.Submarine;

public class ShuffleShipPlacementTest {
	@StrategyStats(description = "Nur zum Testen gedacht!")
	private class StaticShipPlacement extends ShipPlacementStrategy {
		public StaticShipPlacement() {
			super(new Dimension(3, 3));
		}
		
		@Override
		public Fleet getFleet() {
			/* Ausgangspunkt
			 *   0 1 2
			 * A *
			 * B *
			 * C
			 */
			Fleet fleet = new Fleet();
			fleet.setSubmarine1(new Submarine(Orientation.SUED, new Coord("00")));
			
			return fleet;
		}
	}
	
	ShuffleShipPlacement toTest = new ShuffleShipPlacement(new StaticShipPlacement());
	
	private List<Coord> sorted(List<Coord> list){
		Collections.sort(list, Coord.COMPARATOR);
		return list;
	}
	
	@Test
	public void getFleet(){
		//durch das drehen und spiegeln müsste das einzelne schiff einmal am rand "laufen"
		Set<List<Coord>> positions = new HashSet<List<Coord>>();
		for(int i=0; i < 100; i++){
			positions.add(toTest.getFleet().getSubmarine1().getSpace());
		}

		assertEquals("Zufall nicht ausreichend verteilt! " + positions,
				8, positions.size());
		assertTrue(positions.contains(sorted(Arrays.asList(new Coord("00"), new Coord("01")))));
		assertTrue(positions.contains(sorted(Arrays.asList(new Coord("01"), new Coord("02")))));
		assertTrue(positions.contains(sorted(Arrays.asList(new Coord("02"), new Coord("12")))));
		assertTrue(positions.contains(sorted(Arrays.asList(new Coord("12"), new Coord("22")))));
		assertTrue(positions.contains(sorted(Arrays.asList(new Coord("22"), new Coord("21")))));
		assertTrue(positions.contains(sorted(Arrays.asList(new Coord("21"), new Coord("20")))));
		assertTrue(positions.contains(sorted(Arrays.asList(new Coord("20"), new Coord("10")))));
		assertTrue(positions.contains(sorted(Arrays.asList(new Coord("10"), new Coord("00")))));
	}
}
