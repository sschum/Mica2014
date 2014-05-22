package de.tarent.mica.model;

public class Field {
	public static enum Element{	//TODO: Besseren Namen dafür finde ;)
		UNBEKANNT('?'),
		SCHIFF('*'),
		WASSER('~');
		
		private char sign;
		
		private Element(char sign){
			this.sign = sign;
		}
		
		public char getSign() {
			return sign;
		}
	}
	
	private Element[][] area;
	
	Field(Element[][] elements) {
		this.area = elements;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append(" ");
		for(int i=0; i < area[0].length; i++){	//TODO: prüfen, ob eine zeile vorhanden...
			sb.append(" ");
			sb.append(i);
		}
		
		sb.append("\n");
		
		for(int i=0; i < area.length; i++){
			sb.append((char)(65 + i));	//A -> 65
			for(int j=0; j < area[i].length; j++){
				sb.append(" ");
				sb.append(area[i][j].getSign());
			}
			sb.append("\n");
		}
		
		return sb.toString();
	}
}
