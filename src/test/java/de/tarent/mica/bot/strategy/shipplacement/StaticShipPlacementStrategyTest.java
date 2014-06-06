package de.tarent.mica.bot.strategy.shipplacement;

import static org.junit.Assert.*;

import org.junit.Test;

import de.tarent.mica.bot.strategy.shipplacement.StaticShipPlacementStrategy;
import de.tarent.mica.model.Fleet;

public class StaticShipPlacementStrategyTest {

	@Test
	public void getFleet(){
		Fleet fleet = new StaticShipPlacementStrategy().getFleet();
		
		assertNotNull(fleet.getCarrier1());
		assertNotNull(fleet.getCarrier2());
		assertNotNull(fleet.getCruiser1());
		assertNotNull(fleet.getCruiser2());
		assertNotNull(fleet.getDestroyer1());
		assertNotNull(fleet.getDestroyer2());
		assertNotNull(fleet.getSubmarine1());
		assertNotNull(fleet.getSubmarine2());
	}
}
