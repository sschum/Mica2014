package de.tarent.mica.bot.strategy.action;

import de.tarent.mica.Action;
import de.tarent.mica.model.World;

/**
 * Eine Strategie, die entscheided, was als nächstes zu tun ist.
 * 
 * @author rainu
 *
 */
public abstract class ActionStrategy {

	public ActionStrategy() {
		if(!getClass().isAnnotationPresent(ActionStats.class)){
			throw new IllegalStateException("The class " + getClass().getName() + " has no " + ActionStats.class + " annotation!");
		}
	}
	
	/**
	 * Welche Aktion soll als nächstes durchgeführt werden?
	 * 
	 * @param world Aktueller Zustand der Welt.
	 * @return
	 */
	public abstract Action getActionDecision(World world);
	
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
	public static String getShortDescription(Class<? extends ActionStrategy> strategy){
		return strategy.getAnnotation(ActionStats.class).description();
	}
}
