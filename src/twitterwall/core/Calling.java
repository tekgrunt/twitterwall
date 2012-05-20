package twitterwall.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;

import processing.core.PApplet;
import processing.core.PImage;
import twitter.data.object.FallingImage;
import twitter.data.object.FlyingImage;
import twitter.data.object.IMovingImage;
import twitter4j.MediaEntity;
import twitter4j.Tweet;
/**
 * This is the main class. Processing provides a framework with a couple of main methods. These
 * methods are included in this class and explained below.
 * 
 * @author christopherluft
 */
@SuppressWarnings("serial")
public class Calling extends PApplet
{
	private LinkedList<IMovingImage> imageQueue = new LinkedList<IMovingImage>();
	private LinkedList<TwitterBox> tweets = new LinkedList<TwitterBox>();
	
	private static HashMap<String, IMovingImage> imageMap;
	
	public boolean censor = false;
	
	public static int TotalTweetCount = 0;
	public static int RenderedTweetCount = 0;

	private TwitterThread tweetThread;
	private Renderer renderer;

	//I think I was just using this to get a rough count of how many Tweets had run... should probably go to sleep.
	int tweetCount = 0;
	
	/*
	 * The setup() function is a processing method that runs once when the application is booted.
	 * Instantiate objects needed for the lifetime of the app and load images here.
	 */
	public void setup()
	{	
		tweetThread = new TwitterThread(this);
		tweetThread.start();
		renderer = new Renderer(this);
		
		buildImageMap();
		frameRate(30);
		size(Shared.Width, Shared.Height);
	}
	
	/*
	 * The easter egg detector for cat party and friends
	 */
	private void parseEasterEggKeywords(TwitterBox box)
	{
		// Check for keywords...
		StringTokenizer st = new StringTokenizer(box.getText().toLowerCase(), " ");
		while(st.hasMoreTokens())
		{
			partyTime(st.nextToken());
		}
	}
	
	private void partyTime(String word)
	{
		word = word.replace("#", "");
		boolean isParty = word.contains("party");
		if(isParty)
		{
			word = word.replace("party", "");
		}
		if(imageMap.containsKey(word))
		{
			IMovingImage image = imageMap.get(word);
			int partySize = isParty ? (int)(Math.random() * 5) + 5 : 1;
			for(int i = 0 ; i < partySize ; i++)
			{
				if(image.getClass() == FlyingImage.class)
				{
					imageQueue.add(new FlyingImage(image.getImage()));
				}
				else
				{
					imageQueue.add(new FallingImage(image.getImage()));
				}
			}
		}
	}
	
	private void parseColor(TwitterBox box)
	{
		// Check for keywords...
		StringTokenizer st = new StringTokenizer(box.getText().toLowerCase(), " ");
		boolean found = false;
		while(st.hasMoreTokens() && !found)
		{
			String word = st.nextToken().replace("#", "");
			if(renderer.getColorMap().containsKey(word))
			{
				box.setTextColor(word);
			}
		}
	}
	
	private void buildImageMap()
	{
		imageMap = new HashMap<String, IMovingImage>();
		imageMap.put("cat", new FallingImage(loadLocalImage("fallingcat.png")));
		imageMap.put("anvil", new FallingImage(loadLocalImage("anvil.png")));
		imageMap.put("dog", new FallingImage(loadLocalImage("dog.png")));
		imageMap.put("squirrel", new FallingImage(loadLocalImage("squirrel.png")));
		imageMap.put("rainbow", new FallingImage(loadLocalImage("rainbow.png")));
		imageMap.put("fish", new FlyingImage(loadLocalImage("fish.png")));
		imageMap.put("<3", new FallingImage(loadLocalImage("heart.png")));
		imageMap.put("madmen", new FallingImage(loadLocalImage("madmen.png")));
		imageMap.put("pigsfly", new FlyingImage(loadLocalImage("pigsFly.png")));
		imageMap.put("shark", new FlyingImage(loadLocalImage("sharkparty.png")));
		
		imageMap.put("she", new FallingImage(loadLocalImage("dog.png")));
		imageMap.put("you", new FlyingImage(loadLocalImage("sharkparty.png")));
		imageMap.put("my", new FlyingImage(loadLocalImage("fish.png")));
		imageMap.put("so", new FallingImage(loadLocalImage("squirrel.png")));
	}
	
	public PImage loadLocalBackground(String name)
	{
		return super.loadImage(Shared.BackgroundsFolder + name);
	}
	
	public PImage loadLocalImage(String name)
	{
		return super.loadImage(Shared.ImageFolder + name);
	}
	
	public PImage loadLocalIcon(String name)
	{
		return super.loadImage(Shared.IconFolder + name);
	}
	
