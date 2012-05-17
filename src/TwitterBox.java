

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import twitter.data.object.FallingImage;
import twitter.data.object.FlyingImage;
import twitter4j.MediaEntity;
import twitter4j.Tweet;

/**
 * The box containing photo and tweet being animated down the screen. The object is responsible for
 * its own position adn updating it. The array of these objects is iterated through and updated
 * each time it is touched.
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
	Tweet chirp;
	private long created;
	
	public TwitterBox(Tweet tweet, PApplet p)
	{		
		created = Calendar.getInstance().getTimeInMillis();
		this.chirp = tweet;
		this.p = p;
		tweetText = p.loadFont("Verdana-20.vlw");
		processTweet(tweet.getText());
		this.userImage = p.loadImage(tweet.getProfileImageUrl(),"png");
		MediaEntity[] mediaEntities = tweet.getMediaEntities();
		
		if(mediaEntities != null)
		{
			System.out.println("** " + mediaEntities[0].getMediaURLHttps().toString());
			this.tweetImage = p.loadImage(mediaEntities[0].getMediaURLHttps().toString(),"jpg");
		}
		x = 0;
		y = 0;
	}

	public long getTime()
	{
		return created;
	}
	
	public Tweet getTweet()
	{
		return chirp;
	}
	
	private void processTweet(String input)
	{	
		StringTokenizer st = new StringTokenizer(input, " ");
		String temp = "";
		while(st.hasMoreTokens())
		{
			 temp = st.nextToken() + " " + temp;
		}

		ArrayList<String> firstLine = new ArrayList<String>();
		ArrayList<String> secondLine = new ArrayList<String>();
		char[] chars = input.toCharArray();
		int charCount = 0;
		String word = "";
		
		/*
		 * This is some horrendous string parsing I had to do to my own word wrap
		 */
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
	
	public void resetTweet(int newY){
		this.y = newY;
	}
	
	/*
	 * The easter egg detector for cat party and friends
	 */
	public void partyTime(String party)
	{
		party = party.trim();
		if(party.equalsIgnoreCase("cat") || party.equalsIgnoreCase("#cat"))
		{
			Calling.PICS.add(new FallingImage(p.loadImage("fallingcat.png","png"), p));
		}
		if(party.equalsIgnoreCase("catparty") || party.equalsIgnoreCase("#catparty"))
		{
			for(int i = 0 ; i < 1 ; i++)
				Calling.PICS.add(new FallingImage(p.loadImage("fallingcat.png","png"), p));
		}
		if(party.equalsIgnoreCase("anvilparty") || party.equalsIgnoreCase("#anvilparty"))
		{
			for(int i = 0 ; i < 1 ; i++)
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
		if(party.equalsIgnoreCase("pigsfly") || party.equalsIgnoreCase("#pigsfly") || party.equalsIgnoreCase("#whenpigsfly"))
		{	
			for(int i = 0 ; i < 1 ; i++)
				Calling.FLYING_PICS.add(new FlyingImage(p.loadImage("pigsFly.png","png"), p));
		}
		if(party.equalsIgnoreCase("sharkparty") || party.equalsIgnoreCase("#shark"))
		{	
			for(int i = 0 ; i < 5 ; i++)
				Calling.FLYING_PICS.add(new FlyingImage(p.loadImage("sharkparty.png","png"), p));
		}
	}
	
	/*
	 * The easter egg detector for changing text colour. The behaviour for this is not 100%
	 * when a user was changing the text colour it would flicker between some of the tweets  
	 * and effect all tweets on the screen while the original tweet was running... it kind of 
	 * looks ok as is it functions and I would not make this a high priority. 
	 */
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
	/*
	 * Continued from above... I think the problem with this colour setting is that it 
	 * is setting the global Processing stroke and fill and having this reset in another tweet
	 * during the loop as we flip between them.
	 */
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
	/*
	 * Checks for common swear words and replaces them with fitting substitutes.
	 */
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
	
	/*
	 * In order to set the spacing for the various tweets when an image is posted
	 * I grab the height and use it calcualting position.
	 */
	public int getImageSize()
	{
		if(tweetImage != null)
		{
			return tweetImage.height;
		}
		
		return -1;
	}
	
	/*
	 * The object updates its own position on the screen during each loop - sorry for the magic numbers
	 */
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

	/*
	 * I think I had functionilty in the first iteration that if you clicked on a tweet or hit a specific key
	 * when it was in the queue it would remove the text. Being able to manage the live stream of content would
	 * be a big win... just trying to figure out how to do that is the challenge.
	 */
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
