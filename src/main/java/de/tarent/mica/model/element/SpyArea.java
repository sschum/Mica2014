package de.tarent.mica.model.element;

import java.io.Serializable;

import de.tarent.mica.model.Coord;

/**
 * Diese Klasse representiert ein Spionagebereich.
 * 
 * @author rainu
 *
 */
public class SpyArea implements Serializable {
	private static final long serialVersionUID = -3606935546394004896L;
	
	private Coord coord;
	private int segments = -1;
	
	public SpyArea(Coord coord, int segments) {
		this.coord = coord;
		this.segments = segments;
	}
	
	public SpyArea(Coord coord) {
		this.coord = coord;
	}

	public Coord getCoord() {
		return coord;
	}
	public int getSegments() {
		return segments;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coord == null) ? 0 : coord.hashCode());
		result = prime * result + segments;
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
		SpyArea other = (SpyArea) obj;
		if (coord == null) {
			if (other.coord != null)
				return false;
		} else if (!coord.equals(other.coord))
			return false;
		if (segments != other.segments)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SpyArea@" + coord + "(" + segments + ")";
	}
}
