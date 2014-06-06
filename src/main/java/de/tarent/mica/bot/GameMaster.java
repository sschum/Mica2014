package de.tarent.mica.bot;

import java.util.List;

import de.tarent.mica.Action;
import de.tarent.mica.GameActionHandler;
import de.tarent.mica.bot.strategy.action.ActionStrategy;
import de.tarent.mica.bot.strategy.shipplacement.ShipPlacementStrategy;
import de.tarent.mica.model.Fleet;
import de.tarent.mica.model.GameStats;
import de.tarent.mica.model.World;

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
	public Action getNextAction(World world) {
		for(ActionStrategy s : actionStrategies){
			Action action = s.getActionDecision(world);
			if(action != null){
				return action;
			}
		}
		return null;
	}

	@Override
	public void handleGameOver(GameStats stats) {
		// TODO Auto-generated method stub
		
	}
}
