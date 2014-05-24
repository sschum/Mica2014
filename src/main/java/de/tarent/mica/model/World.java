package de.tarent.mica.model;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

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
	private Field enemyView;
	
	private Collection<AbstractShip> ownShips = new ArrayList<AbstractShip>();
	
	public World(int height, int width) {
		ownField = new Field(height, width, Element.WASSER);
		enemyField = new Field(height, width);
		enemyView = new Field(height, width);
	}
	
	public Field getOwnField() {
		return ownField;
	}

	public Field getEnemyField() {
		return enemyField;
	}
	
	public Field getEnemyView() {
		return enemyView;
	}
	
	/**
	 * Platziert ein Schiff auf das eigene Feld.
	 * @param ship
	 * @return
	 */
	public World placeOwnShip(AbstractShip ship){
		validateShipPosition(ship);
		
		for(Coord c : ship.getSpace()) ownField.set(c, Element.SCHIFF);

		ownShips.add(ship);
		
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
		enemyField.set(c, Element.TREFFER);
		
		return this;
	}
	
	/**
	 * Trägt ein Schiff in das Gegnerische Feld ein.
	 * 
	 * @param c
	 * @return
	 */
	public World registerShip(Coord c){
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
		checkOutOfBounce(enemyView, c);
		enemyField.set(c, Element.WASSER);
		
		return this;
	}
	
	/**
	 * Liefert Alle bekannten Positionen der Gegnerischen Schiffe.
	 * Ist keine Position bekannt, wird dennoch ein Set geliefert.
	 * Dieses ist dann jedoch leer!
	 * 
	 * @return
	 */
	public Set<Coord> getShipCoordinates(){
		return enemyField.getCoordinatesFor(Element.SCHIFF);
	}
	
	/**
	 * Trägt einen Treffer in das eigene Feld ein.
	 * Dieses Feld ist die Sicht des Gegners.
	 * 
	 * @param c
	 * @return
	 */
	public World registerEnemyHit(Coord c){
		checkOutOfBounce(enemyView, c);
		enemyView.set(c, Element.TREFFER);
		
		AbstractShip ship = getShip(c);
		ship.addAttackCoord(c);
		
		return this;
	}
	
	/**
	 * Trägt einen Treffer in das eigene Feld ein.
	 * Dieses Feld ist die Sicht des Gegners.
	 * 
	 * @param c
	 * @return
	 */
	public World registerEnemyBurn(Coord c){
		registerEnemyHit(c);
		
		AbstractShip ship = getShip(c);
		ship.setBurning(true);
		
		return this;
	}
	
	/**
	 * Trägt eine Verfehlung in das eigene Feld ein.
	 * Dieses Feld ist die Sicht des Gegners.
	 * 
	 * @param c
	 * @return
	 */
	public World registerEnemyMiss(Coord c){
		checkOutOfBounce(enemyView, c);
		enemyView.set(c, Element.WASSER);
		
		return this;
	}
	
	/**
	 * Trägt ein Schiff als versunken ein.
	 * Dieses Feld ist die Sicht des Gegners.
	 * 
	 * @param c
	 * @return
	 */
	public World registerEnemySunk(Coord c){
		AbstractShip ship = getShip(c);
		
		for(Coord cc : ship.getSpace()){
			registerEnemyHit(cc);
		}
		
		return this;
	}
	
	/**
	 * Trägt ein Schiff in das eigene Feld ein.
	 * Dieses Feld ist die Sicht des Gegners.
	 * 
	 * @param c
	 * @return
	 */
	public World registerEnemyShip(Coord c){
		checkOutOfBounce(enemyView, c);
		enemyView.set(c, Element.SCHIFF);
		
		return this;
	}
	
	/**
	 * Liefert alle Spielerschiffe. Diese {@link Collection} darf und kann
	 * nicht verändert werden!
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<AbstractShip> getOwnShips(){
		return Collections.unmodifiableCollection((Collection<AbstractShip>) ownField);
	}
	
	/**
	 * Liefert das eigene Schiff, was an der gegebenen Position stationiert ist.
	 * 
	 * @param coord
	 * @return Das eigene Schiff an der gegebenen Koordinate. Null wenn kein Schiff an dieser Koordinate vorhanden ist.
	 */
	public AbstractShip getShip(Coord coord){
		for(AbstractShip ship : ownShips){
			if(ship.getSpace().contains(coord)){
				return ship;
			}
		}
		
		return null;
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
