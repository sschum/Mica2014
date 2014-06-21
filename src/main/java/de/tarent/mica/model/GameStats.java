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
	private int playerMoves;
	private int enemyMoves;
	
	public GameStats() {
	}

	public GameStats(World world, boolean won, String playerName,
			String enemyName, int playerMoves, int enemyMoves) {
		this.world = world;
		this.won = won;
		this.playerName = playerName;
		this.enemyName = enemyName;
		this.playerMoves = playerMoves;
		this.enemyMoves = enemyMoves;
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
	public int getPlayerMoves() {
		return playerMoves;
	}
	public void setPlayerMoves(int playerMoves) {
		this.playerMoves = playerMoves;
	}
	public int getEnemyMoves() {
		return enemyMoves;
	}
	public void setEnemyMoves(int enemyMoves) {
		this.enemyMoves = enemyMoves;
	}
}