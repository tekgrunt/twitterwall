

import java.util.LinkedList;
import java.util.Queue;

import processing.core.PApplet;
import processing.core.PImage;

public class Calling extends PApplet{

	private static final long serialVersionUID = 1L;
	private PImage bannerImage;
	private PImage restricted;

	private int width = 1024;
	private int height = 768;
	private TwitterBox inThePipe;
	
	private int lastY = 0;
	
	static Queue<FallingImage> PICS = new LinkedList<FallingImage>();
	static Queue<FlyingImage> FLYING_PICS = new LinkedList<FlyingImage>();
	private Queue<TwitterBox> chirps = new LinkedList<TwitterBox>();
	public static Queue<TwitterBox> TWEETS = new LinkedList<TwitterBox>();
	public boolean censor = false;
		
	TwitterThread tweetThread;
	TwitterThread2 tweetThread2;
	TwitterThread3 tweetThread3;
	TwitterThread4 tweetThread4;

	int tweetCount = 0;
	
	public void setup()
	{
		tweetThread = new TwitterThread(this);
		tweetThread.start();
		tweetThread2 = new TwitterThread2(this);
		tweetThread2.start();
		tweetThread3 = new TwitterThread3(this);
		tweetThread3.start();
		tweetThread4 = new TwitterThread4(this);
		tweetThread4.start();
		bannerImage = loadImage("m2o_banner2.png","png");
		restricted = loadImage("oops.jpg","jpg");
		frameRate(30);		
		size(width, height);
	}//end setup
	
	public void removeDuplicates(TwitterBox tester)
	{
		TwitterBox temp;
		
		for(int i = 0 ; i < chirps.size() ; i++)
		{
			temp = chirps.poll();
			if(temp.first.equals(tester.first))
			{
				temp = null;
				System.out.println("Removing duplicate");
			}
			else
			{
				chirps.add(temp);
			}
		}
	}
	
	public void draw()
	{
		background(1);		

		stroke(250,250,250);
		fill(250,250,250);
				
		if(lastY <= 0)
		{
			TwitterBox temp;
			temp = TWEETS.poll();
			//removeDuplicates(temp);
			System.out.println("Getting tweet...");

			if(temp != null)
			{
				chirps.peek();
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
				tweetCount++;
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

	}//end draw
	
	static public void main(String args[]) 
	{
	    PApplet.main(new String[] { "--present", "Calling" });
	}
	
	public void mousePressed() 
	{		
		for(TwitterBox chirp : TWEETS)
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

