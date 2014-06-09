package de.tarent.mica.bot.strategy.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import de.tarent.mica.Action;
import de.tarent.mica.Action.Type;
import de.tarent.mica.bot.strategy.StrategyStats;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.World;
import de.tarent.mica.model.element.Destroyer;
import de.tarent.mica.model.element.Ship;
import de.tarent.mica.model.element.Ship.Orientation;

@StrategyStats(description = "Diese Strategie spioniert (wenn m\u00f6glich) den Gegner aus.")
public class SpyStrategy extends ActionStrategy {
	protected final List<Coord> spyPoints;
	
	public SpyStrategy(Coord...spyPoints) {
		this.spyPoints = new ArrayList<Coord>(Arrays.asList(spyPoints));
	}

	@Override
	public Action getActionDecision(World world) {
		if(canSpy(world)){
			return nextSpyAction(world);
		}
		
		return null;
	}

	protected boolean canSpy(World world) {
		//ich kann nur spionieren, wenn Destroyer 
		//auf einer ebene eine gemeinsame Position besitzen
		List<Destroyer> destroyers = new ArrayList<Destroyer>(2);
		
		for(Ship s : world.getOwnShips()){
			if(s instanceof Destroyer) destroyers.add((Destroyer)s);
		}
		
		int theoreticallyPossible = getPossibleSpyCount(destroyers);
		if(theoreticallyPossible > 0){
			int usedSpy = world.getSpecialAttackCount(Destroyer.class);
			int possible = theoreticallyPossible - usedSpy;

			return possible > 0;
		}
		
		return false;
	}

	private int getPossibleSpyCount(List<Destroyer> destroyers) {
		int count = 0;
		
		for(Coord d1Coord : destroyers.get(0).getSpace()){
			for(Coord d2Coord : destroyers.get(1).getSpace()){
				if(isCoordSameLevel(d1Coord, d2Coord)) count++;
			}
		}
		
		
		if(	((destroyers.get(0).getOrientation() == Orientation.SUED || destroyers.get(0).getOrientation() == Orientation.NORD) &&
			(destroyers.get(1).getOrientation() == Orientation.SUED || destroyers.get(1).getOrientation() == Orientation.NORD)) 
			
			||
			
			((destroyers.get(0).getOrientation() == Orientation.OST || destroyers.get(0).getOrientation() == Orientation.WEST) &&
			(destroyers.get(1).getOrientation() == Orientation.OST || destroyers.get(1).getOrientation() == Orientation.WEST))	){
			
			//beide sind horizontal/vertikal ausgerichtet
			return count;
		}

		//einer ist vertikal und der andere horiozontal ausgerichtet
		//damit können (theoretisch) nur höchstens ein Punkt infrage kommen ;)
		if(count > 0) return 1;
		return 0;
	}

	private boolean isCoordSameLevel(Coord c1, Coord c2) {
		//horizontal gleich
		if(c1.getY() == c2.getY()) return true;
		
		//vertikal gleich
		if(c1.getX() == c2.getX()) return true;
		
		return false;
	}

	protected Action nextSpyAction(World world) {
		Iterator<Coord> iter = spyPoints.iterator();
		
		while(iter.hasNext()){
			Coord next = iter.next();
			iter.remove();
			return new Action(Type.SPY_DRONE, next);
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return super.toString() + "\n\tspy-points: " + spyPoints.toString().replace("[", "").replace("]", "");
	}
}
