

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import processing.core.PApplet;
import processing.core.PImage;
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

	private int width = 1024;
	private int height = 768;
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
	static Queue<FallingImage> PICS = new LinkedList<FallingImage>();
	static Queue<FlyingImage> FLYING_PICS = new LinkedList<FlyingImage>();
	private Queue<TwitterBox> chirps = new LinkedList<TwitterBox>();
	public boolean censor = false;
	TwitterThread tweetThread;
	public static int TWEET_COUNT = 0;

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
		
		bannerImage = loadImage("m2o_banner2.png","png");
		restricted = loadImage("oops.jpg","jpg");
		frameRate(30);		
		size(width, height);
	}//end setup
	

	/*
	 * The draw() function is a loop that runs at a defined frame rate. During each loop the screen is 
	 * re-drawn. Each loop you have to setup the background colour, text colour, etc like layers.
	 */
	public void draw()
	{
		background(1);		

		stroke(250,250,250);
		fill(250,250,250);
	
		if(lastY <= 0)
		{
			TwitterBox temp;
			temp = Shared.TWEETS.poll();
			
			System.out.println("Getting tweet...");

			if(temp != null)
			{
				if(temp.getImageSize() > 0)
				{
					temp.setY(-1 * temp.getImageSize());
					lastY = temp.getImageSize()+120;
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
				chirp.updateTwitterBox(this);
				chirps.add(chirp);
			}
		}
		
		for(int i = 0 ; i < PICS.size() ; i++)
		{
			FallingImage pic = PICS.poll();
			
			if(pic.getY() > height)
			{
				pic = null;
			}
			else
			{
				pic.updateImage();
				PICS.add(pic);
			}
		}
		
		for(int i = 0 ; i < FLYING_PICS.size() ; i++)
		{
			FlyingImage pic = FLYING_PICS.poll();
			
			if(pic.getX() > width)
			{
				pic = null;
			}
			else
			{
				pic.updateImage();
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

	}//end draw
	
	static public void main(String args[]) 
	{
	    PApplet.main(new String[] { "--present", "Calling" });
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
			  PICS.add(new FallingImage(loadImage("fallingcat.png","png"), this));
		}
		if (key == 'a')
		{
			PICS.add(new FallingImage(loadImage("anvil.png","png"), this));
		}
		if(key == 'm')
		{
			PICS.add(new FallingImage(loadImage("madmen.png","png"), this));
		}
		if(key == '1')
		{
			PICS.add(new FallingImage(loadImage("m2o_banner2.png","png"), this, "noMove"));
		}
		if(key == '2')
		{
			PICS.add(new FallingImage(loadImage("m2o_banner2.png","png"), this, "random"));
		}
		if(key == 'x')
		{
			censor = censor?false:true;
		}
		if(key == 'k')
		{
			 FLYING_PICS.add(new FlyingImage(loadImage("fallingcat.png","png"), this));
		}
		if(key == 'p')
		{
			 FLYING_PICS.add(new FlyingImage(loadImage("pigsFly.png","png"), this));
		}
		if(key == 'h')
		{
			 PICS.add(new FallingImage(loadImage("heart.png","png"), this));
		}
		if(key == 'f')
		{
			 FLYING_PICS.add(new FlyingImage(loadImage("fish.png","png"), this));
		}
		if(key == 's')
		{
			 FLYING_PICS.add(new FlyingImage(loadImage("sharkparty.png","png"), this));
		}
		if(key == 'r')
		{
			 PICS.add(new FallingImage(loadImage("rainbow.png","png"), this));
		}
		if(key == 'd')
		{
			 PICS.add(new FallingImage(loadImage("dog.png","png"), this));
		}
		if(key == 'n')
		{
			 PICS.add(new FallingImage(loadImage("squirrel.png","png"), this));
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
			inThePipe.killTweet();
		
	}		  
}//end class

