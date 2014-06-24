package de.tarent.mica.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.xml.bind.DatatypeConverter;

public class ObjectSerializer {

	public static String serialise(Object object) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);

		oos.writeObject(object);
		oos.close();
		
		return DatatypeConverter.printBase64Binary(baos.toByteArray());
	}
	
	public static Object deserialise(String representation) throws IOException, ClassNotFoundException{
		byte[] input = DatatypeConverter.parseBase64Binary(representation);

		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(input));
		try {
			return ois.readObject();
		} finally {
			ois.close();
		}
	}
}
