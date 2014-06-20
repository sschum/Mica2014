package de.tarent.mica.bot.strategy.action;

import static org.junit.Assert.*;

import org.junit.Test;

import de.tarent.mica.Action;
import de.tarent.mica.bot.strategy.action.RandomAttackStrategy;
import de.tarent.mica.model.World;

public class RandomAttackStrategyTest {
	RandomAttackStrategy toTest = new RandomAttackStrategy();
	
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
					"0 ? ~ ? ~ ? ~ ? ~ ? ~\n" + 
					"1 ~ ? ~ ? ~ ? ~ ? ~ ?\n" + 
					"2 ? ~ ? ~ ? ~ ? ~ ? ~\n" + 
					"3 ~ ? ~ ? ~ ? ~ ? ~ ?\n" + 
					"4 ? ~ ? ~ ? ~ ? ~ ? ~\n" + 
					"5 ~ ? ~ ? ~ ? ~ ? ~ ?\n" + 
					"6 ? ~ ? ~ ? ~ ? ~ ? ~\n" + 
					"7 ~ ? ~ ? ~ ? ~ ? ~ ?\n" + 
					"8 ? ~ ? ~ ? ~ ? ~ ? ~\n" + 
					"9 ~ ? ~ ? ~ ? ~ ? ~ ?\n",
					world.getEnemyField().toString());
		}catch(AssertionError e){
			assertEquals(
					"  0 1 2 3 4 5 6 7 8 9\n" + 
					"0 ~ ? ~ ? ~ ? ~ ? ~ ?\n" + 
					"1 ? ~ ? ~ ? ~ ? ~ ? ~\n" + 
					"2 ~ ? ~ ? ~ ? ~ ? ~ ?\n" + 
					"3 ? ~ ? ~ ? ~ ? ~ ? ~\n" + 
					"4 ~ ? ~ ? ~ ? ~ ? ~ ?\n" + 
					"5 ? ~ ? ~ ? ~ ? ~ ? ~\n" + 
					"6 ~ ? ~ ? ~ ? ~ ? ~ ?\n" + 
					"7 ? ~ ? ~ ? ~ ? ~ ? ~\n" + 
					"8 ~ ? ~ ? ~ ? ~ ? ~ ?\n" + 
					"9 ? ~ ? ~ ? ~ ? ~ ? ~\n",
					world.getEnemyField().toString());
		}
	}
			
}
