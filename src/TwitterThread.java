import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

import processing.core.PApplet;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;


public class TwitterThread extends Thread{

	private Twitter myTwitter;
	private String lastTweet = "";
	private String trendTopic = "@m2o";

	private Query query;
	private QueryResult result;
	
	private int numTweets = 1;
	private TwitterBox inThePipe;
	private PApplet p;
	
	private int topicIndex = 0;
	private ArrayList<String> topics = new ArrayList<String>();
	private ArrayList<String> lastTopics = new ArrayList<String>();
	
	public TwitterThread(PApplet p)
	{
		this.p = p;
	}
	
	@Override
	public void run()
	{
		System.out.println("*** Inside Thread 1 ***");
		myTwitter = new TwitterFactory().getInstance();
		myTwitter.setOAuthConsumer("DataVisual", "v1su@l");	

		topics.add("m2o");
		topics.add("illuminateyaletown");
		topics.add("iyaletown");
		topics.add("cityandslope");
		topics.add("blueprintevents");
		
//		topics.add("#Thingsthatpissmeoffinthemorning");
//		topics.add("#twitterbirsokakolsaydi");
//		topics.add("#BabyHorse");
//		topics.add("Romney 39");
		
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
	
	public void setNewTopic(String tweet)
	{
		
	}
	
	public void removeExisitingTopic(String tweet)
	{
		
	}
	
	private void getTweet(String topic, int topicIndex) 
	{
		query = new Query(topic);
		query.setRpp(3);	
		try 
		{
			result = myTwitter.search(query);
			List<Tweet> tweets = result.getTweets();
			
			System.out.println("--- Number of returned tweets: " + tweets.size());
			
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
						inThePipe = new TwitterBox(t, p);
						System.out.println("$$$ Adding unique item");
						Shared.TWEETS.add(inThePipe);
						lastTopics.set(topicIndex, t.getText());
						Calling.TWEET_COUNT++;
						System.out.println("Tweet Count: " + Calling.TWEET_COUNT + "  TWEETS: " + Shared.TWEETS.size() + "  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>   " + t.getText());
					}
					else
					{
						System.out.println("$$$ Disgarding item");
						inThePipe = null;
					}
				} 
		} 
		catch (TwitterException e) 
		{
			e.printStackTrace();
		}
	//	return null;
	}
}
