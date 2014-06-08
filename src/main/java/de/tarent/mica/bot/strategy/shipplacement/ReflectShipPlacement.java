package de.tarent.mica.bot.strategy.shipplacement;

import java.util.ArrayList;
import java.util.List;

import de.tarent.mica.bot.strategy.StrategyStats;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.Fleet;
import de.tarent.mica.model.element.Ship;
import de.tarent.mica.model.element.UnknownShip;
import de.tarent.mica.util.Random;

@StrategyStats(description = "Diese Schiffstrategie verwendet eine beliebige andere Schiffstrategie um eine Flotte zu generieren." +
		 					 "Anschlie\u00dfend spiegelt diese Schiffstrategy zuf\u00e4llig die gelieferten Schiffpositionen!")
public class ReflectShipPlacement extends ShipPlacementStrategy {
	public static enum MirrorAxis {
		HORIZONTAL, VERTICAL
	}
	
	protected ShipPlacementStrategy delegate;

	public ReflectShipPlacement(ShipPlacementStrategy delegate) {
		super(delegate.worldDimension);
		
		this.delegate = delegate;
	}
	
	@Override
	public Fleet getFleet() {
		//wir generieren keine Flotte selber, sondern LASSEN generieren... wir sind der perfekte Chef :D
		final Fleet fleet = delegate.getFleet();
		
		final Random rnd = new Random();
		rnd.runXTimes(new Runnable() {
			@Override
			public void run() {
				reflect(fleet, rnd.nextBoolean() ? MirrorAxis.HORIZONTAL : MirrorAxis.VERTICAL);				
			}
		});
				
		return fleet;
	}

	/**
	 * Spiegelt die Flottenposition an der angegebenen Spiegelachse.
	 * 
	 * @param fleet
	 * @param axis
	 */
	public void reflect(Fleet fleet, MirrorAxis axis) {
		if(fleet.getCarrier1() != null) fleet.setCarrier1(reflect(fleet.getCarrier1(), axis));
		if(fleet.getCarrier2() != null) fleet.setCarrier2(reflect(fleet.getCarrier2(), axis));
		if(fleet.getCruiser1() != null) fleet.setCruiser1(reflect(fleet.getCruiser1(), axis));
		if(fleet.getCruiser2() != null) fleet.setCruiser2(reflect(fleet.getCruiser2(), axis));
		if(fleet.getDestroyer1() != null) fleet.setDestroyer1(reflect(fleet.getDestroyer1(), axis));
		if(fleet.getDestroyer2() != null) fleet.setDestroyer2(reflect(fleet.getDestroyer2(), axis));
		if(fleet.getSubmarine1() != null) fleet.setSubmarine1(reflect(fleet.getSubmarine1(), axis));
		if(fleet.getSubmarine2() != null) fleet.setSubmarine2(reflect(fleet.getSubmarine2(), axis));
	}

	@SuppressWarnings("unchecked")
	private <T extends Ship> T reflect(T ship, MirrorAxis axis){
		List<Coord> reflectedCoords = new ArrayList<Coord>(ship.getSpace().size());
		for(Coord oCoord : ship.getSpace()){
			Coord nCoord = null;
			
			if(MirrorAxis.VERTICAL == axis){
				nCoord = reflectVertical(oCoord);
			}else if(MirrorAxis.HORIZONTAL == axis){
				nCoord = reflectHorizontal(oCoord);
			}else{
				nCoord = new Coord(oCoord.getX(), oCoord.getY());
			}
			
			reflectedCoords.add(nCoord);
		}

		UnknownShip newShip = new UnknownShip(reflectedCoords);
		return (T)UnknownShip.transformShip(newShip);
	}
	
	private Coord reflectVertical(Coord c){
		/* Vertikal:
		 *   0 1 2 |  0 1 2   A0 => A2; B0 => B2
		 * A *     |A     *
		 * B *	   |B     *	
		 * C       |C      
		 *       
		 */
		
		//linker abstand zum linken rand
		int dX = c.getX();
		
		return new Coord(worldDimension.width - 1 - dX, c.getY());
	}
	
	private Coord reflectHorizontal(Coord c){
		/* Horizontal:
		 *   0 1 2 
		 * A *     
		 * B *		
		 * C      
		 * ---------      A0 => C0; B0 => B0
		 *   0 1 2
		 * A          
		 * B *		     
		 * C *          
		 *      
		 */	
		
		//oberen abstand zum oberen rand
		int dY = c.getY();
		
		return new Coord(c.getX(), worldDimension.height - 1 - dY);
	}
}
