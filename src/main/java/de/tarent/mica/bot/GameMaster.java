package de.tarent.mica.bot;

import de.tarent.mica.Action;
import de.tarent.mica.GameActionHandler;
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

	@Override
	public Fleet getFleet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handleEnemyAction(Action action) {
		// TODO Auto-generated method stub

	}

	@Override
	public Action getNextAction(World world) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handleGameOver(GameStats stats) {
		// TODO Auto-generated method stub

	}

}
