package com.ignatieff.gradientregex;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

public class Fractal {
	/**
	 * Array containing all the disposable colors for this generator.
	 * This list is Kelly's list of maximum contrast colors.
	 */
	private static Color[] COLORS = { byteToColor(0xFFB300), //Vivid Yellow
		    		   byteToColor(0x803E75), //Strong Purple
		    		   byteToColor(0xFF6800), //Vivid Orange
		    		   byteToColor(0xA6BDD7), //Very Light Blue
		    		   byteToColor(0xC10020), //Vivid Red
		    		   byteToColor(0xCEA262), //Grayish Yellow
		    		   byteToColor(0x817066), //Medium Gray

		    		   //The following will not be good for people with defective color vision
		    		   byteToColor(0x007D34), //Vivid Green
		    		   byteToColor(0xF6768E), //Strong Purplish Pink
		    		   byteToColor(0x00538A), //Strong Blue
		    		   byteToColor(0xFF7A5C), //Strong Yellowish Pink
		    		   byteToColor(0x53377A), //Strong Violet
		    		   byteToColor(0xFF8E00), //Vivid Orange Yellow
		    		   byteToColor(0xB32851), //Strong Purplish Red
		    		   byteToColor(0xF4C800), //Vivid Greenish Yellow
		    		   byteToColor(0x7F180D), //Strong Reddish Brown
		    		   byteToColor(0x93AA00), //Vivid Yellowish Green
		    		   byteToColor(0x593315), //Deep Yellowish Brown
		    		   byteToColor(0xF13A13), //Vivid Reddish Orange
		    		   byteToColor(0x232C16)};//Dark Olive Green;
	
	private Map<String, Integer> distanceByRegex;
	private int fractalDepth;
	private Color base;
	
	private int dr, dg, db;
	
	public Fractal(Color c1, Color c2){
		distanceByRegex = new HashMap<String, Integer>();
		base = c1;
		dr = c2.getRed()-  c1.getRed();
		dg = c2.getGreen()-c1.getGreen();
		db = c2.getBlue()- c1.getBlue();
	}
	
	private static int randomColor(){
		return (int)Math.floor(Math.random()*COLORS.length);
	}
	
	public static void generateAndSaveImage(String regex, String output, int size) throws IOException{
		int c1 = randomColor();
		int c2 = c1;
		while(c2==c1)
			c2 = randomColor();
		
		Fractal f = new Fractal(COLORS[c1],COLORS[c2]);
		f.generate(regex, size);
		BufferedImage img = f.getImageData();
		BufferedImage imgWithText = writeToImage(img,regex);
		ImageIO.write(imgWithText, "png", new File(output));
	}
	
	public int getColor(double k){
		//System.out.println("k="+k);
		int r = base.getRed()+(int)(dr*k);
		int g = base.getGreen()+(int)(dg*k);
		int b = base.getBlue()+(int)(db*k);
		//System.out.println("color=["+r+", "+g+", "+b+"]");
		Color diff = new Color(r, g, b);
		return diff.getRGB();
	}
	
	private int getMaximumDistance(){
		Collection<Integer> vals = distanceByRegex.values();
		int max = 1;
		for(Integer i : vals){
			if(i>max)max=i;
		}
		return max;
	}
	
	public BufferedImage getImageData(){
		int width = (int)Math.pow(2, fractalDepth+1);
		BufferedImage img = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB);
		
		Set<String> regexps = distanceByRegex.keySet();
		int max = getMaximumDistance();
		
		for(String s : regexps){
			Point p = regexToPoint(s);
			
			int d = distanceByRegex.get(s);
			
			double r = (double) d / (double)(max);
			
			img.setRGB(p.x, p.y, getColor(r));
		}
		
		return img;
	}

	
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
	
	public void generate(String regex, int depth){
		fractalDepth = depth;
		
		//Get Pattern-object
		Pattern p = Pattern.compile(regex);
		
		//Generate all strings of a given length
		String[] strings = generateRegExps(depth);
		
		//Get all matches
		String[] matches = getMatches(strings, p);
		
		//Assign distances to all matches
		RegExDistance r = new RegExDistance();
		r.process(matches, strings.length, 0);
		distanceByRegex = r.getTable();
	}
	
	private static String[] getMatches(String[] strings, Pattern p){
		ArrayList<String> matches = new ArrayList<String>();
		
		for(String s : strings){
			if(p.matcher(s).matches())
				matches.add(s);
		}
		
		return (String[])matches.toArray(new String[0]);
	}
	
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
	
	private static String[] generateRegExps(int depth){
		//Base case
		if (depth==0)
			return new String[]{"0","1","2","3"};
		
		String[] regexps = generateRegExps(depth-1);
		String[] newRegExps = new String[regexps.length*4];
		
		for(int i=0; i<regexps.length; i++){
			for(int j=0; j<4; j++){
				newRegExps[i*4+j] = j + "" + regexps[i];
				
			}
		}
		
		return newRegExps;
	}
}
