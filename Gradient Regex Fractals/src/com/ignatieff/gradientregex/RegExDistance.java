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
	
	public void process(String[] matches, String[] notMatches){
		
		//First populate the table with the matches
		for(String t : matches){
			table.put(t, 0);
		}
		
		
		//Go through all the strings that *don't* match the regex
		for(String s : notMatches){
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
		
	}
	
	//Slightly modified version of the algorithm found at:
	//http://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Java
	private static int LevenshteinDistance (String s0, String s1) {                          
	    int len0 = s0.length() + 1;                                                     
	    int len1 = s1.length() + 1;                                                     
	 
	    // the array of distances                                                       
	    int[] cost = new int[len0];                                                     
	    int[] newcost = new int[len0];                                                  
	 
	    // initial cost of skipping prefix in String s0                                 
	    for (int i = 0; i < len0; i++) cost[i] = i;                                     
	 
	    // dynamically computing the array of distances                                  
	 
	    // transformation cost for each letter in s1                                    
	    for (int j = 1; j < len1; j++) {                                                
	        // initial cost of skipping prefix in String s1                             
	        newcost[0] = j;                                                             
	 
	        // transformation cost for each letter in s0                                
	        for(int i = 1; i < len0; i++) {                                             
	            // matching current letters in both strings                             
	            int match = (s0.charAt(i - 1) == s1.charAt(j - 1)) ? 0 : 1;             
	 
	            // computing cost for each transformation
	            // insertion and deletion discarded due to all strings having the same length
	            int cost_replace = cost[i - 1] + match;                                
	 
	            // keep minimum cost                                                    
	            newcost[i] = cost_replace;
	        }                                                                           
	 
	        // swap cost/newcost arrays                                                 
	        int[] swap = cost; cost = newcost; newcost = swap;                          
	    }                                                                               
	 
	    // the distance is the cost for transforming all letters in both strings        
	    return cost[len0 - 1];                                                          
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
