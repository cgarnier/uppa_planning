package net.irokwa.uppa.planning;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

	public ArrayList<Promotion> parsePromo(String str) {
		ArrayList<Promotion> result = new ArrayList<Promotion>();
		
		Pattern p = Pattern.compile("new Ressource \\(\"(.*?)\",\"(.*?)\",\"(.*?)\"\\);");
		Matcher m = p.matcher(str);
		while(m.find()) {
		   result.add(new Promotion(m.group(2).replace("&lt;&gt;", "<>"), m.group(3)));
		}
		return result;
	}
	
	public ArrayList<Periode> parsePeriode(String str, String code){
		ArrayList<Periode> result = new ArrayList<Periode>();
		
		
		Pattern p = Pattern.compile("new Periode \\(\"" + code + "\",\"(.*?)\",\"(.*?)\"\\);");
		Matcher m = p.matcher(str);
		while(m.find()) {
		   result.add(new Periode(m.group(1), m.group(2)));

		   
		}
		return result;
	}

}
