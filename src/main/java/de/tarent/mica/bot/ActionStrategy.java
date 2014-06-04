package de.tarent.mica.bot;

import de.tarent.mica.Action;
import de.tarent.mica.model.World;

/**
 * Eine Strategie, die entscheided, was als nächstes zu tun ist.
 * 
 * @author rainu
 *
 */
public interface ActionStrategy {

	/**
	 * Welche Aktion soll als nächstes durchgeführt werden?
	 * 
	 * @param world Aktueller Zustand der Welt.
	 * @return
	 */
	public Action getActionDecision(World world);
	
	/**
	 * Liefert eine kurze Beschreibung der Strategie
	 * 
	 * @return
	 */
	public String getShortDescription();
}
