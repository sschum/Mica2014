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
import de.tarent.mica.model.element.Carrier;

@StrategyStats(description = "Diese Strategie bombardiert (wenn m\u00f6glich) den Gegner.")
public class ClusterbombStrategy extends SpecialAttackStrategy {
	protected final List<Coord> bombPoints;
	
	public ClusterbombStrategy(Coord...bombPoints) {
		this.bombPoints = new ArrayList<Coord>(Arrays.asList(bombPoints));
	}

	@Override
	public Action getActionDecision(World world) {
		if(canBomb(world)){
			return nextBombAction(world);
		}
		
		return null;
	}

	protected boolean canBomb(World world) {
		//ich kann nur bombardieren, wenn Carrier
		//auf einer ebene eine gemeinsame Position besitzen
		//und auch noch zwei "leben" ;)
		return getPossibleSpecialAttackCount(Carrier.class, world) > 0;
	}

	protected Action nextBombAction(World world) {
		Iterator<Coord> iter = bombPoints.iterator();
		
		while(iter.hasNext()){
			Coord next = iter.next();
			iter.remove();
			return new Action(Type.CLUSTERBOMB, next);
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return super.toString() + "\n\tbomb-points: " + bombPoints.toString().replace("[", "").replace("]", "");
	}
}
