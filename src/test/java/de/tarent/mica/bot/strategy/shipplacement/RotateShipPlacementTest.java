package de.tarent.mica.bot.strategy.shipplacement;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import de.tarent.mica.bot.strategy.StrategyStats;
import de.tarent.mica.bot.strategy.shipplacement.ReflectShipPlacement.MirrorAxis;
import de.tarent.mica.bot.strategy.shipplacement.RotateShipPlacement.RotateDirection;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.Fleet;
import de.tarent.mica.model.element.Ship.Orientation;
import de.tarent.mica.model.element.Submarine;

public class RotateShipPlacementTest {
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
	
	RotateShipPlacement toTest = new RotateShipPlacement(new StaticShipPlacement());
	
	@Test
	public void rotate(){
		/* Ausgangspunkt
		 *   0 1 2
		 * A *
		 * B *
		 * C
		 */
		Fleet fleet = new Fleet();
		fleet.setSubmarine1(new Submarine(Orientation.SUED, new Coord("00")));
		
		/* Erwartet
		 *   0 1 2
		 * A   * *
		 * B     
		 * C 
		 */
		fleet.setSubmarine1(new Submarine(Orientation.SUED, new Coord("00")));
		toTest.rotate(fleet, RotateDirection.RIGHT);
		assertEquals(
				new Submarine(Orientation.OST, new Coord("01")).getSpace(),
				fleet.getSubmarine1().getSpace());
		
		/* Erwartet
		 *   0 1 2
		 * A 
		 * B 
		 * C * *
		 */
		fleet.setSubmarine1(new Submarine(Orientation.SUED, new Coord("00")));
		toTest.rotate(fleet, RotateDirection.LEFT);
		assertEquals(
				new Submarine(Orientation.OST, new Coord("20")).getSpace(),
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
		assertTrue(positions.contains(new Coord("00")));	//ausgang
		assertTrue(positions.contains(new Coord("20")));	//ausgang links
		assertTrue(positions.contains(new Coord("01")));	//ausgang rechts
		assertTrue(positions.contains(new Coord("12")));	//ausgang 2x rechts/links
	}
}
