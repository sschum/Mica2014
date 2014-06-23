package de.tarent.mica.integration;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import de.tarent.mica.Controller;
import de.tarent.mica.GameActionHandler;
import de.tarent.mica.cli.GameActionHandlerFactoryCommand;
import de.tarent.mica.model.GameStats;
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
}
