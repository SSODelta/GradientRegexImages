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
	
	public void process(Set<String> matches, Set<String> notMatches){
		
		System.out.println("Computing distances...");
		System.out.println("    Progress: |------------------------------|");
		System.out.print  ("              |");
		
		//First populate the table with the matches
		for(String t : matches){
			table.put(t, 0);
		}
		
		int c = notMatches.size() / 30;
		int i=0;
		
		//Go through all the strings that *don't* match the regex
		for(String s : notMatches){
			
			i++;
			
			if(i%c==0)
				System.out.print("-");
			
			int distance = Integer.MAX_VALUE;
			
			//Loop through all matches and find the minimum distance
			for(String t : matches){
				int d = LevenshteinDistance(s,t);
				
				//If this new distance is smaller than the old, then replace it
				if(d<distance)
					distance = d;
				
				//And if the distance is 1, just break (it can't get smaller than 1)
				if(d==1)
					break;
			}
			
			table.put(s, distance);
		}
		
		System.out.print("|\n");
	}
	
	private static int LevenshteinDistance (String s0, String s1) {                          
	    int dist = 0;
	    
	    for(int i=0; i<s0.length(); i++){
	    	if(s0.charAt(i) != s1.charAt(i))dist++;
	    }
	    
	    return dist;
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
