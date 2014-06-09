package de.tarent.mica.bot.strategy.action;

import java.awt.Dimension;
import java.util.List;

import de.tarent.mica.bot.strategy.StrategyStats;
import de.tarent.mica.model.Coord;
import de.tarent.mica.util.Random;

/**
 * Diese Strategie beschießt zufällig eine Coordinate. 
 * Dieser Zufall ist allerdings noch so "schlau", dass er 
 * die Attacken so koordiniert, dass am ende nur ein Karofeld abgedeckt wird
 * 
 *   0 1 2 3 4 
 * A ? ~ ? ~ ? 
 * B ~ ? ~ ? ~ 
 * C ? ~ ? ~ ? 
 * D ~ ? ~ ? ~ 
 * 
 * @author rainu
 *
 */
@StrategyStats(description = 
		"Diese Strategie beschie\u00dft zuf\u00e4llig eine Coordinate. " +
		"Dieser Zufall ist allerdings noch so \"schlau\", dass er " +
		"die Attacken so koordiniert, dass am Ende nur ein Karofeld abgedeckt wird.")
public class RandomAttackStrategy extends SimpleAttackStrategy {

	@Override
	protected List<Coord> initialiseCoords(Dimension dim) {
		List<Coord> result = super.initialiseCoords(dim);
		new Random().shuffle(result);
		
		return result;
	}
}
