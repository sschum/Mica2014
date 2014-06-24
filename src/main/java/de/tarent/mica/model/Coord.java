package de.tarent.mica.model;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Diese Klasse representiert eine Coordinate eines Spielfeldes.
 * 
 * 
 * @author rainu
 *
 */
public class Coord implements Serializable {
	private static final long serialVersionUID = 5332121396721319701L;

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
	 * Beispielkoordinaten: D7(13, 7), A8(10, 8), F0 (15, 0), 0F (0, 15)
	 * 
	 * @param coord
	 */
	public Coord(String coord){
		this(coord.toUpperCase().charAt(0), coord.toUpperCase().charAt(1));
	}
	
	/**
	 * Koordinate als Buchstabenkennung
	 * @param y
	 * @param x
	 */
	public Coord(char y, char x){
		this.x = Integer.parseInt(String.valueOf(x), 16);
		this.y = Integer.parseInt(String.valueOf(y), 16);
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
	
	public char getXChar() {
		return Integer.toHexString(x).toUpperCase().charAt(0);
	}
	
	public char getYChar() {
		return Integer.toHexString(y).toUpperCase().charAt(0);
	}
	
	public Coord getNorthNeighbor(){
		return new Coord(getX(), getY() - 1);
	}
	
	public Coord getEastNeighbor(){
		return new Coord(getX() + 1, getY());
	}
	
	public Coord getSouthNeighbor(){
		return new Coord(getX(), getY() + 1);
	}
	
	public Coord getWestNeighbor(){
		return new Coord(getX() - 1, getY());
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
		return "" + getYChar() + "" + getXChar();
	}
}