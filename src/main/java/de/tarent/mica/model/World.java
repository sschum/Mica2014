package de.tarent.mica.model;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.tarent.mica.model.Field.Element;
import de.tarent.mica.model.element.AbstractShip;
import de.tarent.mica.model.element.AbstractShip.Orientation;
import de.tarent.mica.model.element.ShipFactory;
import de.tarent.mica.model.element.SpyArea;
import de.tarent.mica.model.element.UnknownShip;

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
	
	private Set<AbstractShip> ownShips = new HashSet<AbstractShip>();
	private Set<SpyArea> ownSpies = new HashSet<SpyArea>();
	
	private Set<AbstractShip> enemyShips = new HashSet<AbstractShip>();
	
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
	public synchronized World placeOwnShip(AbstractShip ship){
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
		
		for(AbstractShip ship : enemyShips){
			if(isNeighbor(c, ship.getSpace())){
				ship.addAttackCoord(c);
				return this;
			}
		}
		
		//kein schiff gefunden... neues anlegen
		enemyShips.add(new UnknownShip(c));
				
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
	 * Liefert alle bisherigen Treffer als Cluster zurück.
	 * Diese Cluster sind Schiffe, die noch nicht(!) gesunken
	 * sind!
	 * 
	 * @return
	 */
	public Set<Set<Coord>> getPotentialShips(){
		List<Coord> allCoords = new ArrayList<Coord>(enemyField.getCoordinatesFor(Element.TREFFER));
		Collections.sort(allCoords, Coord.COMPARATOR);
		
		Iterator<Coord> iter = allCoords.iterator();
		Set<Set<Coord>> result = new HashSet<Set<Coord>>();

		for(;iter.hasNext(); iter.remove()){
			Coord next = iter.next();
			
			boolean added = false;
			for(Set<Coord> curCluster : result){
				if(isNeighbor(next, curCluster)){
					curCluster.add(next);
					added = true;
					break;
				}
			}
			if(added) continue;
			
			Set<Coord> newCluser = new HashSet<Coord>();
			result.add(newCluser);
			
			newCluser.add(next);
		}
		
		return result;
	}
	
	private boolean isNeighbor(Coord toTest, Collection<Coord> cluster) {
		for(Coord coord : cluster){
			/*
			 *   0 1 2
			 * A   +
			 * B + c +
			 * C   +
			 */
			
			if(	new Coord(coord.getX() + 1, coord.getY()).equals(toTest) ||
				new Coord(coord.getX(), coord.getY() + 1).equals(toTest) ||
				new Coord(coord.getX() - 1, coord.getY()).equals(toTest) ||
				new Coord(coord.getX(), coord.getY() - 1).equals(toTest) ){

				return true;
			}
		}
		return false;
	}
	
	/**
	 * Registriert ein Schiff als gesunken. Alle Schiffstreffer müssen
	 * zuvor registriert wurden sein, da sonst von dem falsche Schiffstyp
	 * ausgegangen wird!
	 * 
	 * @param c
	 */
	public void registerSunk(Coord c){
		AbstractShip transformed = null;
		Iterator<AbstractShip> iter = enemyShips.iterator();
		while(iter.hasNext()){
			AbstractShip ship = iter.next();
			if(ship instanceof UnknownShip){
				transformed = transformShip((UnknownShip)ship);
				
				if(transformed == null){
					throw new IllegalStateException("This code should be never reached!");
				}
				
				//neues durch altes ersetzen...
				iter.remove();
				enemyShips.add(transformed);
				break;
			}
		}
	}

	AbstractShip transformShip(UnknownShip ship) {
		Orientation orientation = getOrientation(ship);
		int finalSize = ship.getSpace().size();
		
		return ShipFactory.build(ship.getPosition(), orientation, finalSize);
	}

	Orientation getOrientation(UnknownShip ship) {
		List<Coord> coords = ship.getSpace();
		Coord c1 = coords.get(0);
		Coord c2 = coords.get(1);
		
		if(new Coord(c1.getX() + 1, c1.getY()).equals(c2)){
			return Orientation.OST;
		}else if(new Coord(c1.getX(), c1.getY() + 1).equals(c2)){
			return Orientation.SUED;
		}else if(new Coord(c1.getX() - 1, c1.getY()).equals(c2)){
			return Orientation.WEST;
		}else if(new Coord(c1.getX(), c1.getY() - 1).equals(c2)){
			return Orientation.NORD;
		}else {
			return Orientation.UNBEKANNT;
		}
	}

	/**
	 * Trägt ein Spionagebereich in das Gegnerische Feld ein.
	 * 
	 * @param spy
	 * @return
	 */
	public World registerSpy(SpyArea spy){
		checkOutOfBounce(enemyView, spy.getCoord());
		enemyField.set(spy.getCoord(), Element.SPIONAGE);
		
		ownSpies.add(spy);
		
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
	 * Liefert alle Spionagebereiche des Spielers.
	 * 
	 * @return
	 */
	public Set<SpyArea> getSpyAreas(){
		return Collections.unmodifiableSet(ownSpies);
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
	 * Trägt ein Spionagebereich in das eigene Feld ein.
	 * Dieses Feld ist die Sicht des Gegners.
	 * 
	 * @param spy
	 * @return
	 */
	public World registerEnemySpy(SpyArea spyArea) {
		checkOutOfBounce(enemyView, spyArea.getCoord());
		enemyView.set(spyArea.getCoord(), Element.SPIONAGE);
		
		return this;
	}
	
	/**
	 * Liefert alle Spielerschiffe. Diese {@link Set} darf und kann
	 * nicht verändert werden!
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<AbstractShip> getOwnShips(){
		return Collections.unmodifiableSet((Set<AbstractShip>) ownField);
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
