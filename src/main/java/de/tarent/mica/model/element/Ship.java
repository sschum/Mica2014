package de.tarent.mica.model.element;

import java.io.Serializable;
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
public abstract class Ship implements Serializable {
	private static final long serialVersionUID = 1444748025030628429L;

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
	
	public Set<Coord> getFrameCoordinates(){
		Set<Coord> result = new HashSet<Coord>(16); //(größte Größe) * 2 + 2*3
		
		List<Coord> space = getSpace();
		
		for(Coord c : space){
			result.add(c.getNorthNeighbor());
			result.add(c.getEastNeighbor());
			result.add(c.getSouthNeighbor());
			result.add(c.getWestNeighbor());
		}
		
		//Auch noch die ecken, die nicht direkt am Schiff liegen
		Coord first = space.get(0);
		Coord last = space.get(space.size() - 1);
		
		Coord northWest;
		Coord northEast;
		Coord southWest;
		Coord southEast;
		
		switch(getOrientation()){
		case NORD:
		case SUED:
			northWest = first.getNorthNeighbor().getWestNeighbor();
			northEast = first.getNorthNeighbor().getEastNeighbor();
			southWest = last.getSouthNeighbor().getWestNeighbor();
			southEast = last.getSouthNeighbor().getEastNeighbor();
			break;
		case OST:
		case WEST:
			northWest = first.getNorthNeighbor().getWestNeighbor();
			northEast = last.getNorthNeighbor().getEastNeighbor();
			southWest = first.getSouthNeighbor().getWestNeighbor();
			southEast = last.getSouthNeighbor().getEastNeighbor();
			break;
		default:
			throw new IllegalStateException("Each sunken ship should have a orientation!");
		}
		
		result.add(northWest);
		result.add(northEast);
		result.add(southWest);
		result.add(southEast);
		
		//eigene Koordinaten entfernen
		for(Coord c : space){
			result.remove(c);
		}
		
		return result;
	}
	
	public void replace(Coord newPosition){
		this.position = newPosition;
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
	
	public int getSize(){
		return size;
	}
	
	public static <T extends Ship> int getTheoreticallySpecialAttacks(T ship1, T ship2){
		int theoreticallyPossible = 0;
		
		for(Coord s1Coord : ship1.getSpace()){
			for(Coord s2Coord : ship2.getSpace()){
				if(isCoordSameLevel(s1Coord, s2Coord)) theoreticallyPossible++;
			}
		}
		
		
		if(	((ship1.getOrientation() == Orientation.SUED || ship1.getOrientation() == Orientation.NORD) &&
			(ship2.getOrientation() == Orientation.SUED || ship2.getOrientation() == Orientation.NORD)) 
			
			||
			
			((ship1.getOrientation() == Orientation.OST || ship1.getOrientation() == Orientation.WEST) &&
			(ship2.getOrientation() == Orientation.OST || ship2.getOrientation() == Orientation.WEST))	){
			
			//beide sind horizontal/vertikal ausgerichtet
			//NOOP
		}

		//einer ist vertikal und der andere horiozontal ausgerichtet
		//damit können (theoretisch) nur höchstens ein Punkt infrage kommen ;)
		else if(theoreticallyPossible > 0) theoreticallyPossible = 1;
		
		if(theoreticallyPossible >= (ship1.getSize() * 2)) {
			//sind in einer linie ausgerichtet -> das ergibt nur eine Spzialattake!
			theoreticallyPossible = 1;
		}
		
		return theoreticallyPossible;
	}

	private static boolean isCoordSameLevel(Coord c1, Coord c2) {
		//horizontal gleich
		if(c1.getY() == c2.getY()) return true;
		
		//vertikal gleich
		if(c1.getX() == c2.getX()) return true;
		
		return false;
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
