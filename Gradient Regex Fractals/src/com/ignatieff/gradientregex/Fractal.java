package com.ignatieff.gradientregex;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.imageio.ImageIO;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;

public class Fractal {
	
	/**
	 * Array containing all the disposable colors for this generator.
	 * This list is Kelly's list of maximum contrast colors.
	 */
	private static Color[] COLORS = { 	byteToColor(0xFFB300), //Vivid Yellow
		    							byteToColor(0xFF6800), //Vivid Orange
		    							byteToColor(0xA6BDD7), //Very Light Blue
		    							byteToColor(0xC10020), //Vivid Red
		    							byteToColor(0xCEA262), //Grayish Yellow
		    							byteToColor(0x817066), //Medium Gray
		    		
		    							//The following will not be good for people with defective color vision
		    							byteToColor(0x007D34), //Vivid Green
		    							byteToColor(0x00538A), //Strong Blue
		    							byteToColor(0xFF8E00), //Vivid Orange Yellow
		    							byteToColor(0xF4C800), //Vivid Greenish Yellow
		    							byteToColor(0x7F180D), //Strong Reddish Brown
		    							byteToColor(0x93AA00), //Vivid Yellowish Green
		    							byteToColor(0x593315), //Deep Yellowish Brown
		    							byteToColor(0xF13A13), //Vivid Reddish Orange
		    							byteToColor(0x232C16)};//Dark Olive Green;
	private int fractalDepth;
	private Color base;
	
	private BufferedImage img;
	
	private Set<String> matches;
	
	private int dr, dg, db, maxStrings, added, prog;
	
	public Fractal(Color c1, Color c2){
		base = c1;
		dr = c2.getRed()-  c1.getRed();
		dg = c2.getGreen()-c1.getGreen();
		db = c2.getBlue()- c1.getBlue();
	}
	
	/**
	 * Returns a random color from Kelly's list of maximum contrast colors.
	 * @return
	 */
	private static int randomColor(){
		return (int)Math.floor(Math.random()*COLORS.length);
	}
	
	/**
	 * Generates and saves a RegEx fractal to a file. Uses random colors based on Kelly's list of maximum contrast colors.
	 * @param regex The regular expression to generate from
	 * @param output The path of the output file. Must be of extension .png
	 * @param size The size of the fractal. Returns an image with width/height 2^size
	 * @throws IOException 
	 */
	public static void generateAndSaveImage(String regex, String output, int size) throws IOException{
		int c1 = randomColor();
		int c2 = c1;
		while(c2==c1)
			c2 = randomColor();
		
		Fractal f = new Fractal(COLORS[c1],COLORS[c2]);
		f.generate(regex, size);
		
		System.out.println("Producing image...");
		
		BufferedImage img = f.getImage();
		BufferedImage imgWithText = writeToImage(img,regex);
		
		ImageIO.write(imgWithText, "png", new File(output));
		
		System.out.println("Image saved to "+output+".");
	}
	
	/**
	 * Get the gradient color represented by a double.
	 * Let c1, c2 be the colors this fractal was instantiated with.
	 * Then k=0 returns the color c1 and k=1 returns the color c2.
	 * For k=0.5 returns the color directly between c1 and c2.
	 * @param k The gradient double, must be within range [0, 1].
	 * @return The RGB-integer for the color represented by the double.
	 */
	public int getColor(double k){
		if(k<0 || k>1)return -1;
		int r = base.getRed()+(int)(dr*k);
		int g = base.getGreen()+(int)(dg*k);
		int b = base.getBlue()+(int)(db*k);
		Color diff = new Color(r, g, b);
		return diff.getRGB();
	}

	/**
	 * Maps a string in the regular language to a pixel position in the canvas.
	 * @param regex The string to parse.
	 * @return A Point-object representing the pixel position of the input string.
	 */
	public static Point regexToPoint(String regex){
		
		//If it's the last letter, then we're at base case
		if(regex.length() == 1){
			char c = regex.charAt(0);
			switch(c){
				case '0':
					return new Point(0,1);
				case '1':
					return new Point(0,0);
				case '2':
					return new Point(1,0);
				default:
					return new Point(1,1);
			}
		}
		
		//Get the next location
		Point p = regexToPoint(regex.substring(regex.length()-1,regex.length()));
		
		//Get what this point *would* have been
		Point q = regexToPoint(regex.substring(0, regex.length()-1));
		
		Point k = new Point(q.x*2 + p.x,
				 q.y*2 + p.y);
		
		
		return k;
	}
	
