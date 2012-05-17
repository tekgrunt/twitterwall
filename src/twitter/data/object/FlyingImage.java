package twitter.data.object;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * This is the image class that flies across the screen sideways... I basically duplicated the 
 * falling class and changed some of the magic number due to time constraints. There should 
 * only really be one class for any image moving across the screen and we should be able to send 
 * them in any direction we want.
 */
public class FlyingImage 
{
	private PImage fallingImage = new PImage();;
	private PApplet p;
	private int y;
	private int x;
	private int speed;

	public FlyingImage(PImage fallingImage, PApplet p)
	{
		Random randomGenerator = new Random();
		this.fallingImage = fallingImage;
		this.p = p;
		x = -1 * fallingImage.width;
		y = randomGenerator.nextInt(550) + 160;
		speed = 1 + randomGenerator.nextInt(4);
	}
	
	public void updateImage()
	{
		p.image(fallingImage,x,y);
		x += speed;
	}

	public int getY() {
		return y;
	}
	public int getX() {
		return x;
	}
}
