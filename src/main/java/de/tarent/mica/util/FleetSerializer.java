package de.tarent.mica.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tarent.mica.model.Coord;
import de.tarent.mica.model.Fleet;
import de.tarent.mica.model.element.Carrier;
import de.tarent.mica.model.element.Cruiser;
import de.tarent.mica.model.element.Destroyer;
import de.tarent.mica.model.element.Ship;
import de.tarent.mica.model.element.Ship.Orientation;
import de.tarent.mica.model.element.Submarine;

/**
 * Diese Klasse sorgt dafür, eine {@link Fleet} in eine Datei
 * zu schreiben und aus einer Datei zu lesen.
 * 
 * @author rainu
 *
 */
public class FleetSerializer {

	public static void serialize(Fleet fleet, File dest) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(dest, false));
		
		try{
			bw.write("#<Koordinate>_<Ausrichtung>_<Groesse>");
			for(Ship s : fleet.getAll()){
				bw.write("\n");
				bw.write(toString(s));
			}
		}finally{
			if(bw != null) try{bw.close();}catch(IOException e){}
		}
	}
	
	private static String toString(Ship ship){
		if(ship == null) return "#";
		
		return ship.getPosition() + "_" + ship.getOrientation() + "_" + ship.getSpace().size();
	}
	
	private static Pattern shipPattern = Pattern.compile("^([0-9A-Za-z]{2})_([A-Za-z0-9_]*)_([0-9]*)$");
	private static Ship fromString(String ship){
		Matcher matcher = shipPattern.matcher(ship);
		if(!matcher.matches()) return null;
		
		switch(Integer.parseInt(matcher.group(3))){
		case 5:
			return new Carrier(Orientation.valueOf(matcher.group(2)), new Coord(matcher.group(1)));
		case 4:
			return new Cruiser(Orientation.valueOf(matcher.group(2)), new Coord(matcher.group(1)));
		case 3:
			return new Destroyer(Orientation.valueOf(matcher.group(2)), new Coord(matcher.group(1)));
		case 2:
			return new Submarine(Orientation.valueOf(matcher.group(2)), new Coord(matcher.group(1)));
		default: throw new IllegalStateException("Unbekannter Schiffstyp! " + ship);
		}
	}
	
	public static Fleet deserialize(File source) throws IOException{
		return deserialize(new FileInputStream(source));
	}
	
	public static Fleet deserialize(InputStream is) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		Fleet fleet = new Fleet();
		
		try{
			for(String line = null; (line = br.readLine()) != null;){
				if(line.startsWith("#")) continue;	//# sind Kommentare!
				
				Ship ship = fromString(line);
				if(ship instanceof Carrier){
					if(fleet.getCarrier1() == null){
						fleet.setCarrier1((Carrier)ship);
					}else if(fleet.getCarrier2() == null){
						fleet.setCarrier2((Carrier)ship);
					}
				}else if(ship instanceof Cruiser){
					if(fleet.getCruiser1() == null){
						fleet.setCruiser1((Cruiser)ship);
					}else if(fleet.getCruiser2() == null){
						fleet.setCruiser2((Cruiser)ship);
					}
				} else if(ship instanceof Destroyer){
					if(fleet.getDestroyer1() == null){
						fleet.setDestroyer1((Destroyer)ship);
					}else if(fleet.getDestroyer2() == null){
						fleet.setDestroyer2((Destroyer)ship);
					}
				} else if(ship instanceof Submarine){
					if(fleet.getSubmarine1() == null){
						fleet.setSubmarine1((Submarine)ship);
					}else if(fleet.getSubmarine2() == null){
						fleet.setSubmarine2((Submarine)ship);
					}
				}
			}
		}finally{
			if(br != null) try{br.close();}catch(IOException e){}
		}
		
		return fleet;
	}
}
