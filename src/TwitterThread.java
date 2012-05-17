import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import twitter.data.object.TweetData;
import twitter.data.webservice.WebService;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitterwall.data.connection.DataConnection;

import com.mysql.jdbc.Connection;

/**
 * I initially had the calls to get new Tweets in the main thread; however, on each call there 
 * was a little freeze. I broke this off into its own thread and things got a lot better but still 
 * having the problem with the GC.
 * 
 * @author christopherluft
 *
 */
public class TwitterThread extends Thread{

	private Twitter myTwitter;
	private Query query;
	private QueryResult result;
	private TwitterBox inThePipe;
	private PApplet p;
	private WebService webService; //<-- dead until I sort out what I was working on.
	private DataConnection dataConnection;
	private Connection conn;
	
	/*
	 * The topics array lets us cycle through and poll more than one topic.
	 */
	private ArrayList<String> topics = new ArrayList<String>();
	/*
	 * I can't rmember exactly but I think use this array to check to make sure any of the new items 
	 * coming in are already in the array
	 */
	private ArrayList<String> lastTopics = new ArrayList<String>();
	
	public TwitterThread(PApplet p)
	{
		this.p = p;
		webService = new WebService();
		dataConnection = new DataConnection();
	}
	
	@Override
	public void run()
	{
		System.out.println("*** Inside Thread 1 ***");
		myTwitter = new TwitterFactory().getInstance();
		myTwitter.setOAuthConsumer("DataVisual", "m2blowme2012");	

		topics.add("schoolmemories");
//		topics.add("#Thingsthatpissmeoffinthemorning");
//		topics.add("#twitterbirsokakolsaydi");
//		topics.add("#BabyHorse");
//		topics.add("Romney 39");
		
		//can't remember why this is here
		for(String setup : topics)
		{
			lastTopics.add("");
		}
		
		while(true)
		{
			for(int i = 0 ; i < topics.size() ; i++)
			{
				try 
				{
					Thread.sleep(800);
					
					getTweet(topics.get(i), i);
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/*
	 * Basically I am just polling the Twitter stream and pulling the top 3 items. This was a bit if a 
	 * heuristic... if I remember correctly, when I had it set quite a bit higher (maybe 10 or 15?) the queue
	 * was growing faster than it was falling off if the channels we were watching were very busy - I don't think
	 * this will be a problem as it stands but it would be nice to address it in a more elegant way. Also, in theory 
	 * we could miss some Tweets if more than 3 are posted within the 800 millisecond loop - I do think I saw
	 * some of this type of during Illuminate. 
	 */
	private void getTweet(String topic, int topicIndex) 
	{
		//creating the query for the given topic
		query = new Query(topic);
		//setting the number of results we want
		query.setRpp(3);	
		try 
		{
			//getting the tweets
			result = myTwitter.search(query);
			List<Tweet> tweets = result.getTweets();
			
			System.out.println("--- Number of returned tweets: " + tweets.size());
			
			//cycling through the tweets to see if we grabbed any duplicates and if so dumping them 
			for(Tweet t : tweets)
			{
				int notDuplicate = -1;
				
					System.out.println("Adding new tweet for topic: " + topics.get(topicIndex));
					
					for(TwitterBox box : Shared.TWEETS)
					{
						if(box.chirp.getText().equals(t.getText()))
						{
							notDuplicate++;
						}
					}
					
					if(notDuplicate < 0)
					{	
						String mediaEntry = "";
						
						//this is where we are going to dump into the db
						//webService.sendTweetData(t.getFromUserId(), t.getFromUser(), t.getText(), t.getProfileImageUrl(), t.getSource(), mediaEntry);
						if(Shared.LOCAL_DB_ENABLED)
						{
							dataConnection.enterTweetData(new TweetData(t.getId(), t.getFromUser(), t.getText(), t.getProfileImageUrl(), t.getSource(), ""));
						}
						inThePipe = new TwitterBox(t, p);
						System.out.println("*** Adding unique item");
						Shared.TWEETS.add(inThePipe);
						lastTopics.set(topicIndex, t.getText());
						Calling.TWEET_COUNT++;
						System.out.println("Tweet Count: " + Calling.TWEET_COUNT + "  TWEETS: " + Shared.TWEETS.size() + "  >>>  " + t.getText());
					}
					else
					{
						System.out.println("*** Disgarding item");
						inThePipe = null;
					}
				} 
		} 
		catch (TwitterException e) 
		{
			e.printStackTrace();
		}
	}
}
