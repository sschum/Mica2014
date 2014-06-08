package de.tarent.mica.bot.strategy.shipplacement;

import de.tarent.mica.bot.strategy.StrategyStats;
import de.tarent.mica.bot.strategy.shipplacement.ReflectShipPlacement.MirrorAxis;
import de.tarent.mica.bot.strategy.shipplacement.RotateShipPlacement.RotateDirection;
import de.tarent.mica.model.Fleet;
import de.tarent.mica.util.Random;

@StrategyStats(description = "Diese Schiffstrategie verwendet eine beliebige andere Schiffstrategie um eine Flotte zu generieren." +
		 					 "Anschlie\u00dfend verw\u00fcrffelt (spiegeln und rotieren) sie diese Schiffspositionen. Diese bleiben jedoch noch regelkonform!")
public class ShuffleShipPlacement extends ShipPlacementStrategy {

	protected ShipPlacementStrategy delegate;
	protected ReflectShipPlacement reflectPlacement;
	protected RotateShipPlacement rotatePlacement;

	public ShuffleShipPlacement(ShipPlacementStrategy delegate) {
		super(delegate.worldDimension);
		
		this.delegate = delegate;
		this.reflectPlacement = new ReflectShipPlacement(delegate);
		this.rotatePlacement = new RotateShipPlacement(delegate);
	}

	@Override
	public Fleet getFleet() {
		//wir generieren keine Flotte selber, sondern LASSEN generieren... wir sind der perfekte Chef :D
		final Fleet fleet = delegate.getFleet();

		final Random rnd = new Random();
		rnd.runXTimes(new Runnable() {
			@Override
			public void run() {
				shuffle(fleet);				
			}
		});
		
		return fleet;
	}

	public void shuffle(final Fleet fleet) {
		final Random random = new Random();

		random.runXTimes(new Runnable() {
			@Override
			public void run() {
				if(random.nextBoolean()){
					reflectPlacement.reflect(fleet, random.choose(MirrorAxis.values()));
				}else{
					rotatePlacement.rotate(fleet, random.choose(RotateDirection.values()));
				}
			}
		});
	}

}
