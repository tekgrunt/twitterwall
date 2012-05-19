package twitterwall.data.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import twitter.data.object.TweetData;

public class DataConnection 
{
	Connection conn = null;

	public DataConnection() 
	{
		getConn();
	}

	public void getConn() 
	{
		// mysql credentials for mac
		String url = "jdbc:mysql://localhost:3306/";
		String db = "local_twitter_db";
		String driver = "com.mysql.jdbc.Driver";
		String user = "root";
		String pass = "root";

		try {
			Class.forName(driver).newInstance();
		} 
		catch (InstantiationException e) 
		{
			e.printStackTrace();
		} 
		catch (IllegalAccessException e) 
		{
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		}

		try 
		{
			conn = DriverManager.getConnection(url + db, user, pass);
			System.out.println("Success making database connection");
		} 
		catch (Exception e) 
		{
			System.out
					.println("The database connector failed to make a connection");
			System.out.println("URL: " + url + " \nDatabase: " + db
					+ " \nUser: " + user + "\nPassword: " + pass);
		}
	}
	
	/*
	 * This one is pretty ugly but it  will work for now
	 */
	public void enterTweetData(TweetData tweetData)
	{			
		try
		{	
			boolean isTweetInDB = false;
			//wrong?? is this one tweet per user or each tweet is unique...?
			PreparedStatement test = conn.prepareStatement("select * from tweet_details where twitter_id = " + tweetData.getTwitterId());
			ResultSet result = test.executeQuery();
			
			while(result.next())
			{
				isTweetInDB = true;
			}
			
			test.close();
			
			if(!isTweetInDB)
			{
				PreparedStatement ps = conn.prepareStatement("insert into tweet_details (twitter_id, user_name, tweet, profile_image, source, media_entry, timestamp) values ("+ tweetData.getTwitterId()+"," +
						"'"+ tweetData.getUserName() +"', '"+ tweetData.getTweet().replaceAll("\'", "\'") +"', '"+ tweetData.getProfileImage() +"', '"+ tweetData.getSource() +"', '"+ tweetData.getMediaEntry() +"', NOW());");			
				ps.execute();
				
				ps.close();
			}
		} catch (SQLException sqle) {
			System.out.println("Database TweetData insertion error:  : " + sqle);
		}
	}
}
