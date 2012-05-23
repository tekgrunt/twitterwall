package twitterwall.data.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import twitter4j.Tweet;

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
	public void enterTweetData(Tweet t)
	{			
		try
		{		
			PreparedStatement ps = conn.prepareStatement("insert into tweet_details (twitter_id, user_name, tweet, profile_image, source, media_entry, timestamp) values (?,?,?,?,?,'', NOW());");			
			ps.setLong(1, t.getFromUserId());
			ps.setString(2, t.getFromUser());
			ps.setString(3, t.getText());
			ps.setString(4, t.getProfileImageUrl());
			ps.setString(5, t.getSource());
				
			ps.execute();
				
			ps.close();
		} 
		catch (SQLException sqle) 
		{
			System.out.println("Database TweetData insertion error:  : " + sqle);
		}
	}
}
