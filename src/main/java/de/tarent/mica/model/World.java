package de.tarent.mica.model;

import java.awt.Dimension;

import de.tarent.mica.model.Field.Element;
import de.tarent.mica.model.ship.AbstractShip;

/**
 * Diese Klasse representiert die Spielwelt.
 * 
 * @author rainu
 *
 */
public class World {

	private Field ownField;
	private Field enemyField;
	
	public World(int height, int width) {
		ownField = new Field(height, width);
		enemyField = new Field(height, width);
	}
	
	public Field getOwnField() {
		return ownField;
	}

	public Field getEnemyField() {
		return enemyField;
	}
	
	/**
	 * Platziert ein Schiff auf das eigene Feld.
	 * @param ship
	 * @return
	 */
	public World placeOwnShip(AbstractShip ship){
		validateShipPosition(ship);
		
		for(Coord c : ship.getSpace()) ownField.set(c, Element.SCHIFF);

		return this;
	}
	
	void validateShipPosition(AbstractShip ship) {
		for(Coord c : ship.getSpace()){
			try{
				checkOutOfBounce(ownField, c);
			}catch(IllegalArgumentException e){
				throw new IllegalArgumentException("The given Ship(" + ship + ") is out of bounce!", e);
			}
			
			Element e = ownField.get(c);
			if(Element.SCHIFF.equals(e)){
				throw new IllegalArgumentException("This ship crosses another ship!");
			}
		}
	}

	void checkOutOfBounce(Field field, Coord c) {
		Dimension dim = field.getDimension();
		
		if(c.getX() < 0 || c.getX() >= dim.width || c.getY() < 0 || c.getX() >= dim.height){
			throw new IllegalArgumentException("The given Coord(" + c + ") is out of bounce!");
		}
	}
	
	/**
	 * Trägt einen Treffer in das Gegnerische Feld ein.
	 * 
	 * @param c
	 * @return
	 */
	public World registerHit(Coord c){
		checkOutOfBounce(enemyField, c);
		enemyField.set(c, Element.SCHIFF);
		
		return this;
	}
	
	/**
	 * Trägt eine Verfehlung in das Gegnerische Feld ein.
	 * 
	 * @param c
	 * @return
	 */
	public World registerMiss(Coord c){
		checkOutOfBounce(enemyField, c);
		enemyField.set(c, Element.WASSER);
		
		return this;
	}

	public Dimension getWorldDimension() {
		return ownField.getDimension();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append(enemyField.toString());
		sb.append("\no)=");
		for(int i=0; i < getWorldDimension().getWidth(); i++){
			sb.append("==");
		}
		sb.append("=(o\n\n");
		sb.append(ownField.toString());
		
		return sb.toString();
	}
}
