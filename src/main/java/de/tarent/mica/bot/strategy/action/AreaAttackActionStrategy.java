package de.tarent.mica.bot.strategy.action;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;

import de.tarent.mica.model.Coord;

/**
 * Diese Strategie orientiert sich an der {@link RandomAttackActionStrategy}.
 * Nur wird hier nur der angegebene Bereich betrachtet.
 * 
 * @author rainu
 * TODO: Dekorator-Pattern wäre denke ich angebracht...
 */
@ActionStats(description = "Diese Strategie orientiert sich an der RandomAttackActionStrategy. Nur wird hier nur der angegebene Bereich betrachtet.")
public class AreaAttackActionStrategy extends RandomAttackActionStrategy {
	protected final Coord startCoord;
	protected final Dimension area;
	
	public AreaAttackActionStrategy(Coord startCoord, Dimension area) {
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
	
}
