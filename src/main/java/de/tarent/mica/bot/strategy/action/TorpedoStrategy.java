package de.tarent.mica.bot.strategy.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.tarent.mica.Action;
import de.tarent.mica.Action.Type;
import de.tarent.mica.bot.strategy.StrategyStats;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.Field.Element;
import de.tarent.mica.model.World;
import de.tarent.mica.model.element.Submarine;

@StrategyStats(description = "Diese Strategie sorgt daf\u00fcr, dass Torpedos in die erfolgsversprechendste Richtung gefeuert werden.")
public class TorpedoStrategy extends SpecialAttackStrategy {
	private static final long serialVersionUID = 6794569569155037503L;

	@Override
	public Action getActionDecision(World world) {
		List<Coord> torpedoCoords = collectTorpedoCoords(world);		
		if(torpedoCoords.isEmpty()) return null;
		
		return getAction(world, torpedoCoords);
	}

	protected List<Coord> collectTorpedoCoords(World world) {
		Set<Coord> possibleCoords = new HashSet<Coord>(4);
		if(getPossibleSpecialAttackCount(Submarine.class, world) > 0){
			//jetzt können wir die koordinaten sammeln...
			//davon gibts zum glück nur eine Hand voll :)
			
			List<Submarine> submarines = getShips(Submarine.class, world);
			possibleCoords.addAll(submarines.get(0).getSpace());
			possibleCoords.addAll(submarines.get(1).getSpace());
		}
		
		//anschließend müssen noch die Coordinaten entfernt werden, 
		//die bereits getroffen wurden
		Iterator<Coord> iter = possibleCoords.iterator();
		while(iter.hasNext()){
			Coord c = iter.next();
			
			if(world.getEnemyView().get(c) == Element.TREFFER){
				iter.remove();
			}
		}
		
		return new ArrayList<Coord>(possibleCoords);
	}

	protected Action getAction(World world, List<Coord> torpedoCoords) {
		Action mostLucrative = new Action(Type.TORPEDO_NORD, torpedoCoords.get(0));
		int mostLucrativeCount = 0;
		
		for(Coord c : torpedoCoords){
			int cn = getCountFor(world, c, Type.TORPEDO_NORD);
			int co = getCountFor(world, c, Type.TORPEDO_OST);
			int cs = getCountFor(world, c, Type.TORPEDO_SUED);
			int cw = getCountFor(world, c, Type.TORPEDO_WEST);
			
			if(cn > mostLucrativeCount){
				mostLucrative = new Action(Type.TORPEDO_NORD, c);
				mostLucrativeCount = cn;
			}
			else if(co > mostLucrativeCount){
				mostLucrative = new Action(Type.TORPEDO_OST, c);
				mostLucrativeCount = co;
			}
			else if(cs > mostLucrativeCount){
				mostLucrative = new Action(Type.TORPEDO_SUED, c);
				mostLucrativeCount = cs;
			}
			else if(cw > mostLucrativeCount){
				mostLucrative = new Action(Type.TORPEDO_WEST, c);
				mostLucrativeCount = cw;
			}
		}
		
		return mostLucrative;
	}

	private int getCountFor(World world, Coord c, Type direction) {
		int count = 0;
		
		switch(direction){
		case TORPEDO_NORD:
			while(world.isInWorld(c)){
				if(world.getEnemyField().get(c) == Element.UNBEKANNT){
					count++;
				}
				
				c = c.getNorthNeighbor();
			}
			
			break;
		case TORPEDO_OST:
			while(world.isInWorld(c)){
				if(world.getEnemyField().get(c) == Element.UNBEKANNT){
					count++;
				}
				
				c = c.getEastNeighbor();
			}
			
			break;
		case TORPEDO_SUED:
			while(world.isInWorld(c)){
				if(world.getEnemyField().get(c) == Element.UNBEKANNT){
					count++;
				}
				
				c = c.getSouthNeighbor();
			}
			
			break;
		case TORPEDO_WEST:
			while(world.isInWorld(c)){
				if(world.getEnemyField().get(c) == Element.UNBEKANNT){
					count++;
				}
				
				c = c.getWestNeighbor();
			}
			
			break;
		default: return -1;
		}
		
		return count;
	}

}
