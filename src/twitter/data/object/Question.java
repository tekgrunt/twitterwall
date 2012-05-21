package twitter.data.object;

import processing.core.PImage;
import twitterwall.core.TwitterBox;

public class Question 
{
	private String question = "";
	private String answer = "";
	private PImage correctUserImage;
	private String correctUserName;
	
	public Question(String parseableQuestionAndAnswer)
	{
		//do nothing yet... this could be used if we want to pass the question and answer together and have it parse them into individual entries.
	}
	
	public Question(String question, String answer)
	{
		this.question = question;
		this.answer = answer;
	}
	
	public String getQuestionText()
	{
		return question;
	}
	
	public String getAnswerText()
	{
		return answer;
	}
	
	/* This will check the correctness of the text of the tweet as the answer to this question.
	 * If the text contains the correct anser this will set it to the correct answer tweet.
	 * */
	public boolean trySetCorrectAnswer(TwitterBox tweet)
	{
		if (correctUserName == null && answer.length() != 0 && tweet.getText().contains(answer.toLowerCase()))
		{
			correctUserName = tweet.getTweet().getFromUser();
			correctUserImage = tweet.getUserImage();
			return true;
		}
		return false;
	}
	
	public String correctAnswerUserName()
	{
		return correctUserName;
	}
	
	public PImage correctAnswerUserImage()
	{
		return correctUserImage;
	}
	
	public boolean isAnsweredCorrectly()
	{
		return correctUserName != null;
	}
}
