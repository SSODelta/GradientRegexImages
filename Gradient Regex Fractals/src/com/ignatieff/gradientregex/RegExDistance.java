package com.ignatieff.gradientregex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RegExDistance {

	private Map<String, Integer> table;
	
	public RegExDistance(){
		  table = new HashMap<String, Integer>();
	}
	
	public Map<String, Integer> getTable(){
		return table;
	}
	
	public void process(String[] regexps, int max, int distance){
		
		//Add all strings to the table
		for(String s : regexps){
			if(table.containsKey(s))continue;
			
			table.put(s, distance);
		}
		
		//Get all variations of the strings
		String[] variations = getVariations(regexps);
		
		//If we already have all variations catalogued (no new variations), then stop here.
		if(variations.length == 0)
			return;
		
		//Otherwise, process the variations again (with increased distance).
		process(variations, max, distance+1);
	}
	
	public String[] getVariations(String[] regexps){
		
		ArrayList<String> strings = new ArrayList<String>();
		Set<String> set = table.keySet();
		
		for(String str : regexps){
			for(int i=0; i<str.length(); i++){
				char c = str.charAt(i);
				char[] complements = getComplimentaries(c);
				for(char q : complements){
					StringBuilder sb = new StringBuilder();
					sb.append(str.substring(0, i));
					sb.append(q);
					sb.append(str.substring(i+1, str.length()));
					String variation = sb.toString();
					
					//If the list already contains one variation, then the other variations on this letter also exist.
					if(strings.contains(variation) || set.contains(variation))
						break;//Therefore, we break.
					
					strings.add(variation);
				}
			}
		}
		
		return (String[])strings.toArray(new String[0]);
	}
	
	public static char[] getComplimentaries(char c){
		switch(c){
			case '0':
				return new char[]{'1','2','3'};
			case '1':
				return new char[]{'0','2','3'};
			case '2':
				return new char[]{'0','1','3'};
			default:
				return new char[]{'0','1','2'};
		}
	}
	
}
