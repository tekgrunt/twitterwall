package twitterwall.core;

import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import processing.core.PApplet;
import processing.core.PImage;

import twitter.data.object.FallingImage;
import twitter.data.object.FlyingImage;
import twitter.data.object.IMovingImage;
import twitter.data.object.Question;
import twitter.data.object.TweetSource;
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
	private LinkedList<TwitterBox> displayingTweets = new LinkedList<TwitterBox>();
	
	private Queue<TwitterBox> allTweets = new LinkedList<TwitterBox>();
	
	private Question currentQuestion;
	
	private static HashMap<String, IMovingImage> imageMap;
	
	public boolean censor = false;
	
	private static int TotalTweetCount = 0;
	private static int RenderedTweetCount = 0;

	private TwitterThread tweetThread;
	private Renderer renderer;

	private static Pattern instagramURLPattern = Pattern.compile("og:image\" content=\"([^\"]+)", Pattern.MULTILINE);

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
		currentQuestion = new Question("How much wood could a woodchuck chuck if a woodchuck could chuck wood?? How much wood could a woodchuck chuck if a woodchuck could chuck wood??", "love");
	}
	
	/*
	 * The easter egg detector for cat party and friends
	 */
	public void parseEasterEggKeywords(TwitterBox box)
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
					if(((FallingImage)image).isFullScreen())
					{
						imageQueue.add(new FallingImage(image.getImage(), "random"));
					}
					else
					{
						imageQueue.add(new FallingImage(image.getImage()));
					}
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
		imageMap.put("confetti", new FallingImage(loadLocalImage("confetti.png"), "random"));
		
		imageMap.put("and", new FallingImage(loadLocalImage("dog.png")));
		imageMap.put("if", new FlyingImage(loadLocalImage("sharkparty.png")));
		imageMap.put("to", new FlyingImage(loadLocalImage("fish.png")));
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
	
	public void addNewTweet(Tweet tweet)
	{
		TwitterBox tb = createTwitterBox(tweet);
		allTweets.add(tb);
		if(currentQuestion != null && !currentQuestion.isAnsweredCorrectly())
		{
			if(currentQuestion.trySetCorrectAnswer(tb))
			{
				// queue the confetti... we have a winner!!
				partyTime("confetti");
			}
		}
	}
	
	public int queuedTweetCount()
	{
		return allTweets.size();
	}
	
	public void setCurrentQuestion(Question q)
	{
		currentQuestion = q;
	}
	
	public Question getCurrentQuestion()
	{
		return currentQuestion;
	}
	
	private TwitterBox createTwitterBox(Tweet tweet)
	{
		TwitterBox tb = new TwitterBox(tweet);
		try
		{
			tb.setUserImage(loadImage(tweet.getProfileImageUrl(), "png"));
		//	System.out.println("Profile Image:" + tweet.getProfileImageUrl());
		}
		catch(Exception ex)
		{
			System.out.println("Couldn't load profile image.");
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
				System.out.println("Couldn't load media content.");
				ex.printStackTrace();
			}
		}
		else if (tb.getSource() == TweetSource.Instagram)
		{
			processInstagramTweet(tb);
		}
		TotalTweetCount++;
		return tb;
	}
	
	private void processInstagramTweet(TwitterBox tb)
	{
		InputStream reader = null;
		try
		{
			int index = tb.getText().indexOf("http://t.co");
			if(index > 0)
			{
				String path = tb.getText().substring(index);
				URL url = new URL(path);
				url.openConnection();
				
				byte[] buffer = new byte[10000];
				reader = url.openStream();
		        while (reader.read(buffer) > 0)
		        { 
		        	String s = new String(buffer, "UTF-8");
		        	Matcher matcher = instagramURLPattern.matcher(s);
		        	if(matcher.find())
		        	{
			        	//System.out.println(matcher.group(1));
			        	tb.setImage(loadImage(matcher.group(1)));
		        	}
		        }
			}
		}
		catch (MalformedURLException ex)
		{
			System.out.println("Requested URL was malformed.");
			ex.printStackTrace();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if(reader != null)
			{
				try
				{
					reader.close();
				}
				catch(Exception ex)
				{
				}
			}
		}
	}
	
	/*
	 * The draw() function is a loop that runs at a defined frame rate. During each loop the screen is 
	 * re-drawn. Each loop you have to setup the background colour, text colour, etc like layers.
	 */
	public void draw()
	{
		renderer.renderBackground();
		
		if (displayingTweets.size() < 8)
		{
			int index = displayingTweets.size() -1;
			TwitterBox temp = allTweets.poll();
			
			if(temp != null)
			{	
				RenderedTweetCount++;
				if(RenderedTweetCount % Shared.BackgroundSwitchAmount == 0)
				{
					renderer.nextBackground();
				}
				parseColor(temp);
				
				// determine the starting location for this twitterbox.
				int previousTweetLocation = 0;
				if(index >= 0)
				{ 
					TwitterBox previous = displayingTweets.get(index);
					previousTweetLocation = previous.getY();
				}
				int offset = temp.getImageHeight() + temp.getHeight() + 52;
				temp.setY(previousTweetLocation - offset);
				displayingTweets.add(temp);
				
				// if we only have enough to display on screen add this one back to the queue
				if(allTweets.size() < 8)
				{
					addNewTweet(temp.getTweet());
				}
			}
		}
		
		for(int i = 0; i < displayingTweets.size();)
		{
			TwitterBox tb = displayingTweets.get(i);
			if(tb.getY() > height)
			{
				displayingTweets.remove(tb);
				tb.dispose();
				tb = null;
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
	}
	
	static public void main(String args[]) 
	{
	    PApplet.main(new String[] { "--present", "twitterwall.core.Calling" });
	}
	
	public void mousePressed() 
	{		
		for(TwitterBox chirp : allTweets)
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
			currentQuestion = null;
		}
		if(key == 'e')
		{
			allTweets.clear();
			imageQueue.clear();
		}
		if(key == 'g')
		{
			System.gc();
		}
	}		  
}