	public TwitterBox createTwitterBox(Tweet tweet)
	{
		TwitterBox tb = new TwitterBox(tweet);
		try
		{
			tb.setUserImage(loadImage(tweet.getProfileImageUrl(), "png"));
		//	System.out.println("Profile Image:" + tweet.getProfileImageUrl());
		}
		catch(Exception ex)
		{
			System.err.println("Couldn't load profile image.");
			ex.printStackTrace();
		}
		MediaEntity[] mediaEntities = tweet.getMediaEntities();
		
		if(mediaEntities != null)
		{
			try
			{
				tb.setImage(loadImage(mediaEntities[0].getMediaURLHttps().toString()));
			//	System.out.println("Media Content: " + mediaEntities[0].getMediaURLHttps().toString());
			}
			catch(Exception ex)
			{
				System.err.println("Couldn't load media content.");
				ex.printStackTrace();
			}
		}
		return tb;
	}
	
	/*
	 * The draw() function is a loop that runs at a defined frame rate. During each loop the screen is 
	 * re-drawn. Each loop you have to setup the background colour, text colour, etc like layers.
	 */
	public void draw()
	{
		renderer.renderBackground();
		
		if (tweets.size() < 8)
		{
			int index = tweets.size() -1;
			TwitterBox temp = Shared.TWEETS.poll();
			
			if(temp != null)
			{	
				RenderedTweetCount++;
				if(RenderedTweetCount % 2 == 0)
				{
					renderer.nextBackground();
				}
				parseEasterEggKeywords(temp);
				parseColor(temp);
				
				// determine the starting location for this twitterbox.
				int previousTweetLocation = 0;
				if(index >= 0)
				{ 
					TwitterBox previous = tweets.get(index);
					previousTweetLocation = previous.getY();
				}
				int offset = temp.getImageHeight() + temp.getHeight() + 52;
				temp.setY(previousTweetLocation - offset);
				tweets.add(temp);
				
				if(Shared.TWEETS.size() < 8)
				{
					Shared.TWEETS.add(createTwitterBox(temp.getTweet()));
				}
			}
		}
		
		for(int i = 0; i < tweets.size();)
		{
			//System.out.println("Chirp count: " + chirps.size());
			//System.out.println("TWEETS count: " + Shared.TWEETS.size());
			TwitterBox tb = tweets.get(i);
			if(tb.getY() > height)
			{
				tweets.remove(tb);
				tb.dispose();
				tb = null;
			//	System.out.println("Setting to null: + " + chirps.size());

			//	System.out.println("Chirp count: " + chirps.size());
			//	System.out.println("TWEETS count: " + Shared.TWEETS.size());
			}
			else
			{
				renderer.updateTwitterBox(tb);
				i++;
			}
		}
		for(IMovingImage pic : imageQueue)
		{
			renderer.updateImage(pic);
		}
		
		// clean up any images that are now offscreen.
		for(int i = 0 ; i < imageQueue.size();)
		{
			IMovingImage pic = imageQueue.get(i);
			
			if(pic.getX() > width || pic.getY() > height)
			{
				imageQueue.remove(pic);
				pic.dispose();
				pic = null;
			}
			else
			{
				i++;
			}
		}

		renderer.renderScene();

		if(Shared.TWEETS.size() > 50)
		{
			Shared.TWEETS = new LinkedList<TwitterBox>();
		}
	}
	
	static public void main(String args[]) 
	{
	    PApplet.main(new String[] { "--present", "twitterwall.core.Calling" });
	}
	
	public void mousePressed() 
	{		
		for(TwitterBox chirp : Shared.TWEETS)
		{
			if(chirp.getY() < mouseY + 50 && chirp.getY() > mouseY - 50)
			{
				chirp.killTweet();
			}
		}
	}

	public void keyPressed() 
	{
		// right arrow key
		if(keyCode == 39)
		{
			System.out.println(keyCode);
			renderer.nextBackground();
		}
		if (key == '.')
		{
			
		}
		if (key == 'b')
		{
			renderer.toggleBackground();
		}
		if (key == 'c')
		{
			partyTime("cat");
		}
		if (key == 'a')
		{
			partyTime("anvil");
		}
		if(key == 'm')
		{
			partyTime("madmen");
		}
		if(key == 'x')
		{
			renderer.toggleCensor();
		}
		if(key == 'k')
		{
			partyTime("cat");
		}
		if(key == 'p')
		{
			partyTime("pigsfly");
		}
		if(key == 'h')
		{
			partyTime("heart");
		}
		if(key == 'f')
		{
			partyTime("fish");
		}
		if(key == 's')
		{
			partyTime("sharkparty");
		}
		if(key == 'r')
		{
			partyTime("rainbow");
		}
		if(key == 'd')
		{
			partyTime("dog");
		}
		if(key == 'n')
		{
			partyTime("squirrel");
		}
		if(key == 'q')
		{
			 Shared.TWEETS = new LinkedList<TwitterBox>();
		}
		if(key == 'e')
		{
			imageQueue.clear();
		}
		if(key == 'g')
		{
			System.gc();
		}
	}		  
}

