package de.tarent.mica.bot.strategy.action;

import static org.junit.Assert.*;

import org.junit.Test;

import de.tarent.mica.Action;
import de.tarent.mica.bot.strategy.action.SimpleAttackStrategy;
import de.tarent.mica.model.World;

public class SimpleAttackStrategyTest {
	SimpleAttackStrategy toTest = new SimpleAttackStrategy();
	
	@Test
	public void getActionDecision(){
		World world = new World(10, 10);
		
		while(true){
			Action action = toTest.getActionDecision(world);
			if(action == null) break;
			
			world.registerMiss(action.getCoord());
		}
		
		try{
			assertEquals(
					"  0 1 2 3 4 5 6 7 8 9\n" + 
					"A ? ~ ? ~ ? ~ ? ~ ? ~\n" + 
					"B ~ ? ~ ? ~ ? ~ ? ~ ?\n" + 
					"C ? ~ ? ~ ? ~ ? ~ ? ~\n" + 
					"D ~ ? ~ ? ~ ? ~ ? ~ ?\n" + 
					"E ? ~ ? ~ ? ~ ? ~ ? ~\n" + 
					"F ~ ? ~ ? ~ ? ~ ? ~ ?\n" + 
					"G ? ~ ? ~ ? ~ ? ~ ? ~\n" + 
					"H ~ ? ~ ? ~ ? ~ ? ~ ?\n" + 
					"I ? ~ ? ~ ? ~ ? ~ ? ~\n" + 
					"J ~ ? ~ ? ~ ? ~ ? ~ ?\n",
					world.getEnemyField().toString());
		}catch(AssertionError e){
			assertEquals(
					"  0 1 2 3 4 5 6 7 8 9\n" + 
					"A ~ ? ~ ? ~ ? ~ ? ~ ?\n" + 
					"B ? ~ ? ~ ? ~ ? ~ ? ~\n" + 
					"C ~ ? ~ ? ~ ? ~ ? ~ ?\n" + 
					"D ? ~ ? ~ ? ~ ? ~ ? ~\n" + 
					"E ~ ? ~ ? ~ ? ~ ? ~ ?\n" + 
					"F ? ~ ? ~ ? ~ ? ~ ? ~\n" + 
					"G ~ ? ~ ? ~ ? ~ ? ~ ?\n" + 
					"H ? ~ ? ~ ? ~ ? ~ ? ~\n" + 
					"I ~ ? ~ ? ~ ? ~ ? ~ ?\n" + 
					"J ? ~ ? ~ ? ~ ? ~ ? ~\n",
					world.getEnemyField().toString());
		}
	}
			
}
