package de.tarent.mica.bot.strategy.shipplacement;

import de.tarent.mica.bot.strategy.StrategyStats;
import de.tarent.mica.model.Fleet;

/**
 * Eine Strategie, wie man Schiffe platziert.
 * 
 * @author rainu
 *
 */
public abstract class ShipPlacementStrategy {

	public ShipPlacementStrategy() {
		if(!getClass().isAnnotationPresent(StrategyStats.class)){
			throw new IllegalStateException("The class " + getClass().getName() + " has no " + StrategyStats.class + " annotation!");
		}
	}
	
	/**
	 * Liefert eine Flotte. Es muss nicht gewärleistet sein, dass immer die 
	 * gleiche zurückgeliefert wird!
	 */
	public abstract Fleet getFleet();
	
	/**
	 * Liefert eine kurze Beschreibung der Strategie
	 * 
	 * @return
	 */
	public String getShortDescription(){
		return getShortDescription(getClass());
	}
	
	/**
	 * Liefert eine kurze Beschreibung der übergebenen Strategie
	 * 
	 * @return
	 */
	public static String getShortDescription(Class<? extends ShipPlacementStrategy> strategy){
		return strategy.getAnnotation(StrategyStats.class).description();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
