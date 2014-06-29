package de.tarent.mica.bot.strategy.action;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;

import de.tarent.mica.bot.strategy.StrategyStats;
import de.tarent.mica.model.Coord;

/**
 * Diese Strategie orientiert sich an der {@link RandomAttackStrategy}.
 * Nur wird hier nur der angegebene Bereich betrachtet.
 * 
 * @author rainu
 */
@StrategyStats(description = "Diese Strategie orientiert sich an der RandomAttackStrategy. Nur wird hier nur der angegebene Bereich betrachtet.")
public class AreaAttackStrategy extends RandomAttackStrategy {
	private static final long serialVersionUID = 1098069483677813949L;

	protected final Coord startCoord;
	protected final Dimension area;
	
	public AreaAttackStrategy(Coord startCoord, Dimension area) {
		this.startCoord = startCoord;
		this.area = area;
	}
	
	
	@Override
	protected List<Coord> initialiseCoords(Dimension dim) {
		List<Coord> result = super.initialiseCoords(dim);
		
		//alle Koordinaten herausfiltern, die nicht in mein Bereich fallen
		filter(result);
		
		return result;
	}

	private void filter(List<Coord> list) {
		Iterator<Coord> iter = list.iterator();
		
		while(iter.hasNext()){
			Coord next = iter.next();
			
			if(!isInArea(next)){
				iter.remove();
			}
		}
	}

	private boolean isInArea(Coord coord) {
		return new Rectangle(
				startCoord.getX(), 
				startCoord.getY(), 
				area.width, 
				area.height).contains(coord.getX(), coord.getY());
	}
	
	public Coord getStartCoord() {
		return startCoord;
	}

	public Dimension getArea() {
		return area;
	}
	
	@Override
	public String getShortName() {
		return getClass().getSimpleName() + "@" + startCoord + "_" + area.width + "x" + area.height;
	}

	@Override
	public String toString() {
		return super.toString() + "\n\tstart coordinate: " + startCoord + "\n\tarea: " + area.width + "x" + area.height;
	}
}
