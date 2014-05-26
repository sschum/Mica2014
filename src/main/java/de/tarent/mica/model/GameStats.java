package de.tarent.mica.model;

/**
 * JavaBean, die die allgemeinen Spiele-Stats beinhaltet.
 * 
 * @author rainu
 *
 */
public class GameStats {
	private World world;
	private boolean won;
	private String playerName;
	private String enemyName;
	
	public GameStats() {
	}
	
	public GameStats(World world, boolean won, String playerName,
			String enemyName) {
		this.world = world;
		this.won = won;
		this.playerName = playerName;
		this.enemyName = enemyName;
	}

	public World getWorld() {
		return world;
	}
	public void setWorld(World world) {
		this.world = world;
	}
	public boolean isWon() {
		return won;
	}
	public void setWon(boolean won) {
		this.won = won;
	}
	public String getPlayerName() {
		return playerName;
	}
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	public String getEnemyName() {
		return enemyName;
	}
	public void setEnemyName(String enemyName) {
		this.enemyName = enemyName;
	}
}