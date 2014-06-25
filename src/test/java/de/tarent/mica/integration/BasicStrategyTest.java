package de.tarent.mica.integration;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import de.tarent.mica.Action;
import de.tarent.mica.Controller;
import de.tarent.mica.GameActionHandler;
import de.tarent.mica.Action.Type;
import de.tarent.mica.cli.GameActionHandlerFactoryCommand;
import de.tarent.mica.model.Coord;
import de.tarent.mica.model.Fleet;
import de.tarent.mica.model.GameStats;
import de.tarent.mica.model.World;
import de.tarent.mica.net.WebSocketController;

@Ignore("Nur für manuelle Benutzung gedacht, da der Server nicht autom. gestartet wird!")
public class BasicStrategyTest {
	final int intServerPort = 40000;
	final String intServer = "localhost";
	
	List<GameStats> player1Stats;
	List<GameStats> player2Stats;
	
	@Test
	public void test() throws URISyntaxException, InterruptedException {
		final Controller player1 = new WebSocketController(intServer, intServerPort);
		final Controller player2 = new WebSocketController(intServer, intServerPort);
		
		final GameActionHandler handler1 = new GameActionHandlerFactoryCommand().buildGameActionHandler();
		final GameActionHandler handler2 = new GameActionHandlerFactoryCommand().buildGameActionHandler();
		
		Thread player1Thread = new Thread(new Runnable() {
			@Override
			public void run() {
				player1Stats = player1.play("Player1", handler1);
			}
		});
		
		Thread player2Thread = new Thread(new Runnable() {
			@Override
			public void run() {
				player2Stats = player2.play("Player2", handler2);
			}
		});
		
		player1Thread.start();
		Thread.sleep(500);	//zwischen den platzierungen brauchen wir ein wenig zeit (warum weis ich - noch - nicht)
		player2Thread.start();
		
		player1Thread.join();
		player2Thread.join();
		
		assertTrue("Kein Spieler hat gewonnen oO", 
				player1Stats.get(0).isWon() || player2Stats.get(0).isWon());
	}
	
	class SequenceHandler implements GameActionHandler{
		private final Iterator<Action> actionIter;
		private final Fleet fleet;
		
		public SequenceHandler(Iterable<Action> actions, Fleet fleet) {
			this.actionIter = actions.iterator();
			this.fleet = fleet;
		}
		
		@Override
		public void handleRoundOver(boolean won) {
		}
		@Override
		public void handleEnemyAction(Action action) {
		}
		
		@Override
		public Action getNextAction(World world) {
			if(actionIter.hasNext()) return actionIter.next();
			
			return null;
		}
		@Override
		public Fleet getFleet() {
			return fleet;
		}
	}
	
	@Test
	public void moveSetTest() throws URISyntaxException, InterruptedException {
		final Controller player1 = new WebSocketController(intServer, intServerPort);
		final Controller player2 = new WebSocketController(intServer, intServerPort);
		
		List<Action> actions = Arrays.asList(
				new Action(Type.ATTACK, new Coord("34")),
				new Action(Type.ATTACK, new Coord("35")),
				new Action(Type.SPY_DRONE, new Coord("25")));
		
		final GameActionHandler handler1 = new SequenceHandler(actions, Fleet.defaultShips());
		final GameActionHandler handler2 = new SequenceHandler(actions, Fleet.defaultShips());
		
		Thread player1Thread = new Thread(new Runnable() {
			@Override
			public void run() {
				player1Stats = player1.play("Player1", handler1);
			}
		});
		
		Thread player2Thread = new Thread(new Runnable() {
			@Override
			public void run() {
				player2Stats = player2.play("Player2", handler2);
			}
		});
		
		player1Thread.start();
		Thread.sleep(500);	//zwischen den platzierungen brauchen wir ein wenig zeit (warum weis ich - noch - nicht)
		player2Thread.start();
		
		player1Thread.join();
		player2Thread.join();
		
		try {
			System.out.println(player1Stats.get(0).getWorld().getSpyAreas());
		} finally {
			System.out.println(player2Stats.get(0).getWorld().getSpyAreas());
		}
	}
}
