package de.tarent.mica.bot.strategy.action;

import java.util.ArrayList;
import java.util.List;

import de.tarent.mica.Action;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.World;
import de.tarent.mica.model.element.Ship;
import de.tarent.mica.model.element.Ship.Orientation;

/**
 * Diese Klasse bietet nur Hilfsfunktionen für Spezialattacken an.
 * 
 * @author rainu
 *
 */
public abstract class SpecialAttackStrategy extends ActionStrategy {

	public abstract Action getActionDecision(World world);

	protected int getPossibleSpecialAttackCount(Class<? extends Ship> attackClass, World world){
		List<? extends Ship> ship = getShips(attackClass, world);
		
		if(ship.size() < 2) return 0;
		if(ship.get(0).isSunken() || ship.get(1).isSunken()) return 0;
		
		int theoreticallyPossible = 0;
		
		for(Coord s1Coord : ship.get(0).getSpace()){
			for(Coord s2Coord : ship.get(1).getSpace()){
				if(isCoordSameLevel(s1Coord, s2Coord)) theoreticallyPossible++;
			}
		}
		
		
		if(	((ship.get(0).getOrientation() == Orientation.SUED || ship.get(0).getOrientation() == Orientation.NORD) &&
			(ship.get(1).getOrientation() == Orientation.SUED || ship.get(1).getOrientation() == Orientation.NORD)) 
			
			||
			
			((ship.get(0).getOrientation() == Orientation.OST || ship.get(0).getOrientation() == Orientation.WEST) &&
			(ship.get(1).getOrientation() == Orientation.OST || ship.get(1).getOrientation() == Orientation.WEST))	){
			
			//beide sind horizontal/vertikal ausgerichtet
			//NOOP
		}

		//einer ist vertikal und der andere horiozontal ausgerichtet
		//damit können (theoretisch) nur höchstens ein Punkt infrage kommen ;)
		else if(theoreticallyPossible > 0) theoreticallyPossible = 1;

		int usedSpy = world.getSpecialAttackCount(attackClass);
		int possible = theoreticallyPossible - usedSpy;
		
		return possible;
	}
	
	protected final boolean isCoordSameLevel(Coord c1, Coord c2) {
		//horizontal gleich
		if(c1.getY() == c2.getY()) return true;
		
		//vertikal gleich
		if(c1.getX() == c2.getX()) return true;
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	protected final <T extends Ship> List<T> getShips(Class<T> shipType, World world){
		List<T> ship = new ArrayList<T>(2);
		
		for(Ship s : world.getOwnShips()){
			if(shipType.isInstance(s)) ship.add((T)s);
		}
		
		return ship;
	}
}
