package twitterwall.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
	public boolean showWelcome = false;
	
	private static int TotalTweetCount = 0;
	private static int RenderedTweetCount = 0;

	private TwitterThread tweetThread;
	private Renderer renderer;

	private static Pattern instagramURLPattern = Pattern.compile("og:image\" content=\"([^\"]+)", Pattern.MULTILINE);
	
	private ArrayList<Question> listOfQuestions = new ArrayList<Question>();
	
	private boolean disableImages = false;

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
		listOfQuestions.add(new Question("Enter the #bcamavision contest to win “Basket of Success” prize pack, incl @BCBusiness Magazine membership & several books written by best-selling author @_peterlegge", "1lwyehflkshdlfkjsldfkgasd"));
		listOfQuestions.add(new Question("Find a BCAMA member who has been a member for at least 5 yrs & tag them in a tweet for a chance to win!", "1lwyehflkshdlfkjsldfkgasd"));
		listOfQuestions.add(new Question("Only 10 more mins for a chance to win! Tag a BCAMA member who has been a member for at least 5 yrs.", "1lwyehflkshdlfkjsldfkgasd"));
		listOfQuestions.add(new Question("Question #2 for the#bcamavision twitter contest coming up. Must answer question b/t 10:30 and 11am", "1lwyehflkshdlfkjsldfkgasd"));
		listOfQuestions.add(new Question("Name 1 of the 13 powerful reasons to be a BCAMA member (as listed on our website.) #bcamavision", "1lwyehflkshdlfkjsldfkgasd"));
		listOfQuestions.add(new Question("Twitter Contest Hint: Answers can be found at the Membership booth. #bcamavision", "1lwyehflkshdlfkjsldfkgasd"));
		listOfQuestions.add(new Question("Question #3 for the #bcamavision twitter contest coming up. Must answer question b/t 12 and 12:30pm", "1lwyehflkshdlfkjsldfkgasd"));
		listOfQuestions.add(new Question("Name a @BCAMA Sponsor that is providing our members discounts for their services/ products. #bcamavision", "1lwyehflkshdlfkjsldfkgasd"));
		listOfQuestions.add(new Question("Twitter Contest Hint: Answers can be found at the Members Lounge. #bcamavision", "1lwyehflkshdlfkjsldfkgasd"));
		listOfQuestions.add(new Question("Question #4 for the #bcamavision twitter contest coming up. Must answer question b/t 12:30 and 1pm", "1lwyehflkshdlfkjsldfkgasd"));
		listOfQuestions.add(new Question("Tag four BCAMA members in a tweet. #bcamavision", "1lwyehflkshdlfkjsldfkgasd"));
		listOfQuestions.add(new Question("#bcamavision contest to win a @BCBusiness Magazine membership & books written by best-selling author @_peterlegge. Must answer question b/t 3 and 3:30pm", "1lwyehflkshdlfkjsldfkgasd"));
		listOfQuestions.add(new Question("The winner of the #bcamavision twitter contest will be announced at the After Party at 5:30, and must be present to claim their “Basket of Success” prize pack.", "1lwyehflkshdlfkjsldfkgasd"));




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
		if(!disableImages)
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
		imageMap.put("fish", new FlyingImage(loadLocalImage("fish.png")));
		imageMap.put("<3", new FallingImage(loadLocalImage("heart.png")));
		imageMap.put("love", new FallingImage(loadLocalImage("heart.png")));
		imageMap.put("madmen", new FallingImage(loadLocalImage("madmen.png")));
		imageMap.put("pigsfly", new FlyingImage(loadLocalImage("pigsFly.png")));
		imageMap.put("shark", new FlyingImage(loadLocalImage("sharkparty.png")));
		imageMap.put("liquid", new FallingImage(loadLocalImage("earthdrop.png")));
		imageMap.put("water", new FallingImage(loadLocalImage("earthdrop.png")));
		imageMap.put("confetti", new FallingImage(loadLocalImage("confetti.png"), "random"));
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
		if (key == '1')
		{
			if(currentQuestion == null)
			{
				currentQuestion = listOfQuestions.get(0);
			}
			else
			{
				currentQuestion = null;
			}
		}
		if (key == '2')
		{
			if(currentQuestion == null)
			{
				currentQuestion = listOfQuestions.get(1);
			}
			else
			{
				currentQuestion = null;
			}
		}
		if (key == '3')
		{
			if(currentQuestion == null)
			{
				currentQuestion = listOfQuestions.get(2);
			}
			else
			{
				currentQuestion = null;
			}
		}
		if (key == '4')
		{
			if(currentQuestion == null)
			{
				currentQuestion = listOfQuestions.get(3);
			}
			else
			{
				currentQuestion = null;
			}
		}
		if (key == '5')
		{
			if(currentQuestion == null)
			{
				currentQuestion = listOfQuestions.get(4);
			}
			else
			{
				currentQuestion = null;
			}
		}
		if (key == '6')
		{
			if(currentQuestion == null)
			{
				currentQuestion = listOfQuestions.get(5);
			}
			else
			{
				currentQuestion = null;
			}
		}
		if (key == '7')
		{
			if(currentQuestion == null)
			{
				currentQuestion = listOfQuestions.get(6);
			}
			else
			{
				currentQuestion = null;
			}
		}
		if (key == '8')
		{
			if(currentQuestion == null)
			{
				currentQuestion = listOfQuestions.get(7);
			}
			else
			{
				currentQuestion = null;
			}
		}
		if (key == '9')
		{
			if(currentQuestion == null)
			{
				currentQuestion = listOfQuestions.get(8);
			}
			else
			{
				currentQuestion = null;
			}
		}
		if (key == 'q')
		{
			if(currentQuestion == null)
			{
				currentQuestion = listOfQuestions.get(9);
			}
			else
			{
				currentQuestion = null;
			}
		}
		if (key == 'w')
		{
			if(currentQuestion == null)
			{
				currentQuestion = listOfQuestions.get(10);
			}
			else
			{
				currentQuestion = null;
			}
		}
		if (key == 'e')
		{
			if(currentQuestion == null)
			{
				currentQuestion = listOfQuestions.get(11);
			}
			else
			{
				currentQuestion = null;
			}
		}
		if (key == 'r')
		{
			if(currentQuestion == null)
			{
				currentQuestion = listOfQuestions.get(12);
			}
			else
			{
				currentQuestion = null;
			}
		}
		if (key == 't')
		{
			renderer.toggleBackground();
		}
		if(key == 'y')
		{
			renderer.toggleCensor();
		}
		if(key == 'u')
		{
			renderer.toggleWelcome();
		}
		if(key == 'i')
		{
			System.gc();
		}
		if(key == 'o')
		{
			partyTime("sharkparty");
		}
		
		if (key == 'p')
		{
			partyTime("catparty");
		}

		if (key == 'a')
		{
			partyTime("anvil");
		}
		if(key == 's')
		{
			partyTime("madmen");
		}
		if(key == 'd')
		{
			partyTime("cat");
		}
		if(key == 'f')
		{
			partyTime("pigsflyparty");
		}
		if(key == 'g')
		{
			partyTime("love");
		}
		if(key == 'h')
		{
			partyTime("fish");
		}
		if(key == 'j')
		{
			partyTime("shark");
		}
		if(key == 'k')
		{
			partyTime("confettiparty");
		}
		if(key == 'l')
		{
			partyTime("liquid");
		}
		if(key == 'z')
		{
			if(disableImages)
			{
				disableImages = false;
			}
			else
			{
				disableImages = true;
			}
		}

	}		  
}

