package twitter.data.object;
import java.util.Random;

import processing.core.PImage;

/**
 * This is the original moving image class. It gets updated the same way the tweets do and
 * handles its own position on the screen. The other image class was a cut/paste last minute
 * addition and the images should all be wrapped in one class that takes a direction. Also need to
 * move the images into their own source folder instead of the main.
 * 
 * @author christopherluft
 *
 */
public class FallingImage implements IMovingImage
{
	private PImage image = new PImage();
	private int x;
	private int y;
	private int speed;
	private boolean isFullScreen;	
	
	public boolean isFullScreen()
	{
		return isFullScreen;
	}
	
	public FallingImage(PImage image)
	{
		Random randomGenerator = new Random();
		this.image = image;
		y = -1 * image.height;
		x = randomGenerator.nextInt(1024 - (image.width / 2));
		speed = 2 + randomGenerator.nextInt(3);
	}
	
	public FallingImage(PImage image, String noMove)
	{
		Random randomGenerator = new Random();
		this.image = image;
		y = -1 * image.height;
		x = 0;
		isFullScreen = true;
		if(noMove.equals("random"))
		{
			speed = 2 + randomGenerator.nextInt(4);
		}
		else
		{
			speed = 2;
		}
	}

	public PImage getImage()
	{
		return image;
	}
	
	public void applyUpdate()
	{
		y += speed;
	}

	public int getY() {
		return y;
	}
	public int getX() {
		return x;
	}

	@Override
	public void dispose() 
	{
		image = null;
	}
}
