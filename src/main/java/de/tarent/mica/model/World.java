package de.tarent.mica.model;

import java.awt.Dimension;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tarent.mica.model.Field.Element;
import de.tarent.mica.model.element.Ship;
import de.tarent.mica.model.element.SpyArea;
import de.tarent.mica.model.element.UnknownShip;
import de.tarent.mica.util.Output;

/**
 * Diese Klasse representiert die Spielwelt.
 * 
 * @author rainu
 *
 */
public class World implements Serializable {
	private static final long serialVersionUID = 5109408065862674738L;
	
	private Field ownField;
	private Field enemyField;
	private Field enemyView;
	
	private Set<Ship> ownShips = new HashSet<Ship>();
	private Set<SpyArea> ownSpies = new HashSet<SpyArea>();
	
	private Set<Ship> enemyShips = new HashSet<Ship>();
	
	private Map<Class<? extends Ship>, Integer> specialAttackCounts = new HashMap<Class<? extends Ship>, Integer>();
	private Map<Class<? extends Ship>, Integer> enemySpecialAttackCounts = new HashMap<Class<? extends Ship>, Integer>();
	
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
	 * Prüft ob die angegebene {@link Coord}inate inerhalb der Welt ist.
	 * 
	 * @param c Zu prüfende {@link Coord}inate
	 * @return Liefert true wenn die Koordinate inherhalb der Welt ist. Andernfals false!
	 */
	public boolean isInWorld(Coord c){
		final Dimension dim = getWorldDimension();
		
		return !(c.getX() < 0 || c.getY() < 0 ||
			 	 c.getX() >= dim.width ||
				 c.getY() >= dim.height);
	}
	
	/**
	 * Platziert ein Schiff auf das eigene Feld.
	 * @param ship
	 * @return
	 */
	public World placeOwnShip(Ship ship){
		validateShipPosition(ship);
		
		for(Coord c : ship.getSpace()) ownField.set(c, Element.SCHIFF);

		ownShips.add(ship);
		
		return this;
	}
	
