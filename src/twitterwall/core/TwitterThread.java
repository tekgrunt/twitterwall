package twitterwall.core;

import java.util.ArrayList;
import java.util.List;

import twitter.data.object.TweetData;
import twitter.data.object.TweetSource;
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
public class TwitterThread extends Thread
{
	private Twitter myTwitter;
	private Query query;
	private QueryResult result;
	private Calling p;
	private WebService webService; //<-- dead until I sort out what I was working on.
	private DataConnection dataConnection;
	private Connection conn;
	
	/*
	 * The topics array lets us cycle through and poll more than one topic.
	 */
	private ArrayList<String> topics = new ArrayList<String>();

	public TwitterThread(Calling p)
	{
		this.p = p;
		webService = new WebService();
		if(Shared.LOCAL_DB_ENABLED)
		{
			dataConnection = new DataConnection();
		}
	}
	
	@Override
	public void run()
	{
	//	System.out.println("*** Inside Thread 1 ***");
		myTwitter = new TwitterFactory().getInstance();
		myTwitter.setOAuthConsumer("DataVisual", "m2blowme2012");	

		topics.add("beautiful");
//		topics.add("#ifonlyyoucould");
//		topics.add("#Thingsthatpissmeoffinthemorning");
//		topics.add("#twitterbirsokakolsaydi");
//		topics.add("#BabyHorse");
//		topics.add("Romney 39");
		
		while(true)
		{
			for(int i = 0 ; i < topics.size() ; i++)
			{
				try 
				{
					Thread.sleep(2000);
					
					if(Shared.TWEETS.size() < 20)
					{
						getTweet(topics.get(i), i);
					}
				} 
				
				catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private long filterSinceId = 0;
	
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
		// creating the query for the given topic
		// setting the number of results we want
		query = new Query(topic);
		query.setSinceId(filterSinceId);
		query.setRpp(100);	
		try 
		{
			//getting the tweets
			result = myTwitter.search(query);
			List<Tweet> tweets = result.getTweets();
			
		//	System.out.println("Query returned " + tweets.size());
			
			for(Tweet t : tweets)
			{
				filterSinceId = Math.max(t.getId(), filterSinceId);
			//	System.out.println("FilterKey: " + filterSinceId + " " + t.getCreatedAt());
			//	System.out.println("Adding new tweet for topic: " + topics.get(topicIndex));

				//this is where we are going to dump into the db
				//webService.sendTweetData(t.getFromUserId(), t.getFromUser(), t.getText(), t.getProfileImageUrl(), t.getSource(), mediaEntry);
				if(Shared.LOCAL_DB_ENABLED)
				{
				//	dataConnection.enterTweetData(t);
				}
				
				if(Shared.WEBSERVICE_ENABLED)
				{
					//call here
				}
				//System.out.println("*** Adding unique item");
				Shared.TWEETS.add(p.createTwitterBox(t));

				Calling.TotalTweetCount++;
			//	System.out.println("Tweet Count: " + Calling.TWEET_COUNT + "  TWEETS: " + Shared.TWEETS.size() + "  >>>  " + t.getText());
			} 
		} 
		catch (TwitterException e) 
		{
			e.printStackTrace();
		}
	}
}
