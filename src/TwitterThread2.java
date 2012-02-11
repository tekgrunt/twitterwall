import java.util.List;

import processing.core.PApplet;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;


public class TwitterThread2 extends Thread{

	private Twitter myTwitter;
	private String lastTweet = "";
	private String trendTopic = "#illuminate";
	private Query query;
	private QueryResult result;
	
	private int numTweets = 1;
	private TwitterBox inThePipe;
	private PApplet p;
	
	public TwitterThread2(PApplet p)
	{
		this.p = p;
	}

	@Override
	public void run()
	{
		System.out.println("*** Inside Thread 2 ***");
		myTwitter = new TwitterFactory().getInstance();
		myTwitter.setOAuthConsumer("DataVisual", "v1su@l");	
		query = new Query(trendTopic);

		while(true)
		{
			try {
				Thread.sleep(2900);
				getTweet();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private TwitterBox getTweet() 
	{
		query.setRpp(numTweets);	
		try 
		{
			result = myTwitter.search(query);
			List<Tweet> tweets = result.getTweets();			
			for(Tweet t : tweets)
			{
				if(!t.getText().equals(lastTweet))
				{
					inThePipe = new TwitterBox(t, p);
					//chirps.add(inThePipe);
					Calling.TWEETS.add(inThePipe);
					lastTweet = t.getText();
					System.out.println(">>>>>>>>>>>>>>>   " + t.getText());
					return inThePipe;
				} 
				else
				{
					lastTweet = t.getText();
				}
			}
		} 
		catch (TwitterException e) 
		{
			e.printStackTrace();
		}
		return null;
	}
}
