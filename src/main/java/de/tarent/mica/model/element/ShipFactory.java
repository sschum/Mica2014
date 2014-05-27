package de.tarent.mica.model.element;

import de.tarent.mica.model.Coord;
import de.tarent.mica.model.element.AbstractShip.Orientation;

/**
 * Factory welches Schiffe baut ;)
 * 
 * @author rainu
 *
 */
public class ShipFactory {

	public static AbstractShip build(Coord position, Orientation orientation, int size){
		Class<? extends AbstractShip> finalType = getFinalType(size);
		if(finalType == null){
			throw new NullPointerException("There is no ship type with size = " + size + ". Maybe the class is not loaded yet!");
		}
		
		try {
			return (AbstractShip)finalType.getConstructor(Orientation.class, Coord.class).newInstance(orientation, position);
		} catch (Exception e) {
			throw new IllegalStateException("This code shoul be never reached!", e);
		}
	}
	
	static Class<? extends AbstractShip> getFinalType(int finalSize) {
		for(Class<? extends AbstractShip> curType : AbstractShip.getKnownShipTypes()){
			int curSize = AbstractShip.getSizeOf(curType);
			if(curSize == finalSize){
				return curType;
			}
		}
		
		return null;
	}
}
