package de.tarent.mica.model.element;

import de.tarent.mica.model.Coord;
import de.tarent.mica.model.element.Ship.Orientation;

/**
 * Factory welches Schiffe baut ;)
 * 
 * @author rainu
 *
 */
public class ShipFactory {

	public static Ship build(Coord position, Orientation orientation, int size){
		Class<? extends Ship> finalType = getFinalType(size);
		if(finalType == null){
			throw new NullPointerException("There is no ship type with size = " + size + ". Maybe the class is not loaded yet!");
		}
		
		try {
			return (Ship)finalType.getConstructor(Orientation.class, Coord.class).newInstance(orientation, position);
		} catch (Exception e) {
			throw new IllegalStateException("This code shoul be never reached!", e);
		}
	}
	
	static Class<? extends Ship> getFinalType(int finalSize) {
		for(Class<? extends Ship> curType : Ship.getKnownShipTypes()){
			int curSize = Ship.getSizeOf(curType);
			if(curSize == finalSize){
				return curType;
			}
		}
		
		return null;
	}
}
