package de.tarent.mica.model.element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.tarent.mica.model.Coord;

/**
 * Diese Klasse representiert ein Schiff.
 * 
 * @author rainu
 *
 */
public abstract class Ship {
	public static enum Orientation{
		NORD, OST, SUED, WEST, UNBEKANNT
	}
	
	private static Set<Class<? extends Ship>> knownShips = new HashSet<Class<? extends Ship>>();
	
	public static Set<Class<? extends Ship>> getKnownShipTypes(){
		return Collections.unmodifiableSet(knownShips);
	}
	
	public static void registerShipClass(Class<? extends Ship> clazz){
		knownShips.add(clazz);
	}
	
	//registriere bekannte Unterklassen...
	static {
		registerShipClass(Carrier.class);
		registerShipClass(Cruiser.class);
		registerShipClass(Destroyer.class);
		registerShipClass(Submarine.class);
	}
	
	public static int getSizeOf(Class<? extends Ship> shipType){
		return shipType.getAnnotation(ShipStats.class).size();
	}
	
	protected final int size;
	protected Orientation orientation;
	protected Coord position;
	protected boolean isBurning;
	protected Set<Coord> attackCoords = new HashSet<Coord>();
	
	protected Ship(int size, Orientation orientation, Coord position){
		this.size = size;
		this.orientation = orientation;
		this.position = position;
		
		if(!getClass().isAnnotationPresent(ShipStats.class)){
			throw new IllegalStateException("The class " + getClass().getName() + " has no " + ShipStats.class + " annotation!");
		}
		
		if(!knownShips.contains(getClass())){
			registerShipClass(getClass());
		}
	}
	
	public List<Coord> getSpace(){
		List<Coord> result = new ArrayList<Coord>();
		
		//of curse the position itself ;)
		result.add(position);
		
		for(int i=0; i < size - 1; i++){
			Coord lastCoord = result.get(result.size() - 1);
			Coord c = null;
			switch (orientation) {
			case NORD:
				c = lastCoord.getNorthNeighbor();
				break;
			case OST:
				c = lastCoord.getEastNeighbor();
				break;
			case SUED:
				c = lastCoord.getSouthNeighbor();
				break;
			case WEST:
				c = lastCoord.getWestNeighbor();
				break;
			default:
				break;
			}
			
			result.add(c);
		}
		
		return result;
	}
	
	public boolean isSunken() {
		return attackCoords.size() == size;
	}

	public boolean isBurning() {
		return isBurning;
	}

	public void setBurning(boolean isBurning) {
		this.isBurning = isBurning;
	}

	public boolean isAttacked() {
		return !attackCoords.isEmpty();
	}

	public void addAttackCoord(Coord coord) {
		attackCoords.add(coord);
	}
	
	public Orientation getOrientation() {
		return orientation;
	}

	public Coord getPosition() {
		return position;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attackCoords == null) ? 0 : attackCoords.hashCode());
		result = prime * result + (isBurning ? 1231 : 1237);
		result = prime * result
				+ ((orientation == null) ? 0 : orientation.hashCode());
		result = prime * result
				+ ((position == null) ? 0 : position.hashCode());
		result = prime * result + size;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ship other = (Ship) obj;
		if (attackCoords == null) {
			if (other.attackCoords != null)
				return false;
		} else if (!attackCoords.equals(other.attackCoords))
			return false;
		if (isBurning != other.isBurning)
			return false;
		if (orientation != other.orientation)
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (size != other.size)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " @ " + position + "(" + orientation + ") " + (isSunken() ? "\u03EE" : "") + (isBurning() ? "\u03DF" : "") + " - [" + getSpace() + "]";
	}
}
