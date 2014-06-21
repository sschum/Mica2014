package de.tarent.mica.bot.strategy.shipplacement;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import de.tarent.mica.bot.strategy.StrategyStats;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.Fleet;
import de.tarent.mica.model.element.Carrier;
import de.tarent.mica.model.element.Cruiser;
import de.tarent.mica.model.element.Destroyer;
import de.tarent.mica.model.element.Ship;
import de.tarent.mica.model.element.Ship.Orientation;
import de.tarent.mica.model.element.Submarine;

public class MovingShipPlacementTest {
	@StrategyStats(description = "Nur zum Testen gedacht!")
	private class StaticShipPlacement extends ShipPlacementStrategy {
		public StaticShipPlacement(Dimension dim) {
			super(dim);
		}
		
		@Override
		public Fleet getFleet() {
			Fleet fleet = new Fleet();
			fleet.setCarrier1(new Carrier(Orientation.OST, new Coord("01")));
			fleet.setCarrier2(new Carrier(Orientation.OST, new Coord("61")));
			fleet.setCruiser1(new Cruiser(Orientation.OST, new Coord("21")));
			fleet.setCruiser2(new Cruiser(Orientation.OST, new Coord("26")));
			fleet.setDestroyer1(new Destroyer(Orientation.OST, new Coord("81")));
			fleet.setDestroyer2(new Destroyer(Orientation.OST, new Coord("41")));
			fleet.setSubmarine1(new Submarine(Orientation.OST, new Coord("57")));
			fleet.setSubmarine2(new Submarine(Orientation.OST, new Coord("88")));
			
			return fleet;
		}
	}
	
	MovingShipPlacement toTest = new MovingShipPlacement(new StaticShipPlacement(new Dimension(4, 4)));
	
	@Test
	public void collides(){
		/* Ausgangspunkt
		 *   0 1 2 3 4
		 * 0 *       
		 * 1 *   * * *
		 * 2         
		 * 3 * * * *
		 * 4
		 */
		
		Set<Ship> others = new HashSet<Ship>(3);
		others.add(new Submarine(Orientation.SUED, new Coord("00")));
		others.add(new Destroyer(Orientation.OST, new Coord("12")));
		others.add(new Cruiser(Orientation.OST, new Coord("30")));
		
		assertTrue(toTest.collides(new Submarine(Orientation.SUED, new Coord("00")), others));
		assertTrue(toTest.collides(new Submarine(Orientation.SUED, new Coord("10")), others));
		assertTrue(toTest.collides(new Submarine(Orientation.OST, new Coord("31")), others));
		assertTrue(toTest.collides(new Submarine(Orientation.SUED, new Coord("44")), others));
		
		assertFalse(toTest.collides(new Submarine(Orientation.SUED, new Coord("54")), others));
	}
	
	@Test
	public void getFreePositions(){
		/* Ausgangspunkt
		 *   0 1 2 3
		 * 0 *       
		 * 1 *  
		 * 2
		 * 3
		 */
		
		Set<Ship> ships = new HashSet<Ship>(1);
		ships.add(new Submarine(Orientation.SUED, new Coord("00")));
		
		List<Coord> result = toTest.getFreePositions(ships);
		assertEquals(10, result.size());
		
		/* Ausgangspunkt
		 *   0 1 2 3
		 * 0        
		 * 1   *
		 * 2   *
		 * 3
		 */
		
		ships = new HashSet<Ship>(1);
		ships.add(new Submarine(Orientation.SUED, new Coord("11")));
		
		result = toTest.getFreePositions(ships);
		assertEquals(4, result.size());
	}
	
	@Test
	public void moveFreely(){
		/* Ausgangspunkt
		 *   0 1 2 3
		 * 0        
		 * 1   *
		 * 2   *   *
		 * 3       *
		 */
		Fleet fleet = new Fleet();
		fleet.setSubmarine1(new Submarine(Orientation.SUED, new Coord("11")));
		fleet.setSubmarine2(new Submarine(Orientation.SUED, new Coord("23")));
	
		Set<Coord> positions = new HashSet<Coord>(Arrays.asList(new Coord("03"), new Coord("13"), new Coord("23")));
		for(int i=0; i < 25; i++){
			toTest.moveFreely(fleet, fleet.getSubmarine2());
			positions.remove(fleet.getSubmarine2().getPosition());
		}
		assertTrue("Zufall nicht ausreichend verteilt! " + positions.size(), positions.isEmpty());
		
		/* Ausgangspunkt
		 *   0 1 2 3
		 * 0       *
		 * 1   *   *
		 * 2   *   *
		 * 3       *
		 */
		fleet = new Fleet();
		fleet.setSubmarine1(new Submarine(Orientation.SUED, new Coord("11")));
		fleet.setCruiser1(new Cruiser(Orientation.SUED, new Coord("03")));
		
		toTest.moveFreely(fleet, fleet.getCruiser1());
		assertEquals(new Coord("03"), fleet.getCruiser1().getPosition());
	}
	
