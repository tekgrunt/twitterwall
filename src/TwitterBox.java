

import java.util.ArrayList;
import java.util.StringTokenizer;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import twitter4j.MediaEntity;
import twitter4j.Tweet;

/**
 * The box containing photo and tweet being animated down the screen
 * 
 * @author christopherluft
 *
 */
public class TwitterBox 
{
	protected PImage userImage;
	protected PImage tweetImage;
	private PFont tweetText;
	protected PApplet p;
	protected int x=0;
	protected int y=0;;
	public String first = "";
	protected String second  = "";
	private String colourToken = "white";
	
	public TwitterBox(Tweet tweet, PApplet p)
	{		
		this.p = p;
		tweetText = p.loadFont("Verdana-20.vlw");
		processTweet(tweet.getText());
		this.userImage = p.loadImage(tweet.getProfileImageUrl(),"png");
		MediaEntity[] mediaEntities = tweet.getMediaEntities();
		if(mediaEntities != null)
		{
			System.out.println("**************************************** " + mediaEntities[0].getMediaURLHttps().toString());
			this.tweetImage = p.loadImage(mediaEntities[0].getMediaURLHttps().toString(),"jpg");
		}
		x = 0;
		y = 0;
	}

	private void processTweet(String input)
	{	
		
//		StringTokenizer st = new StringTokenizer(input, " ");
		
//		String temp = "";
//		while(st.hasMoreTokens())
//		{
//			String tempTemp = st.nextToken();
//			
//			partyTime(tempTemp);
//			sanatize(tempTemp);
//			setColour(tempTemp);
//			temp = tempTemp + " " + temp;
//		}
//		first = input;
		
		StringTokenizer st = new StringTokenizer(input, " ");
		String temp = "";
		while(st.hasMoreTokens())
		{
			 temp = st.nextToken() + " " + temp;
		}
		//first = input;//temp;
		ArrayList<String> firstLine = new ArrayList<String>();
		ArrayList<String> secondLine = new ArrayList<String>();
		char[] chars = input.toCharArray();
		int charCount = 0;
		String word = "";
		
		for(char token : chars)
		{
			charCount++;
			word = word + token;
			if(token == ' ' && charCount < 80)
			{
				partyTime(word);
				setColour(word);
				word = sanatize(word);
				firstLine.add(word);
				word = "";
			} 
			else if (token == ' ')
			{
				partyTime(word);
				setColour(word);
				word = sanatize(word);
				secondLine.add(word);
				word = "";
			}
		}
		
		if(chars.length < 80)
		{
			partyTime(word);
			setColour(word);
			word = sanatize(word);
			firstLine.add(word);
		}
		else
		{
			partyTime(word);
			setColour(word);
			word = sanatize(word);
			secondLine.add(word);
		}
		
		for(String f : firstLine)
		{
			first = first + f;
		}
		
		for(String s : secondLine)
		{
			second = second + s;
		}
	}
	
	public void partyTime(String party)
	{
		party = party.trim();
		if(party.equalsIgnoreCase("cat") || party.equalsIgnoreCase("#cat"))
		{
			Calling.PICS.add(new FallingImage(p.loadImage("fallingcat.png","png"), p));
		}
		if(party.equalsIgnoreCase("catparty") || party.equalsIgnoreCase("#catparty"))
		{
			for(int i = 0 ; i < 10 ; i++)
				Calling.PICS.add(new FallingImage(p.loadImage("fallingcat.png","png"), p));
		}
		if(party.equalsIgnoreCase("anvilparty") || party.equalsIgnoreCase("#anvilparty"))
		{
			for(int i = 0 ; i < 10 ; i++)
				Calling.PICS.add(new FallingImage(p.loadImage("anvil.png","png"), p));
		}
		if(party.equalsIgnoreCase("dog") || party.equalsIgnoreCase("#dog"))
		{	
			Calling.PICS.add(new FallingImage(p.loadImage("dog.png","png"), p));
		}
		if(party.equalsIgnoreCase("squirrel") || party.equalsIgnoreCase("#squirrel"))
		{	
			Calling.PICS.add(new FallingImage(p.loadImage("squirrel.png","png"), p));
		}
		if(party.equalsIgnoreCase("rainbow") || party.equalsIgnoreCase("#rainbow"))
		{	
			Calling.PICS.add(new FallingImage(p.loadImage("rainbow.png","png"), p));
		}
		if(party.equalsIgnoreCase("fish") || party.equalsIgnoreCase("#fish"))
		{	
			Calling.FLYING_PICS.add(new FlyingImage(p.loadImage("fish.png","png"), p));
		}
		if(party.equalsIgnoreCase("heart") || party.equalsIgnoreCase("#heart") || party.equalsIgnoreCase("<3"))
		{	
			Calling.PICS.add(new FallingImage(p.loadImage("heart.png","png"), p));
		}
		if(party.equalsIgnoreCase("dog") || party.equalsIgnoreCase("#dog") || party.equalsIgnoreCase("<3"))
		{	
			Calling.PICS.add(new FallingImage(p.loadImage("dog.png","png"), p));
		}
		if(party.equalsIgnoreCase("madmen") || party.equalsIgnoreCase("#madmen"))
		{	
			Calling.PICS.add(new FallingImage(p.loadImage("madmen.png","png"), p));
		}
		if(party.equalsIgnoreCase("pigsfly") || party.equalsIgnoreCase("#pigsfly"))
		{	
			for(int i = 0 ; i < 3 ; i++)
				Calling.FLYING_PICS.add(new FlyingImage(p.loadImage("pigsFly.png","png"), p));
		}
		if(party.equalsIgnoreCase("sharkparty") || party.equalsIgnoreCase("#shark"))
		{	
			for(int i = 0 ; i < 5 ; i++)
				Calling.FLYING_PICS.add(new FlyingImage(p.loadImage("sharkparty.png","png"), p));
		}
	}
	
