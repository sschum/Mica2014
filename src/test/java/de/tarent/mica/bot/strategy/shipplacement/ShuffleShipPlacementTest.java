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
			fleet.setSubmarine1(new Submarine(Orientation.SUED, new Coord("A0")));
			
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
		assertTrue(positions.contains(sorted(Arrays.asList(new Coord("A0"), new Coord("A1")))));
		assertTrue(positions.contains(sorted(Arrays.asList(new Coord("A1"), new Coord("A2")))));
		assertTrue(positions.contains(sorted(Arrays.asList(new Coord("A2"), new Coord("B2")))));
		assertTrue(positions.contains(sorted(Arrays.asList(new Coord("B2"), new Coord("C2")))));
		assertTrue(positions.contains(sorted(Arrays.asList(new Coord("C2"), new Coord("C1")))));
		assertTrue(positions.contains(sorted(Arrays.asList(new Coord("C1"), new Coord("C0")))));
		assertTrue(positions.contains(sorted(Arrays.asList(new Coord("C0"), new Coord("B0")))));
		assertTrue(positions.contains(sorted(Arrays.asList(new Coord("B0"), new Coord("A0")))));
	}
}
