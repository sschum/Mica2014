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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((carrier1 == null) ? 0 : carrier1.hashCode());
		result = prime * result
				+ ((carrier2 == null) ? 0 : carrier2.hashCode());
		result = prime * result
				+ ((cruiser1 == null) ? 0 : cruiser1.hashCode());
		result = prime * result
				+ ((cruiser2 == null) ? 0 : cruiser2.hashCode());
		result = prime * result
				+ ((destroyer1 == null) ? 0 : destroyer1.hashCode());
		result = prime * result
				+ ((destroyer2 == null) ? 0 : destroyer2.hashCode());
		result = prime * result
				+ ((submarine1 == null) ? 0 : submarine1.hashCode());
		result = prime * result
				+ ((submarine2 == null) ? 0 : submarine2.hashCode());
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
		Fleet other = (Fleet) obj;
		if (carrier1 == null) {
			if (other.carrier1 != null)
				return false;
		} else if (!carrier1.equals(other.carrier1))
			return false;
		if (carrier2 == null) {
			if (other.carrier2 != null)
				return false;
		} else if (!carrier2.equals(other.carrier2))
			return false;
		if (cruiser1 == null) {
			if (other.cruiser1 != null)
				return false;
		} else if (!cruiser1.equals(other.cruiser1))
			return false;
		if (cruiser2 == null) {
			if (other.cruiser2 != null)
				return false;
		} else if (!cruiser2.equals(other.cruiser2))
			return false;
		if (destroyer1 == null) {
			if (other.destroyer1 != null)
				return false;
		} else if (!destroyer1.equals(other.destroyer1))
			return false;
		if (destroyer2 == null) {
			if (other.destroyer2 != null)
				return false;
		} else if (!destroyer2.equals(other.destroyer2))
			return false;
		if (submarine1 == null) {
			if (other.submarine1 != null)
				return false;
		} else if (!submarine1.equals(other.submarine1))
			return false;
		if (submarine2 == null) {
			if (other.submarine2 != null)
				return false;
		} else if (!submarine2.equals(other.submarine2))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Fleet [carrier1=" + carrier1 + ", carrier2=" + carrier2
				+ ", cruiser1=" + cruiser1 + ", cruiser2=" + cruiser2
				+ ", destroyer1=" + destroyer1 + ", destroyer2=" + destroyer2
				+ ", submarine1=" + submarine1 + ", submarine2=" + submarine2
				+ "]";
	}
}