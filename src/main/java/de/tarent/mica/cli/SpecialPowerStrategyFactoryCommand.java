package de.tarent.mica.cli;

import asg.cliche.Command;
import asg.cliche.Param;
import de.tarent.mica.bot.strategy.action.ClusterbombStrategy;
import de.tarent.mica.bot.strategy.action.SpecialAttackStrategy;
import de.tarent.mica.bot.strategy.action.SpecialPowerStrategy;
import de.tarent.mica.bot.strategy.action.SpyStrategy;
import de.tarent.mica.bot.strategy.action.TorpedoStrategy;
import de.tarent.mica.model.Coord;

public class SpecialPowerStrategyFactoryCommand {
	private SpecialAttackStrategy carrierStrategy;
	private SpecialAttackStrategy cruiserStrategy;
	private SpecialAttackStrategy destroyerStrategy;
	private SpecialAttackStrategy submarineStrategy;
	
	public SpecialPowerStrategyFactoryCommand() {
	}
	
	public SpecialPowerStrategy buildStrategy() {
		return new SpecialPowerStrategy(carrierStrategy, cruiserStrategy, destroyerStrategy, submarineStrategy);
	}
	
	
	@Command(abbrev = "ass", description = "F\u00fcgt eine SpyStrategy hinzu.")
	public String addSpyStrategy(
			@Param(name = "coord", description = "Die zu spionierenden Koordinaten.") 
			Coord...cords){
		
		destroyerStrategy = new SpyStrategy(cords);
		
		return "Strategie gesetzt.";
	}
	
	@Command(abbrev = "ats", description = "F\u00fcgt eine TorpedoStrategy hinzu.")
	public String addTorpedoStrategy(){
		submarineStrategy = new TorpedoStrategy();
		
		return "Strategie gesetzt.";
	}
	
	@Command(abbrev = "acs", description = "F\u00fcgt eine ClusterbombStrategy hinzu.")
	public String addClusterbombStrategy(
			@Param(name = "coord", description = "Die zu bombardierenden Koordinaten.") 
			Coord...cords){
		
		carrierStrategy = new ClusterbombStrategy(cords);
		
		return "Strategie gesetzt.";
	}
}
