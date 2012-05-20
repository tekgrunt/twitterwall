package twitter.data.webservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import twitter4j.Tweet;
/**
 * This should currently be thought of as a stub. I was having trouble sending URLs over GET and was unable to either send POST data
 * or pull it out on the server... I hacked at this for the better part of a day - for the time being I am going to store all data 
 * fields locally in the DB and just send some specific data to the server over GET. I am alos going to set up an end point
 * so that the admin user can add hash tags to the DB on the server and we will pick them up in the twitter wall and start searching 
 * for them... maybe check with the server once every couple of minutes or something.
 * 
 * @author christopherluft
 *
 */
public class WebService 
{	
	public WebService()
	{

	}

	public void sendTweetData(Tweet tweet)
	{
		String tweetData = "?twitter_id=" + tweet.getFromUserId() + "&user_name=" + tweet.getFromUser();// + "&profile_image=" + profile_image;//"&tweet=" + tweet ;//+ "&profile_image=" + profile_image + "&source=" + source + "&media_entry=" + media_entry;
				
		try 
		{			
			String encodedString = URLEncoder.encode(tweetData,"UTF-8");			
			URL encodedUrl = new URL("http://christopherluft.com/TwitterWallServer/insert.php" + encodedString);
			
			System.out.println("****************** " + "http://christopherluft.com/TwitterWallServer/insert.php" + encodedString);
			
			URLConnection uc = encodedUrl.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			String inputLine;

	        while ((inputLine = in.readLine()) != null) 
	        {
	            System.out.println(inputLine);
	        }
	        in.close();
		} 
		catch (MalformedURLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static ArrayList<String> getSearchTerms() 
	{		
		String resultString = "";
		try 
		{			
		//	String encodedString = URLEncoder.encode(tweetData,"UTF-8");			
			URL encodedUrl = new URL("http://christopherluft.com/TwitterWallServer/searchTerms.php" );
						
			URLConnection uc = encodedUrl.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			String inputLine;

	        while ((inputLine = in.readLine()) != null) 
	        {
	            resultString = resultString.concat(inputLine);
	        }
	        in.close();
		} 
		catch (MalformedURLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("********   " + resultString);
		return null;
	}
	
}
