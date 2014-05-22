package de.tarent.mica.model;

import static de.tarent.mica.model.Field.Element.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.tarent.mica.model.Field.Element;

public class FieldTest {

	@Test
	public void toString_Test(){
		Field field = new Field(new Element[][]{
				new Element[]{UNBEKANNT, WASSER, WASSER, WASSER, WASSER, WASSER, WASSER, WASSER, WASSER, SCHIFF},
				new Element[]{WASSER, UNBEKANNT, WASSER, WASSER, WASSER, WASSER, WASSER, WASSER, SCHIFF, WASSER},
				new Element[]{WASSER, WASSER, UNBEKANNT, WASSER, WASSER, WASSER, WASSER, SCHIFF, WASSER, WASSER},
				new Element[]{WASSER, WASSER, WASSER, UNBEKANNT, WASSER, WASSER, SCHIFF, WASSER, WASSER, WASSER},
				new Element[]{WASSER, WASSER, WASSER, WASSER, UNBEKANNT, SCHIFF, WASSER, WASSER, WASSER, WASSER},
				new Element[]{WASSER, WASSER, WASSER, WASSER, SCHIFF, UNBEKANNT, WASSER, WASSER, WASSER, WASSER},
				new Element[]{WASSER, WASSER, WASSER, SCHIFF, WASSER, WASSER, UNBEKANNT, WASSER, WASSER, WASSER},
				new Element[]{WASSER, WASSER, SCHIFF, WASSER, WASSER, WASSER, WASSER, UNBEKANNT, WASSER, WASSER},
				new Element[]{WASSER, SCHIFF, WASSER, WASSER, WASSER, WASSER, WASSER, WASSER, UNBEKANNT, WASSER},
				new Element[]{SCHIFF, WASSER, WASSER, WASSER, WASSER, WASSER, WASSER, WASSER, WASSER, UNBEKANNT},
		});
		
		assertEquals(
				"  0 1 2 3 4 5 6 7 8 9\n" + 
				"A ? ~ ~ ~ ~ ~ ~ ~ ~ *\n"+ 
				"B ~ ? ~ ~ ~ ~ ~ ~ * ~\n"+ 
				"C ~ ~ ? ~ ~ ~ ~ * ~ ~\n"+ 
				"D ~ ~ ~ ? ~ ~ * ~ ~ ~\n"+ 
				"E ~ ~ ~ ~ ? * ~ ~ ~ ~\n"+ 
				"F ~ ~ ~ ~ * ? ~ ~ ~ ~\n"+ 
				"G ~ ~ ~ * ~ ~ ? ~ ~ ~\n"+ 
				"H ~ ~ * ~ ~ ~ ~ ? ~ ~\n"+ 
				"I ~ * ~ ~ ~ ~ ~ ~ ? ~\n"+ 
				"J * ~ ~ ~ ~ ~ ~ ~ ~ ?\n", 
				field.toString());
	}
}
