package de.tarent.mica.bot.strategy.shipplacement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.tarent.mica.bot.strategy.StrategyStats;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.Fleet;
import de.tarent.mica.model.element.Ship;
import de.tarent.mica.model.element.Ship.Orientation;
import de.tarent.mica.util.Random;

@StrategyStats(description = "Diese Schiffstrategie verwendet eine beliebige andere Schiffstrategie um eine Flotte zu generieren." +
							 "Anschlie\u00dfend werden die Schiffe verschoben. Dabei wird darauf geachtet, dass die Anzahl an Spezialattacken unver\u00e4ndert bleibt!")
public class MovingShipPlacement extends ShipPlacementStrategy {
	protected ShipPlacementStrategy delegate;

	public MovingShipPlacement(ShipPlacementStrategy delegate) {
		super(delegate.worldDimension);
		
		this.delegate = delegate;
	}
	
	@Override
	public Fleet getFleet() {
		//wir generieren keine Flotte selber, sondern LASSEN generieren... wir sind der perfekte Chef :D
		final Fleet fleet = delegate.getFleet();

		Random rnd = new Random();
		rnd.runXTimes(new Runnable() {
			@Override
			public void run() {
				move(fleet);
			}
		});
		
		return fleet;
	}
	
	public void move(Fleet fleet){
		Random rnd = new Random();
		
		int count = Ship.getTheoreticallySpecialAttacks(fleet.getCarrier1(), fleet.getCarrier2());
		if(count == 0){
			moveFreely(fleet, rnd.nextBoolean() ? fleet.getCarrier1() : fleet.getCarrier2());
		}else{
			move(fleet, fleet.getCarrier1(), fleet.getCarrier2(), count);
		}
		
		count = Ship.getTheoreticallySpecialAttacks(fleet.getCruiser1(), fleet.getCruiser2());
		if(count == 0){
			moveFreely(fleet, rnd.nextBoolean() ? fleet.getCruiser1() : fleet.getCruiser2());
		}else{
			move(fleet, fleet.getCruiser1(), fleet.getCruiser2(), count);
		}
		
		count = Ship.getTheoreticallySpecialAttacks(fleet.getDestroyer1(), fleet.getDestroyer2());
		if(count == 0){
			moveFreely(fleet, rnd.nextBoolean() ? fleet.getDestroyer1() : fleet.getDestroyer2());
		}else{
			move(fleet, fleet.getDestroyer1(), fleet.getDestroyer2(), count);
		}
		
		count = Ship.getTheoreticallySpecialAttacks(fleet.getSubmarine1(), fleet.getSubmarine2());
		if(count == 0){
			moveFreely(fleet, rnd.nextBoolean() ? fleet.getSubmarine1() : fleet.getSubmarine2());
		}else{
			move(fleet, fleet.getSubmarine1(), fleet.getSubmarine2(), count);
		}
	}
	
	protected <T extends Ship> void move(Fleet fleet, final T ship1, final T ship2, final int specialAttacks){
		if(specialAttacks == 1){
			boolean sameLine = false;
			boolean vertical = false;
			
			if(	(ship1.getOrientation() == Orientation.WEST || ship1.getOrientation() == Orientation.OST) &&
				(ship2.getOrientation() == Orientation.WEST || ship2.getOrientation() == Orientation.OST)){
				//horizontale ausrichtung
				sameLine = ship1.getPosition().getY() == ship2.getPosition().getY();
				vertical = false;
			}else 
			if( (ship1.getOrientation() == Orientation.NORD || ship1.getOrientation() == Orientation.SUED) &&
				(ship2.getOrientation() == Orientation.NORD || ship2.getOrientation() == Orientation.SUED)){
				//vertikale ausrichtung
				sameLine = ship1.getPosition().getX() == ship2.getPosition().getX();
				vertical = true;
			}
			
			if(sameLine){
				moveLine(fleet, ship1, ship2, vertical);
				return;
			}
		}
		
		final Set<Ship> others = fleet.getAll(); others.remove(ship1);
		final Random rnd = new Random();
		
		rnd.runXTimes(new Runnable() {
			@Override
			public void run() {
				Orientation o = rnd.choose(Orientation.values());
				moveTo(ship1, others, o, specialAttacks);
			}
		});
		
		others.add(ship1); others.remove(ship2);
		rnd.runXTimes(new Runnable() {
			@Override
			public void run() {
				Orientation o = rnd.choose(Orientation.values());
				moveTo(ship2, others, o, specialAttacks);
			}
		});
	}
	
