package de.tarent.mica.bot.strategy.action;

import java.util.ArrayList;
import java.util.List;

import de.tarent.mica.Action;
import de.tarent.mica.model.World;
import de.tarent.mica.model.element.Ship;

/**
 * Diese Klasse bietet nur Hilfsfunktionen für Spezialattacken an.
 * 
 * @author rainu
 *
 */
public abstract class SpecialAttackStrategy extends ActionStrategy {
	private static final long serialVersionUID = -7878435072071230970L;

	public abstract Action getActionDecision(World world);

	protected int getPossibleSpecialAttackCount(Class<? extends Ship> attackClass, World world){
		List<? extends Ship> ship = getShips(attackClass, world);
		
		if(ship.size() < 2) return 0;
		if(ship.get(0).isSunken() || ship.get(1).isSunken()) return 0;
		
		int theoreticallyPossible = Ship.getTheoreticallySpecialAttacks(ship.get(0), ship.get(1));

		int used = world.getSpecialAttackCount(attackClass);
		int possible = theoreticallyPossible - used;
		
		return possible;
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
