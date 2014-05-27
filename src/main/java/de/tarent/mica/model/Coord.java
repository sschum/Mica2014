package de.tarent.mica.model;

import java.util.Comparator;

/**
 * Diese Klasse representiert eine Coordinate eines Spielfeldes.
 * 
 * 
 * @author rainu
 *
 */
public class Coord{
	public static Comparator<Coord> COMPARATOR = new Comparator<Coord>() {
		@Override
		public int compare(Coord o1, Coord o2) {
			int compareOfLetter = Character.compare(o1.getYChar(), o2.getYChar());
			if(compareOfLetter != 0) return compareOfLetter;
			
			return Integer.compare(o1.getX(), o2.getX());
		}
	};
	
	private final int x;
	private final int y;
	
	/**
	 * Beispielkoordinaten: D7, A8, J0
	 * 
	 * @param coord
	 */
	public Coord(String coord){
		this(coord.toUpperCase().charAt(0), Integer.parseInt(coord.substring(1)));
	}
	
	/**
	 * Koordinate als Buchstabenkennung
	 * @param y
	 * @param x
	 */
	public Coord(char y, int x){
		this.x = x;
		this.y = (int)Character.toUpperCase(y) - 65;
	}
	
	public Coord(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public char getYChar() {
		return (char)(y + 65);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coord other = (Coord) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	@Override
	public String toString() {
		//D7
		return "" + ((char)(65 + y)) + x;
	}
}