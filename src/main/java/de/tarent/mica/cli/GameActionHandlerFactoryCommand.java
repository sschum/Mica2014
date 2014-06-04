package de.tarent.mica.cli;

import java.util.ArrayList;
import java.util.List;

import de.tarent.mica.GameActionHandler;
import de.tarent.mica.bot.ActionStrategy;
import de.tarent.mica.bot.GameMaster;
import de.tarent.mica.bot.HitTraceActionStrategy;
import de.tarent.mica.bot.RandomAttackActionStrategy;
import de.tarent.mica.bot.ShipPlacementStrategy;
import de.tarent.mica.bot.StaticShipPlacementStrategy;

public class GameActionHandlerFactoryCommand{
	private List<ActionStrategy> actionStrategies = new ArrayList<ActionStrategy>();
	private ShipPlacementStrategy shipPlacementStrategy;
	
	public GameActionHandlerFactoryCommand() {
		//default-einstellungen hier...
		shipPlacementStrategy = new StaticShipPlacementStrategy();
		
		//das "unwarscheinlichste" muss als erstes
		actionStrategies.add(new HitTraceActionStrategy(true));
		actionStrategies.add(new RandomAttackActionStrategy());
	}
	
	public GameActionHandler buildGameActionHandler() {
		return new GameMaster(shipPlacementStrategy, actionStrategies);
	}
	
}