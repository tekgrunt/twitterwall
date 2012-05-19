package twitterwall.core;

import java.awt.Color;
import java.util.HashMap;

import processing.core.PFont;
import processing.core.PImage;
import twitter.data.object.IMovingImage;
import twitter.data.object.TweetSource;

public class Renderer 
{
	private PFont font;
	private Calling p;

	private HashMap<String, Color> colorMap;
	private HashMap<TweetSource, PImage> sourceMap;
	
	public Renderer(Calling p)
	{
		this.p = p;
		font = p.loadFont("fonts/Verdana-20.vlw");
		buildColorMap();
		buildSourceMap();
	}
	
	public PFont getFont()
	{
		return font;
	}
	
	private void buildSourceMap()
	{
		sourceMap = new HashMap<TweetSource, PImage>();
		sourceMap.put(TweetSource.Web, loadIcon("web.png"));
		sourceMap.put(TweetSource.Mobile, loadIcon("mobile.png"));
		sourceMap.put(TweetSource.Android, loadIcon("android.png"));
		sourceMap.put(TweetSource.iOS, loadIcon("ios.png"));
		sourceMap.put(TweetSource.Blackberry, loadIcon("blackberry.png"));
		sourceMap.put(TweetSource.Echofon, loadIcon("echofon.png"));
		sourceMap.put(TweetSource.Hootsuite, loadIcon("hootsuite.png"));
		sourceMap.put(TweetSource.Instagram, loadIcon("instagram.png"));
		sourceMap.put(TweetSource.Plume, loadIcon("plume.png"));
		sourceMap.put(TweetSource.Ubersocial, loadIcon("ubersocial.png"));
		sourceMap.put(TweetSource.Tweetbot, loadIcon("tweetbot.png"));
		sourceMap.put(TweetSource.Tweetcaster, loadIcon("tweetcaster.png"));
		sourceMap.put(TweetSource.TweetDeck, loadIcon("tweetdeck.png"));
		sourceMap.put(TweetSource.TwitterFeed, loadIcon("twitterfeed.png"));
	}
	
	private PImage loadIcon(String name)
	{
		PImage toReturn = p.loadLocalIcon(name);
		toReturn.resize(Shared.IconSize, Shared.IconSize);
		return toReturn;
	}
	
	private void buildColorMap()
	{
		colorMap = new HashMap<String, Color>();
		colorMap.put("blue", new Color(0, 0, 250));
		colorMap.put("red", new Color(250, 0, 0));
		colorMap.put("green", new Color(0, 250, 0));
		colorMap.put("yellow", new Color(250, 250, 0));
		colorMap.put("pink", new Color(249, 74, 173));
		colorMap.put("purple", new Color(203, 25, 201));
		colorMap.put("brown", new Color(188, 113, 5));
		colorMap.put("orange", new Color(247,167,12));
		colorMap.put("white", new Color(250, 250, 250));
	}
	
	private void colourTweetText(String color)
	{	
		Color c = (colorMap.containsKey(color)) ? colorMap.get(color) : colorMap.get("white");
		this.p.stroke(c.getRed(), c.getGreen(), c.getBlue());
		this.p.fill(c.getRed(), c.getGreen(), c.getBlue());
	}
	
	/*
	 * The object updates its own position on the screen during each loop - sorry for the magic numbers
	 */
	public void updateTwitterBox(TwitterBox box)
	{
		box.y += 1;
		p.textFont(font);
		
		colourTweetText(box.getTextColour());

		if(box.getSecondLine() == null || box.getSecondLine().length() == 0)
		{
			p.text(box.getFirstLine(), box.x + 70, box.y + 30);
		}
		else
		{
			p.text(box.getFirstLine(), box.x + 70, box.y + 15);
			p.text(box.getSecondLine(), box.x + 70, box.y + 45);	
		}
		if(box.getUserImage() != null)
		{
			p.image(box.getUserImage(), box.x, box.y);
		}
		if(box.getSource() != TweetSource.Unknown)
		{
			p.image(sourceMap.get(box.getSource()), box.x + 964 + 10, box.y);
		}
		if(box.getImage() != null)
		{
			p.image(box.getImage(), box.x + 200, box.y + 100);	
		}
	}
	
	public void updateImage(IMovingImage image)
	{
		p.image(image.getImage(), image.getX(), image.getY());
		image.applyUpdate();
	}
}
