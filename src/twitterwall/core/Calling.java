package twitterwall.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
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
	private PImage bannerImage;
	private PImage restricted;
	private PImage bg;
	
	private TwitterBox inThePipe;
	
	private int lastY = 0;
	
	/*
	 * I have a bunch of queues here for the different items that move across the screen.
	 * Once they have moved across the screen we set them to null and the GC picks them up.
	 * I think it is the GC that is causing the occasional jerk/freeze that happens.
	 * I am not sure if there is anything that can be done to make this better but it would
	 * be a big win. 
	 * 
	 */
	private LinkedList<IMovingImage> imageQueue = new LinkedList<IMovingImage>();
	private LinkedList<TwitterBox> chirps = new LinkedList<TwitterBox>();

	private static HashMap<String, IMovingImage> imageMap;
	
	public boolean censor = false;
	public static int TWEET_COUNT = 0;

	private TwitterThread tweetThread;
	private Renderer renderer = new Renderer(this);

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
		
		bannerImage = loadLocalImage("m2o_banner2.png");
		restricted = loadLocalImage("oops.jpg");
		buildImageMap();
		frameRate(30);
		size(Shared.Width, Shared.Height);
		bg = super.loadImage("background/02182_campmeekerwaterfall_1024x768.jpg");
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
		
	//	imageMap.put("she", new FallingImage(loadLocalImage("dog.png")));
	//	imageMap.put("love", new FlyingImage(loadLocalImage("sharkparty.png")));
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
		background(bg);		

		if (chirps.size() < 8)
		{
			int index = chirps.size() -1;
			TwitterBox temp = Shared.TWEETS.poll();

			if(temp != null)
			{	
				parseEasterEggKeywords(temp);
				parseColor(temp);
				
				// determine the starting location for this twitterbox.
				int previousTweetLocation = 0;
				if(index >= 0)
				{ 
					TwitterBox previous = chirps.get(index);
					previousTweetLocation = previous.getY();
				}
				int offset = temp.getImageHeight() + temp.getHeight() + 52;
				temp.setY(previousTweetLocation - offset);
				chirps.add(temp);
			}
		}
		
		for(int i = 0; i < chirps.size();)
		{
			//System.out.println("Chirp count: " + chirps.size());
			//System.out.println("TWEETS count: " + Shared.TWEETS.size());
			TwitterBox chirp = chirps.get(i);
			if(chirp.getY() > height)
			{
				chirps.remove(chirp);
				chirp = null;
			//	System.out.println("Setting to null: + " + chirps.size());

			//	System.out.println("Chirp count: " + chirps.size());
			//	System.out.println("TWEETS count: " + Shared.TWEETS.size());
			}
			else
			{
				renderer.updateTwitterBox(chirp);
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
				pic = null;
			}
			else
			{
				i++;
			}
		}
		image(bannerImage, 0, 0);
		if(censor)
		{
			image(restricted, 100, 120);
		}
		
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
	
	public void keyPressed() {
		if (key == '.')
		{
			
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
		if(key == '1')
		{
			imageQueue.add(new FallingImage(loadLocalImage("m2o_banner2.png"), "noMove"));
		}
		if(key == '2')
		{
			imageQueue.add(new FallingImage(loadLocalImage("m2o_banner2.png"), "random"));
		}
		if(key == 'x')
		{
			censor = !censor;
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
		
		if (key == '/')
		{
			inThePipe.killTweet();
		}
	}		  
}

