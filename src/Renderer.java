import java.awt.Color;
import java.util.HashMap;

import processing.core.PApplet;
import processing.core.PFont;
import twitter.data.object.FallingImage;
import twitter.data.object.FlyingImage;
import twitter.data.object.IMovingImage;

public class Renderer 
{
	private PFont font;
	private PApplet p;

	private HashMap<String, Color> colorMap;
	
	public Renderer(PApplet p)
	{
		font = p.loadFont("fonts/Verdana-20.vlw");
		buildColorMap();
		this.p = p;
	}
	
	public PFont getFont()
	{
		return font;
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
			p.image(box.getUserImage(), box.x + 964 + 10, box.y);
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
