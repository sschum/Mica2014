package de.tarent.mica.bot.strategy.action;

import java.util.List;

import de.tarent.mica.bot.strategy.StrategyStats;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.World;
import de.tarent.mica.model.element.SpyArea;
import de.tarent.mica.util.Random;

@StrategyStats(description = "Diese Strategie attackiert zuf\u00e4llig spionierte Bereiche, bei denen noch etwas \"zu hohlen\" ist.")
public class RandomSpyAttackStrategy extends SpyAttackStrategy {

	@Override
	protected List<SpyArea> collectUnlootedSpyAreas(World world) {
		List<SpyArea> result = super.collectUnlootedSpyAreas(world);
		new Random().shuffle(result);
		
		return result;
	}
	
	@Override
	protected List<Coord> collectPossipleCoords(World world, SpyArea spy) {
		List<Coord> result =  super.collectPossipleCoords(world, spy);
		new Random().shuffle(result);
		
		return result;
	}
}
