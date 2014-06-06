package de.tarent.mica.bot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.tarent.mica.Action;
import de.tarent.mica.Action.Type;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.Field.Element;
import de.tarent.mica.model.World;
import de.tarent.mica.model.element.Ship;

/**
 * Diese Strategy versucht getroffene aber noch nicht versunkene
 * Schiffe weiter zu treffen.
 * 
 * @author rainu
 *
 */
public class HitTraceActionStrategy implements ActionStrategy {
	private boolean ignoreBurningShips = true;
		
	public HitTraceActionStrategy(){
		
	}
	
	public HitTraceActionStrategy(boolean ignoreBurningShips){
		this.ignoreBurningShips = ignoreBurningShips;
	}
	
	@Override
	public Action getActionDecision(World world) {
		Set<Ship> ships = world.getEnemyShips();
		if(ships.isEmpty()) return null;
		
		for(Ship ship : ships){
			if(shouldAttack(ship)){
				return getAction(world, ship);
			}
		}
		
		return null;
	}

	private Action getAction(World world, Ship ship) {
		List<Coord> coords = ship.getSpace();
		if(coords.isEmpty()) return null;	//keine entscheidungsgrundlage vorhanden!
		
		if(coords.size() >= 2){
			return getActionForMultipleCoords(world, coords);
		}else{
			return getActionForSingleCoord(world, coords.get(0));
		}
	}
	
	Action getActionForMultipleCoords(World world, List<Coord> coords) {
		List<Coord> clone = new ArrayList<Coord>(coords);
		Collections.sort(clone, Coord.COMPARATOR);
		
		Coord first = clone.get(0);
		Coord last = clone.get(clone.size() - 1);
		
		boolean isHorizontal = first.getY() == last.getY();
		
		Coord gab = getGap(isHorizontal, clone);
		if(gab != null) return buildAction(gab);
		
		Coord edge = getEdge(isHorizontal, world, clone);
		if(edge != null) return buildAction(edge);
		
		return null;
	}
	
	private Coord getGap(boolean isHorizontal, List<Coord> coords) {
		Coord first = coords.get(0);
		Coord last = coords.get(coords.size() - 1);
		
		if(isHorizontal){
			Coord coord = first;
			
			//solange nach osten gehen, bis ich eine lücke gefunden habe
			while(!coord.equals(last) && coords.contains(coord)){
				coord = coord.getEastNeighbor();
			}
			
			if(!coord.equals(last)){
				return coord;
			}
		}else{
			Coord coord = first;
			
			//solange nach süden gehen, bis ich eine lücke gefunden habe
			while(!coord.equals(last) && coords.contains(coord)){
				coord = coord.getSouthNeighbor();
			}
			
			if(!coord.equals(last)){
				return coord;
			}
		}
		
		//keine lücke gefunden
		return null;
	}
	
	private Coord getEdge(boolean isHorizontal, World world, List<Coord> coords) {
		Coord first = coords.get(0);
		Coord last = coords.get(coords.size() - 1);
		
		if(isHorizontal){
			Coord west = first.getWestNeighbor();
			Coord east = last.getEastNeighbor();
			
			if(world.isInWorld(west) && world.getEnemyField().get(west) == Element.UNBEKANNT){
				return west;
			}else if(world.isInWorld(east) && world.getEnemyField().get(east) == Element.UNBEKANNT){
				return east;
			}
		}else{
			Coord north = first.getNorthNeighbor();
			Coord south = last.getSouthNeighbor();
			
			if(world.isInWorld(north) && world.getEnemyField().get(north) == Element.UNBEKANNT){
				return north;
			}else if(world.isInWorld(south) && world.getEnemyField().get(south) == Element.UNBEKANNT){
				return south;
			}
		}
		
		return null;
	}

	Action getActionForSingleCoord(World world, Coord coord) {
		List<Coord> possipleCoords = new ArrayList<Coord>(4);
		possipleCoords.add(coord.getNorthNeighbor());
		possipleCoords.add(coord.getEastNeighbor());
		possipleCoords.add(coord.getSouthNeighbor());
		possipleCoords.add(coord.getWestNeighbor());
		
		Iterator<Coord> iter = possipleCoords.iterator();
		while(iter.hasNext()){
			Coord next = iter.next();
			if(!world.isInWorld(next)){
				iter.remove();
			}else if(world.getEnemyField().get(next) != Element.UNBEKANNT){
				iter.remove();
			}
		}
		if(possipleCoords.isEmpty()) return null;	//keine möglichkeiten mehr...
		Collections.shuffle(possipleCoords);
		
		return buildAction(possipleCoords.get(0));
	}
	
	private Action buildAction(Coord coord) {
		return new Action(Type.ATTACK, coord);
	}
	
	protected boolean shouldAttack(Ship ship){
		if(ignoreBurningShips && ship.isBurning()) return false;
		
		return !ship.isSunken();
	}
	
	@Override
	public String getShortDescription() {
		return "Diese Strategie versucht getroffene - aber noch nicht versunkene - Schiffe weiter zu treffen.";
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
