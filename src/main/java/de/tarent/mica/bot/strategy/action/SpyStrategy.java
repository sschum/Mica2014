package de.tarent.mica.bot.strategy.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.tarent.mica.Action;
import de.tarent.mica.Action.Type;
import de.tarent.mica.bot.strategy.StrategyStats;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.World;
import de.tarent.mica.model.element.Destroyer;

@StrategyStats(description = "Diese Strategie spioniert (wenn m\u00f6glich) den Gegner aus.")
public class SpyStrategy extends SpecialAttackStrategy {
	private static final long serialVersionUID = 7680520248124658901L;

	protected final List<Coord> spyPoints;
	protected List<Coord> sessionSpyPoints;
	
	public SpyStrategy(Coord...spyPoints) {
		this.spyPoints = Collections.unmodifiableList(new ArrayList<Coord>(Arrays.asList(spyPoints)));
		
		reset();
	}
	
	@Override
	public void reset() {
		super.reset();

		//kopie der liste erstellen
		this.sessionSpyPoints = new ArrayList<Coord>(spyPoints);
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
		//und auch noch zwei "leben" ;)
		return getPossibleSpecialAttackCount(Destroyer.class, world) > 0;
	}

	protected Action nextSpyAction(World world) {
		Iterator<Coord> iter = sessionSpyPoints.iterator();
		
		while(iter.hasNext()){
			Coord next = iter.next();
			iter.remove();
			return new Action(Type.SPY_DRONE, next);
		}
		
		return null;
	}
	
	@Override
	public String getShortName() {
		return getClass().getSimpleName() + "@" + spyPoints.toString().replace("[", "").replace("]", "").replace(", ", "_");
	}
	
	@Override
	public String toString() {
		return super.toString() + "\n\tspy-points: " + spyPoints.toString().replace("[", "").replace("]", "");
	}
}
