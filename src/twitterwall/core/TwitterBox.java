package twitterwall.core;

import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

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
	private PImage userImage;
	private PImage tweetImage;
	private Tweet chirp;
	private TweetSource source;
	
	// TODO: make these accessors.
	public int x = 0;
	public int y = 0;
	private long created;
	
	private boolean checkedForKeywords = false;
	
	public String first = "";
	protected String second = "";
	private String colorToken = "white";
	
	private static Pattern cursingFilter = Pattern.compile("fuck|cunt|shit", Pattern.CASE_INSENSITIVE);
	
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
	
	public boolean isCheckedForKeywords()
	{
		return checkedForKeywords;
	}
	
	public void isCheckedForKeywords(boolean bool)
	{
		checkedForKeywords = bool;
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
		input = sanitize(input);
		if(input.length() < 80)
		{
			first = input;
			return;
		}
		StringTokenizer st = new StringTokenizer(input, " ");
		String token = "";
		while(st.hasMoreTokens())
		{
			 token = st.nextToken();
			 if(first.length() + token.length() > 80)
			 {
				 second = input.substring(first.length());
				 if(second.length() > 80)
				 {
					 first = input.substring(0, 80);
					 second = input.substring(80);
				 }
				 break;
			 }
			 else
			 {
				 first = first + " " + token;
			 }
		}
		first = first.trim();
		second = second.trim();
		return;
	}

	/*
	 * Checks for common swear words and replaces them with asterisks.
	 */
	public String sanitize(String input)
	{
		String text = input.trim();
		text = text.replace("\n", " ");
		return cursingFilter.matcher(text).replaceAll("****");
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
	 * I think I had functionality in the first iteration that if you clicked on a tweet or hit a specific key
	 * when it was in the queue it would remove the text. Being able to manage the live stream of content would
	 * be a big win... just trying to figure out how to do that is the challenge.
	 */
	public void killTweet()
	{		
	//	first = "";
	//	second = "";
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
	
	public void dispose()
	{
		this.userImage = null;
		this.tweetImage = null;
		this.chirp = null;
	}
}
