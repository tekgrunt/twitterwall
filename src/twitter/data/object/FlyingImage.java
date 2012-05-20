package twitter.data.object;

import java.util.Random;
import processing.core.PImage;

/**
 * This is the image class that flies across the screen sideways... I basically duplicated the 
 * falling class and changed some of the magic number due to time constraints. There should 
 * only really be one class for any image moving across the screen and we should be able to send 
 * them in any direction we want.
 */
public class FlyingImage implements IMovingImage
{
	private PImage image;
	private int y;
	private int x;
	private int count = 1;
	private int maxCount = 20;
	private int speed;
	private Random randomGenerator;
	
	public FlyingImage(PImage image)
	{
		randomGenerator = new Random();
		this.image = image;
		x = -1 * image.width;
		y = randomGenerator.nextInt(550) + 160;
		speed = 1 + randomGenerator.nextInt(4);
		maxCount = 20 + randomGenerator.nextInt(50);
	}
	
	public void applyUpdate()
	{
		if(count >= maxCount)
		{
			count = 0;
			speed = 2 + randomGenerator.nextInt(4);
		}
		count++;
		x += speed;
	}

	public PImage getImage()
	{
		return image;
	}
	
	public int getY() 
	{
		return y;
	}
	
	public int getX() 
	{
		return x;
	}

	@Override
	public void dispose() 
	{
		randomGenerator = null;
		image = null;
	}
}
