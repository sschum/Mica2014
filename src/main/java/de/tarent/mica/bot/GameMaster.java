package de.tarent.mica.bot;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.tarent.mica.Action;
import de.tarent.mica.GameActionHandler;
import de.tarent.mica.bot.strategy.action.ActionStrategy;
import de.tarent.mica.bot.strategy.shipplacement.ShipPlacementStrategy;
import de.tarent.mica.model.Fleet;
import de.tarent.mica.model.World;
import de.tarent.mica.util.Logger;
import de.tarent.mica.util.ObjectSerializer;

/**
 * Diese Klasse ist für alle automatisierten Actionen zuständig.
 * Sie ist somit der "Kopf" des Bots.
 * 
 * @author rainu
 *
 */
public class GameMaster implements GameActionHandler {

	private ShipPlacementStrategy shipPlacementStrategy;
	private List<ActionStrategy> actionStrategies;
	private Set<Integer> usedFleets = new HashSet<Integer>();
	
	public GameMaster(ShipPlacementStrategy shipPlacementStrategy, List<ActionStrategy> actionStrategies) {
		this.shipPlacementStrategy = shipPlacementStrategy;
		this.actionStrategies = actionStrategies;
	}
	
	@Override
	public Fleet getFleet() {
		Fleet fleet = null;
		
		for(int i = 0;  i < 1312; i++){
			fleet = shipPlacementStrategy.getFleet();
			if(usedFleets.contains(fleet.hashCode())){
				Logger.info("Generated fleet was already used! Try to generate a new uniq fleet...");
			}else{
				break;
			}
		}
		usedFleets.add(fleet.hashCode());
		
		return fleet;
	}

	@Override
	public void handleEnemyAction(Action action) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void handleRoundOver(boolean won) {
		for(ActionStrategy s : actionStrategies){
			s.reset();
		}
	}

	
	@Override
	public Action getNextAction(World world) {
		for(ActionStrategy s : actionStrategies){
			Logger.debug("Ask " + s.getShortName() + " for decision...");
			
			try{
				Action action = s.getActionDecision(world);
				if(action != null){
					Logger.debug("The " + s.getShortName() + " has been decided for " + action);
	
					return action;
				}
			}catch(RuntimeException e){
				Logger.error(s.getShortName() + " occures an error!", e);
			}
		}
		
		try {
			Logger.debug("Serialized strategies: " + ObjectSerializer.serialise(actionStrategies));
			Logger.debug("Serialized world: " + ObjectSerializer.serialise(world));
		} catch (IOException e) {}
		
		return null;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		List<ActionStrategy> actionStrategies = (List<ActionStrategy>)ObjectSerializer.deserialise("");
		World world = (World)ObjectSerializer.deserialise("");
		
		System.out.println(world);
		System.out.println(new GameMaster(null, actionStrategies).getNextAction(world));
	}
}
