package de.tarent.mica.bot;

import java.io.IOException;
import java.util.List;

import de.tarent.mica.Action;
import de.tarent.mica.GameActionHandler;
import de.tarent.mica.bot.strategy.action.ActionStrategy;
import de.tarent.mica.bot.strategy.shipplacement.ShipPlacementStrategy;
import de.tarent.mica.model.Fleet;
import de.tarent.mica.model.World;
import de.tarent.mica.util.Logger;
import de.tarent.mica.util.ObjectSerializer;

/**
 * Diese Klasee ist für alle automatisierten Actionen zuständig.
 * Sie ist somit der "Kopf" des Bots.
 * 
 * @author rainu
 *
 */
public class GameMaster implements GameActionHandler {

	private ShipPlacementStrategy shipPlacementStrategy;
	private List<ActionStrategy> actionStrategies;
	
	public GameMaster(ShipPlacementStrategy shipPlacementStrategy, List<ActionStrategy> actionStrategies) {
		this.shipPlacementStrategy = shipPlacementStrategy;
		this.actionStrategies = actionStrategies;
	}
	
	@Override
	public Fleet getFleet() {
		return shipPlacementStrategy.getFleet();
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
			Logger.debug("Ask " + s.getClass().getSimpleName() + " for decision...");
			
			Action action = s.getActionDecision(world);
			if(action != null){
				Logger.debug("The " + s.getClass().getSimpleName() + " has been decided for " + action);
				return action;
			}
		}
		
		try {
			Logger.debug("Serialized strategies: " + ObjectSerializer.serialise(actionStrategies));
			Logger.debug("Serialized world: " + ObjectSerializer.serialise(world));
		} catch (IOException e) {}
		return null;
	}
}
