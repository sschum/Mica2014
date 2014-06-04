package de.tarent.mica.cli;

import java.io.IOException;
import java.net.URISyntaxException;

import de.tarent.mica.Controller;
import de.tarent.mica.GameActionHandler;
import de.tarent.mica.bot.GameMaster;
import de.tarent.mica.net.WebSocketController;
import asg.cliche.Command;
import asg.cliche.Param;
import asg.cliche.Shell;
import asg.cliche.ShellFactory;

public class Commands {

	private Shell shell;
	public void setShell(Shell shell) {
		this.shell = shell;
	}
	
	@Command(abbrev = "sg", description="Startet ein Spielchen...")
	public String startGame(
			@Param(name = "host") String host,
			@Param(name = "port") int port) throws IOException{
		
		return startGame(host, port, "Rainu");
	}
	
	@Command(abbrev = "sg", description="Startet ein Spielchen...")
	public String startGame(
			@Param(name = "host") String host,
			@Param(name = "port") int port,
			@Param(name = "playerName") String playerName) throws IOException {
		
		GameActionHandlerFactoryCommand cmd = new GameActionHandlerFactoryCommand();
		Shell subShell = ShellFactory.createSubshell("game-master", shell, "game-master-factory", cmd);
		subShell.commandLoop();
		
		//nachdem die sub-shell verlassen wurde, bekomme ich wieder die kotrolle
		GameActionHandler handler = cmd.buildGameActionHandler();
		
		Controller gameController = buildController(host, port);
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