	private <T extends Ship> void moveLine(final Fleet fleet, final T ship1, final T ship2, final boolean vertical){
		final Random rnd = new Random();
		
		rnd.runXTimes(new Runnable() {
			@Override
			public void run() {
				@SuppressWarnings("unchecked")
				T toMove = rnd.choose(ship1, ship2);
				Set<Ship> others = fleet.getAll(); others.remove(toMove);
				Orientation o = vertical ? rnd.choose(Orientation.NORD, Orientation.SUED) : rnd.choose(Orientation.WEST, Orientation.OST);
				
				moveTo(toMove, others, o, 1);
			}
		});
	}
	
	protected void moveFreely(Fleet fleet, final Ship ship){
		final Set<Ship> others = fleet.getAll(); others.remove(ship);
		final Random rnd = new Random();
		
		rnd.runXTimes(new Runnable() {
			@Override
			public void run() {
				Orientation o = rnd.choose(Orientation.values());
				moveTo(ship, others, o, 0);
			}
		});
	}
	
	private <T extends Ship> void moveTo(T toMove, Set<Ship> others, Orientation o, final int specialAttackCount) {
		do{
			switch (o) {
			case NORD:
				toMove.replace(toMove.getPosition().getNorthNeighbor());
				if(isOutOfBounce(toMove.getPosition())) toMove.replace(new Coord(toMove.getPosition().getX(), worldDimension.height - 1));
				break;
			case OST:
				toMove.replace(toMove.getPosition().getEastNeighbor());
				if(isOutOfBounce(toMove.getPosition())) toMove.replace(new Coord(0, toMove.getPosition().getY()));
				break;
			case SUED:
				toMove.replace(toMove.getPosition().getSouthNeighbor());
				if(isOutOfBounce(toMove.getPosition())) toMove.replace(new Coord(toMove.getPosition().getX(), 0));
				break;
			case WEST:
				toMove.replace(toMove.getPosition().getWestNeighbor());
				if(isOutOfBounce(toMove.getPosition())) toMove.replace(new Coord(worldDimension.width - 1, toMove.getPosition().getY()));
				break;
			default:	//skip
			}
		}while(	collides(toMove, others) || 
				isOutOfBounce(toMove) || 
				(specialAttackCount > 0 && specialAttackChanged(toMove, others, specialAttackCount)));
	}
	
	private boolean specialAttackChanged(Ship ship, Set<Ship> others, int previousCount){
		Ship sameKind = null;
		for(Ship s : others){
			if(ship.getClass() == s.getClass()){
				sameKind = s;
				break;
			}
		}
		
		if(sameKind == null) return false;
		
		return Ship.getTheoreticallySpecialAttacks(ship, sameKind) != previousCount;
	}
	
	private boolean isOutOfBounce(Ship ship){
		for(Coord c : ship.getSpace()){
			if(isOutOfBounce(c)) return true;
		}
		return false;
	}

	private boolean isOutOfBounce(Coord c) {
		return c.getX() < 0 || c.getX() >= worldDimension.width || c.getY() < 0 || c.getY() >= worldDimension.height;
	}

	protected boolean collides(Ship ship, Set<Ship> others) {
		Set<Coord> coords = ship.getFrameCoordinates();
		coords.addAll(ship.getSpace());
		
		for(Ship otherShip : others){
			for(Coord otherCoord : otherShip.getSpace()){
				if(coords.contains(otherCoord)){
					return true;
				}
			}
		}
		
		return false;
	}

	protected List<Coord> getFreePositions(Set<Ship> placedShips){
		List<Coord> free = new ArrayList<Coord>(worldDimension.height * worldDimension.width);
		
		for(int x=0; x < worldDimension.width; x++){
			for(int y=0; y < worldDimension.height; y++){
				free.add(new Coord(x, y));
			}
		}
		for(Ship s : placedShips){
			free.removeAll(s.getSpace());
			free.removeAll(s.getFrameCoordinates());
		}
		
		return free;
	}
	
}
