package de.tarent.mica.bot.strategy.shipplacement;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import de.tarent.mica.bot.strategy.StrategyStats;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.Fleet;
import de.tarent.mica.model.element.Ship;
import de.tarent.mica.model.element.UnknownShip;
import de.tarent.mica.util.Logger;
import de.tarent.mica.util.Random;

@StrategyStats(description = "Diese Schiffstrategie verwendet eine beliebige andere Schiffstrategie um eine Flotte zu generieren." +
		 "Anschlie\u00dfend rotiert diese Schiffstrategy zuf\u00e4llig die gelieferten Schiffpositionen!")
public class RotateShipPlacement extends ShipPlacementStrategy {
	public static enum RotateDirection {
		RIGHT, LEFT
	}
	
	protected ShipPlacementStrategy delegate;

	public RotateShipPlacement(ShipPlacementStrategy delegate) {
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
				rotate(fleet, rnd.nextBoolean() ? RotateDirection.RIGHT : RotateDirection.LEFT);				
			}
		});
				
		return fleet;
	}

	/**
	 * Dreht die gegebene Flottenposition in die gegebene Richtung.
	 * 
	 * @param fleet
	 * @param direction
	 */
	public void rotate(Fleet fleet, RotateDirection direction) {
		Logger.debug("Rotate fleet to " + direction.name() + " ...");
		
		if(fleet.getCarrier1() != null) fleet.setCarrier1(rotate(fleet.getCarrier1(), direction));
		if(fleet.getCarrier2() != null) fleet.setCarrier2(rotate(fleet.getCarrier2(), direction));
		if(fleet.getCruiser1() != null) fleet.setCruiser1(rotate(fleet.getCruiser1(), direction));
		if(fleet.getCruiser2() != null) fleet.setCruiser2(rotate(fleet.getCruiser2(), direction));
		if(fleet.getDestroyer1() != null) fleet.setDestroyer1(rotate(fleet.getDestroyer1(), direction));
		if(fleet.getDestroyer2() != null) fleet.setDestroyer2(rotate(fleet.getDestroyer2(), direction));
		if(fleet.getSubmarine1() != null) fleet.setSubmarine1(rotate(fleet.getSubmarine1(), direction));
		if(fleet.getSubmarine2() != null) fleet.setSubmarine2(rotate(fleet.getSubmarine2(), direction));
	}

	@SuppressWarnings("unchecked")
	private <T extends Ship> T rotate(T ship, RotateDirection direction){
		List<Coord> reflectedCoords = new ArrayList<Coord>(ship.getSpace().size());
		for(Coord oCoord : ship.getSpace()){
			Coord nCoord = null;
			
			if(RotateDirection.LEFT == direction){
				nCoord = rotateLeft(oCoord);
			}else if(RotateDirection.RIGHT == direction){
				nCoord = rotateRight(oCoord);
			}else{
				nCoord = new Coord(oCoord.getX(), oCoord.getY());
			}
			
			reflectedCoords.add(nCoord);
		}

		UnknownShip newShip = new UnknownShip(reflectedCoords);
		return (T)UnknownShip.transformShip(newShip, false);
	}
	
	private Coord rotateRight(Coord c){
		/* rechts:
		 *   0 1 2 |  0 1 2   A0 => A2; B0 => A1
		 * A *     |A   * *
		 * B *	   |B     	
		 * C       |C      
		 *       
		 */
		
		double[] pt = {c.getX(), c.getY()};
		AffineTransform.getRotateInstance(Math.toRadians(90), ((double)worldDimension.width) / 2, ((double)worldDimension.height) / 2)
			.transform(pt, 0, pt, 0, 1); // specifying to use this double[] to hold coords
		
		double newX = pt[0];
		double newY = pt[1];
		
		return new Coord((int)newX - 1, (int)newY); //-1 da wir bei 0 beginnen!
	}
	
	private Coord rotateLeft(Coord c){
		/* links:
		 *   0 1 2 |  0 1 2   A0 => C0; B0 => C1
		 * A *     |A  
		 * B *	   |B     	
		 * C       |C * * 
		 *       
		 */
		
		double[] pt = {c.getX(), c.getY()};
		AffineTransform.getRotateInstance(Math.toRadians(-90), ((double)worldDimension.width) / 2, ((double)worldDimension.height) / 2)
			.transform(pt, 0, pt, 0, 1); // specifying to use this double[] to hold coords
		
		double newX = pt[0];
		double newY = pt[1];
		
		return new Coord((int)newX, (int)newY - 1);	//-1 da wir bei 0 beginnen!
	}
}
