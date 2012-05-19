package twitterwall.core;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

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
	static Queue<IMovingImage> PICS = new LinkedList<IMovingImage>();
	static Queue<IMovingImage> FLYING_PICS = new LinkedList<IMovingImage>();
	private Queue<TwitterBox> chirps = new LinkedList<TwitterBox>();

	private static HashMap<String, IMovingImage> imageMap;
	
	public boolean censor = false;
	TwitterThread tweetThread;
	public static int TWEET_COUNT = 0;
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
	}
	
	/*
	 * The easter egg detector for cat party and friends
	 */
	public static void partyTime(String word)
	{
		word = word.trim().replace("#", "").toLowerCase();
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
					Calling.FLYING_PICS.add(new FlyingImage(image.getImage()));
				}
				else
				{
					Calling.PICS.add(new FallingImage(image.getImage()));
				}
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
		imageMap.put("heart", new FallingImage(loadLocalImage("heart.png")));
		imageMap.put("madmen", new FallingImage(loadLocalImage("madmen.png")));
		imageMap.put("pigsfly", new FlyingImage(loadLocalImage("pigsFly.png")));
		imageMap.put("shark", new FlyingImage(loadLocalImage("sharkparty.png")));
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
			System.out.println("Profile Image:" + tweet.getProfileImageUrl());
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

			System.out.println("Media Content: " + mediaEntities[0].getMediaURLHttps().toString());
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
		background(1);		

		if(lastY <= 0)
		{
			TwitterBox temp = Shared.TWEETS.poll();
			
			System.out.println("Getting tweet...");

			if(temp != null)
			{
				if(temp.getImageHeight() > 0)
				{
					temp.setY(-1 * temp.getImageHeight());
					lastY = temp.getImageHeight() + 120;
					System.out.println("SET: " + lastY);
				}
				else
				{
					lastY = 120;
				}
				chirps.add(temp);
			}
			else
			{
				lastY = 120;
			}
		}
		else
		{
			lastY--;
		}
		
		for(int i = 0 ; i < chirps.size() ; i++)
		{
			TwitterBox chirp = chirps.poll();
		
			if(chirp.getY() > height)
			{
				chirp = null;
				System.out.println("Setting to null: + " + chirps.size());

				System.out.println("Chirp count: " + chirps.size());
				System.out.println("TWEETS count: " + Shared.TWEETS.size());
			}
			else
			{
				renderer.updateTwitterBox(chirp);
				chirps.add(chirp);
			}
		}
		
		for(int i = 0 ; i < PICS.size() ; i++)
		{
			IMovingImage pic = PICS.poll();
			
			if(pic.getY() > height)
			{
				pic = null;
			}
			else
			{
				renderer.updateImage(pic);
				PICS.add(pic);
			}
		}
		
		for(int i = 0 ; i < FLYING_PICS.size() ; i++)
		{
			IMovingImage pic = FLYING_PICS.poll();
			
			if(pic.getX() > width)
			{
				pic = null;
			}
			else
			{
				renderer.updateImage(pic);
				FLYING_PICS.add(pic);
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
		//WAIT = false;

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
			PICS.add(new FallingImage(loadLocalImage("m2o_banner2.png"), "noMove"));
		}
		if(key == '2')
		{
			PICS.add(new FallingImage(loadLocalImage("m2o_banner2.png"), "random"));
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
			while(FLYING_PICS.size() > 0)
			{
				FLYING_PICS.poll();
			}
		}
		
		if (key == '/')
		{
			inThePipe.killTweet();
		}
	}		  
}

