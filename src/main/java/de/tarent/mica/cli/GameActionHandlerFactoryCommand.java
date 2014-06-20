package de.tarent.mica.cli;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asg.cliche.Command;
import asg.cliche.Param;
import de.tarent.mica.GameActionHandler;
import de.tarent.mica.bot.GameMaster;
import de.tarent.mica.bot.strategy.action.ActionStrategy;
import de.tarent.mica.bot.strategy.action.AreaAttackStrategy;
import de.tarent.mica.bot.strategy.action.HitTraceStrategy;
import de.tarent.mica.bot.strategy.action.RandomAttackStrategy;
import de.tarent.mica.bot.strategy.action.RandomSpyAttackStrategy;
import de.tarent.mica.bot.strategy.action.SpyAttackStrategy;
import de.tarent.mica.bot.strategy.action.SpyStrategy;
import de.tarent.mica.bot.strategy.action.TorpedoStrategy;
import de.tarent.mica.bot.strategy.shipplacement.ShipPlacementStrategy;
import de.tarent.mica.bot.strategy.shipplacement.ShuffleShipPlacement;
import de.tarent.mica.bot.strategy.shipplacement.StaticShipPlacementStrategy;
import de.tarent.mica.model.Coord;

public class GameActionHandlerFactoryCommand{
	private List<ActionStrategy> actionStrategies = new ArrayList<ActionStrategy>();
	private ShipPlacementStrategy shipPlacementStrategy;
	
	public GameActionHandlerFactoryCommand() {
		//default-einstellungen hier...
		shipPlacementStrategy = new ShuffleShipPlacement(new StaticShipPlacementStrategy(new Dimension(10, 10)));	//TODO: die WeltDimension auslagern...
		
		//das "unwarscheinlichste" muss als erstes
		actionStrategies.add(new HitTraceStrategy(true));
		actionStrategies.add(new RandomAttackStrategy());
	}
	
	public GameActionHandler buildGameActionHandler() {
		return new GameMaster(shipPlacementStrategy, actionStrategies);
	}
	
	@Command(abbrev = "ssps", description = "Zeigt die aktuell benutzte Schiff-Strategie.")
	public String showShipPlacementStrategy(){
		if(shipPlacementStrategy == null) return "Keine Schiffstrategie konfiguriert!";
		
		return shipPlacementStrategy + " : " + shipPlacementStrategy.getShortDescription();
	}
	
	@Command(abbrev = "sas", description = "Zeigt die aktuell benutzten Action-Strategien.")
	public String showActionStrategies(){
		if(	actionStrategies == null ||
			actionStrategies.isEmpty()) {
			
			return "Keine Action-Strategien konfiguriert!";
		}
		
		StringBuffer sb = new StringBuffer();
		
		for(ActionStrategy as : actionStrategies){
			sb.append(as);
			sb.append("\n");
			sb.append(as.getShortDescription());
			
			sb.append("\n\n");
		}
		
		return sb.toString();
	}
	
	@Command(abbrev = "cas", description = "Entfernt alle bisher konfigurierten Action-Strategien.")
	public String clearActionStrategies(){
		actionStrategies.clear();
		
		return "Alle Action-Strategien entfernt!";
	}
	
	@Command(abbrev = "ahts", description = "F\u00fcgt eine HitTraceStrategy hinzu.")
	public String addHitTraceStrategy(
			@Param(name = "ignoreBurningShips", description = "Sollen brennende Schiffe ignoriert werden?") 
			boolean ignoreBurningShips){
		
		actionStrategies.add(new HitTraceStrategy(ignoreBurningShips));
		
		return "Strategie hinzugef\u00fcgt.";
	}
	
	@Command(abbrev = "aras", description = "F\u00fcgt eine RandomAttackStrategy hinzu.")
	public String addRandomAttackStrategy(){
		actionStrategies.add(new RandomAttackStrategy());
		
		return "Strategie hinzugef\u00fcgt.";
	}
	
	@Command(abbrev = "aaas", description = "F\u00fcgt eine AreaAttackStrategy hinzu.")
	public String addAreaAttackStrategy(
			@Param(name = "startCoord", description = "Oberer Eckpunkt des Zielbereiches.") 
			Coord startCoord,
			@Param(name = "width", description = "Breite des Zielbereiches.") 
			int width,
			@Param(name = "height", description = "H\u00f6he des Zielbereiches.") 
			int height){
		
		actionStrategies.add(new AreaAttackStrategy(startCoord, new Dimension(width, height)));
		
		return "Strategie hinzugef\u00fcgt.";
	}
	
	@Command(abbrev = "sac", description = "Zeigt die Abdeckung der AreaAttackStrategy an.")
	public String showAreaCover(){
		List<AreaAttackStrategy> areaStrategies = new ArrayList<AreaAttackStrategy>(actionStrategies.size());
		for(ActionStrategy s : actionStrategies){
			if(s instanceof AreaAttackStrategy) areaStrategies.add((AreaAttackStrategy)s);
		}
		
		//dimension ermitteln
		Map<Coord, Character> cover = new HashMap<Coord, Character>();
		
		char representation = 'A';
		for(AreaAttackStrategy as : areaStrategies){
			Coord start = as.getStartCoord();
			Dimension area = as.getArea();
			
			for(int x=0; x < area.width; x++){
				for(int y=0; y < area.height; y++){
					cover.put(new Coord(start.getX() + x, start.getY() + y), representation);
				}
			}
			
			representation++;
		}
		
		List<Coord> sortedCoords = new ArrayList<Coord>(cover.keySet());
		Collections.sort(sortedCoords, Coord.COMPARATOR);
		
		StringBuffer sb = new StringBuffer("\n  ");
		Coord last = null;
		for(int i=0; i < sortedCoords.size(); i++){
			Coord c = sortedCoords.get(i);
			
			if(last != null && last.getY() != c.getY()){
				break;
			}
			
			sb.append(i + " ");
			last = c;
		}
		
		int row = 0;
		sb.append("\n" + Integer.toHexString(row++).toUpperCase() + " ");
		last = null;

		for(Coord c : sortedCoords){
			if(last != null && last.getY() != c.getY()){
				sb.append("\n" + Integer.toHexString(row++).toUpperCase() + " ");
			}
			
			sb.append(cover.get(c) + " ");
			last = c;
		}
		
		return sb.toString();
	}
	
	@Command(abbrev = "ass", description = "F\u00fcgt eine SpyStrategy hinzu.")
	public String addSpyStrategy(
			@Param(name = "coord", description = "Die zu spionierenden Koordinaten.") 
			Coord...cords){
		actionStrategies.add(new SpyStrategy(cords));
		
		return "Strategie hinzugef\u00fcgt.";
	}
	
	@Command(abbrev = "asas", description = "F\u00fcgt eine SpyAttackStrategy hinzu.")
	public String addSpyAttackStrategy(){
		actionStrategies.add(new SpyAttackStrategy());
		
		return "Strategie hinzugef\u00fcgt.";
	}
	
	@Command(abbrev = "arsas", description = "F\u00fcgt eine RandomSpyAttackStrategy hinzu.")
	public String addRandomSpyAttackStrategy(){
		actionStrategies.add(new RandomSpyAttackStrategy());
		
		return "Strategie hinzugef\u00fcgt.";
	}
	
	@Command(abbrev = "ats", description = "F\u00fcgt eine TorpedoStrategy hinzu.")
	public String addTorpedoStrategy(){
		actionStrategies.add(new TorpedoStrategy());
		
		return "Strategie hinzugef\u00fcgt.";
	}
}