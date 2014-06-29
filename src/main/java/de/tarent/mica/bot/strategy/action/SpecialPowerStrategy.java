package de.tarent.mica.bot.strategy.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.tarent.mica.Action;
import de.tarent.mica.bot.strategy.StrategyStats;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.Field.Element;
import de.tarent.mica.model.World;
import de.tarent.mica.model.element.Carrier;
import de.tarent.mica.model.element.Cruiser;
import de.tarent.mica.model.element.Destroyer;
import de.tarent.mica.model.element.Ship;
import de.tarent.mica.model.element.Submarine;

@StrategyStats(description = "Diese Strategie sorgt daf\u00fcr, dass alle verf\u00fcgbaren Speciallattacken ausgef\u00fchrt werden." +
		 "Dabei wird darauf geachtet, dass zuerst die Attacke ausgef\u00fchrt wird, die am wenigsten zur Verf\u00fcgung steht!" +
		 "Sollte ein Schiff angeschlagen sein, wird erst versucht deren Speciallatacke zu verwenden, bevor es zu sp\u00e4t ist.")
public class SpecialPowerStrategy extends SpecialAttackStrategy {
	private static final long serialVersionUID = 7290811094275558664L;

	protected SpecialAttackStrategy carrierStrategy;
	protected SpecialAttackStrategy cruiserStrategy;
	protected SpecialAttackStrategy destroyerStrategy;
	protected SpecialAttackStrategy submarineStrategy;
	
	public SpecialPowerStrategy(SpecialAttackStrategy carrierStrategy,
			SpecialAttackStrategy cruiserStrategy,
			SpecialAttackStrategy destroyerStrategy,
			SpecialAttackStrategy submarineStrategy) {
		super();
		
		this.carrierStrategy = carrierStrategy;
		this.cruiserStrategy = cruiserStrategy;
		this.destroyerStrategy = destroyerStrategy;
		this.submarineStrategy = submarineStrategy;
	}
	
	@Override
	public void reset() {
		super.reset();

		if(carrierStrategy != null) carrierStrategy.reset();
		if(cruiserStrategy != null) cruiserStrategy.reset();
		if(destroyerStrategy != null) destroyerStrategy.reset();
		if(submarineStrategy != null) submarineStrategy.reset();
	}

	@Override
	public Action getActionDecision(World world) {
		List<Ship> underAttack = getAllShipsUnderAttack(world);
		if(!underAttack.isEmpty()){
			for(Ship ship : underAttack){
				if(getPossibleSpecialAttackCount(ship.getClass(), world) > 0){
					return useStrategy(ship.getClass(), world);
				}
			}
		}
		
		//kein schiff ist angegriffen...
		return useMostSpecialAttack(world);
	}

	protected Action useMostSpecialAttack(World world) {
		int most = 0;
		Class<? extends Ship> shipClass = null;
		
		if(submarineStrategy != null && getPossibleSpecialAttackCount(Submarine.class, world) > most){
			shipClass = Submarine.class;
			most = getPossibleSpecialAttackCount(Submarine.class, world);
		}
		
		if(destroyerStrategy != null && getPossibleSpecialAttackCount(Destroyer.class, world) > most){
			shipClass = Destroyer.class;
			most = getPossibleSpecialAttackCount(Destroyer.class, world);
		}
		
		if(cruiserStrategy != null && getPossibleSpecialAttackCount(Cruiser.class, world) > most){
			shipClass = Cruiser.class;
			most = getPossibleSpecialAttackCount(Cruiser.class, world);
		}
		
		if(carrierStrategy != null && getPossibleSpecialAttackCount(Carrier.class, world) > most){
			shipClass = Carrier.class;
			most = getPossibleSpecialAttackCount(Carrier.class, world);
		}
		
		return useStrategy(shipClass, world);
	}

	protected Action useStrategy(Class<? extends Ship> shipClass, World world){
		if(shipClass == Carrier.class && carrierStrategy != null){
			return carrierStrategy.getActionDecision(world);
		}
		if(shipClass == Cruiser.class && cruiserStrategy != null){
			return cruiserStrategy.getActionDecision(world);
		}
		if(shipClass == Destroyer.class && destroyerStrategy != null){
			return destroyerStrategy.getActionDecision(world);
		}
		if(shipClass == Submarine.class && submarineStrategy != null){
			return submarineStrategy.getActionDecision(world);
		}
		
		return null;
	}

	protected List<Ship> getAllShipsUnderAttack(final World world) {
		List<Ship> underAttack = new ArrayList<Ship>();
		
		for(Ship ship : world.getOwnShips()){
			if(ship.isAttacked() || ship.isBurning()){
				underAttack.add(ship);
			}
		}
		
		//nach verfügbaren sektoren sortieren
		Collections.sort(underAttack, new Comparator<Ship>() {
			@Override
			public int compare(Ship s1, Ship s2) {
				int s1Left = s1.getSize() - getHitCount(s1);
				int s2Left = s2.getSize() - getHitCount(s2);
				
				int result = Integer.compare(s1Left, s2Left);
				if(result == 0){
					result = Boolean.compare(s2.isBurning(), s1.isBurning());
				}
				
				return result;
			}
			
			private int getHitCount(Ship s){
				int hit = 0;
				
				for(Coord c : s.getSpace()){
					if(world.getEnemyView().get(c) == Element.TREFFER){
						hit++;
					}
				}
				
				return hit;
			}
		});
		
		return underAttack;
	}

	@Override
	public String getShortName() {
		return getClass().getSimpleName() + 
				"@" + ( carrierStrategy != null ? carrierStrategy.getShortName() : "") + 
				"_" + ( cruiserStrategy != null ? cruiserStrategy.getShortName() : "") +
				"_" + ( destroyerStrategy != null ? destroyerStrategy.getShortName() : "") +
				"_" + ( submarineStrategy != null ? submarineStrategy.getShortName() : "");
	}
}
