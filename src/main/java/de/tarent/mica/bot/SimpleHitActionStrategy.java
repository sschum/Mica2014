package de.tarent.mica.bot;

import java.util.Random;

import de.tarent.mica.Action;
import de.tarent.mica.Action.Type;
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
public class SimpleHitActionStrategy implements ActionStrategy {
	private final Coord start;
	private boolean currentSwitch;
	private Coord current;
	
	public SimpleHitActionStrategy(){
		currentSwitch = new Random().nextBoolean();
		if(currentSwitch){
			this.start = new Coord(0, 0);
			
		}else{
			this.start = new Coord(1, 0);
		}
		
		this.current = this.start;
	}
	
	@Override
	public Action getActionDecision(World world) {
		while(world.getEnemyField().get(current) != Element.UNBEKANNT){
			current = current.getEastNeighbor().getEastNeighbor();	//in zweierschritten reicht vollkommen!
			if(!world.isInWorld(current)){
				current = new Coord(currentSwitch ? 1 : 0, current.getY() + 1);
				currentSwitch = !currentSwitch;
				
				if(current.getY() >= world.getWorldDimension().height) return null;	//alle Felder abgeklappert
			}
		}
		
		return new Action(Type.ATTACK, current);
	}

}
