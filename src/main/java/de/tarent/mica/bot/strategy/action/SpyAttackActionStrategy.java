package de.tarent.mica.bot.strategy.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.tarent.mica.Action;
import de.tarent.mica.Action.Type;
import de.tarent.mica.bot.strategy.StrategyStats;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.Field.Element;
import de.tarent.mica.model.World;
import de.tarent.mica.model.element.SpyArea;

@StrategyStats(description = "Diese Strategie attackiert spionierte Bereiche, bei denen noch etwas \"zu hohlen\" ist.")
public class SpyAttackActionStrategy extends ActionStrategy {

	@Override
	public Action getActionDecision(World world) {
		SpyArea spy = nextSpyArea(world);
		if(spy == null) return null;	
		
		return nextAction(world, spy);
	}

	protected SpyArea nextSpyArea(World world) {
		for(SpyArea sa : world.getSpyAreas()){
			if(!isLooted(world, sa)){
				return sa;
			}
		}
		
		return null;
	}

	private boolean isLooted(World world, SpyArea spy) {
		int discovered = spy.getSegments();
		int taken = 0;

		//mögliche Koordinaten sammeln...
		List<Coord> area = collectPossipleCoords(world, spy);
		
		for(Coord c : area){
			Element e = world.getEnemyField().get(c);
			if(e == Element.TREFFER || e == Element.SCHIFF){
				taken++;
			}
		}
		
		return taken >= discovered;
	}

	protected List<Coord> collectPossipleCoords(World world, SpyArea spy) {
		/*
		 *  * => Coord
		 *  # => Spy (inklusive *)
		 *  
		 *   0 1 2 3 4 5
		 * A
		 * B   # # #
		 * C   # * #
		 * D   # # # 
		 * E
		 * 
		 */
		
		List<Coord> area = new ArrayList<Coord>(9);
		area.add(spy.getCoord());
		area.add(spy.getCoord().getNorthNeighbor());
		area.add(spy.getCoord().getEastNeighbor());
		area.add(spy.getCoord().getSouthNeighbor());
		area.add(spy.getCoord().getWestNeighbor());
		area.add(spy.getCoord().getNorthNeighbor().getWestNeighbor());
		area.add(spy.getCoord().getNorthNeighbor().getEastNeighbor());
		area.add(spy.getCoord().getSouthNeighbor().getWestNeighbor());
		area.add(spy.getCoord().getSouthNeighbor().getEastNeighbor());
		
		Iterator<Coord> iter = area.iterator();
		while(iter.hasNext()) 
			if(!world.isInWorld(iter.next())) 
				iter.remove();
		
		
		return area;
	}

	protected Action nextAction(World world, SpyArea spy) {
		List<Coord> area = collectPossipleCoords(world, spy);
		
		for(Coord c : area){
			Element e = world.getEnemyField().get(c);
			if(e == Element.UNBEKANNT){
				return new Action(Type.ATTACK, c);
			}
		}
		
		return null;
	}

}
