package twitterwall.core;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

import processing.core.PImage;
import twitter.data.object.TweetSource;
import twitter4j.Tweet;

/**
 * The box containing photo and tweet being animated down the screen. The object is responsible for
 * its own position and updating it. The array of these objects is iterated through and updated
 * each time it is touched.
 * 
 * @author christopherluft
 *
 */
public class TwitterBox 
{
	protected PImage userImage;
	protected PImage tweetImage;
	
	// TODO: make these accessors.
	public int x = 0;
	public int y = 0;
	
	public String first = "";
	protected String second = "";
	private String colorToken = "white";
	Tweet chirp;
	private long created;
	private TweetSource source;
	
	public PImage getUserImage() 
	{
		return userImage;
	}

	public void setUserImage(PImage image) 
	{
		this.userImage = image;
	}

	public PImage getImage()
	{
		return tweetImage;
	}
	
	public void setImage(PImage image)
	{
		this.tweetImage = image;
	}
	
	public String getFirstLine()
	{
		return first;
	}
	
	public String getSecondLine()
	{
		return second;
	}
	
	public void setTextColor(String key)
	{
		colorToken = key;
	}
	
	public String getTextColor()
	{
		return colorToken;
	}
	
	public TweetSource getSource()
	{
		return source;
	}
	
	public String getText()
	{
		return this.chirp.getText();
	}
	
	public int getHeight()
	{
		return (second.length() > 0) ? 80 : 52;
	}
	
	public TwitterBox(Tweet tweet)
	{		
		created = Calendar.getInstance().getTimeInMillis();
		this.chirp = tweet;
		processTweet(tweet.getText());
		source = TweetSource.create(tweet.getSource());
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
		sanitize(input);
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
				firstLine.add(word);
				word = "";
			} 
			else if (token == ' ')
			{
				secondLine.add(word);
				word = "";
			}
		}
		
		if(chars.length < 80)
		{
			firstLine.add(word);
		}
		else
		{
			secondLine.add(word);
		}
		
		for(String f : firstLine)
		{
			first += f;
		}
		
		for(String s : secondLine)
		{
			second += s;
		}
	}

	/*
	 * Checks for common swear words and replaces them with fitting substitutes.
	 */
	public String sanitize(String text)
	{
		text.trim();
		text.replace("\n", "");
		text.replace("fuck", "bleep");
		text.replace("cunt", "bleep");
		text.replace("shit", "poop");
		return text;
	}
	
	/*
	 * In order to set the spacing for the various tweets when an image is posted
	 * I grab the height and use it calcualting position.
	 */
	public int getImageHeight()
	{
		if(tweetImage != null)
		{
			return tweetImage.height;
		}
		
		return -1;
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
