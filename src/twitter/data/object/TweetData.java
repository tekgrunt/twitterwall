package twitter.data.object;

public class TweetData 
{
	private long twitterId;
	private String userName;
	private String tweet;
	private String profileImage;
	private String source;
	private String mediaEntry;
	
	public TweetData(long twitterId, String userName, String tweet, String profileImage, String source, String mediaEntry)
	{
		this.twitterId = twitterId;
		this.userName = userName;
		this.tweet = tweet;
		this.profileImage = profileImage;
		this.source = source;
		this.mediaEntry = mediaEntry;
	}

	public long getTwitterId() {
		return twitterId;
	}

	public void setTwitterId(long twitterId) {
		this.twitterId = twitterId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTweet() {
		return tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
	}

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getMediaEntry() {
		return mediaEntry;
	}

	public void setMediaEntry(String mediaEntry) {
		this.mediaEntry = mediaEntry;
	}
}
