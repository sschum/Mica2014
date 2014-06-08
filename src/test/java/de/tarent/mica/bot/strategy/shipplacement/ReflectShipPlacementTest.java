package de.tarent.mica.bot.strategy.shipplacement;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import de.tarent.mica.bot.strategy.StrategyStats;
import de.tarent.mica.bot.strategy.shipplacement.ReflectShipPlacement.MirrorAxis;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.Fleet;
import de.tarent.mica.model.element.Ship.Orientation;
import de.tarent.mica.model.element.Submarine;

public class ReflectShipPlacementTest {
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
	
	ReflectShipPlacement toTest = new ReflectShipPlacement(new StaticShipPlacement());
	
	@Test
	public void mirror(){
		/* Ausgangspunkt
		 *   0 1 2
		 * A *
		 * B *
		 * C
		 */
		Fleet fleet = new Fleet();
		fleet.setSubmarine1(new Submarine(Orientation.SUED, new Coord("A0")));
		
		/* Erwartet
		 *   0 1 2
		 * A     *
		 * B     *
		 * C 
		 */
		fleet.setSubmarine1(new Submarine(Orientation.SUED, new Coord("A0")));
		toTest.reflect(fleet, MirrorAxis.VERTICAL);
		assertEquals(
				new Submarine(Orientation.SUED, new Coord("A2")).getSpace(),
				fleet.getSubmarine1().getSpace());
		
		/* Erwartet
		 *   0 1 2
		 * A 
		 * B *
		 * C *
		 */
		fleet.setSubmarine1(new Submarine(Orientation.SUED, new Coord("A0")));
		toTest.reflect(fleet, MirrorAxis.HORIZONTAL);
		assertEquals(
				new Submarine(Orientation.SUED, new Coord("B0")).getSpace(),
				fleet.getSubmarine1().getSpace());
	}
	
	@Test
	public void getFleet(){
		Set<Coord> positions = new HashSet<Coord>();
		for(int i=0; i < 100; i++){
			positions.add(toTest.getFleet().getSubmarine1().getPosition());
		}
		
		assertEquals("Zufall nicht ausreichend verteilt! " + positions,
				4, positions.size());
		assertTrue(positions.contains(new Coord("A0")));	//ausgang
		assertTrue(positions.contains(new Coord("B0")));	//ausgang horizontal
		assertTrue(positions.contains(new Coord("A2")));	//ausgang vertikal
		assertTrue(positions.contains(new Coord("B2")));	//ausgang vertikal wiederum vertikal gespiegelt
	}
}