	@Test
	public void moveLine_vertical(){
		toTest = new MovingShipPlacement(new StaticShipPlacement(new Dimension(3, 7)));
		/* Ausgangspunkt
		 *   0 1 2
		 * 0   *    
		 * 1   *
		 * 2   
		 * 3   
		 * 4
		 * 5   *
		 * 6   *
		 */
		Fleet fleet = new Fleet();
		fleet.setSubmarine1(new Submarine(Orientation.SUED, new Coord("01")));
		fleet.setSubmarine2(new Submarine(Orientation.SUED, new Coord("51")));
		
		Set<Coord> positions = new HashSet<Coord>(Arrays.asList(new Coord("01"), new Coord("11"), new Coord("21"), new Coord("31"), new Coord("41"), new Coord("51")));
		for(int i=0; i < 30; i++){
			toTest.move(fleet, fleet.getSubmarine1(), fleet.getSubmarine2(), 1);
			positions.remove(fleet.getSubmarine1().getPosition());
			positions.remove(fleet.getSubmarine2().getPosition());
		}
		assertTrue("Zufall nicht ausreichend verteilt! " + positions.size(), positions.isEmpty());
		
		/* Ausgangspunkt
		 *   0 1 2
		 * 0   *    
		 * 1   *
		 * 2   
		 * 3 * * * 
		 * 4
		 * 5   *
		 * 6   *
		 */
		fleet = new Fleet();
		fleet.setSubmarine1(new Submarine(Orientation.SUED, new Coord("01")));
		fleet.setSubmarine2(new Submarine(Orientation.SUED, new Coord("51")));
		fleet.setDestroyer1(new Destroyer(Orientation.OST, new Coord("30")));
		
		toTest.move(fleet, fleet.getSubmarine1(), fleet.getSubmarine2(), 1);
		assertEquals(new Coord("01"), fleet.getSubmarine1().getPosition());
		assertEquals(new Coord("51"), fleet.getSubmarine2().getPosition());
	}
	
	@Test
	public void moveLine_horizontal(){
		toTest = new MovingShipPlacement(new StaticShipPlacement(new Dimension(7, 3)));
		/* Ausgangspunkt
		 *   0 1 2 3 4 5 6
		 * 0       
		 * 1 * *       * *
		 * 2   
		 */
		Fleet fleet = new Fleet();
		fleet.setSubmarine1(new Submarine(Orientation.OST, new Coord("10")));
		fleet.setSubmarine2(new Submarine(Orientation.OST, new Coord("15")));
		
		Set<Coord> positions = new HashSet<Coord>(Arrays.asList(new Coord("10"), new Coord("11"), new Coord("12"), new Coord("13"), new Coord("14"), new Coord("15")));
		for(int i=0; i < 30; i++){
			toTest.move(fleet, fleet.getSubmarine1(), fleet.getSubmarine2(), 1);
			positions.remove(fleet.getSubmarine1().getPosition());
			positions.remove(fleet.getSubmarine2().getPosition());
		}
		assertTrue("Zufall nicht ausreichend verteilt! " + positions.size(), positions.isEmpty());
		
		/* Ausgangspunkt
		 *   0 1 2 3 4 5 6
		 * 0       *
		 * 1 * *   *   * *
		 * 2       *
		 */
		fleet = new Fleet();
		fleet.setSubmarine1(new Submarine(Orientation.OST, new Coord("10")));
		fleet.setSubmarine2(new Submarine(Orientation.OST, new Coord("15")));
		fleet.setDestroyer1(new Destroyer(Orientation.SUED, new Coord("03")));
		
		toTest.move(fleet, fleet.getSubmarine1(), fleet.getSubmarine2(), 1);
		assertEquals(new Coord("10"), fleet.getSubmarine1().getPosition());
		assertEquals(new Coord("15"), fleet.getSubmarine2().getPosition());
	}
	
	@Test
	public void move(){
		toTest = new MovingShipPlacement(new StaticShipPlacement(new Dimension(4, 3)));
		/* Ausgangspunkt
		 *   0 1 2 3
		 * 0       *
		 * 1 * *   * 
		 * 2   
		 */
		Fleet fleet = new Fleet();
		fleet.setSubmarine1(new Submarine(Orientation.OST, new Coord("10")));
		fleet.setSubmarine2(new Submarine(Orientation.SUED, new Coord("03")));
		
		Set<Coord> positions = new HashSet<Coord>(Arrays.asList(new Coord("10"), new Coord("03"), new Coord("13"), new Coord("00"), new Coord("20")));
		for(int i=0; i < 30; i++){
			toTest.move(fleet, fleet.getSubmarine1(), fleet.getSubmarine2(), 1);
			positions.remove(fleet.getSubmarine1().getPosition());
			positions.remove(fleet.getSubmarine2().getPosition());
		}
		assertTrue("Zufall nicht ausreichend verteilt! " + positions.size(), positions.isEmpty());
	}
	
	@Test
	public void move_all(){
		toTest = new MovingShipPlacement(new StaticShipPlacement(new Dimension(10, 10)));
		
		Set<String> fleetConfigs = new HashSet<String>();
		Fleet f = toTest.getFleet();
		
		for(int i=0; i < 1312; i++){
			toTest.move(f);
			fleetConfigs.add(f.toString());
		}
		
		assertTrue("Zu wenig Kombinationen generiert! " + fleetConfigs.size(),
				fleetConfigs.size() >= 500);
	}
	
	@Test
	public void getFleet(){
		toTest = new MovingShipPlacement(new StaticShipPlacement(new Dimension(10, 10)));
		
		Fleet original = new StaticShipPlacement(new Dimension(10, 10)).getFleet();
		
		assertFalse(original.equals(toTest.getFleet()));
	}
}
