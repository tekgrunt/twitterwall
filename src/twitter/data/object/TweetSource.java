package twitter.data.object;

public enum TweetSource 
{
	Unknown,
	Web,
	Mobile,
	iOS,
	Android,
	Blackberry,
	Plume, 
	Ubersocial,
	Echofon,
	Instagram,
	TwitterFeed,
	TweetDeck,
	Tweetcaster,
	Tweetbot,
	Twicca,
	SocialOomph,
	Buffer,
	Hootsuite;

	
	public static TweetSource create(String url)
	{
		if(url.contains(">web<"))
		{
			return TweetSource.Web;
		}
		else if(url.contains("Twitter for iPhone") || (url.contains("Twitter for iPad")))
		{
			return TweetSource.iOS;
		}
		else if(url.contains("Twitter for Android"))
		{
			return TweetSource.Android;
		}
		else if(url.contains("Twitter for BlackBerry"))
		{
			return TweetSource.Blackberry;
		}
		else if(url.contains("HootSuite"))
		{
			return TweetSource.Hootsuite;
		}
		else if(url.contains("mobile.twitter.com"))
		{
			return TweetSource.Mobile;
		}
		else if(url.contains("http://instagr.am"))
		{
			return TweetSource.Instagram;
		}
		else if(url.contains(">Plume"))
		{
			return TweetSource.Plume;
		}
		else if(url.contains("ubersocial.com"))
		{
			return TweetSource.Ubersocial;
		}
		else if(url.contains("Echofon"))
		{
			return TweetSource.Echofon;
		}
		else if(url.contains("http://www.tweetcaster.com"))
		{
			return TweetSource.Tweetcaster;
		}
		else if(url.contains("http://tapbots.com/tweetbot"))
		{
			return TweetSource.Tweetbot;
		}
		else if(url.contains("http://twitterfeed.com"))
		{
			return TweetSource.TwitterFeed;
		}
		else if(url.contains("http://www.tweetdeck.com"))
		{
			return TweetSource.TweetDeck;
		}
		else if(url.contains("http://www.socialoomph.com"))
		{
			return TweetSource.SocialOomph;
		}
		else if(url.contains(">twicca"))
		{
			return TweetSource.Twicca;
		}
		else if(url.contains("http://bufferapp.com"))
		{
			return TweetSource.Buffer;
		}
		else
		{
			System.out.println("SOURCE: " + url);
		}
		return TweetSource.Unknown;
	}
}
