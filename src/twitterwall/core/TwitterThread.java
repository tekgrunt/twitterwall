package twitterwall.core;

import java.util.ArrayList;
import java.util.List;

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
	private ArrayList<String> blockedUsers = new ArrayList<String>();

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
		myTwitter = new TwitterFactory().getInstance();
		myTwitter.setOAuthConsumer("DataVisual", "m2blowme2012");	

		topics.add("beautiful");
		
		long startTime = System.currentTimeMillis();
		
		while(true)
		{
			if(System.currentTimeMillis() > startTime + Shared.ADMIN_CYCLE * 1000)
			{
				/*
				 * I agree with keeping the tweet requests down but for this event where we are going to be doing the 
				 * admin over twitter we have to monitor the admin channel closer... none of it is hyper critical but 
				 * should take effect within a couple of minutes. This issue is another strong argument for a web client
				 * as we can poll our DB as much as we want and probably get quite snappy response times.
				 */
				getTweet(Shared.ADMIN_TAG);		
				startTime = System.currentTimeMillis();
			}
			
			if(p.queuedTweetCount() < 20)
			{
				for(int i = 0 ; i < topics.size() ; i++)
				{
					try 
					{
						Thread.sleep(2000);
						getTweet(topics.get(i));
					} 
					catch (InterruptedException e) {
				
						e.printStackTrace();
					}
				}
			}
		}
	}
		
	private long filterSinceId = 0;
	
	private void getTweet(String topic) 
	{
		// creating the query for the given topic
		// setting the number of results we want
		query = new Query(topic);
		query.setSinceId(filterSinceId);
		query.setRpp(4);	
		try 
		{
			//getting the tweets
			result = myTwitter.search(query);
			List<Tweet> tweets = result.getTweets();
			
			System.out.println("Number of tweets: " + tweets.size() + " for topic " + topic);
			
			for(Tweet t : tweets)
			{
				filterSinceId = Math.max(t.getId(), filterSinceId);
	
				if(topic.equalsIgnoreCase(Shared.ADMIN_TAG))
				{
					if(t.getFromUser().equalsIgnoreCase(Shared.ADMIN_USER))
					{
						processAdminCommand(t.getText());
					}
				}
				else
				{
					//don't show tweets from user that we block
					if(!blockedUsers.contains(t.getFromUser()))
					{
						p.addNewTweet(t);
					}
				}
				//no matter what we are storing the tweet data
				processTweetData(t);
			} 
		} 
		catch (TwitterException e) 
		{
			e.printStackTrace();
		}
	}
	
	private void processTweetData(Tweet t)
	{
		if(Shared.LOCAL_DB_ENABLED)
		{
		//	dataConnection.enterTweetData(t);
		}
		
		if(Shared.WEBSERVICE_ENABLED)
		{
			//call here
		}
	}
	
	/*
	 * We will expect admin texts to be of a specific format or we dump them.
	 * We are going to split on the colon. The expected format will be as follows:
	 * #admin #add searchterm 
	 * #admin #remove searchterm
	 * #admin #block username
	 * #admin #question #on 1
	 * #admin #question #off 2
	 */
	private void processAdminCommand(String tweet)
	{
		tweet = tweet.trim();
		String[] temp = tweet.split(" ");
				
		if(temp.length >= 3)//prevents null access
		{
			if(temp[1].equalsIgnoreCase("#add"))
			{
				if(!topics.contains(temp[2].toLowerCase()))
				{
					topics.add(temp[2].toLowerCase());
				}
			}
			else if(temp[1].equalsIgnoreCase("#remove"))
			{	
				topics.remove(temp[2].toLowerCase());
			}
			else if(temp[1].equalsIgnoreCase("#block"))
			{
				if(!blockedUsers.contains(temp[2].toLowerCase()))
				{
					blockedUsers.add(temp[2].toLowerCase());
				}
			}
			else if(temp[1].equalsIgnoreCase("#unblock"))
			{
				blockedUsers.remove(temp[2].toLowerCase());
			}
		}
		
	}
}
