package com.ignatieff.gradientregex;

import java.io.IOException;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		
		boolean b = true;
		
		while(b){
			System.out.print(">");
			processData(s.nextLine());
		}
		s.close();
	}
	
	private static void processData(String k){
		try {
			Fractal.generateAndSaveImage(k, rnd()+".png", 13);
		} catch (IOException e) {
			System.out.println("Unable to generate image: "+e.getMessage());
		} finally {
			System.out.println();
		}
	}
	
	private static int rnd(){
		return (int)Math.floor(Math.random()*Integer.MAX_VALUE);
	}

}
