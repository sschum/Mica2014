package de.tarent.mica.cli;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import de.tarent.mica.Controller;
import de.tarent.mica.GameActionHandler;
import de.tarent.mica.model.GameStats;
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
		cmd.setShell(subShell);
		subShell.commandLoop();
		
		//nachdem die sub-shell verlassen wurde, bekomme ich wieder die kotrolle
		GameActionHandler handler = cmd.buildGameActionHandler();
		
		Controller gameController = buildController(host, port);
		List<GameStats> stats = gameController.play(playerName, handler);
		
		int playerWin = 0;
		
		StringBuffer sb = new StringBuffer();
		sb.append("===================\n");
		sb.append("=   GAME OVER     =\n");
		sb.append("===================\n");
		for(GameStats stat : stats){
			sb.append(stat.getPlayerName());
			sb.append("(");
			sb.append(stat.getPlayerMoves());
			sb.append(")");
			sb.append(stat.isWon() ? " - Winner -" : " - Looser -");
			sb.append(" VS ");
			sb.append(stat.getEnemyName());
			sb.append("(");
			sb.append(stat.getEnemyMoves());
			sb.append(")");
			sb.append(!stat.isWon() ? " - Winner -" : " - Looser -");
			sb.append("\n\n");
			sb.append(stat.getWorld());
			sb.append("\n\n");
			
			if(stat.isWon()) playerWin++;
		}
		sb.append("Final score: Player ");
		sb.append(playerWin);
		sb.append(" Enemy ");
		sb.append(stats.size() - playerWin);
		sb.append("\n\n");

		return sb.toString();
	}

	private WebSocketController buildController(String host, int port) {
		try{
			return new WebSocketController(host, port);
		}catch(URISyntaxException e){
			throw new RuntimeException(e);
		}
	}
	
}
