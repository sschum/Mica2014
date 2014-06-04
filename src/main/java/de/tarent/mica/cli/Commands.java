package de.tarent.mica.cli;

import java.net.URISyntaxException;

import de.tarent.mica.Controller;
import de.tarent.mica.GameActionHandler;
import de.tarent.mica.bot.GameMaster;
import de.tarent.mica.net.WebSocketController;
import asg.cliche.Command;
import asg.cliche.Param;

public class Commands {

	@Command(abbrev = "sg", description="Startet ein Spielchen...")
	public String startGame(
			@Param(name = "host") String host,
			@Param(name = "port") int port){
		
		return startGame(host, port, "Rainu");
	}
	
	@Command(abbrev = "sg", description="Startet ein Spielchen...")
	public String startGame(
			@Param(name = "host") String host,
			@Param(name = "port") int port,
			@Param(name = "playerName") String playerName){
		
		Controller gameController = buildController(host, port);
		GameActionHandler handler = new GameMaster();
		
		gameController.play(playerName, handler);
		
		return null;
	}

	private WebSocketController buildController(String host, int port) {
		try{
			return new WebSocketController(host, port);
		}catch(URISyntaxException e){
			throw new RuntimeException(e);
		}
	}
}
