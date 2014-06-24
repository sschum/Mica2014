package de.tarent.mica.bot.strategy.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.tarent.mica.Action;
import de.tarent.mica.Action.Type;
import de.tarent.mica.bot.strategy.StrategyStats;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.Field.Element;
import de.tarent.mica.model.World;
import de.tarent.mica.model.element.Ship;
import de.tarent.mica.util.Random;

/**
 * Diese Strategy versucht getroffene aber noch nicht versunkene
 * Schiffe weiter zu treffen.
 * 
 * @author rainu
 *
 */
@StrategyStats(description = "Diese Strategie versucht getroffene - aber noch nicht versunkene - Schiffe weiter zu treffen.")
public class HitTraceStrategy extends ActionStrategy {
	private static final long serialVersionUID = -3473563700285439959L;

	private final boolean ignoreBurningShips;
		
	public HitTraceStrategy(){
		this.ignoreBurningShips = true;
	}
	
	public HitTraceStrategy(boolean ignoreBurningShips){
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
		
		//gibt es schiffe in unmittelbarer nähe?
		Ship nearShip = getNearShip(world, ship);
		if(nearShip != null){
			List<Coord> allCoords = new ArrayList<Coord>(ship.getSpace());
			allCoords.addAll(nearShip.getSpace());
			boolean isHorizontal = allCoords.get(0).getY() == allCoords.get(1).getY();
			
			Coord gab = getGap(isHorizontal, allCoords);
			if(gab != null) return buildAction(gab);
		}
		
		if(coords.size() >= 2){
			return getActionForMultipleCoords(world, coords);
		}else{
			return getActionForSingleCoord(world, coords.get(0));
		}
	}
	
	private Ship getNearShip(World world, Ship ship) {
		for(Ship other : world.getEnemyShips()){
			if(other.equals(ship)) continue;
			
			for(Coord c : ship.getSpace()){
				if(	other.getSpace().contains(c.getNorthNeighbor().getNorthNeighbor()) ||
					other.getSpace().contains(c.getEastNeighbor().getEastNeighbor()) ||
					other.getSpace().contains(c.getSouthNeighbor().getSouthNeighbor()) ||
					other.getSpace().contains(c.getWestNeighbor().getWestNeighbor())){
					
					return other;
				}
			}
		}
		
		return null;
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
			
			if(world.isInWorld(west) && isUnknown(world.getEnemyField().get(west))){
				return west;
			}else if(world.isInWorld(east) && isUnknown(world.getEnemyField().get(east))){
				return east;
			}
		}else{
			Coord north = first.getNorthNeighbor();
			Coord south = last.getSouthNeighbor();
			
			if(world.isInWorld(north) && isUnknown(world.getEnemyField().get(north))){
				return north;
			}else if(world.isInWorld(south) && isUnknown(world.getEnemyField().get(south))){
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
			}else if(isHitOrMiss(world.getEnemyField().get(next))){
				iter.remove();
			}
		}
		if(possipleCoords.isEmpty()) return null;	//keine möglichkeiten mehr...
		new Random().shuffle(possipleCoords);
		
		return buildAction(possipleCoords.get(0));
	}
	
	private Action buildAction(Coord coord) {
		return new Action(Type.ATTACK, coord);
	}
	
	protected boolean shouldAttack(Ship ship){
		if(ignoreBurningShips && ship.isBurning()) return false;
		
		return !ship.isSunken();
	}
	
	private boolean isUnknown(Element element){
		return element == Element.UNBEKANNT || element == Element.SPIONAGE;
	}
	
	private boolean isHitOrMiss(Element element){
		return element == Element.SCHIFF || element == Element.TREFFER || element == Element.WASSER;
	}
	
	@Override
	public String toString() {
		return super.toString() + "\n\tignore burning ships: " + ignoreBurningShips;
	}
}
