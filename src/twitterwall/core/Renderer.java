package twitterwall.core;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;

import processing.core.PFont;
import processing.core.PImage;
import twitter.data.object.IMovingImage;
import twitter.data.object.TweetSource;

public class Renderer 
{
	private PImage bg;	
	private PImage bannerImage;
	private PImage restricted;
	
	
	private boolean showBackground = true;
	private int currentBackground = 0;
	boolean changingBackground;
	int backgroundAlpha = 0;
	
	
	private PFont font;
	private Calling p;

	private HashMap<String, Color> colorMap;
	private HashMap<TweetSource, PImage> sourceMap;

	private LinkedList<PImage> backgrounds = new LinkedList<PImage>();
	private boolean censored;
	
	public Renderer(Calling p)
	{
		this.p = p;
		font = p.loadFont("fonts/Verdana-20.vlw");
		buildColorMap();
		buildSourceMap();
		loadBackgrounds();
		bg = backgrounds.get(0);
		bannerImage = p.loadLocalImage("m2o_banner2.png");
		restricted = p.loadLocalImage("oops.jpg");
	}
	
	public HashMap<String, Color> getColorMap()
	{
		return colorMap;
	}
	
	public PFont getFont()
	{
		return font;
	}
	
	private void loadBackgrounds()
	{
		backgrounds.add(p.loadLocalBackground("02182_campmeekerwaterfall_1024x768.jpg"));
		backgrounds.add(p.loadLocalBackground("01665_streamssun_1024x768.jpg"));
		backgrounds.add(p.loadLocalBackground("02110_lowerfallsyellowstone_1024x768.jpg"));
		backgrounds.add(p.loadLocalBackground("02118_waterfall_1024x768.jpg"));
		backgrounds.add(p.loadLocalBackground("02174_gazeduponbyangelsintheirflight_1024x768.jpg"));
		backgrounds.add(p.loadLocalBackground("02291_yosemitefalls_1024x768.jpg"));
		backgrounds.add(p.loadLocalBackground("02414_blackforest_1024x768.jpg"));
		backgrounds.add(p.loadLocalBackground("02449_burneyfalls_1024x768.jpg"));
	}
	
	public void toggleBackground()
	{
		showBackground = !showBackground;
	}
	
	public void nextBackground()
	{
		backgroundAlpha = 0;
		changingBackground = true;
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
		sourceMap.put(TweetSource.Twicca, loadIcon("twicca.png"));
		sourceMap.put(TweetSource.SocialOomph, loadIcon("socialoomph.png"));
		sourceMap.put(TweetSource.Buffer, loadIcon("buffer.png"));
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
		colorMap.put("blue", new Color(102, 153, 255));
		colorMap.put("red", Color.red);
		colorMap.put("green", Color.green);
		colorMap.put("yellow", Color.yellow);
		colorMap.put("pink", new Color(255, 153, 204));
		colorMap.put("purple", Color.magenta);
		colorMap.put("brown", new Color(188, 113, 5));
		colorMap.put("orange", Color.orange);
		colorMap.put("white", Color.white);
		colorMap.put("rt", new Color(153, 204, 255));
	}
	
	/*
	 * The object updates its own position on the screen during each loop - sorry for the magic numbers
	 */
	public void updateTwitterBox(TwitterBox box)
	{
		int offset = 0;
		if(box.y > 0 && !box.isCheckedForKeywords())
		{
			box.isCheckedForKeywords(true);
			p.parseEasterEggKeywords(box);
		}
		box.y++;
		p.textFont(font);

		// draw the background for the tweet text.
		Color c = (colorMap.containsKey(box.getTextColor())) ? colorMap.get(box.getTextColor()) : colorMap.get("white");
		this.p.fill(0, 0, 0);
		this.p.stroke(c.getRed(), c.getGreen(), c.getBlue());
		p.rect(box.x + 65, box.y, 890, box.getHeight());
		
		// draw the tweet text
		this.p.fill(c.getRed(), c.getGreen(), c.getBlue());
		p.text(box.getFirstLine(), box.x + 80, box.y + 18, 870, 20);
		
		if(box.getSecondLine().length() != 0)
		{
			p.text(box.getSecondLine(), box.x + 80, box.y + 46, 870, 20);
			offset = 14;
		}
		
		// draw the user's profile image.
		if(box.getUserImage() != null)
		{
			// draw a 2px border around the user picture. Then draw the picture.
			int imageX = 6 + box.x;
			int imageY = offset + box.y;
			this.p.fill(250, 250, 250);
			this.p.stroke(250, 250, 250);
			p.rect(imageX, imageY, 51, 51);
			p.image(box.getUserImage(), imageX + 2, imageY + 2);
		}
		
		// draw the tweet source icon. 
		if(box.getSource() != TweetSource.Unknown)
		{
			p.image(sourceMap.get(box.getSource()), box.x + 964, box.y + offset);
		}
		
		// draw any associated image.
		if(box.getImage() != null)
		{
			PImage image = box.getImage();
			p.image(image, (p.width - image.width) / 2, box.y + box.getHeight() + 7);	
		}
	}
	
	public void updateImage(IMovingImage image)
	{
		p.image(image.getImage(), image.getX(), image.getY());
		image.applyUpdate();
	}

	public void renderScene() 
	{
		p.image(bannerImage, 0, 0);
		if(censored)
		{
			p.image(restricted, 100, 120);
		}
	}

	public void renderBackground() 
	{
		if(showBackground)
		{
			p.background(bg);
			if(changingBackground || backgroundAlpha != 0)
			{
				if(changingBackground)
				{
					p.stroke(0, 0, 0, backgroundAlpha);
					p.fill(0, 0, 0, backgroundAlpha);
					p.rect(0, 0, p.width, p.height);
					backgroundAlpha += 5;
					if(backgroundAlpha >= 255)
					{
						currentBackground = (currentBackground + 1) % backgrounds.size(); 
						bg = backgrounds.get(currentBackground);
						changingBackground = false;
					}
				}
				else
				{
					p.stroke(0, 0, 0, backgroundAlpha);
					p.fill(0, 0, 0, backgroundAlpha);
					p.rect(0, 0, p.width, p.height);
					backgroundAlpha -= 5;
				}
			}
		}
		else
		{
			p.background(1);
		}
	}

	public void toggleCensor() 
	{
		censored = !censored;
	}
}
