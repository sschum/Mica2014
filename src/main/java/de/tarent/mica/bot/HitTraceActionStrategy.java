package de.tarent.mica.bot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import de.tarent.mica.Action;
import de.tarent.mica.Action.Type;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.World;
import de.tarent.mica.model.Field.Element;
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
		
		if(first.getY() == last.getY()){
			//auf horizontaler Ebene gleich!
			if(	first.getWestNeighbor().equals(last) ||
				first.getEastNeighbor().equals(last)){
				
				//nachbarn
				Coord left = first.getWestNeighbor();
				Coord right = last.getEastNeighbor();
				
				if(!world.isInWorld(left)) return buildAction(right);
				if(!world.isInWorld(right)) return buildAction(left);
				
				//links wie auch rechts sind möglich...
				boolean rand = new Random().nextBoolean();
				
				return buildAction(rand ? left : right);
			}else{
				//zwischenraum nutzen!
				return buildAction(first.getEastNeighbor());
			}
		}else if(first.getX() == last.getX()){
			//auf vertikaler Ebene gleich
			if(	first.getNorthNeighbor().equals(last) ||
				first.getSouthNeighbor().equals(last)){
				
				//nachbarn
				Coord up = first.getNorthNeighbor();
				Coord down = last.getSouthNeighbor();
				
				if(!world.isInWorld(up)) return buildAction(down);
				if(!world.isInWorld(down)) return buildAction(up);
				
				//oben wie auch unten sind möglich...
				boolean rand = new Random().nextBoolean();
				
				return buildAction(rand ? up : down);
			}else{
				//zwischenraum nutzen!
				return buildAction(first.getSouthNeighbor());
			}
		}else{
			return null;
		}
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
}
