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
	
	public HashMap<String, Color> getColorMap()
	{
		return colorMap;
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
		
//		colorMap.put("my", new Color(250, 250, 0));
//		colorMap.put("so", new Color(249, 74, 173));
//		colorMap.put("rt", new Color(203, 25, 201));
	}
	
	/*
	 * The object updates its own position on the screen during each loop - sorry for the magic numbers
	 */
	public void updateTwitterBox(TwitterBox box)
	{
		int offset = 0;
		
		box.y++;
		p.textFont(font);

		Color c = (colorMap.containsKey(box.getTextColor())) ? colorMap.get(box.getTextColor()) : colorMap.get("white");
		this.p.fill(0, 0, 0);
		this.p.stroke(c.getRed(), c.getGreen(), c.getBlue());
		p.rect(box.x + 65, box.y, 890, box.getHeight());
		
		this.p.fill(c.getRed(), c.getGreen(), c.getBlue());
		p.text(box.getFirstLine(), box.x + 80, box.y + 34);
		
		if(box.getSecondLine().length() != 0)
		{
			p.text(box.getSecondLine(), box.x + 80, box.y + 64);
			offset = 14;
		}
		if(box.getUserImage() != null)
		{
			// draw a 2px border around the user picture. Then draw the picture.
			int imageX = 6 + box.x;
			int imageY = offset + box.y;
			this.p.fill(250, 250, 250);
			p.rect(imageX, imageY, 51, 51);
			p.image(box.getUserImage(), imageX + 2, imageY + 2);
		}
		if(box.getSource() != TweetSource.Unknown)
		{
			p.image(sourceMap.get(box.getSource()), box.x + 964, box.y + offset);
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
