package de.tarent.mica.bot.strategy.action;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.tarent.mica.Action;
import de.tarent.mica.Action.Type;
import de.tarent.mica.bot.strategy.StrategyStats;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.Field.Element;
import de.tarent.mica.model.World;

/**
 * Diese Strategy sorgt dafür, dass normale Attacken im Karoformat ausgeführt werden:
 * 
 *   0 1 2 3 4 
 * A ? ~ ? ~ ? 
 * B ~ ? ~ ? ~ 
 * C ? ~ ? ~ ? 
 * D ~ ? ~ ? ~ 
 * 
 * 
 * @author rainu
 *
 */
@StrategyStats(description = "Diese Strategie sorgt daf\u00fcr, dass normale Attacken im Karoformat ausgef\u00fchrt werden.")
public class SimpleAttackStrategy extends ActionStrategy {
	private static final long serialVersionUID = 3942463973695814235L;

	protected List<Coord> todo = null;
	protected Dimension todoDimension;
	
	
	@Override
	public void reset() {
		super.reset();

		todo = null;
		todoDimension = null;
	}
	
	@Override
	public Action getActionDecision(World world) {
		if(todoDimension == null || !todoDimension.equals(world.getWorldDimension())){
			//erste initialisierung...
			todo = new ArrayList<Coord>();
			todoDimension = world.getWorldDimension();
			
			todo = initialiseCoords(todoDimension);
		}
		
		Iterator<Coord> iter = todo.iterator();
		while(iter.hasNext()){
			Coord next = iter.next();
			iter.remove();
			
			if(world.getEnemyField().get(next) == Element.UNBEKANNT){
				return new Action(Type.ATTACK, next);
			}
		}
		
		return null;
	}

	protected List<Coord> initialiseCoords(Dimension dim) {
		boolean sw = true;
		Coord start = new Coord(0, 0);

		List<Coord> result = new ArrayList<Coord>();
		result.add(start);
		
		Coord current = start;
		while(current.getY() < dim.height){
			current = current.getEastNeighbor().getEastNeighbor();	//in zweierschritten reicht vollkommen!
			if(!isInDimension(dim, current)){
				current = new Coord(sw ? 1 : 0, current.getY() + 1);
				if(!isInDimension(dim, current)) break;
				
				sw = !sw;
			}
			
			result.add(new Coord(current.getX(), current.getY()));
		}
		
		return result;
	}
	
	private boolean isInDimension(Dimension dim, Coord c){
		return !(c.getX() < 0 || c.getY() < 0 ||
			 	 c.getX() >= dim.width ||
				 c.getY() >= dim.height);
	}
}
