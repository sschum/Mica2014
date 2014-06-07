package de.tarent.mica.cli;

import de.tarent.mica.model.Coord;
import asg.cliche.InputConverter;

public class InputConverterContainer {

	private InputConverter coordConverter = new InputConverter() {
		@Override
		public Object convertInput(String original, Class toClass) throws Exception {
			if(Coord.class == toClass){
				return new Coord(original);
			}

			return null;
		}
	};

	//häslich wie die nacht aber ist leider so implementiert :/
	public InputConverter[] CLI_INPUT_CONVERTERS = new InputConverter[]{
			coordConverter
	};
}
