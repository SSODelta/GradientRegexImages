package com.ignatieff.gradientregex;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.PatternSyntaxException;

public class Main {

	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		
		boolean b = true;
		int depth = 8;
		
		while(b){
			String q = "";
			try{
				System.out.print(">");
				q = s.nextLine().toLowerCase();
				if(q.startsWith("d=")){
					depth = Integer.parseInt(q.substring(2));
					continue;
				}
				Fractal.generateAndSaveImage(q, rnd()+".png", depth);
			} catch(PatternSyntaxException e){
				System.out.println("Unable to parse regex: "+q+".");
			} catch (IOException e) {
				System.out.println("Unable to save image.");
			} catch(NumberFormatException e){
				System.out.println("Unable to parse integer: "+q.substring(2));
			} finally{
				System.out.println();
			}
		}
		s.close();
	}
	
	private static int rnd(){
		return (int)Math.floor(Math.random()*Integer.MAX_VALUE);
	}

}
