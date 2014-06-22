package de.tarent.mica.util;

public class Output {

	public static String makeSideBySide(String s1, String s2, String delimiter){
		StringBuffer sb = new StringBuffer(s1.length() + s2.length());		

		String[] s1Split = s1.split("\n");
		String[] s2Split = s2.split("\n");
		
		final int max = Math.max(s1Split.length, s2Split.length);
		int s1MaxLength = 0;
		int s2MaxLength = 0;
		
		for(String s : s1Split) s1MaxLength = Math.max(s.length(), s1MaxLength);
		for(String s : s2Split) s2MaxLength = Math.max(s.length(), s2MaxLength);
		
		for(int i=0; i < max; i++){
			if(i < s1Split.length){
				sb.append(s1Split[i]);
				
				if(s1Split[i].length() < s1MaxLength){
					for(int j = s1Split[i].length(); j < s1MaxLength; j++){
						sb.append(" ");
					}
				}
			}else{
				for(int j=0; j < s1MaxLength; j++){
					sb.append(" ");
				}
			}
			
			sb.append(delimiter);
			
			if(i < s2Split.length){
				sb.append(s2Split[i]);
			}else{
				for(int j=0; j < s2MaxLength; j++){
					sb.append(" ");
				}
			}
			
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	public static String makeSideBySide(String delimiter, String...strings){
		if(strings.length == 0) return "";
		
		if(strings.length == 1){
			return makeSideBySide(strings[0], "", delimiter);
		}

		String s = strings[0];
		for(int i=1; i < strings.length; i++){
			s = makeSideBySide(s, strings[i], delimiter);
		}
		
		return s;
	}
	
}