	/**
	 * Converts a regex-string to an Automaton-object.
	 * @param regex The string representation of the regular expression.
	 * @return An Automaton-object representing the regular expression.
	 */
	private static Automaton getAutomaton(String regex){
		RegExp r = new RegExp(regex);
		return r.toAutomaton(true);
	}
	
	/**
	 * Gets the image object associated with this object. Will return null if generate() hasn't run on this object.
	 * @return A BufferedImage.
	 */
	public BufferedImage getImage(){
		return img;
	}
	/**
	 * Generates content for this Fractal-object.
	 * @param regex The Regular Expression to generate an image of.
	 * @param depth The size of the image, where the width/height of the image is 2^depth
	 */
	public void generate(String regex, int depth){
		
		added = 0;
		maxStrings = (int)Math.pow(4,depth);
		prog = maxStrings / 40;
		int size = (int)Math.pow(2,depth);
		img = new BufferedImage(size,size,BufferedImage.TYPE_INT_RGB);
		
		fractalDepth = depth;
		
		System.out.println("Constructing DFA's...");
		
		//Construct automaton from RegExp
		Automaton aut = getAutomaton(regex);
		
		//Construct DFA from NFA
		aut.determinize();
		
		//Get the automaton that accepts all strings of length n
		Automaton length_n = getAutomaton("0|1|2|3").repeat(depth);
		
		//Get the complement automaton
		Automaton complement = aut.complement().intersection(length_n);
		complement.determinize();
	
		System.out.println("Computing language...");
		
		matches = aut.getStrings(depth);
		
		System.out.println("Processing states...");
		System.out.println("	Progress: |----------------------------------------|");
		System.out.print  ("	          |");
		
		processState(complement.getInitialState(), new StringBuilder(), 0);
		
		System.out.print("|\n");
	}
	
	/**
	 * Recursively processes a single State-object.
	 * @param s The State to process
	 * @param sb The StringBuilder associated with this object.
	 * @param k The depth of recursion. 
	 */
	private void processState(State s, StringBuilder sb, int k){
		
		if(k==fractalDepth){
			if(s.isAccept())
				addString(sb.toString());
			
			return;
		}
		Set<Transition> transitions = s.getTransitions();
		for(Transition t : transitions){
			
			State newS = t.getDest();
			
			for(char c=t.getMin(); c<t.getMax()+1; c++){
				processState(newS, new StringBuilder(sb).append(c), k+1);
			}
		}
	}
	
	/**
	 * Adds this string to the image
	 * @param k
	 */
	private void addString(String k){
		
		added++;
		if(added%prog==0)System.out.print("-");
		
		int d = getDistance(k);

		Point p = regexToPoint(k);
		
		double r = (double) d / (double) fractalDepth;
		
		int c = getColor(r);
		
		img.setRGB(p.x, p.y, c);
	}
	
	/**
	 * Computes the hammer distance between some string k, and the set of matches.
	 * @param k The string
	 * @return
	 */
	private int getDistance(String k){
		int dist = Integer.MAX_VALUE;
		for(String s : matches){
			int d = hammerDistance(s,k);
			if(d==1)return 1;
			if(d<dist)dist=d;
		}
		return dist;
	}
	
	/**
	 * Computes the Hammer distance between two strings of equal length.
	 * @param s0 
	 * @param s1
	 * @return
	 */
	private static int hammerDistance (String s0, String s1) {                          
	    int dist = 0;
	    
	    for(int i=0; i<s0.length(); i++){
	    	if(s0.charAt(i) != s1.charAt(i))dist++;
	    }
	    
	    return dist;
	}
	
	/**
	 * Converts the binary representation of a color (i.e. 0x2BDF3F) to a Color-object
	 * @param color The binary representation of a color
	 * @return A Color-object representing the color byte.
	 */
	private static Color byteToColor(int color){
		return new Color(color);
	}
	
	/**
	 * Writes text to a BufferedImage-object in the lower right corner.
	 * Used to write the name of the regex unto the image object.
	 * @param image The image to write on.
	 * @param textToWrite The text to write
	 * @return A new BufferedImage, which has text written unto it.
	 */
	private static BufferedImage writeToImage(BufferedImage image, String text){
		Graphics2D g = image.createGraphics();
		g.setFont(new Font("Sans-Serif", Font.BOLD, 17));
		FontMetrics fm = g.getFontMetrics(); 
		int w = fm.stringWidth(text) + 10;
		int h = fm.getHeight() + 3;
		g.setColor(Color.WHITE);
		g.fillRect(0, image.getHeight()-h, w, h);
		g.setColor(Color.BLACK);
		g.drawString(text, 5, image.getHeight() - 6);
		g.dispose();
		return image;
	}
}