	void validateShipPosition(Ship ship) {
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
		
		if(c.getX() < 0 || c.getX() >= dim.width || c.getY() < 0 || c.getY() >= dim.height){
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
		
		boolean found = false;
		for(Ship ship : enemyShips){
			if(isNeighbor(c, ship.getSpace())){
				ship.addAttackCoord(c);
				found = true;
				break;
			}
		}
		
		//kein schiff gefunden... neues anlegen
		if(!found) {
			enemyShips.add(new UnknownShip(c));
		}
				
		//es kann passieren, dass jetzt aus zwei schiffen eines wird...
		/*  0 1 2 3
		 * 0  * <= erster hit
		 * 1  * <= dritter hit
		 * 2  * <= zweiter hit
		 * 3
		 * 
		 * Die ersten zwei treffer lassen vermuten, dass es zwei schiffe sind,
		 * erst der dritte treffer offenbart, dass es EIN schiff ist.
		 */
		List<Ship> toRemove = new ArrayList<Ship>();
		
		List<Ship> enemyShips = new ArrayList<Ship>(this.enemyShips);
		mainLoop: for(int i=0; i < enemyShips.size(); i++){
			Ship ship = enemyShips.get(i);
			
			for(int j=0; j < enemyShips.size(); j++){
				if(j == i) continue;
				
				Ship oShip = enemyShips.get(j);
				Coord first = ship.getSpace().get(0);
				Coord last = ship.getSpace().get(ship.getSpace().size() - 1);
				
				List<Coord> oShipCoords = oShip.getSpace();
				if(	oShipCoords.contains(first) ||
					oShipCoords.contains(first.getNorthNeighbor()) ||
					oShipCoords.contains(first.getEastNeighbor()) ||
					oShipCoords.contains(first.getSouthNeighbor()) ||
					oShipCoords.contains(first.getWestNeighbor()) ||
					oShipCoords.contains(last) ||
					oShipCoords.contains(last.getNorthNeighbor()) ||
					oShipCoords.contains(last.getEastNeighbor()) ||
					oShipCoords.contains(last.getSouthNeighbor()) ||
					oShipCoords.contains(last.getWestNeighbor())){
					
					//aus zwei wird eines...
					Ship newShip = new UnknownShip();
					for(Coord sc : enemyShips.get(i).getSpace()){
						newShip.addAttackCoord(sc);
					}
					for(Coord sc : enemyShips.get(j).getSpace()){
						newShip.addAttackCoord(sc);
					}

					toRemove.add(enemyShips.get(i));
					toRemove.add(enemyShips.get(j));
					enemyShips.add(newShip);
					
					break mainLoop;
				}
			}
		}
		
		if(!toRemove.isEmpty()){
			Iterator<Ship> iter = enemyShips.iterator();
			while(iter.hasNext()){
				Ship ship = iter.next();
				
				for(Ship rm : toRemove){
					if(ship.equals(rm)){
						iter.remove();
						break;
					}
				}
			}
			
			this.enemyShips.clear();
			this.enemyShips.addAll(enemyShips);
		}
		
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
			
			if(	coord.getNorthNeighbor().equals(toTest) ||
				coord.getEastNeighbor().equals(toTest) ||
				coord.getSouthNeighbor().equals(toTest) ||
				coord.getWestNeighbor().equals(toTest) ){

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
		//fragt mich nicht, warum man erst eine kopie machen muss
		//... der EnemyShips-Iterator löscht den Eintrag nicht O.o
		List<Ship> copyOfEnemyShips = new ArrayList<Ship>(enemyShips);
		Iterator<Ship> iter = copyOfEnemyShips.iterator();
		while(iter.hasNext()){
			Ship ship = iter.next();
			if(ship instanceof UnknownShip && ship.getSpace().contains(c)){
				Ship transformed = UnknownShip.transformShip((UnknownShip)ship, true);
				
				if(transformed == null){
					throw new IllegalStateException("This code should be never reached!");
				}

				iter.remove();
				enemyShips = new HashSet<Ship>(copyOfEnemyShips);
				enemyShips.add(transformed);
				break;
			}
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
	 * Trägt "Nichts" in das Gegnerische Feld ein. "Nichts" bedeutet,
	 * dass man sich sicher ist, dass an dieser Stelle kein Schiff o.ä.
	 * existiert!
	 * 
	 * @param c
	 * @return
	 */
	public World registerNothing(Coord c) {
		checkOutOfBounce(enemyField, c);
		enemyField.set(c, Element.WASSER);
		
		return this;
	}
	
	/**
	 * Registriert eine Benutzung einer Spzialattake. Jedes Schiff hat eine solche!
	 * 
	 * @param shipType Um welche Attacke handelt es sich?
	 * @return
	 */
	public World increaseSpecialAttack(Class<? extends Ship> shipType){
		if(!specialAttackCounts.containsKey(shipType)){
			specialAttackCounts.put(shipType, 0);
		}
		
		specialAttackCounts.put(shipType, specialAttackCounts.get(shipType) + 1);
		return this;
	}
	
	/**
	 * DeRegistriert eine Benutzung einer Spzialattake. Jedes Schiff hat eine solche!
	 * 
	 * @param shipType Um welche Attacke handelt es sich?
	 * @return
	 */
	public World decreaseSpecialAttack(Class<? extends Ship> shipType){
		if(!specialAttackCounts.containsKey(shipType)){
			specialAttackCounts.put(shipType, 0);
		}else{
			specialAttackCounts.put(shipType, specialAttackCounts.get(shipType) - 1);
		}

		return this;
	}
	
	/**
	 * Liefert die Anzahl der Spezialattacken, die der Spieler bereits genutzt hat.
	 * 
	 * @param shipType Welche Spezailattacke?
	 * @return
	 */
	public int getSpecialAttackCount(Class<? extends Ship> shipType){
		if(!specialAttackCounts.containsKey(shipType)){
			return 0;
		}
		
		return specialAttackCounts.get(shipType);
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
		
		Ship ship = getShip(c);
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
		
		Ship ship = getShip(c);
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
		Ship ship = getShip(c);
		
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
	 * Registriert eine Benutzung einer Spzialattake des Feindes. 
	 * Jedes Schiff hat eine solche!
	 * 
	 * @param shipType Um welche Attacke handelt es sich?
	 * @return
	 */
	public World registerEnemySpecialAttack(Class<? extends Ship> shipType){
		if(!enemySpecialAttackCounts.containsKey(shipType)){
			enemySpecialAttackCounts.put(shipType, 0);
		}
		
		enemySpecialAttackCounts.put(shipType, enemySpecialAttackCounts.get(shipType) + 1);
		return this;
	}
	
	/**
	 * Liefert die Anzahl der Spezialattacken, die der Gegner bereits genutzt hat.
	 * 
	 * @param shipType Welche Spezailattacke?
	 * @return
	 */
	public int getEnemySpecialAttackCount(Class<? extends Ship> shipType){
		if(!enemySpecialAttackCounts.containsKey(shipType)){
			return 0;
		}
		
		return enemySpecialAttackCounts.get(shipType);
	}
	
	/**
	 * Liefert alle Spielerschiffe. Diese {@link Set} darf und kann
	 * nicht verändert werden!
	 * 
	 * @return
	 */
	public Set<Ship> getOwnShips(){
		return Collections.unmodifiableSet(ownShips);
	}

	/**
	 * Liefert alle generische Schiffe. Sind diese Schiffe noch nicht
	 * gesunken, sind es {@link UnknownShip}s. Andernfals die entsprechende
	 * Kindklasse.
	 * 
	 * @return
	 */
	public Set<Ship> getEnemyShips(){
		return Collections.unmodifiableSet(enemyShips);
	}
	
	/**
	 * Liefert das eigene Schiff, was an der gegebenen Position stationiert ist.
	 * 
	 * @param coord
	 * @return Das eigene Schiff an der gegebenen Koordinate. Null wenn kein Schiff an dieser Koordinate vorhanden ist.
	 */
	public Ship getShip(Coord coord){
		for(Ship ship : ownShips){
			if(ship.getSpace().contains(coord)){
				return ship;
			}
		}
		
		return null;
	}
	
	/**
	 * Liefert das gegnerische Schiff, was an der gegebenen Position stationiert ist.
	 * 
	 * @param coord
	 * @return Das eigene Schiff an der gegebenen Koordinate. Null wenn kein Schiff an dieser Koordinate vorhanden ist.
	 */
	public Ship getEnemyShip(Coord coord){
		for(Ship ship : enemyShips){
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
		return Output.makeSideBySide(" | ", 
				"Player-Field:\n" + ownField,
				"Enemy-Field:\n" + enemyField,
				"Enemy-View:\n" + enemyView);
	}
}
