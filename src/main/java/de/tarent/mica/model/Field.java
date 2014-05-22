package de.tarent.mica.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Field {
	public static enum Element{	//TODO: Besseren Namen dafür finde ;)
		UNBEKANNT('?'),
		SCHIFF('*'),
		WASSER('~');
		
		private char sign;
		
		private Element(char sign){
			this.sign = sign;
		}
		
		public char getSign() {
			return sign;
		}
	}
	
	private Element[][] area;
	
	/*
	 * Only for tests
	 */
	Field(Element[][] elements) {
		this.area = elements;
	}

	public Field(int heigth, int width) {
		this.area = new Element[heigth][width];
		
		for(int i=0; i < heigth; i++){
			Arrays.fill(area[i], Element.UNBEKANNT);
		}
	}
	
	/**
	 * Liefert das {@link Element} an der angegebenen {@link Coord}inate.
	 * 
	 * @param c
	 * @return
	 */
	public Element get(Coord c){
		return area[c.getY()][c.getX()];
	}
	
	/**
	 * Setzt das {@link Element} an die angegebene {@link Coord}inate.
	 * 
	 * @param c
	 * @param e
	 * @return
	 */
	public Field set(Coord c, Element e){
		area[c.getY()][c.getX()] = e;
		
		return this;
	}
	
	/**
	 * Liefert alle Coordinaten des übergebenen Elementes.
	 * 
	 * @param e
	 * @return
	 */
	public Set<Coord> getCoordinatesFor(Element e){
		Set<Coord> result = new HashSet<Coord>();
		
		for(int y=0; y < area.length; y++){
			for(int x=0; x < area[y].length; x++){
				final Coord c = new Coord(x, y);
				
				if(e.equals(get(c))){
					result.add(c);
				}
			}
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append(" ");
		for(int i=0; i < area[0].length; i++){	//TODO: prüfen, ob eine zeile vorhanden...
			sb.append(" ");
			sb.append(i);
		}
		
		sb.append("\n");
		
		for(int i=0; i < area.length; i++){
			sb.append((char)(65 + i));	//A -> 65
			for(int j=0; j < area[i].length; j++){
				sb.append(" ");
				sb.append(area[i][j].getSign());
			}
			sb.append("\n");
		}
		
		return sb.toString();
	}
}