	public void setColour(String token)
	{
		token = token.trim();
		
		if(token.equalsIgnoreCase("white") || token.equalsIgnoreCase("#white"))
		{
			colourToken = "white";
		}
		if(token.equalsIgnoreCase("blue") || token.equalsIgnoreCase("#blue"))
		{
			colourToken = "blue";
		}
		else if(token.equalsIgnoreCase("red") || token.equalsIgnoreCase("#red"))
		{
			colourToken = "red";
		}
		else if(token.equalsIgnoreCase("green") || token.equalsIgnoreCase("#green"))
		{
			colourToken = "green";
		}
		else if(token.equalsIgnoreCase("yellow") || token.equalsIgnoreCase("#yellow"))
		{
			colourToken = "yellow";
		}
		else if(token.equalsIgnoreCase("pink") || token.equalsIgnoreCase("#pink"))
		{
			colourToken = "pink";
		}
		else if(token.equalsIgnoreCase("purple") || token.equalsIgnoreCase("#purple"))
		{
			colourToken = "purple";
		}
		else if(token.equalsIgnoreCase("brown") || token.equalsIgnoreCase("#brown"))
		{
			colourToken = "brown";
		}
		else if(token.equalsIgnoreCase("orange") || token.equalsIgnoreCase("#orange"))
		{
			colourToken = "orange";
		}
	}
	
	public void colourTweet(String token)
	{	
		if(token.equalsIgnoreCase("blue"))
		{
			this.p.stroke(0,0,250);
			this.p.fill(0,0,250);
		}
		else if(token.equalsIgnoreCase("red"))
		{
			this.p.stroke(250,0,0);
			this.p.fill(250,0,0);
		}
		else if(token.equalsIgnoreCase("green"))
		{
			this.p.stroke(0,250,0);
			this.p.fill(0,250,0);
		}
		else if(token.equalsIgnoreCase("yellow"))
		{
			this.p.stroke(250,250,0);
			this.p.fill(250,250,0);
		}
		else if(token.equalsIgnoreCase("pink"))
		{
			this.p.stroke(249,74,173);
			this.p.fill(249,74,173);
		}
		else if(token.equalsIgnoreCase("purple"))
		{
			this.p.stroke(203,25,201);
			this.p.fill(203,25,201);
		}
		else if(token.equalsIgnoreCase("brown"))
		{
			this.p.stroke(188,113,5);
			this.p.fill(188,113,5);
		}
		else if(token.equalsIgnoreCase("orange"))
		{
			this.p.stroke(247,167,12);
			this.p.fill(247,167,12);
		}
	}
	
	public String sanatize(String token)
	{
		token.trim();
		if(token.equalsIgnoreCase("fuck") || token.trim().equalsIgnoreCase("cunt"))
			return "bleep ";
		else if(token.equalsIgnoreCase("fucking"))
			return "bleeping ";
		else if(token.equalsIgnoreCase("fucker"))
			return "bleeper ";
		else if(token.equalsIgnoreCase("shit"))
			return "poo ";
		
		return token;
	}
	
	public int getImageSize()
	{
		if(tweetImage != null)
		{
			return tweetImage.height;
		}
		
		return -1;
	}
	
	public void updateTwitterBox(PApplet p)
	{
		y += 1;
		p.textFont(tweetText);
		
		colourTweet(colourToken);
		
		if(second == null || second.length() == 0)
		{
			p.text(first, x + 70, y + 30);
		}
		else
		{
			p.text(first, x + 70, y + 15);
			p.text(second, x + 70, y + 45);	
		}
		p.image(userImage,x,y);
		p.image(userImage,x + 964 + 10,y);
	
		if(tweetImage != null)
		{
			p.image(tweetImage, x+200, y+100);	
		}
	}

	public void killTweet()
	{		
		first="";
		second="";
	}

	public PImage getUserImage() 
	{
		return userImage;
	}

	public void setUserImage(PImage userImage) 
	{
		this.userImage = userImage;
	}

	public PApplet getP() {
		return p;
	}

	public void setP(PApplet p) 
	{
		this.p = p;
	}

	public int getX() 
	{
		return x;
	}

	public void setX(int x) 
	{
		this.x = x;
	}

	public int getY() 
	{
		return y;
	}

	public void setY(int y) 
	{
		this.y = y;
	}
}
