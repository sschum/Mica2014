package de.tarent.mica.model;

import de.tarent.mica.model.element.Carrier;
import de.tarent.mica.model.element.Cruiser;
import de.tarent.mica.model.element.Destroyer;
import de.tarent.mica.model.element.Submarine;

/**
 * JavaBean für eine Schiffsflotte.
 * 
 * @author rainu
 *
 */
public class Fleet {
	private Carrier carrier1;
	private Carrier carrier2;
	private Cruiser cruiser1;
	private Cruiser cruiser2;
	private Destroyer destroyer1;
	private Destroyer destroyer2;
	private Submarine submarine1;
	private Submarine submarine2;
	
	public Fleet(Carrier carrier1, Carrier carrier2, Cruiser cruiser1,
			Cruiser cruiser2, Destroyer destroyer1, Destroyer destroyer2,
			Submarine submarine1, Submarine submarine2) {
		this.carrier1 = carrier1;
		this.carrier2 = carrier2;
		this.cruiser1 = cruiser1;
		this.cruiser2 = cruiser2;
		this.destroyer1 = destroyer1;
		this.destroyer2 = destroyer2;
		this.submarine1 = submarine1;
		this.submarine2 = submarine2;
	}
	
	public Fleet(){
		
	}
	
	public Carrier getCarrier1() {
		return carrier1;
	}
	public void setCarrier1(Carrier carrier1) {
		this.carrier1 = carrier1;
	}
	public Carrier getCarrier2() {
		return carrier2;
	}
	public void setCarrier2(Carrier carrier2) {
		this.carrier2 = carrier2;
	}
	public Cruiser getCruiser1() {
		return cruiser1;
	}
	public void setCruiser1(Cruiser cruiser1) {
		this.cruiser1 = cruiser1;
	}
	public Cruiser getCruiser2() {
		return cruiser2;
	}
	public void setCruiser2(Cruiser cruiser2) {
		this.cruiser2 = cruiser2;
	}
	public Destroyer getDestroyer1() {
		return destroyer1;
	}
	public void setDestroyer1(Destroyer destroyer1) {
		this.destroyer1 = destroyer1;
	}
	public Destroyer getDestroyer2() {
		return destroyer2;
	}
	public void setDestroyer2(Destroyer destroyer2) {
		this.destroyer2 = destroyer2;
	}
	public Submarine getSubmarine1() {
		return submarine1;
	}
	public void setSubmarine1(Submarine submarine1) {
		this.submarine1 = submarine1;
	}
	public Submarine getSubmarine2() {
		return submarine2;
	}
	public void setSubmarine2(Submarine submarine2) {
		this.submarine2 = submarine2;
	}
}