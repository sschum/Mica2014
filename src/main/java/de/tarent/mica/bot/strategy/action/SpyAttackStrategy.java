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
public class SpyAttackStrategy extends ActionStrategy {
	private static final long serialVersionUID = 5641292138770748478L;

	@Override
	public Action getActionDecision(World world) {
		SpyArea spy = nextSpyArea(world);
		if(spy == null) return null;	
		
		return nextAction(world, spy);
	}

	protected SpyArea nextSpyArea(World world) {
		List<SpyArea> possipleAreas = collectUnlootedSpyAreas(world);
		if(possipleAreas.isEmpty()) return null;
		
		return possipleAreas.get(0);
	}
	
	protected List<SpyArea> collectUnlootedSpyAreas(World world) {
		List<SpyArea> unlooted = new ArrayList<SpyArea>(3);
		
		for(SpyArea sa : world.getSpyAreas()){
			if(!isLooted(world, sa)){
				unlooted.add(sa);
			}
		}
		
		return unlooted;
	}

	private boolean isLooted(World world, SpyArea spy) {
		int discovered = spy.getSegments();
		int taken = 0;

		//mögliche Koordinaten sammeln...
		List<Coord> area = collectPossipleCoords(world, spy);
		
		for(Coord c : area){
			Element e = world.getEnemyField().get(c);
			if(isHit(e)){
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
			if(isUnknown(e)){
				return new Action(Type.ATTACK, c);
			}
		}
		
		return null;
	}
	
	private boolean isUnknown(Element element){
		return element == Element.UNBEKANNT || element == Element.SPIONAGE;
	}
	
	private boolean isHit(Element element){
		return element == Element.SCHIFF || element == Element.TREFFER;
	}
	
	@Override
	public String getShortName() {
		return getClass().getSimpleName();
	}
}
