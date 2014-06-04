package de.tarent.mica.bot;

import java.awt.Dimension;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import de.tarent.mica.model.Coord;

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
public class RandomAttackActionStrategy extends SimpleAttackActionStrategy {

	@Override
	protected List<Coord> initialiseCoords(Dimension dim) {
		List<Coord> result = super.initialiseCoords(dim);
		
		Random rand = new Random(13121989 * System.currentTimeMillis());
		for(int i = rand.nextInt() % 13; i >= 0; i--){
			Collections.shuffle(result, rand);
		}
		
		return result;
	}
	
	@Override
	public String getShortDescription() {
		return 	"Diese Strategie beschie\u00dft zuf\u00e4llig eine Coordinate. " +
				"Dieser Zufall ist allerdings noch so \"schlau\", dass er " +
				"die Attacken so koordiniert, dass am Ende nur ein Karofeld abgedeckt wird.";
	}
}
